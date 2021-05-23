package com.example.warehouse_app

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestoreSettings
import kotlinx.android.synthetic.main.activity_order.*

class Order_Activity : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()
    val settings = firestoreSettings {
        isPersistenceEnabled = false
    }
    var order_id = ""
    var order_index = 0
    var docid = ""
    var item_list = ArrayList<ArrayList<String>>()
    var item_ids_arr = ArrayList<String>()
    private val RV_Adapter = RecycleView_Adapter(this, item_list)

    override fun onBackPressed(){
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
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
                alert_builder.setTitle("Czy napewno chcesz się wylogować")
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
        }
        else
            setContentView(R.layout.activity_order_completed)

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