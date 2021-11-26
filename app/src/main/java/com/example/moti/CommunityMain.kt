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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
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
    lateinit var communityAdapter: CommunityAdapter
    //var communityData = ArrayList<Content>()

    lateinit var selectedData :  MutableList<Post>

    class CommunityAdapter(private val context: Context) : RecyclerView.Adapter<CommunityAdapter.CustomViewHolder>() {

        //var hashtagList =  ArrayList<MutableCollection<String>> ()
        // var placeList = ArrayList<Place> ()

        var communityData: MutableList<Post> = mutableListOf<Post>()

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int,
        ): CommunityAdapter.CustomViewHolder {
            Log.d("[2]firebaseMAdapter2", communityData.toString())
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_placeinfo, parent, false)
            return CommunityAdapter.CustomViewHolder(view)

        }

        override fun onBindViewHolder(holder: CommunityAdapter.CustomViewHolder, position: Int) {
            //holder.imgV_image.src
            holder.txtV_title.text  = communityData.get(position).toString()
            //holder.recy_hashtag
            holder.constL_startbtn
            Log.d("[2]onBindViewHolder",communityData.get(position).toString())
        }

        override fun getItemCount(): Int {
            return communityData.size
        }


        class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            // val textV_hashtag = itemView.findViewById<TextView>(R.id.textV_hashtag)
            val imgV_image = itemView.findViewById<ImageView>(R.id.imgV_image)
            val txtV_title = itemView.findViewById<TextView>(R.id.txtV_title)
            val recy_hashtag = itemView.findViewById<RecyclerView>(R.id.recy_hashtag)
            val constL_startbtn = itemView.findViewById<ConstraintLayout>(R.id.constL_startbtn)
        }


    }

    /*
    class HashTagAdapter(private val context: Context, var hashtagList: ArrayList<String>) : RecyclerView.Adapter<HashTagAdapter.ViewHolder>(){

        //var hashtagList =  ArrayList<MutableCollection<String>> ()
        // var hashtagList = ArrayList<String>()
        //val mutableData = MutableLiveData<MutableList<Post>>()



        inner class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {

            val textV_hashtag = view?.findViewById<TextView>(R.id.textV_hashtag)
            val constL_rectangle = view?.findViewById<ConstraintLayout>(R.id.constL_rectangle)


            fun bind( context: Context, position: Int) {
                textV_hashtag!!.text = hashtagList.get(position)
            }
        }



        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int,
        ): HashTagAdapter.ViewHolder {
            Log.d("firebaseMAdapter1",hashtagList.toString())
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_hashtag,parent,false)
            return ViewHolder(view)

        }


        override fun onBindViewHolder(holder: HashTagAdapter.ViewHolder, position: Int) {
            holder.bind( context, position)

            var selectedHashTag = hashtagList.get(position)

            holder.constL_rectangle!!.setOnClickListener {
                itemClickListener.onClick(it, position)
                //rowindex = position
                notifyDataSetChanged()

            }


        }

        /*
        fun searchPlaces(selectedHashTag: String) {
            Log.i("ttt", "dddd")

            var mutableData: MutableList<Post> = mutableListOf<Post>()
            val postRef = Firebase.database.getReference("community")


            postRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("firebaseM2","호출됨")
                    val postList: MutableList<Post> = mutableListOf<Post>()

                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            Log.d("firebaseM2",userSnapshot.value.toString())
                            val getData = userSnapshot.getValue(Post::class.java)
                            getData?.toString()?.let { it1 -> Log.d("firebaseM3", it1) }

                            // 현재 선택된 해시태그만 가져옴
                            if (getData?.hashtag?.containsValue(selectedHashTag) == true) {
                                postList.add(getData!!)

                            }

                            mutableData = postList

                            communityAdapter = CommunityAdapter(applicationContext, mutableData)


                            Log.d("firebaseM0",mutableData.toString())


                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })

        }

         */

        interface OnItemClickListener {
            fun onClick(v: View, position: Int)
        }
        // (3) 외부에서 클릭 시 이벤트 설정
        fun setItemClickListener(onItemClickListener: OnItemClickListener) {
            this.itemClickListener = onItemClickListener
        }
        // (4) setItemClickListener로 설정한 함수 실행
        private lateinit var itemClickListener : OnItemClickListener

        override fun getItemCount() = hashtagList.size



    }





     */







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


        //어댑터 연결
        hashtagRecyclerView = findViewById(R.id.hashtag)
        hashTagAdapter = HashTagAdapter(this)
        hashtagRecyclerView.adapter = hashTagAdapter




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




        // 해시태그 클릭 리스너
        hashTagAdapter.setItemClickListener(object : HashTagAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                Log.d("hashTagAdapterListener", "클릭됨")

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



}

