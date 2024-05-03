package com.example.warehouse_app

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestoreSettings
import kotlinx.android.synthetic.main.activity_order.*
import kotlinx.android.synthetic.main.activity_order.order_rv
import kotlinx.android.synthetic.main.activity_order_completed.*

class Order_Activity : Heap_sort(), FilterDialog.DialogListener, SortDialog.DialogListener{

    val db = FirebaseFirestore.getInstance()
    val settings = firestoreSettings {
        isPersistenceEnabled = false
    }
    var order_id = ""
    var order_index = 0
    var docid = ""
    private var filter_str = ""
    private var filter_index = -1
    private var sort_index = -1
    private var sort_type = -1
    var default_item_list = ArrayList<ArrayList<String>>()
    var item_list = ArrayList<ArrayList<String>>()
    var item_ids_arr = ArrayList<String>()
    private val RV_Adapter = RecycleView_Adapter(this, item_list)

    override fun onBackPressed(){
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    fun reset_rvlist()
    {
        if (default_item_list.size != 0) {
            item_list.clear()
            item_list.addAll(default_item_list)
            default_item_list.clear()
        }
    }

    override fun Sort(field_id: Int, desc: Int) {
        if (filter_str == "" && filter_index == -1)
        {
            default_item_list.clear()
            default_item_list.addAll(item_list)
        }
        sort_index = field_id
        sort_type = desc
        heap_sort(item_list, field_id)
        if (desc == 1) { item_list.reverse() }
        RV_Adapter.notifyDataSetChanged()
    }

    override fun ResetSort()
    {
        sort_type = -1
        sort_index = -1
        reset_rvlist()
        if (filter_str != "" && filter_index > -1)
        {
            Filter(filter_str, filter_index)
        }
        RV_Adapter.notifyDataSetChanged()
    }

    override fun Filter(filtered_txt: String, field_id: Int) {
        Resetfilter()
        var _filter_helper = ArrayList<ArrayList<String>>()
        if (sort_type == -1 && sort_index == -1)
        {
            default_item_list.clear()
            default_item_list.addAll(item_list)
        }
        filter_index = field_id
        filter_str = filtered_txt
        _filter_helper.addAll(item_list)
        item_list.clear()
        if (field_id == 2)
        {
            for (i in 0.._filter_helper.size - 1)
            {
                if (_filter_helper[i][field_id].toInt() == filtered_txt.toInt())
                {
                    item_list.add(_filter_helper[i])
                }
            }
        }
        else
        {
            for (i in 0.._filter_helper.size - 1)
            {
                if (_filter_helper[i][field_id].contains(filtered_txt, true))
                {
                    item_list.add(_filter_helper[i])
                }
            }
        }
        RV_Adapter.notifyDataSetChanged()
    }

    override fun Resetfilter()
    {
        filter_index = -1
        filter_str = ""
        reset_rvlist()
        if (sort_index > -1 && sort_type > -1)
        {
            Sort(sort_index, sort_type)
        }
        RV_Adapter.notifyDataSetChanged()
    }

    fun set_dialog(btn1 : Button, btn2 : Button){
        val dialog1 = FilterDialog()
        val dialog2 = SortDialog()
        dialog1.items_bool = true
        btn1.setOnClickListener {
            dialog1.show(supportFragmentManager, "Dialog")
        }
        dialog2.items_bool = true
        btn2.setOnClickListener {
            dialog2.show(supportFragmentManager, "Dialog")
        }
    }


    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        super.onCreate(savedInstanceState)
        db.firestoreSettings = settings
        order_id = intent.getStringExtra("order_id")!!
        order_index = intent.getIntExtra("order_index", 0)
        docid = intent.getStringExtra("docid")!!

        if(docid == "Orders") {
            setContentView(R.layout.activity_order)
            readybtn.setOnClickListener {
                val alert_builder = AlertDialog.Builder(this)
                alert_builder.setTitle(R.string.order_confirm_dialog_title)
                alert_builder.setNegativeButton(R.string.yes) { _: DialogInterface, _: Int ->
                    val result_intent = Intent()
                    result_intent.putExtra("result", order_index)
                    result_intent.putExtra("document_id", order_id)
                    setResult(1, result_intent)
                    finish()
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }
                alert_builder.setPositiveButton(R.string.no){ _: DialogInterface, _: Int ->}
                alert_builder.show()
            }
            set_dialog(order_items_filter, order_items_sort)
        }
        else {
            setContentView(R.layout.activity_order_completed)
            set_dialog(completed_order_filter, completed_order_sort)
        }

        val order_address = intent.getStringExtra("order_address")
        val order_no = intent.getStringExtra("order_number")

        this.title = order_address + " " + order_no

        db.collection(docid).document(order_id)
                .get()
                .addOnSuccessListener {
                    item_ids_arr = it["Item_array"] as ArrayList<String>
                    for (j in 0..item_ids_arr.size - 1 step 2){
                        val inner_arr = ArrayList<String>()
                        db.collection("Items").document(item_ids_arr[j])
                                .get()
                                .addOnSuccessListener {
                                    if (it["Place"] != null){
                                        inner_arr.add(it["Place"] as String)
                                    }
                                    else {inner_arr.add("text")}
                                    if (it["Product_code"] != null){
                                        inner_arr.add(it["Product_code"] as String)
                                    }
                                    else {inner_arr.add("text")}
                                    inner_arr.add(item_ids_arr[j+1])
                                    item_list.add(inner_arr)
                                    RV_Adapter.notifyDataSetChanged()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show()
                                }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show()
                }

        order_rv.adapter = RV_Adapter
        order_rv.layoutManager = LinearLayoutManager(this)
    }
}