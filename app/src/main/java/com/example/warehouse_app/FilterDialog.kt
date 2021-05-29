package com.example.warehouse_app

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatDialogFragment

class FilterDialog : AppCompatDialogFragment() {

    lateinit private var listener : DialogListener

    lateinit var filter_edittext : EditText
    lateinit var spinner : Spinner

    lateinit var oc_edittext : EditText
    lateinit var oc_spinner : Spinner

    var completed_bool = false

    var field_id: Int = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity?.layoutInflater
        val view = inflater?.inflate(R.layout.layout_dialog, null)

        filter_edittext = view!!.findViewById(R.id.filtered_text)
        spinner = view!!.findViewById(R.id.field_spinner)

        oc_spinner = view!!.findViewById(R.id.field_spinner_completed_order)
        oc_edittext = view!!.findViewById(R.id.filtered_text_completed_order)

        if (completed_bool){
            filter_edittext.visibility = View.GONE
            spinner.visibility = View.GONE
            oc_spinner.visibility = View.VISIBLE
            oc_edittext.visibility = View.VISIBLE
        }

        builder.setView(view)
            .setTitle("Filtruj")
            .setPositiveButton("Anuluj") { _, _ -> }
            .setNeutralButton("Reset") { _, _ ->
                listener.Resetfilter()
            }
            .setNegativeButton("Zapisz") { _, _ ->
                val filtered: String
                filtered = if (completed_bool) {
                    oc_edittext.text.toString()
                } else {
                    filter_edittext.text.toString()
                }
                listener.Filter(filtered, field_id)
            }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected( adapter_vw: AdapterView<*>?
                                         , view: View?
                                         , position: Int
                                         , id: Long)
            {
                field_id = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) { }


        }

        oc_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected( adapter_vw: AdapterView<*>?
                                         , view: View?
                                         , position: Int
                                         , id: Long)
            {
                field_id = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        return builder.create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as DialogListener
    }

    interface DialogListener{
        fun Filter(filtered_txt : String, field_id : Int)
        fun Resetfilter()
    }
}