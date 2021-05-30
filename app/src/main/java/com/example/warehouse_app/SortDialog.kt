package com.example.warehouse_app

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.appcompat.widget.SwitchCompat

class SortDialog : AppCompatDialogFragment() {

    lateinit private var listener : DialogListener

    lateinit var switch : SwitchCompat
    lateinit var spinner : Spinner

    lateinit var oc_switch : SwitchCompat
    lateinit var oc_spinner : Spinner

    var items_bool = false

    var field_id: Int = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity?.layoutInflater
        val view = inflater?.inflate(R.layout.layout_dialog_sort, null)

        switch = view!!.findViewById(R.id.desc1)
        spinner = view!!.findViewById(R.id.field_spinner1)

        oc_switch = view!!.findViewById(R.id.desc2)
        oc_spinner = view!!.findViewById(R.id.field_spinner2)

        if (items_bool){
            switch.visibility = View.GONE
            spinner.visibility = View.GONE
            oc_spinner.visibility = View.VISIBLE
            oc_switch.visibility = View.VISIBLE
        }

        builder.setView(view)
            .setTitle("Sortuj")
            .setPositiveButton("Anuluj") { _, _ -> }
            .setNeutralButton("Reset") { _, _ ->
                listener.ResetSort()
            }
            .setNegativeButton("Zapisz") { _, _ ->
                val desc : Boolean
                desc = if (items_bool) {
                    oc_switch.isChecked
                } else {
                    switch.isChecked
                }
                listener.Sort(field_id, desc)
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
        fun Sort(field_id : Int, desc : Boolean)
        fun ResetSort()
    }
}