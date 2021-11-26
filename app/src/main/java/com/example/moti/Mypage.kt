package com.example.moti

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Mypage : AppCompatActivity() {


    lateinit var textV_name : TextView
    lateinit var myHashtags : RecyclerView
    lateinit var myTickets : RecyclerView


    lateinit var hashtagRecyclerView: RecyclerView
    lateinit var hashTagAdapter : CommunityMain.HashTagAdapter
    var hashtagList =  ArrayList<String>()


    var rowindex : Int = 0

    lateinit var communityRecyclerView: RecyclerView
    lateinit var communityAdapter: CommunityMain.CommunityAdapter
    var communityData = ArrayList<Post>()



        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)


        textV_name = findViewById(R.id.textV_name)


            //어댑터 연결
            hashtagRecyclerView = findViewById(R.id.myHashtags)
            hashTagAdapter = CommunityMain.HashTagAdapter(this, hashtagList)
            hashtagRecyclerView.adapter = hashTagAdapter


            communityRecyclerView = findViewById(R.id.myTickets)


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


                    // 어댑터에 데이터 저장
                    hashtagList.apply {
                        Log.d("firebaseM", hashtagList.toString())
                        hashTagAdapter =
                            CommunityMain.HashTagAdapter(this@Mypage, hashtagList)
                        //hashTagAdapter.hashtagList = hashtagList
                        hashtagRecyclerView.adapter = hashTagAdapter
                        hashTagAdapter.notifyDataSetChanged()
                        // 해시태그 클릭 리스너
                        hashTagAdapter.setItemClickListener(object : CommunityMain.HashTagAdapter.OnItemClickListener{
                            override fun onClick(v: View, position: Int) {
                                Log.d("hashTagAdapterListener", "클릭됨")
                                rowindex = position
                                searchPlaces(hashtagList[position])
                                //searchCategory(category_group_code, radius, sort)
                            }
                        })

                    }

                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

        } // onCreate() 종료 지점


    fun searchPlaces(selectedHashTag: String) {
        Log.i("ttt", "dddd")

        // 사용자 티켓 정보 가져오기
        val postRef = Firebase.database.getReference("user")

        //  TODO("CurrentUser로 바꿔야함 ")
        postRef.child("Ru1rsTPKgKctYN2mY1OfEW89hnn1/course/date").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                var tickets = ArrayList<Post>()

                if (snapshot.exists()) {
                    for (years in snapshot.children) { // 년도 year
                        for (months in years.children){ // 월 month
                            for (dates in months.children) {  // 일자 dates
                                for (ridings in dates.children){  // 일일 riding 객체

                                    if (ridings.hasChild("Coordinates") && ridings.hasChild("Record")){
                                        val CurrentUser = FirebaseAuth.getInstance().currentUser

                                        Log.d("saveTicket", ridings.value.toString())
                                        Log.d("saveTicket++",  ridings.child("Hashtag").value.toString())


                                        var ticket: Post = Post(
                                                postId = ridings.key,
                                                riderId = CurrentUser?.uid.toString(),
                                               // hashtag = ridings.child("Record/Hashtag").value as MutableMap<String, String>,
                                                hashtag = ridings.child("Hashtag").value as MutableMap<String, String>,
                                                date = ridings.child("Record/date").toString(),
                                                title = ridings.child("Record/title").value.toString(),
                                                photoUrl = "imsi"
                                            )

                                            tickets.add(ticket)

                                            Log.d("checkOK", ridings.value.toString())

                                    }

                                    communityAdapter = CommunityMain.CommunityAdapter(applicationContext, tickets)
                                    communityRecyclerView.adapter = communityAdapter


                                }
                            }

                        }

                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}

class HashTag (
    var key : String ?= "",
    var value : String ?= "",
)
