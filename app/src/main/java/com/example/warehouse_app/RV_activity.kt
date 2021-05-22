package com.example.warehouse_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_r_v_activity.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestoreSettings
import kotlin.collections.ArrayList

class RV_activity() : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()
    val settings = firestoreSettings {
        isPersistenceEnabled = false
    }
    lateinit var drawer : DrawerLayout
    var user_id = ""
    var rv_list = ArrayList<ArrayList<String>>()
    private val RV_Adapter = RecycleView_Adapter(this, rv_list)

    override fun onBackPressed(){
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START)
        }
        else {
            val preference = getSharedPreferences("log_prefs", MODE_PRIVATE)
            val editor = preference.edit()
            editor.putBoolean("lgdin", false)
            editor.putString("Wid", null)
            editor.apply()
            super.onBackPressed()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val rem_index = data?.getIntExtra("result", 0)
        if (resultCode == 1){
            rv_list.removeAt(rem_index!!)
        }
        RV_Adapter.notifyDataSetChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        RV_Adapter.bool_switch()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_r_v_activity)

        val toolbar = findViewById<Toolbar>(R.id.my_toolbar)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_recycler_layout)
        val toggle = ActionBarDrawerToggle(this, drawer, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        db.firestoreSettings = settings
        user_id = intent.getStringExtra("Wid")!!

        findViewById<NavigationView>(R.id.nav_view).getHeaderView(0)
            .findViewById<TextView>(R.id.user_credentials).text = user_id

        db_call()
        main_recycler.adapter = RV_Adapter
        main_recycler.layoutManager = LinearLayoutManager(this)
    }

    fun db_call() {
        db.collection("Orders")
            .whereEqualTo("worker_id", user_id)
            .get()
            .addOnCompleteListener {
                    if (it.isSuccessful) {
                        for (document in it.result!!) {
                            val inner_array = ArrayList<String>()
                            inner_array.add(document.data["Address"] as String)
                            inner_array.add(document.data["Order_no"] as String)
                            inner_array.add(document.data["Item_quant"] as String)
                            inner_array.add(document.id)
                            if (!rv_list.contains(inner_array)){
                                rv_list.add(inner_array)
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