package com.example.moti

import android.content.Intent
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

    var colindex : Int = 0
    var rowindex : Int = 0

    lateinit var communityRecyclerView: RecyclerView
    lateinit var communityAdapter: CommunityMain.CommunityAdapter
    var tickets = ArrayList<Post>()



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
                    hashtagList.add(0,"전체")
                   // hashtagList.add(1,"핫스팟")


                    // 어댑터에 데이터 저장
                    hashtagList.apply {
                        Log.d("firebaseM", hashtagList.toString())
                        hashTagAdapter =
                            CommunityMain.HashTagAdapter(this@Mypage, hashtagList)
                        //hashTagAdapter.hashtagList = hashtagList
                        hashtagRecyclerView.adapter = hashTagAdapter
                        hashTagAdapter.notifyDataSetChanged()

                        searchPlaces("전체")  // 디폴트 값

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

                tickets = ArrayList<Post>()

                if (snapshot.exists()) {
                    for (years in snapshot.children) { // 년도 year
                        for (months in years.children){ // 월 month
                            for (dates in months.children) {  // 일자 dates
                                for (ridings in dates.children){  // 일일 riding 객체

                                    if (ridings.hasChild("Coordinates") && ridings.hasChild("Record")){
                                        val CurrentUser = FirebaseAuth.getInstance().currentUser

                                        Log.d("saveTicket", ridings.value.toString())
                                        Log.d("saveTicket++",  ridings.child("Hashtag").value.toString())

                                        if (selectedHashTag == "전체")  // '전체' 택한 경우
                                        {
                                            Log.d("전체", ridings.value.toString())
                                            var ticket: Post = Post(
                                                recordId = ridings.key,
                                                riderId = CurrentUser?.uid.toString(),
                                                // hashtag = ridings.child("Record/Hashtag").value as MutableMap<String, String>,
                                                date = ridings.child("Record/date").toString(),
                                                title = ridings.child("Record/title").value.toString(),
                                                photoUrl = "imsi"
                                            )

                                            if (ridings!!.child("Hashtag").value != null){
                                                ticket.hashtag = ridings!!.child("Hashtag").value as MutableMap<String, String>
                                            }

                                            tickets.add(ticket)

                                        }

                                       else { // 이 외의 해시태그 선택한 경우

                                           if (ridings!!.child("Hashtag").value != null) {  // 해시태그가 null 이 아닌 것 중에서

                                               var tempHashtag : MutableMap<String, String>
                                               tempHashtag = ridings!!.child("Hashtag").value as MutableMap<String, String>

                                               if (tempHashtag.containsValue(selectedHashTag) == true ){ //선택한 해시태그만 담음
                                                   var ticket: Post = Post(
                                                       recordId = ridings.key,
                                                       riderId = CurrentUser?.uid.toString(),
                                                       hashtag = ridings.child("Hashtag").value as MutableMap<String, String>,
                                                       date = ridings.child("Record/date").toString(),
                                                       title = ridings.child("Record/title").value.toString(),
                                                       photoUrl = "imsi"
                                                   )
                                                   tickets.add(ticket)
                                               }
                                           }
                                        }

//                                        var ticket: Post = Post(
//                                                recordId = ridings.key,
//                                                riderId = CurrentUser?.uid.toString(),
//                                               // hashtag = ridings.child("Record/Hashtag").value as MutableMap<String, String>,
//                                                date = ridings.child("Record/date").toString(),
//                                                title = ridings.child("Record/title").value.toString(),
//                                                photoUrl = "imsi"
//                                            )
//
//                                        if (ridings!!.child("Hashtag").value != null){
//                                            ticket.hashtag = ridings!!.child("Hashtag").value as MutableMap<String, String>
//                                        }
//
//                                            tickets.add(ticket)
//
//                                            Log.d("checkOK", ridings.value.toString())

                                    }
                                    communityAdapter = CommunityMain.CommunityAdapter(applicationContext, tickets)
                                    communityRecyclerView.adapter = communityAdapter

                                    communityAdapter.setItemClickListener(object : CommunityMain.CommunityAdapter.OnItemClickListener{
                                        override fun onClick(v: View, position: Int) {
                                            Log.d("CommunityAdapter", "클릭됨")
                                            colindex = position
                                            showPlaces(tickets[position])

                                            //selectedPlace = listOf(communityData[position])
                                            //searchCategory(category_group_code, radius, sort)
                                        }
                                    })

                                }// 일일 riding for문 끝
                            }

                        }

                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun showPlaces(selectedPlace: Post) {
        Log.i("showPlaces", selectedPlace.title.toString())

        var riderId = selectedPlace.riderId
        var date = selectedPlace.date?.split("/")


        Log.i("selectedPlace1", date.toString())
        Log.i("selectedPlace3", selectedPlace.title.toString())
        Log.i("selectedPlace4", selectedPlace.postId.toString())
        Log.i("selectedPlace5", selectedPlace.recordId.toString()) //

        Log.i("selectedPlace6", selectedPlace.riderId.toString())


        val intent = Intent(this, AfterRidingActivity::class.java)
        intent.putExtra("selectedPlace", selectedPlace)
        startActivity(intent)

        /*
        // 사용자 티켓 정보 가져오기
        val postRef = Firebase.database.getReference("user")

        //  TODO("${riderId}/course/date로 바꿔야함 ")

        postRef.child("Ru1rsTPKgKctYN2mY1OfEW89hnn1/course/date/${date?.get(0)}/${date?.get(1)}/${date?.get(2)}/${recordId}").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    Log.i("showPlacesSN", snapshot.value.toString())

                    nowSelectedPlace = selectedPlace
                }

            }
            override fun onCancelled(error: DatabaseError) {

            }
        })

         */

    }
}
