package com.eundaeng.schooltable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.json.JSONObject
import org.jsoup.Jsoup

object Comci {/*
    suspend fun getTimeTable(schoolId: Int, grade: Int, cl: Int): Array<Pair<String, String>> {
        val json: JSONObject = GlobalScope.async(Dispatchers.IO) {
            val url = "https://api.biy.kr/v2/timetable/timetable.php?c=43321&b=2-2&t=json"
            val doc = Jsoup.connect(url).ignoreContentType(true).get().text()
            JSONObject(doc)
        }.await()
        val day = arrayOf("monday", "tuesday", "wednesday", "thursday", "friday")
        val list = JSONObject()
        for (i in 0..4)
            for (j in 1..8) {
                val sb = json.getJSONObject("school").getJSONObject(day[i])
                    .getJSONObject(j.toString()).getString("sb")
                val th = json.getJSONObject("school").getJSONObject(day[i])
                    .getJSONObject(j.toString()).getString("th")
                 = Pair(sb, th)
            }
        return list
    }*/
}