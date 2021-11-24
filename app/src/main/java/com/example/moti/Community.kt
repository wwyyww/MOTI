package com.example.moti

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_ENTER
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.internal.ViewUtils.dpToPx

class Community : AppCompatActivity() {


    lateinit var textV_depart : TextView
    lateinit var textV_layover : TextView
    lateinit var textV_dest : TextView
    lateinit var textV_totalTime : TextView
    lateinit var editT_hashtag : EditText
    lateinit var linL_hashtag : LinearLayout
    lateinit var btn_upload : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community)

        textV_depart = findViewById(R.id.textV_depart)
        textV_layover = findViewById(R.id.textV_layover)
        textV_dest = findViewById(R.id.textV_dest)
        textV_totalTime = findViewById(R.id.textV_totalTime)
        editT_hashtag = findViewById(R.id.editT_hashtag)
        linL_hashtag = findViewById(R.id.linL_hashtag)
        btn_upload = findViewById(R.id.btn_upload)



/*
        if (event.action == KeyEvent.ACTION_DOWN && keyCode == KEYCODE_ENTER) {
            var hashtag : TextView = TextView(this)
            hashtag.setText(editT_hashtag.text)
            hashtag.background = resources.getDrawable(R.drawable.edittext_round)
            hashtag.textSize = 12f
            //hashtag.setPadding(0, 0, dpToPx(this, 16).toInt(), 0)
            linL_hashtag.addView(hashtag)

        }
        editT_hashtag.setOnKeyListener { v, keyCode, event ->

            true
        }
*/

    }
}