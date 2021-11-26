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

var mutableData: MutableList<Post> = mutableListOf<Post>()

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

            var selectedHashtag : String = hashtagList[position]
            Log.d("firebaseM2","클릭됨 ${selectedHashtag}")



            val postRef = Firebase.database.getReference("community")
            postRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("firebaseM2","호출됨")
                    val postList: MutableList<Post> = mutableListOf<Post>()

                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            Log.d("firebaseM2",userSnapshot.value.toString())
                            val getData = userSnapshot.getValue(Post::class.java)
                            getData?.toString()?.let { it1 -> Log.d("firebaseM3", it1) }
                            if (getData?.hashtag?.containsValue(selectedHashtag) == true) {
                                // 현재 선택된 해시태그만 가져옴
                               // getData.postId?.let { it1 -> Log.d("firebaseM3", it1) }
                                postList.add(getData!!)

                            }

                            mutableData = postList
                           // Log.d("firebaseM5", mutableData?.toString())
                            Log.d("firebaseM0",mutableData.toString())


                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }

    }

    override fun getItemCount(): Int {
        return hashtagList.size
    }


    class CustomViewHolder(itemView : View): RecyclerView.ViewHolder(itemView) {
        val textV_hashtag = itemView.findViewById<TextView>(R.id.textV_hashtag)
        val constL_rectangle = itemView.findViewById<ConstraintLayout>(R.id.constL_rectangle)
    }

}

////////////

class CommunityAdapter(private val context: Context) : RecyclerView.Adapter<CommunityAdapter.CustomViewHolder>() {

    //var hashtagList =  ArrayList<MutableCollection<String>> ()
    // var placeList = ArrayList<Place> ()


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


