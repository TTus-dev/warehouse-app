package com.example.warehouse_app

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestoreSettings

class MainActivity : AppCompatActivity(){

    val db = FirebaseFirestore.getInstance()
    val settings = firestoreSettings {
        isPersistenceEnabled = false
    }

    lateinit var editor : SharedPreferences.Editor


    override fun onResume() {
        _create()
        super.onResume()
    }

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preference = getSharedPreferences("log_prefs", MODE_PRIVATE)
        editor = preference.edit()
        val logged_in = preference.getBoolean("lgdin", false)


        if (logged_in){
            val i = Intent(this, RV_activity::class.java)
            i.putExtra("Wid", preference.getString("Wid","000"))
            startActivity(i)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
        else {
            _create()
        }
    }

    fun _create(){

        setContentView(R.layout.activity_main)
        db.firestoreSettings = settings

        findViewById<ConstraintLayout>(R.id.mainlayout).visibility = View.VISIBLE

        val i = Intent(this, RV_activity::class.java)

        findViewById<Button>(R.id.button).setOnClickListener {
            db.collection("Workers").get().addOnCompleteListener {
                if (it.isSuccessful) {
                    for (document in it.result!!) {
                        if (document["Login"].toString() == findViewById<EditText>(R.id.Login)
                                .text.toString() &&
                                document["Passwd"].toString() == findViewById<EditText>(R.id.Password)
                                .text.toString()) {
                            editor.putBoolean("lgdin", true)
                            editor.putString("Wid", document.id)
                            editor.apply()
                            i.putExtra("Wid", document.id)
                            startActivity(i)
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                            findViewById<ConstraintLayout>(R.id.mainlayout).visibility = View.GONE
                        }
                    }
                }
            }
        }
    }
}