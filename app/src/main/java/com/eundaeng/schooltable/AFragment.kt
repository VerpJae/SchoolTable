package com.eundaeng.schooltable

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*
import org.json.JSONObject
import org.jsoup.Jsoup

class AFragment : Fragment()
{
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        val view = inflater.inflate(R.layout.comci, container, false)
        val defaultId = R.id.b1
        val idArray = arrayOf(R.id.b1, R.id.b2, R.id.b3, R.id.b4, R.id.b5
                            , R.id.b6, R.id.b7, R.id.b8, R.id.b9, R.id.b10
                            , R.id.b11, R.id.b12, R.id.b13, R.id.b14, R.id.b15
                            , R.id.b16, R.id.b17, R.id.b18, R.id.b19, R.id.b20
                            , R.id.b21, R.id.b22, R.id.b23, R.id.b24, R.id.b25
                            , R.id.b26, R.id.b27, R.id.b28, R.id.b29, R.id.b30
                            , R.id.b31, R.id.b32, R.id.b33, R.id.b34, R.id.b35
                            , R.id.b36, R.id.b37, R.id.b38, R.id.b39, R.id.b40)
        val mon = arrayOf(R.id.b1, R.id.b6, R.id.b11, R.id.b16, R.id.b21, R.id.b26, R.id.b31, R.id.b36)
        val tue = arrayOf(R.id.b2, R.id.b7, R.id.b12, R.id.b17, R.id.b22, R.id.b27, R.id.b32, R.id.b37)
        val wed = arrayOf(R.id.b3, R.id.b8, R.id.b13, R.id.b18, R.id.b23, R.id.b28, R.id.b33, R.id.b38)
        val thu = arrayOf(R.id.b4, R.id.b9, R.id.b14, R.id.b19, R.id.b24, R.id.b29, R.id.b34, R.id.b39)
        val fri = arrayOf(R.id.b5, R.id.b10, R.id.b15, R.id.b20, R.id.b25, R.id.b30, R.id.b35, R.id.b40)
        val week = arrayOf(mon, tue, wed, thu, fri)
        CoroutineScope(Dispatchers.IO).launch {
            val url = "https://api.biy.kr/v2/timetable/timetable.php?c=43321&b=3-2&t=json"
            val doc = Jsoup.connect(url).ignoreContentType(true).get().text()
            val json = JSONObject(doc)

            //val timetable = Co mci.kt.getTimeTable(43321, 2, 2)
            val timetable = arrayOfNulls<Pair<String, String>>(40)
            val day = arrayOf("","monday", "tuesday", "wednesday", "thursday", "friday")
            for (i in 1..5) {
                for (j in 1..8) {
                    view.findViewById<Button>(week[i-1][j-1]).text = json.getJSONObject("school").getJSONObject(day[i])
                            .getJSONObject(j.toString()).getString("sb") + "\n" +
                    json.getJSONObject("school").getJSONObject(day[i])
                            .getJSONObject(j.toString()).getString("th")

                }
            }
            /*
            for (i in 0..39) {
                val a = view.findViewById<Button>(idArray[i])
                a.text = timetable[i]?.first + "\n" + timetable[i]?.second
                println(timetable[i]?.first + "index = $i, default id = $defaultId current id = ${idArray[i]}")
            }*/
        }
        return view
    }
}