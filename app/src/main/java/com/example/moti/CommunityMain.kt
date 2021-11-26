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
    var communityData = ArrayList<Content>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community_main)

        hashtagRecyclerView = findViewById(R.id.hashtag)

        communityRecyclerView = findViewById(R.id.community_main_contents)

        edit_searchBar = findViewById(R.id.edit_searchBar)

        hashtagList = ArrayList<String>()


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




        // 추가함
        hashTagAdapter = HashTagAdapter(this@CommunityMain)
        hashtagRecyclerView.adapter = hashTagAdapter
        hashTagAdapter.notifyDataSetChanged()


        // 해쉬 태그 선택 시 해당 장소들 뿌려지도록
        hashTagAdapter.setItemClickListener(object : HashTagAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {

                var selectedHashtag : String = hashtagList[position]
                Log.d("firebaseM2","클릭됨 ${selectedHashtag}")


                val mutableData = MutableLiveData<MutableList<Post>>()

                val postRef = Firebase.database.getReference("community")
                postRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val postList: MutableList<Post> = mutableListOf<Post>()

                        if (snapshot.exists()) {
                            for (userSnapshot in snapshot.children) {

                                val getData = userSnapshot.getValue(Post::class.java)
                                if (getData?.hashtag?.containsValue(selectedHashtag) == true) {
                                    // 현재 선택된 해시태그만 가져옴
                                    Log.d("firebaseM2", getData?.toString())
                                    postList.add(getData!!)

                                }

                                mutableData.value = postList

                            }


                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })

                }



        })





    }



}


//class Communityy(var image: Int)
//
//class CommunityAdapter(val context: Context, private val CommunityData: ArrayList<Communityy>): RecyclerView.Adapter<CommunityAdapter.ViewHolder>() {
//
//    inner class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
//        val course = view!!.findViewById<ImageView>(R.id.community_content)
//
//        fun bind(content: Content, context: Context) {
//            course!!.setImageResource(R.drawable.seoul)
//        }
//
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityAdapter.ViewHolder {
//        val view = LayoutInflater.from(context).inflate(R.layout.community_main_item, parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: CommunityAdapter.ViewHolder, position: Int) {
//        holder.bind(CommunityData[position], context)
//        holder.itemView.setOnClickListener {
//            itemClickListener.onClick(it, position)
//        }
//    }
//
//    interface OnItemClickListener {
//        fun onClick(v: View, position: Int)
//    }
//    // (3) 외부에서 클릭 시 이벤트 설정
//    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
//        this.itemClickListener = onItemClickListener
//    }
//    // (4) setItemClickListener로 설정한 함수 실행
//    private lateinit var itemClickListener : OnItemClickListener
//
//    override fun getItemCount() = CommunityData.size
//
//}
