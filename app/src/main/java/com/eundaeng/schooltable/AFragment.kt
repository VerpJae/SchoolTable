package com.eundaeng.schooltable

import android.content.Context.MODE_PRIVATE
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.regex.Matcher
import java.util.regex.Pattern


class AFragment : Fragment()
{
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        val view = inflater.inflate(R.layout.comci, container, false)

        //내부 저장소와 연결되어 있는 쓰기 스트림 추출
        //MODE_PRIVATE 파일 덮어쓰기 , MODE_APPEND 파일 새로 쓰기
        //내부 저장소와 연결되어 있는 쓰기 스트림 추출
        //MODE_PRIVATE 파일 덮어쓰기 , MODE_APPEND 파일 새로 쓰기
        var fos: FileOutputStream = requireActivity().openFileOutput("myFile.dat", MODE_PRIVATE)
        var fis: FileInputStream? = null
        var readstr = ""
        try {
            fis = requireActivity().openFileInput("myFile.dat")
            val txt = ByteArray(fis!!.available())   // 읽어들일 파일의 크기만큼 메모리 할당
            fis.read(txt)
            readstr = txt.toString(Charsets.UTF_8)
            if(readstr == "") {
                fos.write("GRADEa 1 aCLASSb 2 b".toByteArray())
                fos.close()
                fos = requireActivity().openFileOutput("myFile.dat", MODE_PRIVATE)
            }
        }catch (e : IOException){
            fos.write("GRADEa 1 aCLASSb 2 b".toByteArray())
            fos.close()
            fos = requireActivity().openFileOutput("myFile.dat", MODE_PRIVATE)
            fis = requireActivity().openFileInput("myFile.dat")
            readstr = "GRADEa 1 aCLASSb 2 b"
        }finally {
            fis = requireActivity().openFileInput("myFile.dat")
            val txt = ByteArray(fis!!.available())   // 읽어들일 파일의 크기만큼 메모리 할당
            fis.read(txt)
            readstr = txt.toString(Charsets.UTF_8)
            println("readstr: $readstr")
        }
        val sch = view.findViewById<Spinner>(R.id.sch)
        val schlist = arrayOf("영월중학교")
        sch.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, schlist)

        val gr = view.findViewById<Spinner>(R.id.gr)
        val grlist = arrayOf("1","2","3")
        gr.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, grlist)
        val grade: String = " ${gr.selectedItem} "

        val cl = view.findViewById<Spinner>(R.id.cl)
        val cllist = arrayListOf<ArrayList<String>>()

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

        var rgrade: String = "3"
        var rclass: String = "2"

        val pattern: Pattern = Pattern.compile("GRADEa (\\d) aCLASSb (\\d) b")
        var matcher: Matcher = pattern.matcher(readstr)
        while (matcher.find()) {    // 정규식과 매칭되는 값이 있으면
            rgrade = matcher.group(1)!!.trim()
            rclass = matcher.group(2)!!.trim()
            matcher = pattern.matcher(readstr)
        }
        CoroutineScope(Dispatchers.IO).launch {
            val schid = Comcigan.searchSchool("영월중학교")
            val timetable = Comcigan.getTimeTable(schid[0]!!.second, rgrade.toInt(), rclass.toInt())
            val day = arrayOf("","monday", "tuesday", "wednesday", "thursday", "friday")
            val timelist = (timetable.get("시간표") as ArrayList<ArrayList<String>>)
            val otimelist = (timetable.get("본시간표") as ArrayList<ArrayList<String>>)
            for (i in 1..5) {
                for (j in 1..timelist[i-1].size) {
                    /*view.findViewById<Button>(week[i-1][j-1]).text = json.getJSONObject("school").getJSONObject(day[i])
                            .getJSONObject(j.toString()).getString("sb") + "\n" +
                    json.getJSONObject("school").getJSONObject(day[i])
                            .getJSONObject(j.toString()).getString("th")
                    */
                    if(otimelist[i-1][j-1] != timelist[i-1][j-1])
                        view.findViewById<Button>(week[i-1][j-1]).background = Color.parseColor("#fcef00").toDrawable()
                    view.findViewById<Button>(week[i-1][j-1]).text = timelist[i-1][j-1]
                    println((timetable.get("시간표") as ArrayList<ArrayList<String>>)[0][0])
                }
            }
            //val timetable = Co mci.kt.getTimeTable(43321, 2, 2)
            for(i in 1 until timetable.getJSONArray("학급수").length()) {
                println((8 - timetable.getJSONArray("학급수").getInt(i)))
                val realcl = (8 - timetable.getJSONArray("학급수").getInt(i))
                val cclist = arrayListOf<String>()
                for(j in 1..realcl){
                    cclist.add(" $j ")
                    println("realcl : $j")
                }
                cllist.add(cclist)
            }
            var str: String
            requireActivity().runOnUiThread(java.lang.Runnable {
                gr.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        println("position : $position, grade : ${grlist[position]}")
                        cl.adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_dropdown_item,
                            cllist[position]
                        )
                        str = "GRADEa${grade}aCLASSb${cl.selectedItem}b"
                        fos.write(str.toByteArray())
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        TODO("Not yet implemented")
                    }
                }
                cl.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        str = "GRADE[$grade]CLASS[${cl.selectedItem}]"
                        fos.write(str.toByteArray())
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        TODO("Not yet implemented")
                    }
                }

                cl.adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    cllist[3 - grade.substring(1,2).toInt()]
                )
            })

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