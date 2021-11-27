package com.example.moti

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.skt.Tmap.TMapGpsManager
import com.skt.Tmap.TMapPolyLine
import com.skt.Tmap.TMapView
import kotlinx.android.synthetic.main.activity_community_main.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.main_tmaplayout2
import java.util.*


class CommunityMain: AppCompatActivity(), TMapGpsManager.onLocationChangedCallback {

    lateinit var edit_searchBar: EditText
    var tmapview: TMapView? = null
    lateinit var searchPlace: PoiItem

    lateinit var floatingActionButton : FloatingActionButton

    lateinit var hashtagRecyclerView: RecyclerView
    lateinit var hashTagAdapter : HashTagAdapter
    var hashtagList =  ArrayList<String>()

    var rowindex : Int = 0
    var colindex : Int = 0

    lateinit var communityRecyclerView: RecyclerView
    lateinit var communityAdapter: CommunityAdapter
    var communityData = ArrayList<Post>()

    lateinit var nowSelectedPlace : Post

    //gps 관련
    var tMapGPS: TMapGpsManager? = null
    lateinit var polyline : TMapPolyLine
    lateinit var address:String


    companion object {
        const val PERMISSION_REQUEST_CODE = 1001
    }


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

    /*
    class SubHashtagAdapter (val context: Context, val hashtagList: ArrayList<String>) : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

            val view: View = LayoutInflater.from(context).inflate(R.layout.item_hashtag2, null)


            val hastTag = view.findViewById<TextView>(R.id.textV_hashtag2)


           // val hastTagList = hastTag[position]

            hastTag.text = hashtagList[position]

            return view
        }

        override fun getItem(position: Int): Any {
            return hashtagList[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getCount(): Int {
            return hashtagList.size
        }
    }
*/


    class SubHashtagAdapter(private val context: Context, private val arrayList: ArrayList<String>) : RecyclerView.Adapter<SubHashtagAdapter.ViewHolder>() {


        inner class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
            val hashtag = view!!.findViewById<TextView>(R.id.textV_hashtag2)

            fun bind(context: Context, position: Int) {
                hashtag!!.text = arrayList[position]
            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int,
        ): SubHashtagAdapter.ViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.item_hashtag2, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: SubHashtagAdapter.ViewHolder, position: Int) {
            holder.bind( context, position)
        }

        override fun getItemCount() = arrayList.size

    }

    class CommunityAdapter(private val context: Context, private val CommunityData: ArrayList<Post>) : RecyclerView.Adapter<CommunityAdapter.ViewHolder>() {
        //var arrayOfListView = ArrayList<String>()

        var colindex  = 0

        //var communityData: MutableList<Post> = mutableListOf<Post>()

        inner class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
            val title = view!!.findViewById<TextView>(R.id.txtV_title)
            //val imgV_image = itemView.findViewById<ImageView>(R.id.imgV_image)
            val list_hashtag = itemView.findViewById<RecyclerView>(R.id.list_hashtag)
            //val constL_startbtn = itemView.findViewById<ConstraintLayout>(R.id.constL_startbtn)


            fun bind(community : Post, context: Context, position: Int) {
                title!!.text = community.title

                var al: List<String> = ArrayList<String>(community.hashtag.values)
                Log.d("al", al.toString())
                var adapter = SubHashtagAdapter(context, al as ArrayList<String>)
                list_hashtag.adapter = adapter

/*
                val layoutManager = LinearLayoutManager(context)
                layoutManager.orientation = LinearLayoutManager.VERTICAL
                list_hashtag.adapter.setLayoutManager(layoutManager)

 */

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




    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community_main)

        hashtagRecyclerView = findViewById(R.id.mytickets)


        communityRecyclerView = findViewById(R.id.community_main_contents)
        edit_searchBar = findViewById(R.id.edit_searchBar)
        floatingActionButton = findViewById(R.id.floatingActionButton)

        floatingActionButton.visibility = View.GONE


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

        //위치 권한 확인
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "권한 승인", Toast.LENGTH_SHORT).show()
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), // 1
                RidingActivity.PERMISSION_REQUEST_CODE) // 2
        }

        // GPS using T Map
        tMapGPS = TMapGpsManager(this)

        // Initial Setting
        // Initial Setting
        tMapGPS!!.minTime=1000
        tMapGPS!!.minDistance= 10F
        tMapGPS!!.provider= TMapGpsManager.NETWORK_PROVIDER

        tMapGPS!!.OpenGps()


        // 검색바
        edit_searchBar.setOnClickListener {
            val intent = Intent(this, SearchPoi::class.java)
            intent.putExtra("type", "searchPlace")
            resultLauncher.launch(intent)
        }

        // start 버튼 누르면
        floatingActionButton.setOnClickListener {
                if (this::nowSelectedPlace.isInitialized )
                {
                    val intent = Intent(this, SelectPlace::class.java)
                    intent.putExtra("reriding", nowSelectedPlace)
                    startActivity(intent)
                }
        }




    }


    override fun onLocationChange(location: Location) {
        tmapview!!.setLocationPoint(location.longitude, location.latitude)
        tmapview!!.setCenterPoint(location.longitude, location.latitude)
        this.address = getCompleteAddressString(this, location.latitude, location.longitude)
        textV_nowPlace.text=getCompleteAddressString(this, location.latitude, location.longitude)


    }



    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            RidingActivity.PERMISSION_REQUEST_CODE -> {  // 1
                if (grantResults.isEmpty()) {  // 2
                    throw RuntimeException("Empty permission result")
                }
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {  // 3
                    Toast.makeText(this, "권한 승인", Toast.LENGTH_SHORT).show()
                } else {
                    if (shouldShowRequestPermissionRationale(
                            Manifest.permission.ACCESS_FINE_LOCATION)) { // 4
                        Log.d(ContentValues.TAG, "User declined, but i can still ask for more")
                        requestPermissions(
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            RidingActivity.PERMISSION_REQUEST_CODE)
                    } else {
                        Log.d(ContentValues.TAG, "User declined and i can't ask")
                        showDialogToGetPermission()   // 5
                    }
                }
            }
        }
    }


    //주소 좌표를 한글 주소로 반환
    private fun getCompleteAddressString(context: Context?, LATITUDE: Double, LONGITUDE: Double): String {
        var strAdd = ""
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1)
            if (addresses != null) {
                val returnedAddress: Address = addresses[0]
                val strReturnedAddress = StringBuilder("")
                for (i in 0..returnedAddress.getMaxAddressLineIndex()) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n")
                }
                strAdd = strReturnedAddress.toString()
                Log.w("MyCurrentloctionaddress", strReturnedAddress.toString())
            } else {
                Log.w("MyCurrentloctionaddress", "No Address returned!")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.w("MyCurrentloctionaddress", "Canont get Address!")
        }

        // "대한민국 " 글자 지워버림
        strAdd = strAdd.substring(5)
        return strAdd
    }


    private fun showDialogToGetPermission() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Permisisons request")
            .setMessage("We need the location permission for some reason. " +
                    "You need to move on Settings to grant some permissions")

        builder.setPositiveButton("OK") { dialogInterface, i ->
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", packageName, null))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)   // 6
        }
        builder.setNegativeButton("Later") { dialogInterface, i ->
            // ignore
        }
        val dialog = builder.create()
        dialog.show()
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

                                //selectedPlace = listOf(communityData[position])
                                //searchCategory(category_group_code, radius, sort)

                                //  start 버튼 누르면
                                floatingActionButton.visibility = View.VISIBLE
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

                    nowSelectedPlace = selectedPlace
                 }

                }
            override fun onCancelled(error: DatabaseError) {

            }
        })

    }



    }






