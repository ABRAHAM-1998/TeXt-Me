package com.twentytwo.textme.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.twentytwo.textme.Model.UsersReg
import com.twentytwo.textme.R

class MyListAdapter(context: Context, var items: List<UsersReg>) :
    ArrayAdapter<UsersReg>(context, R.layout.item_contacts, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = layoutInflater.inflate(R.layout.item_contacts, null)

        var textView: TextView = view.findViewById(R.id.description)
        var textView1: TextView = view.findViewById(R.id.contact_names)
        var ContactsImage: ImageView = view.findViewById(R.id.ContactsImage)

        var person: UsersReg = items[position]
        textView.text = person.uid
        textView1.text = person.name
        Glide.with(context)
            .load(person.proFileImageUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .into(ContactsImage)

        return view
    }

}