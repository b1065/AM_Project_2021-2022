package com.example.losulosu.DB.List

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.losulosu.R

class ListAdapter(private val context: Activity, private val login: Array<String>, private val score: Array<String>)
    : ArrayAdapter<String>(context, R.layout.custom_table, login) {

    @SuppressLint("ViewHolder", "InflateParams")
    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.custom_table, null, true)

        val loginText = rowView.findViewById(R.id.textLogin) as TextView
        val scoreText = rowView.findViewById(R.id.textScore) as TextView

        loginText.text = login[position]
        scoreText.text = score[position]
        return rowView
    }
}