package com.example.moti

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_after_riding.*
import kotlinx.android.synthetic.main.activity_riding.*


class AfterRidingActivity: AppCompatActivity() {

    var pushKey=String()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_after_riding)

        //action bar 숨기기
        var actionBar: ActionBar?
        actionBar=supportActionBar
        actionBar?.hide()


        after_time_txtview.text=intent.getStringExtra("time")
        pushKey= intent.getStringExtra("pushKey")!!
        Log.d("intent", "check pushKey ${pushKey}")


        after_next_button.setOnClickListener {
            val intent = Intent(this, SharingActivity::class.java).apply {
//                putExtra("time", riding_timer_textview.text)
                putExtra("pushKey", pushKey)

            }.run {startActivity(this) }
        }


    }

}