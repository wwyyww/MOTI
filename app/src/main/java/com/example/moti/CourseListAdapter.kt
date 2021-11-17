package com.example.moti

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase

class CourseListAdapter(private val context: Context) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseListAdapter.CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.course_item_recycler,parent,false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseListAdapter.CustomViewHolder, position: Int) {


        // 사진 불러오기
//        Firebase.storage.reference.child("community")
//            .child(postList[position].photoUrl.toString()).downloadUrl.addOnCompleteListener {
//                if (it.isSuccessful) {
//                    Glide.with(holder.itemView?.context)
//                        .load(it.result)
//                        .placeholder(R.drawable.loading4)
//                        .into(holder.myPostitem_photo)
//                }
//            }

        // item 클릭 시
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView?.context, MainActivity::class.java)
            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }

    }




    class CustomViewHolder(itemView : View): RecyclerView.ViewHolder(itemView) {
        val myPostitem_photo = itemView.findViewById<ImageView>(R.id.imgView_course)

    }

}
