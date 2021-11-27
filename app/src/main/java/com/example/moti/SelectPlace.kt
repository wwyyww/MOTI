package com.example.moti

import KaKaoAPI_category
import ResultCategoryKeyword
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SelectPlace : AppCompatActivity() {

    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK 638dc203e2c1c41fc03b925e2d80613b"  // REST API 키
    }

    //임의로
    var lat = "37.6403239"
    var long = "127.0678909"

    lateinit var menuRecyclerView: RecyclerView
    lateinit var menuAdapter: MenuAdapter
    var menuData = ArrayList<Menu>()

    lateinit var selectRecyclerView: RecyclerView
    lateinit var selectAdapter: SelectAdapter
    var selectData = ArrayList<Select>()

    lateinit var sort : String
    lateinit var category_group_code : String
    var radius : Int = 0

    lateinit var textV_departure : TextView
    lateinit var textV_destination : TextView
    lateinit var textV_layover1 : TextView

    lateinit var v_departure : View
    lateinit var v_destination : View
    lateinit var v_layover1 : View

    lateinit var btn_next : Button
    var rowindex : Int = 0

    lateinit var departure: PoiItem
    lateinit var destination: PoiItem
    lateinit var layover1 :PoiItem

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
            window.statusBarColor = Color.parseColor("#0CE795")
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

    class Select(var placename: String, var phone: String, var addressname: String, var road_add: String, var url: String, var category: String)

    class SelectAdapter(val context: Context, private val SelectData: ArrayList<Select>): RecyclerView.Adapter<SelectAdapter.ViewHolder>() {
        inner class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
            val place_name = view!!.findViewById<TextView>(R.id.place_name)
            val place_cate = view!!.findViewById<TextView>(R.id.category_name)
            val place_add = view!!.findViewById<TextView>(R.id.address)
            val place_phone = view!!.findViewById<TextView>(R.id.phone)
            val place_img = view!!.findViewById<ImageView>(R.id.image)


            fun bind(select : Select, context: Context, position: Int) {
                place_name!!.text = select.placename
                place_cate!!.text = select.category
                place_add!!.text = select.addressname
                if(select.addressname != "" && select.road_add != ""){
                    place_add!!.text = select.addressname +" | " + select.road_add
                } else if(select.addressname != "" || select.road_add != ""){
                    place_add!!.text = select.addressname + select.road_add
                }
                place_phone!!.text = select.phone
                if ( position%3 == 0 ) {
                    place_img!!.setImageResource(R.drawable.cafe2)
                } else if ( position%3 == 1) {
                    place_img!!.setImageResource(R.drawable.cafe3)
                } else {
                    place_img!!.setImageResource(R.drawable.cafe)
                }


//                if(rowindex == position){
//                    menu_btn!!.setTextColor(ContextCompat.getColor(context, R.color.green))
//                    menu_btn!!.setBackgroundResource(R.drawable.solid_button2)
//                } else {
//                    menu_btn!!.setTextColor(ContextCompat.getColor(context, R.color.gray))
//                    menu_btn!!.setBackgroundResource(R.drawable.solid_button)
//                }
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectAdapter.ViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.select_place_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: SelectAdapter.ViewHolder, position: Int) {
            holder.bind(SelectData[position], context, position)
            holder.itemView.setOnClickListener {
                itemClickListener.onClick(it, position)
//                rowindex = position
//                notifyDataSetChanged()
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

        override fun getItemCount() = SelectData.size

    }

    class Menu(var menu: String)

    class MenuAdapter(val context: Context, private val MenuData: ArrayList<Menu>): RecyclerView.Adapter<MenuAdapter.ViewHolder>() {


        var rowindex : Int = 0

        inner class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
            val menu_btn = view!!.findViewById<TextView>(R.id.btn_menu)


            fun bind(menu : Menu, context: Context, position: Int) {
                menu_btn!!.text = menu.menu
                if(rowindex == position){
                    menu_btn!!.setTextColor(ContextCompat.getColor(context, R.color.green))
                    menu_btn!!.setBackgroundResource(R.drawable.solid_button2)
                } else {
                    menu_btn!!.setTextColor(ContextCompat.getColor(context, R.color.gray))
                    menu_btn!!.setBackgroundResource(R.drawable.solid_button)
                }
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuAdapter.ViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.community_hashtag_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: MenuAdapter.ViewHolder, position: Int) {
            holder.bind(MenuData[position], context, position)
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

        override fun getItemCount() = MenuData.size

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_place)
        transparentStatusAndNavigation()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        radius = 10000
        sort = "accuracy"
        category_group_code = "CE7"

        val actionBar: ActionBar?
        actionBar=supportActionBar
        actionBar?.hide()

        menuData.add(Menu("카페"))
        menuData.add(Menu("음식점"))
        menuData.add(Menu("편의점"))
        menuData.add(Menu("마트"))
        menuData.add(Menu("관광명소"))
        menuData.add(Menu("문화시설"))
        menuData.add(Menu("숙박"))

        menuRecyclerView = findViewById(R.id.category)
        menuAdapter = MenuAdapter(this, menuData)
        menuRecyclerView.adapter = menuAdapter

        menuAdapter.setItemClickListener(object: MenuAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                rowindex = position
                sort = "accuracy"
                radius = 10000

                //Toast.makeText(v.context, "${categoryData[position].menu}", Toast.LENGTH_SHORT).show()
                if (menuData[position].menu == "카페"){
                    category_group_code = "CE7"
                } else if(menuData[position].menu == "음식점"){
                    category_group_code = "FD6"
                }else if(menuData[position].menu == "편의점"){
                    category_group_code = "CS2"
                }else if(menuData[position].menu == "마트"){
                    category_group_code = "MT1"
                }else if(menuData[position].menu == "관광명소"){
                    category_group_code = "AT4"
                }else if(menuData[position].menu == "문화시설"){
                    category_group_code = "CT1"
                }else if(menuData[position].menu == "숙박"){
                    category_group_code = "AD5"
                }

                searchCategory(category_group_code, radius, sort)
            }
        })


        selectRecyclerView = findViewById(R.id.select_main)
        searchCategory(category_group_code, radius, sort)
        //selectAdapter = SelectAdapter(this, selectData)
        //selectRecyclerView.adapter = selectAdapter


        // shared 된 인텐트 값 가져오기
        val intent: Intent = getIntent()
        var sharedPlaces = intent.getParcelableExtra<PoiItem>("sharing")
        Log.d("sharing", "getIntent : ${sharedPlaces}")



        textV_departure = findViewById(R.id.textV_departure)
        textV_destination = findViewById(R.id.textV_destination)
        textV_layover1 = findViewById(R.id.textV_layover1)
        btn_next = findViewById(R.id.btn_next)
        v_departure = findViewById(R.id.v_departure)
        v_layover1 = findViewById(R.id.v_layover1)
        v_destination = findViewById(R.id.v_destination)



        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                var placePoiItem = data?.getParcelableExtra<PoiItem>("placePoiItem")
                Log.d("successM", "onActivityResult : ${placePoiItem}")

                if (placePoiItem?.type =="departure"){
                    departure = placePoiItem
//                    placePoiItemList.add(0,placePoiItem )
                    Log.d("successM", "final : ${placePoiItem}")
                    textV_departure.text = placePoiItem.name
                    textV_departure.setTextColor(Color.BLACK)

                }else if (placePoiItem?.type =="destination"){
                    destination = placePoiItem
                    //placePoiItemList.add(1,placePoiItem )
                    Log.d("successM", "final : ${destination}")
                    textV_destination.text = destination.name
                    textV_destination.setTextColor(Color.BLACK)
1
                }else if (placePoiItem?.type =="layover1"){
                    layover1 = placePoiItem
                    textV_layover1.text = layover1.name
                    textV_layover1.setTextColor(Color.BLACK)
                }
            }
        }



        if (this::destination.isInitialized && this::departure.isInitialized && this::layover1.isInitialized) {

            textV_destination.text = destination.name
            textV_departure.text = departure.name
            textV_layover1.text = layover1.name
        }



        if (textV_departure.text != null && textV_destination != null &&  textV_destination != null ){
            btn_next.visibility =  View.VISIBLE
        }else{
            btn_next.visibility =  View.GONE
        }



        v_departure.setOnClickListener {
            val intent = Intent(this, SearchPoi::class.java)
            intent.putExtra("type", "departure")
            resultLauncher.launch(intent)
        }


        v_destination.setOnClickListener {
            val intent = Intent(this, SearchPoi::class.java)
            intent.putExtra("type", "destination")
            resultLauncher.launch(intent)
        }


        v_layover1.setOnClickListener {
            val intent = Intent(this, SearchPoi::class.java)
            intent.putExtra("type", "layover1")
            resultLauncher.launch(intent)
        }


        btn_next.setOnClickListener {

            Log.d("SUCCESSM", "다음 버튼 : ${departure} ${destination} ${layover1} ")
            val intent = Intent(this, ReadyActivity::class.java).apply {
                putExtra("departure", departure)
                putExtra("destination", destination)
                putExtra("layover", layover1)
            }.run {startActivity(this) }

        }

    }

    // 카테고리 검색 함
    private fun searchCategory(category_group_code: String, radius: Int, sort: String) {

        Log.i("ttt", "dddd")
        val retrofit = Retrofit.Builder()   // Retrofit 구성
            .baseUrl(KaKaoTestActivity.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KaKaoAPI_category::class.java)   // 통신 인터페이스를 객체로 생성
        val call = api.getSearchKeyword(KaKaoTestActivity.API_KEY, category_group_code, this.long.toString(), this.lat.toString(), radius, sort)   // 검색 조건 입력

        // API 서버에 요청
        call.enqueue(object: Callback<ResultCategoryKeyword> {
            override fun onResponse(
                call: Call<ResultCategoryKeyword>,
                response: Response<ResultCategoryKeyword>
            ) {
                Log.i("Testttt", "Body: ${response.body()?.documents?.get(0)?.place_name}")
                selectData = ArrayList<Select>()
                if (response.body()?.documents?.size!!.toInt() > 0){
                    for (i in 0..response.body()?.documents?.size!!.toInt()-1){
                        //var doc = Jsoup.connect(response.body()?.documents?.get(i)?.place_url.toString()).get()
                        selectData.add(Select(response.body()?.documents?.get(i)?.place_name.toString(), response.body()?.documents?.get(i)?.phone.toString(),
                            response.body()?.documents?.get(i)?.address_name.toString(), response.body()?.documents?.get(i)?.road_address_name.toString(),
                            response.body()?.documents?.get(i)?.place_url.toString(),response.body()?.documents?.get(i)?.category_name.toString()))
                    }
                }

                selectAdapter = SelectAdapter(applicationContext, selectData)
                selectRecyclerView.adapter = selectAdapter
                selectAdapter.setItemClickListener(object: SelectAdapter.OnItemClickListener{
                    override fun onClick(v: View, position: Int) {
                        //Toast.makeText(v.context, "${contentData[position].url}", Toast.LENGTH_SHORT).show()
                        val i = Intent(Intent.ACTION_VIEW)
                        i.data = Uri.parse(selectData[position].url.toString())
                        startActivity(i)
                    }
                })

            }

            override fun onFailure(call: Call<ResultCategoryKeyword>, t: Throwable) {
                // 통신 실패
                Log.w("KaKaoTestActivity", "통신 실패: ${t.message}")
            }
        })
    }

}