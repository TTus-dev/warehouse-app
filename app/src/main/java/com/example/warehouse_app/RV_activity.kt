package com.example.warehouse_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_r_v_activity.*
import com.google.firebase.firestore.FirebaseFirestore

class RV_activity() : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()
    val user_id = "001"
    var rv_list = ArrayList<ArrayList<String>>()
    var item_list = ArrayList<ArrayList<String>>()
    private val RV_Adapter = RecycleView_Adapter(this, rv_list)

    override fun onBackPressed(){
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        RV_Adapter.bool_switch()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_r_v_activity)

        db.collection("Orders")
            .get()
            .addOnCompleteListener {

                if (it.isSuccessful) {
                    for (document in it.result!!) {
                        val inner_array = ArrayList<String>()
                        if (document.data["worker_id"] == user_id) {

                            if (document.data["Address"] != null) {
                                inner_array.add(document.data["Address"] as String)
                            }
                            else {inner_array.add("text")}

                            if (document.data["Order_no"] != null) {
                                inner_array.add(document.data["Order_no"] as String)
                            }
                            else {inner_array.add("text")}

                            if (document.data["Item_quant"] != null) {
                                inner_array.add(document.data["Item_quant"] as String)
                            }
                            else {inner_array.add("text")}

                            inner_array.add(document.id)
                            rv_list.add(inner_array)
                        }
                        RV_Adapter.notifyDataSetChanged()
                    }
                }
                else if (!it.isSuccessful){
                    Toast.makeText(this, "Wystąpił błąd", Toast.LENGTH_SHORT).show()
                }
            }

        main_recycler.adapter = RV_Adapter
        main_recycler.layoutManager = LinearLayoutManager(this)
    }
}