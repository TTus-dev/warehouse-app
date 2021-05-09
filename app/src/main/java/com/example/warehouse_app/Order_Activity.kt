package com.example.warehouse_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_order.*

class Order_Activity : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()
    var order_id = ""
    var item_list = ArrayList<ArrayList<String>>()
    var item_ids_arr = ArrayList<String>()
    private val RV_Adapter = RecycleView_Adapter(this, item_list)

    override fun onBackPressed(){
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        order_id = intent.getStringExtra("order_id")!!

        db.collection("Orders").document(order_id)
                .get()
                .addOnSuccessListener {
                    item_ids_arr = it["Item_array"] as ArrayList<String>
                    //Log.d("dbg2", "test: " + item_ids_arr.size)
                    for (j in 0..item_ids_arr.size - 1 step 2){
                        Log.d("dbg2", item_ids_arr[j])
                        val inner_arr = ArrayList<String>()
                        db.collection("Items").document(item_ids_arr[j])
                                .get()
                                .addOnSuccessListener {
                                    if (it["Place"] != null){
                                        inner_arr.add(it["Place"] as String)
                                    }
                                    else {inner_arr.add("text")}
                                    if (it["Place"] != null){
                                        inner_arr.add(it["Product_code"] as String)
                                    }
                                    else {inner_arr.add("text")}
                                    inner_arr.add(item_ids_arr[j+1])
                                    item_list.add(inner_arr)
                                    Log.d("dbg2", item_list.toString())
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