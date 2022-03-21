package com.eundaeng.schooltable

import org.json.JSONObject
import org.jsoup.Jsoup
import java.lang.Double.isNaN
import kotlin.math.floor

object Comcigan {
        fun getTimeTable(schoolId: String, grade: Int, cl: Int): JSONObject {
            val result = JSONObject()
            try {
                if (!isNaN(schoolId.toDouble())) {
                    val i: String = Jsoup
                        .connect("http://comci.kr:4082/st")
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36")
                        .get()
                        .select("script")
                        .get(1)
                        .html()
                    val encodeText = java.lang.String(android.util.Base64.encodeToString(java.lang.String((scData(i) + schoolId + "_0_1")).getBytes(),android.util.Base64.DEFAULT))
                    val dataf: String = Jsoup
                        .connect("http://comci.kr:4082" + getUrl(i).split("?")[0] + "?" + encodeText)
                        .get()
                        .text()
                    var data = JSONObject(dataf.replace(Regex("""\u0000"""),""))
                    println(data)

                    val zaryo = getVariableName(i)
                    result.put("수업시간", data.get("일과시간"))
                    result.put("학급수", data.getJSONArray("학급수"))
                    val timetable = arrayListOf<ArrayList<String>>()
                    val otimetable = arrayListOf<ArrayList<String>>()

                    var ord: Any
                    var dad: Double
                    var th: Int
                    var sb: Int
                    var na: String
                    var ts: Int
                    var ss: Int
                    for (we in 1..6) {
                        val tarr = ArrayList<String>()
                        val otarr = ArrayList<String>()
                        for (t in 1..8) {
                            ord = data.getJSONArray(zaryo[0]).getJSONArray(grade).getJSONArray(cl).getJSONArray(we).getDouble(t) // 기본시간표
                            dad = data.getJSONArray(zaryo[1]).getJSONArray(grade).getJSONArray(cl).getJSONArray(we).getDouble(t) // 시간표
                            th = floor(dad / 100).toInt() // 시간표
                            ts = floor(ord / 100).toInt() // 기본 시간표
                            sb = (dad - th * 100).toInt() //시간표
                            ss = (ord - ts * 100).toInt() //기본 시간표
                            if (dad > 100) {
                                na = if (th < data.getJSONArray(zaryo[3]).length()) {
                                    data.getJSONArray(zaryo[4]).getString(th).substring(0, 2)
                                } else {
                                    ""
                                }
                                println("hi "+ data.getJSONArray(zaryo[5]).getString(ss) + data.getJSONArray(zaryo[5]).getString(sb))
                                otarr.add(t - 1, data.getJSONArray(zaryo[5]).getString(ss) + "\n" + na)
                                tarr.add(t - 1, data.getJSONArray(zaryo[5]).getString(sb) + "\n" + na)
                            }
                        }
                        otimetable.add(we - 1, otarr)
                        timetable.add(we - 1, tarr)
                    }
                    result.put("status", "true")
                    result.put("본시간표", otimetable)
                    result.put("시간표", timetable)
                    return result
                } else {
                    result.put("status", "false")
                }
            } catch (e: Error) {
                result.put("status", "false")
            }
            return result
        }
        fun searchSchool(schoolName: String): Array<Pair<String, String>?> {
            var result: Any = Jsoup
                .connect(("http://comci.kr:4082" + getUrl(Jsoup.connect("http://comci.kr:4082/st").header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36").get().select("script").get(1).html()) + java.net.URLEncoder.encode(schoolName, "EUC-KR")))
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36")
                .header("Referer", "http://comci.kr:4082/st")
                .get()
                .text();
            result = JSONObject((result as String).replace(Regex("\u0000/"), ""))
            result = result.getJSONArray("학교검색")
            val resarr = arrayOfNulls<Pair<String, String>>(result.length())
            println(result)
            for(i in 0 until result.length()) {
                val x = result.getJSONArray(i)
                println(x)
                val name = (x.getString(2) + "(" + x.getString(1) + ")")
                val value = x.getString(3)
                resarr[i] = Pair(name,value)
            }
            return resarr
        }
    fun getVariableName(i: String): List<String> {
        println(i)
        val fa = Regex("자료\\.자료\\d\\d\\d").findAll(i)
        return fa.toList().map { it -> it.value.substring(3, 8); }
    }
        fun getUrl(i: String): String {
            return Regex("""/([^']+)""").findAll(i.substring(i.indexOf("url"))).toList()[0].value
        }
        fun scData(i: String): String {
            return Regex("""[^']+""").findAll(i.substring(i.indexOf("sc_data('") + 8)).toList()[0].value
        }/*
        return {
            searchSchool: searchSchool.bind(),
            getTimeTable: getTimeTable.bind()
        }*/
}