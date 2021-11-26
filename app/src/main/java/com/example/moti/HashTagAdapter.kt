package com.example.moti

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView

class HashTagAdapter(private val context: Context) : RecyclerView.Adapter<HashTagAdapter.CustomViewHolder>(){

    //var hashtagList =  ArrayList<MutableCollection<String>> ()
    var hashtagList = ArrayList<String>()



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): HashTagAdapter.CustomViewHolder {
        Log.d("firebaseMAdapter1",hashtagList.toString())
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_hashtag,parent,false)
        return HashTagAdapter.CustomViewHolder(view)

    }

    override fun onBindViewHolder(holder: HashTagAdapter.CustomViewHolder, position: Int) {
        holder.textV_hashtag.text = hashtagList.get(position)
        Log.d("firebaseMAdapter2",hashtagList.get(position))

    }

    override fun getItemCount(): Int {
        return hashtagList.size
    }


    class CustomViewHolder(itemView : View): RecyclerView.ViewHolder(itemView) {
        val textV_hashtag = itemView.findViewById<TextView>(R.id.textV_hashtag)
    }
}