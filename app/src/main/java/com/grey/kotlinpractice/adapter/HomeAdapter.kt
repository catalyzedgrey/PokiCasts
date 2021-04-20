package com.grey.kotlinpractice.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.grey.kotlinpractice.R
import com.grey.kotlinpractice.data.Model
import com.squareup.picasso.Picasso

class HomeAdapter(context: Context, private val resource: Int, private var itemList: ArrayList<Model.Podcast>) : ArrayAdapter<HomeAdapter.MyViewHolder>(context, resource) {

    class MyViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        val episodeCount: TextView = view.findViewById(R.id.home_episode_count)
        val podIcon: ImageView = view.findViewById(R.id.icon)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var mConvertView = convertView
        val holder: MyViewHolder
        if (mConvertView == null) {
            mConvertView = LayoutInflater.from(context).inflate(resource, parent, false)
            holder = MyViewHolder(mConvertView)

            mConvertView.tag = holder
        } else {
            holder = mConvertView.tag as MyViewHolder
        }

        if(itemList[position].trackCount!! <= 99){
            holder.episodeCount.text = itemList[position].trackCount.toString()
        }else{
            holder.episodeCount.text = "99"
        }
        Picasso.get().load(itemList[position].artworkUrl600).into(holder.podIcon)

        return mConvertView!!

    }

    fun updateList(newList: ArrayList<Model.Podcast>) {
        itemList.clear()
        itemList = newList
        notifyDataSetChanged()

    }

    override fun getCount(): Int {
        return itemList.size
    }
}