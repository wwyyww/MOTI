package com.example.moti

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class PoiItemAdapter(private val context : Context, var type : String) : RecyclerView.Adapter<PoiItemAdapter.CustomViewHolder>() {
    //private var poiItemList = ArrayList<PoiItem>()

    var poiItemList = mutableListOf<Poi>()



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PoiItemAdapter.CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_poi_info,parent,false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: PoiItemAdapter.CustomViewHolder, position: Int) {
        holder.textV_name.text = poiItemList.get(position).name
        holder.textV_radius.text = poiItemList.get(position).radius + " km"
        holder.textV_lowerBizName.text = poiItemList.get(position).lowerBizName
        holder.textV_fullAddressRoad.text = poiItemList.get(position).newAddressList.newAddress[0].fullAddressRoad
        holder.textV_lowerAddrName.text = poiItemList.get(position).lowerAddrName

        holder.img_mapIcon.setOnClickListener {

            var placePoiItem : PoiItem = PoiItem(
                type= type,
                name = poiItemList.get(position).name.toString(),
                radius = poiItemList.get(position).radius.toString(),
                lowerBizName = poiItemList.get(position).lowerBizName.toString(),
                lowerAddrName = poiItemList.get(position).lowerAddrName.toString(),
                frontLat = poiItemList.get(position).frontLat.toString(),
                frontLon= poiItemList.get(position).frontLon.toString(),
                fullAddressRoad =  poiItemList.get(position).newAddressList.newAddress[0].fullAddressRoad,
            )

           val intent = Intent()
            intent.putExtra("placePoiItem", placePoiItem)
            Log.d("successM", "Adapater : ${placePoiItem}")
            (context as Activity).setResult(Activity.RESULT_OK, intent)
            (context as Activity).finish()
        }

        Log.d("ADAPTER", position.toString()+ poiItemList.get(position).name )
    }

    override fun getItemCount(): Int {
        return poiItemList.size
    }



    class CustomViewHolder(itemView : View): RecyclerView.ViewHolder(itemView) {
        val textV_name = itemView.findViewById<TextView>(R.id.textV_name)
        var textV_radius = itemView.findViewById<TextView>(R.id.textV_radius)
        var textV_lowerBizName = itemView.findViewById<TextView>(R.id.textV_lowerBizName)
        var textV_fullAddressRoad = itemView.findViewById<TextView>(R.id.textV_fullAddressRoad)
        var textV_lowerAddrName = itemView.findViewById<TextView>(R.id.textV_lowerAddrName)
        var img_mapIcon = itemView.findViewById<ImageView>(R.id.img_mapIcon)
    }

}