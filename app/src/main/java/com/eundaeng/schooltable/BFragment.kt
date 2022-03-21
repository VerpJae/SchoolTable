package com.eundaeng.schooltable

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.recycler.*
import org.json.JSONObject
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BFragment : Fragment()
{

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        val view: View = inflater.inflate(R.layout.recycler, container, false)
        val refreshlayout: SwipeRefreshLayout? = view.findViewById(R.id.refresh_layout)
        ThreadClass().start()
        refreshlayout?.setOnRefreshListener {
            ThreadClass().start()
            refreshlayout.isRefreshing = false
        }
        return view
    }
    inner class ThreadClass:Thread() {
        val month = arrayOf<String>("31", "28", "31", "30", "31", "30", "31", "31", "30", "31", "30", "31")
        val api = "https://open.neis.go.kr/hub/mealServiceDietInfo"
        val KEY = "71b5a83bba9e4a6083b903e1b1f7e144"
        val Type = "json"
        val SD_CODE = "7800076"
        val currentTime: Long = System.currentTimeMillis()  // ms로 반환
        val yyyy = SimpleDateFormat("yyyy", Locale.KOREA).format(currentTime)
        val MM = SimpleDateFormat("MM", Locale.KOREA).format(currentTime)
        val dd = SimpleDateFormat("dd", Locale.KOREA).format(currentTime)
        val tmr = SimpleDateFormat("yyyyMMdd", Locale.KOREA).format(currentTime + 24*60*60*1000)
        override fun run() {
            if(yyyy.toInt()%4 == 0){
                month.set(1, "29")
            }
            val today =
                "$api?KEY=$KEY&Type=$Type&pIndex=1&pSize=100&ATPT_OFCDC_SC_CODE=K10&SD_SCHUL_CODE=$SD_CODE&MMEAL_SC_CODE=2&MLSV_FROM_YMD=${yyyy+MM+dd}&MLSV_TO_YMD=${yyyy+MM+month[MM.toInt()]}"

            val parse =
                Jsoup.connect(today)
                    .ignoreContentType(true).get().text()
            val list = ArrayList<Meal>()
            /*
                list.add(DataStudent(null,"1번","정상","2091-02-30 07:59:24"))
                list.add(DataStudent(null,"2번","유증상","2091-02-30 07:41:37"))
                list.add(DataStudent(null,"3번","미참여","2091-02-30 06:52:02"))
                */
            val jsonArr =
                JSONObject(parse).getJSONArray("mealServiceDietInfo").getJSONObject(1)
                    .getJSONArray("row")
            for(i in 0 until jsonArr.length()){
                val basicDate = jsonArr.getJSONObject(i).getString("MLSV_YMD")
                val preDate = basicDate.substring(4,8)
                val day = SimpleDateFormat("(E)", Locale.KOREA).format(SimpleDateFormat("yyyyMMdd", Locale.KOREA).parse(basicDate)!!)
                val date = preDate + day
                val dish = jsonArr.getJSONObject(i).getString("DDISH_NM").replace(Regex("\\d+\\."), "").replace(" ", "\n")
                list.add(Meal(date, dish))
            }

            val adapter = MainRvAdapter(MainActivity.ApplicationContext(), list)
            activity!!.runOnUiThread {
                xml_main_rv_students.layoutManager =
                    LinearLayoutManager(
                        MainActivity.ApplicationContext(),
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                xml_main_rv_students.adapter = adapter
                xml_main_rv_students.layoutManager =
                    GridLayoutManager(MainActivity.ApplicationContext(), 1)

                adapter.notifyDataSetChanged()
            }
        }
    }

}