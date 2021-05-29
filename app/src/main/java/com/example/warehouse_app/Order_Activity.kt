package com.example.warehouse_app

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
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

class Order_Activity : AppCompatActivity(), FilterDialog.DialogListener {

    val db = FirebaseFirestore.getInstance()
    val settings = firestoreSettings {
        isPersistenceEnabled = false
    }
    var order_id = ""
    var order_index = 0
    var docid = ""
    var default_item_list = ArrayList<ArrayList<String>>()
    var item_list = ArrayList<ArrayList<String>>()
    var item_ids_arr = ArrayList<String>()
    val dialog = FilterDialog()
    private val RV_Adapter = RecycleView_Adapter(this, item_list)

    override fun onBackPressed(){
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun Filter(filtered_txt: String, field_id: Int) {
        Resetfilter()
        default_item_list.clear()
        default_item_list.addAll(item_list)
        item_list.clear()
        for ( i in 0..default_item_list.size-1){
            if (filtered_txt == default_item_list[i][field_id]) {
                item_list.add(default_item_list[i])
            }
        }
        RV_Adapter.notifyDataSetChanged()
    }

    override fun Resetfilter() {
        if (default_item_list.size != 0) {
            item_list.clear()
            item_list.addAll(default_item_list)
            default_item_list.clear()
            RV_Adapter.notifyDataSetChanged()
        }
    }

    fun set_dialog(btn : Button){
        btn.setOnClickListener {
            dialog.completed_bool = true
            dialog.show(supportFragmentManager, "Dialog")
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
                alert_builder.setTitle("Czy napewno chcesz zatwierdzić zamówienie ?")
                alert_builder.setNegativeButton("Tak") { _: DialogInterface, _: Int ->
                    val result_intent = Intent()
                    result_intent.putExtra("result", order_index)
                    result_intent.putExtra("document_id", order_id)
                    setResult(1, result_intent)
                    finish()
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }
                alert_builder.setPositiveButton("Nie"){ _: DialogInterface, _: Int ->}
                alert_builder.show()
            }
            set_dialog(order_items_filter)
        }
        else {
            setContentView(R.layout.activity_order_completed)
            set_dialog(completed_order_filter)
        }

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
                                    Toast.makeText(this, "Wystąpił błąd", Toast.LENGTH_SHORT).show()
                                }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Wystąpił błąd", Toast.LENGTH_SHORT).show()
                }

        order_rv.adapter = RV_Adapter
        order_rv.layoutManager = LinearLayoutManager(this)
    }
}