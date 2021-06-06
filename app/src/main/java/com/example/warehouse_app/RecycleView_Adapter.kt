package com.example.warehouse_app

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycleview_item.view.*

class RecycleView_Adapter(
        private val mcontext: Context,
        private val orderlist: ArrayList<ArrayList<String>>): RecyclerView.Adapter<RecycleView_Adapter.ViewHolder>(){

    var oa_bool = false
    var drawer_selection = 0

    fun drawer_sel_set(x : Int) {
        drawer_selection = x
    }

    override fun onCreateViewHolder(viewgr: ViewGroup, position: Int): ViewHolder {
        val OrderlistItem = LayoutInflater.from(mcontext).inflate(R.layout.recycleview_item, viewgr, false)
        return ViewHolder(OrderlistItem)
    }

    override fun getItemCount(): Int {
        return orderlist.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(orderlist[position])
        if (oa_bool) {
            holder.itemView.setOnClickListener {
                val act_origin = mcontext as Activity
                val i = Intent(act_origin, Order_Activity::class.java)
                i.putExtra("order_id", orderlist[position][3])
                i.putExtra("order_address", orderlist[position][0])
                i.putExtra("order_number", orderlist[position][1])
                if (drawer_selection == 0)
                {
                    i.putExtra("docid", "Orders")
                    i.putExtra("order_index", position)
                    act_origin.startActivityForResult(i, 1)
                }
                else
                {
                    i.putExtra("docid", "Completed_orders")
                    act_origin.startActivity(i)
                }
            }
        }
    }


    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(order_attr: ArrayList<String>){
            itemView.tv1.text = order_attr[0]
            itemView.tv2.text = order_attr[1]
            itemView.tv3.text = order_attr[2]
        }
    }

}