package com.example.warehouse_app

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycleview_item.view.*

class RecycleView_Adapter(
        private val context: Context,
        private val orderlist: ArrayList<ArrayList<String>>): RecyclerView.Adapter<RecycleView_Adapter.ViewHolder>(){

    var oa_bool = false

    fun bool_switch() {
        oa_bool = true
    }

    override fun onCreateViewHolder(viewgr: ViewGroup, position: Int): ViewHolder {
        val OrderlistItem = LayoutInflater.from(context).inflate(R.layout.recycleview_item, viewgr, false)
        return ViewHolder(OrderlistItem)
    }

    override fun getItemCount(): Int {
        return orderlist.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(orderlist[position])
    }


    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(order_attr: ArrayList<String>){
            itemView.tv1.text = order_attr[0]
            itemView.tv2.text = order_attr[1]
            itemView.tv3.text = order_attr[2]
            if (oa_bool) {
                itemView.setOnClickListener {
                    val i = Intent(itemView.context, Order_Activity::class.java)
                    i.putExtra("order_id", order_attr[3])
                    itemView.context.startActivity(i)
                }
            }
        }
    }
}