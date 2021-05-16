package com.example.warehouse_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_r_v_activity.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestoreSettings
import kotlin.collections.ArrayList

class RV_activity() : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()
    val settings = firestoreSettings {
        isPersistenceEnabled = false
    }
    var user_id = ""
    var rv_list = ArrayList<ArrayList<String>>()
    private val RV_Adapter = RecycleView_Adapter(this, rv_list)

    override fun onBackPressed(){
        var preference = getSharedPreferences("log_prefs", MODE_PRIVATE)
        var editor = preference.edit()
        editor.putBoolean("lgdin", false)
        editor.putString("Wid", null)
        editor.apply()
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var rem_index = data?.getIntExtra("result", 0)
        Log.d("dbg", "D: " + resultCode)
        if (resultCode == 1){
            rv_list.removeAt(rem_index!!)
        }
        RV_Adapter.notifyDataSetChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        RV_Adapter.bool_switch()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_r_v_activity)

        db.firestoreSettings = settings
        user_id = intent.getStringExtra("Wid")!!

        db_call()
        main_recycler.adapter = RV_Adapter
        main_recycler.layoutManager = LinearLayoutManager(this)
    }

    fun db_call() {
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
                                if (!rv_list.contains(inner_array)){
                                    rv_list.add(inner_array)
                                }
                            }
                            RV_Adapter.notifyDataSetChanged()
                        }
                    }
                    else if (!it.isSuccessful){
                        Toast.makeText(this, "Wystąpił błąd", Toast.LENGTH_SHORT).show()
                    }
                }

    }
}