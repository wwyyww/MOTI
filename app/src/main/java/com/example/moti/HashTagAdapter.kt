package com.example.moti

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.MutableLiveData

import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

//lateinit var postList:  MutableLiveData<MutableList<Post>>

//val mutableData = MutableLiveData<MutableList<Post>>()




class HashTagAdapter(private val context: Context) : RecyclerView.Adapter<HashTagAdapter.CustomViewHolder>(){

    //var hashtagList =  ArrayList<MutableCollection<String>> ()
    var hashtagList = ArrayList<String>()
    //val mutableData = MutableLiveData<MutableList<Post>>()


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

        holder.constL_rectangle.setOnClickListener {
            itemClickListener.onClick(it, position)
            notifyDataSetChanged()
        }
    }



    class CustomViewHolder(itemView : View): RecyclerView.ViewHolder(itemView) {
        val textV_hashtag = itemView.findViewById<TextView>(R.id.textV_hashtag)
        val constL_rectangle = itemView.findViewById<ConstraintLayout>(R.id.constL_rectangle)
    }



    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }
    // (3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener : OnItemClickListener


    override fun getItemCount(): Int {
        return hashtagList.size
    }


}

////////////

class CommunityAdapter(val context: Context) : RecyclerView.Adapter<CommunityAdapter.CustomViewHolder>() {

    //var hashtagList =  ArrayList<MutableCollection<String>> ()
    // var placeList = ArrayList<Place> ()

    var mutableData: MutableList<Post> = mutableListOf<Post>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CommunityAdapter.CustomViewHolder {
        Log.d("[2]firebaseMAdapter2", mutableData.toString())
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_placeinfo, parent, false)
        return CommunityAdapter.CustomViewHolder(view)

    }

    override fun onBindViewHolder(holder: CommunityAdapter.CustomViewHolder, position: Int) {
         //holder.imgV_image.src
         holder.txtV_title.text  = mutableData.get(position).toString()
         //holder.recy_hashtag
         holder.constL_startbtn
        Log.d("[2]onBindViewHolder",mutableData.get(position).toString())
    }

    override fun getItemCount(): Int {
        return mutableData.size
    }


    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // val textV_hashtag = itemView.findViewById<TextView>(R.id.textV_hashtag)
        val imgV_image = itemView.findViewById<ImageView>(R.id.imgV_image)
        val txtV_title = itemView.findViewById<TextView>(R.id.txtV_title)
        val recy_hashtag = itemView.findViewById<RecyclerView>(R.id.recy_hashtag)
        val constL_startbtn = itemView.findViewById<ConstraintLayout>(R.id.constL_startbtn)
    }


}


