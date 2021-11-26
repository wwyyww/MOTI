package com.example.moti

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class CommunityAdapter(private val context: Context) : RecyclerView.Adapter<CommunityAdapter.CustomViewHolder>() {

    //var hashtagList =  ArrayList<MutableCollection<String>> ()
     var placeList = ArrayList<Place> ()


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CommunityAdapter.CustomViewHolder {
        Log.d("firebaseMAdapter1", placeList.toString())
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_placeinfo, parent, false)
        return CommunityAdapter.CustomViewHolder(view)

    }

    override fun onBindViewHolder(holder: CommunityAdapter.CustomViewHolder, position: Int) {
      //  holder.imgV_image.text = hashtagList.get(position)


    }

    override fun getItemCount(): Int {
        return placeList.size
    }


    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // val textV_hashtag = itemView.findViewById<TextView>(R.id.textV_hashtag)
        val imgV_image = itemView.findViewById<ImageView>(R.id.imgV_image)
        val txtV_title = itemView.findViewById<TextView>(R.id.txtV_title)
        val recy_hashtag = itemView.findViewById<RecyclerView>(R.id.recy_hashtag)
        val constL_start = itemView.findViewById<ConstraintLayout>(R.id.constL_start)
    }
}
