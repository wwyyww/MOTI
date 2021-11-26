package com.example.moti

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Button
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_hashtag.*
import kotlinx.android.synthetic.main.activity_sharing.*


@Suppress("DEPRECATION")
class SharingActivity: AppCompatActivity() {

    //db용
    private var auth : FirebaseAuth? = null
    private lateinit var database: DatabaseReference
    private val CurrentUser = FirebaseAuth.getInstance().currentUser
    val uid = CurrentUser?.uid

    var pushKey=String()
    var selectList=ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sharing)

        //action bar 숨기기
        var actionBar: ActionBar?
        actionBar=supportActionBar
        actionBar?.hide()

        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference
//        pushKey= intent.getStringExtra("pushKey")!!
        pushKey="abcdefg"



        sharing_button.setOnClickListener {
            database.child("community/$pushKey/uid").setValue("$uid")
            val intent = Intent(this, SelectHashtag::class.java)
            startActivityForResult(intent, 2000)


        }



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d("sharing", "onActivityResult")

        if (requestCode == 2000 && resultCode == RESULT_OK) {
            selectList= data?.getStringArrayListExtra("hashtagList")!!
            Log.d("sharing", "receive selectlist $selectList")

        }else{
            Log.d("sharing", "no data")
        }

    }

}


public class SelectHashtag : Activity() {

    var hashtagList=ArrayList<hashtag>()
    var selectList=ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_hashtag)

        setHashtag()

        hashtag_check_button.setOnClickListener{
            var myintent=Intent()
            myintent.putExtra("hashtagList", selectList)

            setResult(RESULT_OK, myintent)
            finish()
        }





    }

    fun setHashtag(){

        for (i in 1..5){
            var tag = hashtag()
            var getID = resources.getIdentifier("hashtag_category${i}_btn", "id", "com.example.moti")
            var test=findViewById<Button>(getID)

            tag.text="${test.text}"
            hashtagList.add(tag)
            test.setOnClickListener {
                hashtagList[i-1].select=!hashtagList[i-1].select
                if (hashtagList[i-1].select){
                    test.background=ContextCompat.getDrawable(this, R.drawable.hashtag_select)
                    selectList.add("${test.text}")
                }else{
                    test.background=ContextCompat.getDrawable(this, R.drawable.hashtag)
                    selectList.remove("${test.text}")
                    Log.d("sharing", "$selectList")

                }
            }
//            Log.d("sharing", "check button id ${test.text}")
        }

        for (i in 1..3){
            var tag = hashtag()
            var getID = resources.getIdentifier("hashtag_mood${i}_btn", "id", "com.example.moti")
            var test=findViewById<Button>(getID)
            tag.text="${test.text}"
            hashtagList.add(tag)
            test.setOnClickListener {
                hashtagList[i+4].select=!hashtagList[i+4].select
                if (hashtagList[i+4].select){
                    test.background=ContextCompat.getDrawable(this, R.drawable.hashtag_select)
                    selectList.add("${test.text}")
                }else{
                    test.background=ContextCompat.getDrawable(this, R.drawable.hashtag)
                    selectList.remove("${test.text}")
                    Log.d("sharing", "$selectList")
                }
            }
        }




    }



}

data class hashtag(
    var text: String? = null,
    var select: Boolean = false,
)