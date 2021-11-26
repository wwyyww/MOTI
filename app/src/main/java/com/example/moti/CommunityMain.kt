package com.example.moti

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.skt.Tmap.TMapView
import kotlinx.android.synthetic.main.activity_main.*


class CommunityMain: AppCompatActivity() {

    lateinit var edit_searchBar: EditText
    var tmapview: TMapView? = null
    lateinit var searchPlace: PoiItem


    lateinit var hashtagRecyclerView: RecyclerView
    lateinit var hashTagAdapter : HashTagAdapter
    //lateinit var hashtagList : MutableList<String>
    lateinit var hashtagList :  ArrayList<String>


    lateinit var communityRecyclerView: RecyclerView
    lateinit var communityAdapter: ContentAdapter
    //var communityData = ArrayList<Content>()


    //lateinit var postList:  MutableLiveData<MutableList<Post>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community_main)

        hashtagRecyclerView = findViewById(R.id.hashtag)
        communityRecyclerView = findViewById(R.id.community_main_contents)
        edit_searchBar = findViewById(R.id.edit_searchBar)

        // 저장 배열 초기화
        hashtagList = ArrayList<String>()
       // postList= MutableLiveData<MutableList<Post>>()


        // 검색 결과 가져오기
        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    var placePoiItem = data?.getParcelableExtra<PoiItem>("placePoiItem")
                    Log.d("successM", "getIntent : ${placePoiItem}")

                    if (placePoiItem != null) {
                        searchPlace = placePoiItem
                    }

                }
            }

        // 액션바 숨기기
        val actionBar: ActionBar?
        actionBar = supportActionBar
        actionBar?.hide()


        // 해쉬태그 어댑터 설정
        hashTagAdapter = HashTagAdapter(this@CommunityMain)
        hashtagRecyclerView.adapter = hashTagAdapter


        //tmap 세팅
        tmapview = TMapView(this)
        tmapview!!.setSKTMapApiKey("l7xx3a42dd9f094c468e969018fba936e361")
        tmapview!!.setIconVisibility(true)
        tmapview!!.setCompassMode(true)
        tmapview!!.setTrackingMode(true)
        main_tmaplayout2.addView(tmapview)


        // 검색바
        edit_searchBar.setOnClickListener {
            val intent = Intent(this, SearchPoi::class.java)
            intent.putExtra("type", "searchPlace")
            resultLauncher.launch(intent)
        }


        // 사용자 해시태그 목록 가져오기
        val user = Firebase.auth.currentUser   // fireabase 현재 사용자 불러오기
        val CurrentUser = FirebaseAuth.getInstance().currentUser

        val userRef = Firebase.database.getReference("users")

        userRef.addValueEventListener(object : ValueEventListener {
            var hashtagListM : MutableMap<String, String> = HashMap()

            override fun onDataChange(snapshot: DataSnapshot) {

                Log.d("firebaseM", snapshot.child("Ru1rsTPKgKctYN2mY1OfEW89hnn1").child("taste").childrenCount.toString())
                hashtagListM = snapshot.child("Ru1rsTPKgKctYN2mY1OfEW89hnn1").child("taste").value as MutableMap<String, String>

                hashtagList = ArrayList<String>(hashtagListM.values)
                hashtagList.add(0,"많이 찾는")
                hashtagList.add(1,"핫스팟")


                hashtagList.apply {
                    Log.d("firebaseM", hashtagList.toString())
                    hashTagAdapter = HashTagAdapter(this@CommunityMain)
                    hashTagAdapter.hashtagList = hashtagList
                    hashtagRecyclerView.adapter = hashTagAdapter
                    hashTagAdapter.notifyDataSetChanged()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


        // 선택한 해시태그 기반 데이터 불러오기





    }



}

