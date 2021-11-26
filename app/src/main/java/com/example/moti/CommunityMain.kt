package com.example.moti

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
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
    var hashtagList =  ArrayList<String>()

    var rowindex : Int = 0
    var colindex : Int = 0

    lateinit var communityRecyclerView: RecyclerView
    lateinit var communityAdapter: CommunityAdapter
    var communityData = ArrayList<Post>()

    //lateinit var selectedData :  MutableList<Post>

    class HashTagAdapter(val context: Context, private val HashTagData: ArrayList<String>): RecyclerView.Adapter<HashTagAdapter.ViewHolder>() {

        var rowindex = 0


        inner class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
            val hashtag = view!!.findViewById<TextView>(R.id.textV_hashtag)


            fun bind(hash : String, context: Context, position: Int) {
                hashtag!!.text = hash
                if (rowindex == position) {
                    hashtag!!.setTextColor(ContextCompat.getColor(context, R.color.green))

                } else {
                    hashtag!!.setTextColor(ContextCompat.getColor(context, R.color.gray))

                }
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HashTagAdapter.ViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.item_hashtag, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: HashTagAdapter.ViewHolder, position: Int) {
            holder.bind(HashTagData[position], context, position)
            holder.itemView.setOnClickListener {
                itemClickListener.onClick(it, position)
                rowindex = position
                notifyDataSetChanged()
            }
        }

        interface OnItemClickListener {
            fun onClick(v: View, position: Int)
        }
        // (3) 외부에서 클릭 시 이벤트 설정
        fun setItemClickListener(onItemClickListener: OnItemClickListener) {
            this.itemClickListener = onItemClickListener
        }
        // (4) setItemClickListener로 설정한 함수 실행
        private lateinit var itemClickListener : OnItemClickListener

        override fun getItemCount() = HashTagData.size

    }


    class CommunityAdapter(private val context: Context, private val CommunityData: ArrayList<Post>) : RecyclerView.Adapter<CommunityAdapter.ViewHolder>() {

        var colindex  = 0

        //var communityData: MutableList<Post> = mutableListOf<Post>()

        inner class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
            val title = view!!.findViewById<TextView>(R.id.txtV_title)
            //val imgV_image = itemView.findViewById<ImageView>(R.id.imgV_image)
            val list_hashtag = itemView.findViewById<RecyclerView>(R.id.list_hashtag)
            val constL_startbtn = itemView.findViewById<ConstraintLayout>(R.id.constL_startbtn)


            fun bind(community : Post, context: Context, position: Int) {
                title!!.text = community.title

                /*
                var arrayOfListView = ArrayList<String>()
                arrayOfListView = community.hashtag as ArrayList<String>
                val adapter = ArrayAdapter(this@CommunityAdapter, android.R.layout.activity_list_item, arrayOfListView)
                list_hashtag.adapter = adapter

                 */
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityAdapter.ViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.item_placeinfo, parent, false)
            return ViewHolder(view)
        }


        override fun onBindViewHolder(holder: CommunityAdapter.ViewHolder, position: Int) {
            holder.bind(CommunityData[position], context, position)
            holder.itemView.setOnClickListener {
                itemClickListener.onClick(it, position)
                colindex = position
                notifyDataSetChanged()
            }

            //holder.item
        }

        interface OnItemClickListener {
            fun onClick(v: View, position: Int)
        }
        // (3) 외부에서 클릭 시 이벤트 설정
        fun setItemClickListener(onItemClickListener: OnItemClickListener) {
            this.itemClickListener = onItemClickListener
        }
        // (4) setItemClickListener로 설정한 함수 실행
        private lateinit var itemClickListener : OnItemClickListener

        override fun getItemCount() = CommunityData.size


    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community_main)

        hashtagRecyclerView = findViewById(R.id.mytickets)


        communityRecyclerView = findViewById(R.id.community_main_contents)
        edit_searchBar = findViewById(R.id.edit_searchBar)




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


        //어댑터 연결
        hashtagRecyclerView = findViewById(R.id.mytickets)
        hashTagAdapter = HashTagAdapter(this, hashtagList)
        hashtagRecyclerView.adapter = hashTagAdapter

//        // 해시태그 클릭 리스너
//        hashTagAdapter.setItemClickListener(object : HashTagAdapter.OnItemClickListener{
//            override fun onClick(v: View, position: Int) {
//                Log.d("hashTagAdapterListener", "클릭됨")
//                rowindex = position
//
//                searchCategory(category_group_code, radius, sort)
//            }
//        })


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
                    hashTagAdapter = HashTagAdapter(this@CommunityMain, hashtagList)
                    //hashTagAdapter.hashtagList = hashtagList
                    hashtagRecyclerView.adapter = hashTagAdapter
                    hashTagAdapter.notifyDataSetChanged()
                    // 해시태그 클릭 리스너
                    hashTagAdapter.setItemClickListener(object : HashTagAdapter.OnItemClickListener{
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











        // 커뮤니티 어댑터 설정
        /*
        mutableData  = mutableListOf<Post>()
        mutableData.add(Post("sdfsdfsdf"))

        communityAdapter = CommunityAdapter(this@CommunityMain)
        communityAdapter.mutableData = mutableData
        communityRecyclerView.adapter = communityAdapter

         */



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






    }

    fun searchPlaces(selectedHashTag: String) {
        Log.i("ttt", "dddd")

        var mutableData: MutableList<Post> = mutableListOf<Post>()
        val postRef = Firebase.database.getReference("community")


        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("firebaseM2","호출됨")
                //val postList: MutableList<Post> = mutableListOf<Post>()
                communityData = ArrayList<Post>()
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        Log.d("firebaseM2",userSnapshot.value.toString())
                        val getData = userSnapshot.getValue(Post::class.java)
                        getData?.toString()?.let { it1 -> Log.d("firebaseM3", it1) }

                        // 현재 선택된 해시태그만 가져옴
                        if (getData?.hashtag?.containsValue(selectedHashTag) == true) {
                            communityData.add(getData!!)
                            Log.d("CM", getData.hashtag.toString())
                        }

                        //mutableData = postList

                        communityAdapter = CommunityAdapter(applicationContext, communityData)
                        communityRecyclerView.adapter = communityAdapter

                        communityAdapter.setItemClickListener(object : CommunityAdapter.OnItemClickListener{
                            override fun onClick(v: View, position: Int) {
                                Log.d("CommunityAdapter", "클릭됨")
                                colindex = position
                                showPlaces(communityData[position])
                                //searchCategory(category_group_code, radius, sort)
                            }
                        })
                        //Log.d("firebaseM0",mutableData.toString())


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
        var recordId = selectedPlace.recordId

        Log.i("showPlaces", date.toString())
        Log.i("recordId", recordId.toString())
        // 사용자 티켓 정보 가져오기
        val postRef = Firebase.database.getReference("user")

        //  TODO("${riderId}/course/date로 바꿔야함 ")

        postRef.child("Ru1rsTPKgKctYN2mY1OfEW89hnn1/course/date/${date?.get(0)}/${date?.get(1)}/${date?.get(2)}/${recordId}").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {


                if (snapshot.exists()) {
                    Log.i("showPlacesSN", snapshot.value.toString())
                 }

                }
            override fun onCancelled(error: DatabaseError) {

            }
        })

    }


}



