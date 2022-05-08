package com.example.goalin.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.ContextCompat
import com.example.goalin.R

class SelectView<T>(context: Context, attrs: AttributeSet) : AppCompatSpinner(context, attrs) {
    data class Option<V>(
        val label: String,
        val value: V
    )

    var selectedOption: Option<T>? = null

    var hint = ""

    var options: List<Option<T>> = listOf()
        set(valueParam) {
            val value = valueParam.toMutableList()
            val default = Option(hint, null) as Option<T>
            value.add(0, default)

            val arrayAdapter: ArrayAdapter<Option<T>> = object : ArrayAdapter<Option<T>>(context, R.layout.view_spinner_item, value){
                override fun getDropDownView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup
                ): View {
                    val view = super.getDropDownView(position, convertView, parent) as TextView

                    view.text = value[position].label

                    if (position == 0) {
                        view.setTextColor(
                            ContextCompat.getColor(context, R.color.gray_500)
                        )
                    }

                    return view
                }

                override fun isEnabled(position: Int) = position != 0

                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val inflater = LayoutInflater.from(context).inflate(R.layout.view_spinner_item, null) as TextView

                    inflater.text = value[position].label

                    if (position == 0) {
                        inflater.setTextColor(
                            ContextCompat.getColor(context, R.color.gray_500)
                        )
                    }

                    return inflater
                }
            }

            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            adapter = arrayAdapter
            field = value

            if (selectedOption != null) {
                setSelection(
                    value.indexOf(selectedOption)
                )
            }

            onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedOption = options[position]
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            }
        }

    override fun performClick(): Boolean {
        isFocusableInTouchMode = true
        requestFocus()
        isFocusableInTouchMode = false
        return super.performClick()
    }

    init {
        background = ContextCompat.getDrawable(context, R.drawable.spinner_state)
        hint = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "hint") ?: "Pilih menu"
        options = listOf()
    }
}
