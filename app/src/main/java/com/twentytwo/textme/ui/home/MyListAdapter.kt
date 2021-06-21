package com.twentytwo.textme.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.twentytwo.textme.Model.Feeds
import com.twentytwo.textme.Model.Users
import com.twentytwo.textme.R

class MyListAdapter(context: Context, var items: List<Users>) :
    ArrayAdapter<Users>(context, R.layout.item_contacts, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = layoutInflater.inflate(R.layout.item_contacts, null)

        var textView: TextView = view.findViewById(R.id.description)
        var textView1: TextView = view.findViewById(R.id.contact_names)
        var ContactsImage: ImageView = view.findViewById(R.id.ContactsImage)

        var person: Users = items[position]
        textView.text = person.lastseen
        textView1.text = person.name
        Glide.with(context)
            .load(person.proFileImageUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .into(ContactsImage)

        return view
    }

}

class FeedsAdapter(context: Context, var items: List<Feeds>) :
    ArrayAdapter<Feeds>(context, R.layout.items_feeds, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = layoutInflater.inflate(R.layout.items_feeds, null)

        val textView3: TextView = view.findViewById(R.id.feeds_title)
        var feeds: ImageView = view.findViewById(R.id.imageView)
        var descreption: TextView = view.findViewById(R.id.feed_desc)
        var location: TextView = view.findViewById(R.id.feed_location)
        var ContactsImage: ImageView = view.findViewById(R.id.ContactsImage)


        var itemsfeed: Feeds = items[position]
        textView3.text = itemsfeed.title
        descreption.text = itemsfeed.descreption
        location.text = itemsfeed.location
        Glide.with(context)
            .load(itemsfeed.profileUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .into(ContactsImage)
        Glide.with(context)
            .load(itemsfeed.imagePath)
            .placeholder(R.drawable.ic_launcher_background)
            .into(feeds)

        return view
    }

}