package com.example.moti

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


class CouresListActivity:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_courselist)

        val gridLayoutManager = GridLayoutManager(this, 2)
        var rv_courseList: RecyclerView = findViewById<RecyclerView>(R.id.courselist_recyclerview)

        rv_courseList.layoutManager = gridLayoutManager
        rv_courseList.setHasFixedSize(true)

    }

}