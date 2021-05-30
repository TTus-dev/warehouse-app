package com.example.warehouse_app

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_r_v_activity.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestoreSettings
import kotlin.collections.ArrayList

class RV_activity : Heap_sort(), NavigationView.OnNavigationItemSelectedListener
    ,FilterDialog.DialogListener
    ,SortDialog.DialogListener {

    private val db = FirebaseFirestore.getInstance()
    private var db_call_val = "Orders"
    private val settings = firestoreSettings {
        isPersistenceEnabled = false
    }
    lateinit var drawer : DrawerLayout
    private var user_id = ""
    private var default_rv_list = ArrayList<ArrayList<String>>()
    private var rv_list = ArrayList<ArrayList<String>>()
    private val RV_Adapter = RecycleView_Adapter(this, rv_list)

    override fun onCreate(savedInstanceState: Bundle?) {
        RV_Adapter.oa_bool = true

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

        val navigview = findViewById<NavigationView>(R.id.nav_view)
        navigview.bringToFront()
        get_credentials()
        navigview.setNavigationItemSelectedListener(this)

        db_call("Orders")
        main_recycler.adapter = RV_Adapter
        main_recycler.layoutManager = LinearLayoutManager(this)

        swipe_refreshlayout.setOnRefreshListener {
            if (db_call_val == "Orders"){
                db_call(db_call_val)
                RV_Adapter.notifyDataSetChanged()
            }
        }

        filter.setOnClickListener {
            FilterDialog().show(supportFragmentManager, "Dialog")
        }
        sort.setOnClickListener {
            SortDialog().show(supportFragmentManager, "Dialog")
        }
    }

    fun empty_list() {
        if (rv_list.size > 0) {
            empty_txtvw.visibility = View.GONE
        } else {
            empty_txtvw.visibility = View.VISIBLE
        }
    }


    override fun Sort(field_id: Int, desc: Boolean) {
        Resetfilter()
        default_rv_list.clear()
        default_rv_list.addAll(rv_list)
        heap_sort(rv_list, field_id)
        if (desc) { rv_list.reverse() }
        RV_Adapter.notifyDataSetChanged()
    }

    override fun ResetSort() {
        Resetfilter()
    }

    override fun Filter(filtered_txt: String, field_id: Int) {
        Resetfilter()
        default_rv_list.clear()
        default_rv_list.addAll(rv_list)
        rv_list.clear()
        if (field_id == 0) {
            for (i in 0..default_rv_list.size - 1) {
                if (default_rv_list[i][field_id].contains(filtered_txt, true)) {
                    rv_list.add(default_rv_list[i])
                }
            }
        }
        else{
            for (i in 0..default_rv_list.size - 1) {
                if (default_rv_list[i][field_id].toInt() == filtered_txt.toInt()) {
                    rv_list.add(default_rv_list[i])
                }
            }
        }
        empty_list()
        RV_Adapter.notifyDataSetChanged()
    }

    override fun Resetfilter() {
        if (default_rv_list.size != 0) {
            rv_list.clear()
            rv_list.addAll(default_rv_list)
            default_rv_list.clear()
            empty_list()
            RV_Adapter.notifyDataSetChanged()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun get_credentials(){
        db.collection("Workers")
            .document(user_id)
            .get()
            .addOnCompleteListener{
                if (it.isSuccessful) {
                    val navigview = findViewById<NavigationView>(R.id.nav_view)
                    navigview.getHeaderView(0).findViewById<TextView>(R.id.user_credentials)
                        .text = it.result?.data!!["Name"] as String +
                            " " + it.result?.data!!["Surname"] as String
                }
                else if (!it.isSuccessful){
                    Toast.makeText(this, "Wystąpił błąd", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun alert(){
        val alert_builder = AlertDialog.Builder(this)
        alert_builder.setTitle("Czy napewno chcesz się wylogować")
        alert_builder.setNegativeButton("Tak") { _: DialogInterface, _: Int ->
            val preference = getSharedPreferences("log_prefs", MODE_PRIVATE)
            val editor = preference.edit()
            editor.putBoolean("lgdin", false)
            editor.putString("Wid", null)
            editor.apply()
            super.onBackPressed()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
        alert_builder.setPositiveButton("Nie"){ _: DialogInterface, _: Int ->}
        alert_builder.show()
    }

    override fun onBackPressed(){
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START)
        }
        else {
            alert()
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun move_to_completed(completed_docid : String, array_id : Int){
        var _1: HashMap<String, String>
        var _2: ArrayList<String>
        db.collection("Orders")
            .document(completed_docid)
            .get()
            .addOnCompleteListener{
                if (it.isSuccessful) {
                    _1 = it.result!!.data as HashMap<String, String>
                    _2 = it.result!!.data?.get("Item_array") as ArrayList<String>
                    val new_id = db.collection("Completed_orders").document().id
                    val new_doc = db.collection("Completed_orders").document(new_id)
                    new_doc.set(_1)
                    new_doc.update("Item_array", _2)
                    db.collection("Orders").document(completed_docid).delete()
                    rv_list.removeAt(array_id)
                    RV_Adapter.notifyDataSetChanged()
                }
                else
                    Toast.makeText(this, "Wystąpił błąd", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val rem_index = data?.getIntExtra("result", 0)
        val completed_id = data?.getStringExtra("document_id")
        if (resultCode == 1){
            move_to_completed(completed_id!!, rem_index!!)
        }
        RV_Adapter.notifyDataSetChanged()
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId){

            R.id.nav_current_orders ->
            {
                default_rv_list.clear()
                db_call_val = "Orders"
                db_call(db_call_val)
                RV_Adapter.drawer_sel_set(0)
                RV_Adapter.notifyDataSetChanged()
            }

            R.id.nav_completed_orders ->
            {
                default_rv_list.clear()
                /*swipe_refreshlayout.isEnabled = false
                swipe_refreshlayout.isRefreshing = false*/
                db_call_val = "Completed_orders"
                db_call(db_call_val)
                RV_Adapter.drawer_sel_set(1)
                RV_Adapter.notifyDataSetChanged()
            }

            R.id.nav_logout ->
            {
                alert()
            }

        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    @Suppress("UNCHECKED_CAST")
    private fun db_call(type : String) {
        rv_list.clear()
        swipe_refreshlayout.isRefreshing = true
        db.collection(type)
            .whereEqualTo("worker_id", user_id)
            .get()
            .addOnCompleteListener {
                    if (it.isSuccessful) {
                        for (document in it.result!!) {
                            val inner_array = ArrayList<String>()
                            inner_array.add(document.data["Address"] as String)
                            inner_array.add(document.data["Order_no"] as String)
                            val itemarr = document.data["Item_array"] as ArrayList<String>
                            var item_count = 0
                            for (i in 1 until itemarr.size step 2){
                                item_count += itemarr[i].toInt()
                            }
                            inner_array.add(item_count.toString())
                            inner_array.add(document.id)
                            if (!rv_list.contains(inner_array)){
                                rv_list.add(inner_array)
                            }
                            empty_list()
                            swipe_refreshlayout.isRefreshing = false
                            RV_Adapter.notifyDataSetChanged()
                        }
                    }
                    else if (!it.isSuccessful){
                        Toast.makeText(this, "Wystąpił błąd", Toast.LENGTH_SHORT).show()
                    }
                swipe_refreshlayout.isRefreshing = false
                }
    }
}