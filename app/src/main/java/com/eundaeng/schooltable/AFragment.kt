package com.eundaeng.schooltable

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
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
import io.teamif.patrick.comcigan.ComciganAPI
import kotlinx.coroutines.*
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.regex.Matcher
import java.util.regex.Pattern


class AFragment : Fragment()
{
    fun createFile(filename:String, mimeType:String) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = mimeType
            putExtra(Intent.EXTRA_TITLE, filename)
        }
        startActivityForResult(intent, 1)
    }
    fun saveCache(data: String) {
        requireContext().openFileOutput("data.lol", Context.MODE_PRIVATE).use { stream ->
            stream.write(data.toByteArray())
        }
        println(data)
    }
    fun readCache(): String {
        var contents = ""
        try {
            requireContext().openFileInput("data.lol").bufferedReader().useLines { lines ->
                contents = lines.joinToString("\n")
            }
        }catch(e: FileNotFoundException){
            contents = ""
        }
        return contents
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        val view = inflater.inflate(R.layout.comci, container, false)
        var readstr =
        if(readCache() == "") {
            saveCache("1-4")
            "1-4"
        } else readCache()
        val sch = view.findViewById<Spinner>(R.id.sch)
        val schlist = arrayOf("영월고등학교")
        sch.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, schlist)

        val gr = view.findViewById<Spinner>(R.id.gr)
        val grlist = arrayOf("1","2","3")
        gr.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, grlist)

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



        fun abc() {
            val rgrade: String = readstr.split("-")[0]
            val rclass: String = readstr.split("-")[1]
            gr.setSelection(rgrade.trim().toInt() - 1)
            cl.setSelection(rclass.toInt() - 1)
            CoroutineScope(Dispatchers.IO).launch {
                //val schid = Comcigan.searchSchool("영월고등학교")
                //val timetable = Comcigan.getTimeTable(schid[0]!!.second, rgrade.toInt(), rclass.toInt())
                val timetable = Comcigan.getTimeTable(rgrade.trim().toInt(), rclass.toInt())
                val timetotal = Comcigan.getClassNumb() //4,4,4
                println(timetotal.joinToString(","))
                for (i in 0..2) {
                    val cclist = arrayListOf<String>()
                    for (j in 1..timetotal[i])
                        cclist.add("$j")
                    cllist.add(cclist)
                }
                for (we in 0..4) {
                    for (ctime in 0..7) {
                        val timetime = timetable.getJSONArray(we).getJSONObject(ctime)
                        val sb = timetime.getString("subject")
                        val th = timetime.getString("teacher")
                        view.findViewById<Button>(week[we][ctime]).text = sb + "\n" + th
                        if (timetime.getString("isChanged") == "true")
                            view.findViewById<Button>(week[we][ctime]).background =
                                Color.parseColor("#fcef00").toDrawable()
                        else
                            view.findViewById<Button>(week[we][ctime]).background =
                                Color.parseColor("#fdffde").toDrawable()
                    }
                }
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
                            cl.setSelection(0)
                            if(readstr != "${gr.selectedItem}-${cl.selectedItem}") {
                                readstr = "${gr.selectedItem}-${cl.selectedItem}"
                                saveCache(readstr)
                                abc()
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            //TODO("Not yet implemented")
                        }
                    }
                    cl.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            if(readstr != "${gr.selectedItem}-${cl.selectedItem}") {
                                readstr = "${gr.selectedItem}-${cl.selectedItem}"
                                saveCache(readstr)
                                abc()
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            //TODO("Not yet implemented")
                        }
                    }

                    cl.adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        cllist[gr.selectedItemPosition]
                    )
                    cl.setSelection(rclass.toInt()-1)
                })
                /*
                val day = arrayOf("", "monday", "tuesday", "wednesday", "thursday", "friday")
                val timelist = (timetable.get("시간표") as ArrayList<ArrayList<String>>)
                val otimelist = (timetable.get("본시간표") as ArrayList<ArrayList<String>>)
                for (i in 1..5) {
                    for (j in 1..timelist[i - 1].size) {
                        /*view.findViewById<Button>(week[i-1][j-1]).text = json.getJSONObject("school").getJSONObject(day[i])
                            .getJSONObject(j.toString()).getString("sb") + "\n" +
                    json.getJSONObject("school").getJSONObject(day[i])
                            .getJSONObject(j.toString()).getString("th")
                    */
                        if (otimelist[i - 1][j - 1] != timelist[i - 1][j - 1])
                            view.findViewById<Button>(week[i - 1][j - 1]).background =
                                Color.parseColor("#fcef00").toDrawable()
                        view.findViewById<Button>(week[i - 1][j - 1]).text = timelist[i - 1][j - 1]
                        println((timetable.get("시간표") as ArrayList<ArrayList<String>>)[0][0])
                    }
                }
                //val timetable = Co mci.kt.getTimeTable(43321, 2, 2)
                for (i in 1 until timetable.getJSONArray("학급수").length()) {
                    println((8 - timetable.getJSONArray("학급수").getInt(i)))
                    val realcl = (8 - timetable.getJSONArray("학급수").getInt(i))
                    val cclist = arrayListOf<String>()
                    for (j in 1..realcl) {
                        cclist.add(" $j ")
                        println("realcl : $j")
                    }
                    cllist.add(cclist)
                }*/


                /*
            for (i in 0..39) {
                val a = view.findViewById<Button>(idArray[i])
                a.text = timetable[i]?.first + "\n" + timetable[i]?.second
                println(timetable[i]?.first + "index = $i, default id = $defaultId current id = ${idArray[i]}")
            }*/
            }
        }
        abc()

        return view
    }
}