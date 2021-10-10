package com.eundaeng.schooltable

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class MainRvAdapter(val context: Context, val mealList: ArrayList<Meal>) :
    RecyclerView.Adapter<MainRvAdapter.Holder>() {
    var array = mealList
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.mealtable, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return mealList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mealList[position])
        holder.itemView.setOnClickListener {

            Toast.makeText(context, "Clicked: ${mealList.get(position).date}", Toast.LENGTH_SHORT).show()
            //새 액티비티를 열고 웹뷰를 이용해서 상세보기 페이지를 보여 준다.

        }
    }
    fun add(meal: Meal) {
        array.add(meal)
        notifyItemInserted(array.size)
    }
    fun reset(){
        val size = array.size
        array = arrayListOf()
        for(i in 0..size) {
            notifyItemRemoved(size)
        }
    }
    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        val textdate = itemView?.findViewById<TextView>(R.id.text_date)
        val textmeal = itemView?.findViewById<TextView>(R.id.text_meal)

        fun bind (meal: Meal) {
            //context: Context
            /* dogPhoto의 setImageResource에 들어갈 이미지의 id를 파일명(String)으로 찾고,
            이미지가 없는 경우 안드로이드 기본 아이콘을 표시한다.*/

            /* 나머지 TextView와 String 데이터를 연결한다. */
            textdate?.text = meal.date
            textmeal?.text = meal.dish
        }
    }
}