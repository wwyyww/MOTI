package com.example.moti

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Mypage : AppCompatActivity() {

    private fun transparentStatusAndNavigation() {
        //make full transparent statusBar
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                        or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, true
            )
        }
        if (Build.VERSION.SDK_INT >= 19) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(
                (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                        or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION), false
            )
            //window.statusBarColor = Color.TRANSPARENT
            window.navigationBarColor = Color.TRANSPARENT
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = Color.TRANSPARENT
            window.navigationBarColor = Color.TRANSPARENT
        }
    }

    private fun setWindowFlag(bits: Int, on: Boolean) {
        val win = window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }

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
            transparentStatusAndNavigation()
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

            //action bar ?????????
            var actionBar: ActionBar?
            actionBar=supportActionBar
            actionBar?.hide()
            textV_name = findViewById(R.id.textV_name)


            //????????? ??????
            hashtagRecyclerView = findViewById(R.id.myHashtags)
            hashTagAdapter = CommunityMain.HashTagAdapter(this, hashtagList)
            hashtagRecyclerView.adapter = hashTagAdapter


            communityRecyclerView = findViewById(R.id.myTickets)


            // ????????? ???????????? ?????? ????????????
            val user = Firebase.auth.currentUser   // fireabase ?????? ????????? ????????????
            val CurrentUser = FirebaseAuth.getInstance().currentUser

            val userRef = Firebase.database.getReference("users")

            userRef.addValueEventListener(object : ValueEventListener {
                var hashtagListM : MutableMap<String, String> = HashMap()

                override fun onDataChange(snapshot: DataSnapshot) {

                    Log.d("firebaseM", snapshot.child("Ru1rsTPKgKctYN2mY1OfEW89hnn1").child("taste").childrenCount.toString())
                    hashtagListM = snapshot.child("Ru1rsTPKgKctYN2mY1OfEW89hnn1").child("taste").value as MutableMap<String, String>

                    hashtagList = ArrayList<String>(hashtagListM.values)
                    hashtagList.add(0,"??????")
                   // hashtagList.add(1,"?????????")


                    // ???????????? ????????? ??????
                    hashtagList.apply {
                        Log.d("firebaseM", hashtagList.toString())
                        hashTagAdapter =
                            CommunityMain.HashTagAdapter(this@Mypage, hashtagList)
                        //hashTagAdapter.hashtagList = hashtagList
                        hashtagRecyclerView.adapter = hashTagAdapter
                        hashTagAdapter.notifyDataSetChanged()

                        searchPlaces("??????")  // ????????? ???

                        // ???????????? ?????? ?????????
                        hashTagAdapter.setItemClickListener(object : CommunityMain.HashTagAdapter.OnItemClickListener{
                            override fun onClick(v: View, position: Int) {
                                Log.d("hashTagAdapterListener", "?????????")
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

        } // onCreate() ?????? ??????


    fun searchPlaces(selectedHashTag: String) {
        Log.i("ttt", "dddd")

        // ????????? ?????? ?????? ????????????
        val postRef = Firebase.database.getReference("user")

        //  TODO("CurrentUser??? ???????????? ")
        postRef.child("Ru1rsTPKgKctYN2mY1OfEW89hnn1/course/date").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                tickets = ArrayList<Post>()

                if (snapshot.exists()) {
                    for (years in snapshot.children) { // ?????? year
                        for (months in years.children){ // ??? month
                            for (dates in months.children) {  // ?????? dates
                                for (ridings in dates.children){  // ?????? riding ??????

                                    if (ridings.hasChild("Coordinates") && ridings.hasChild("Record")){
                                        val CurrentUser = FirebaseAuth.getInstance().currentUser

                                        Log.d("saveTicket", ridings.value.toString())
                                        Log.d("saveTicket++",  ridings.child("Hashtag").value.toString())

                                        if (selectedHashTag == "??????")  // '??????' ?????? ??????
                                        {
                                            Log.d("??????", ridings.value.toString())
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

                                       else { // ??? ?????? ???????????? ????????? ??????

                                           if (ridings!!.child("Hashtag").value != null) {  // ??????????????? null ??? ?????? ??? ?????????

                                               var tempHashtag : MutableMap<String, String>
                                               tempHashtag = ridings!!.child("Hashtag").value as MutableMap<String, String>

                                               if (tempHashtag.containsValue(selectedHashTag) == true ){ //????????? ??????????????? ??????
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
                                            Log.d("CommunityAdapter", "?????????")
                                            colindex = position
                                            showPlaces(tickets[position])

                                            //selectedPlace = listOf(communityData[position])
                                            //searchCategory(category_group_code, radius, sort)
                                        }
                                    })

                                }// ?????? riding for??? ???
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
        // ????????? ?????? ?????? ????????????
        val postRef = Firebase.database.getReference("user")

        //  TODO("${riderId}/course/date??? ???????????? ")

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
