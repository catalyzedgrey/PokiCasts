package com.grey.kotlinpractice.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.grey.kotlinpractice.R
import com.grey.kotlinpractice.data.Model
import com.squareup.picasso.Picasso

class CustomGridAdapter(
    private val context: Context,
    private var allResults: ArrayList<Model.Podcast>
) : RecyclerView.Adapter<CustomGridAdapter.MyViewHolder>() {

    class MyViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        val podIcon: ImageView

        init {
            podIcon = view.findViewById(R.id.icon)
        }

    }

//    override fun getCount(): Int {
//        return allResults.count(); }

//    override fun getItem(position: Int): Any {
//        return position.toLong(); }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun updateList(newList: ArrayList<Model.Podcast>) {
        allResults = newList
        notifyDataSetChanged()
    }

//    override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View {
//        val layoutInflater: LayoutInflater = LayoutInflater.from(context);
//        val mainRow: View = layoutInflater.inflate(R.layout.list_item, viewGroup, false);
//
//        val podcastName: TextView = mainRow.findViewById<TextView>(R.id.textView);
//        podcastName.text = allResults[position].artistName;
//
//        val podcastIcon: ImageView = mainRow.findViewById(R.id.icon)
//
//        val url: String = allResults[position].artworkUrl100!!
//        Picasso.get().load(url).into(podcastIcon)
//
//        return mainRow;
//    }

    //Create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return MyViewHolder(view)
    }

    //Replace the contents of a view
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val url: String = allResults[position].artworkUrl600!!
        Picasso.get().load(url).into(holder.podIcon)
    }

    override fun getItemCount(): Int {
        return allResults.size
    }



}
