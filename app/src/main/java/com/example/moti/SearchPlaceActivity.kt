package com.example.moti

import KaKaoAPI_category
import ResultCategoryKeyword
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_ready_ticket.view.*
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchPlaceActivity : AppCompatActivity() {

    lateinit var back : Button
    lateinit var popup : Button
    lateinit var distancebtn : Button
    lateinit var lat : String
    lateinit var long : String
    lateinit var address : String
    lateinit var myplace : TextView
    lateinit var distancetxt: TextView
    lateinit var sort : String
    lateinit var category_group_code : String
    lateinit var seekBar: SeekBar
    var radius : Int = 0
    var rowindex : Int = 0

    lateinit var menuRecyclerView: RecyclerView
    lateinit var menuAdapter: CategoryAdapter
    var categoryData = ArrayList<Category_Menu>()

    lateinit var contentRecyclerView: RecyclerView
    lateinit var contentAdapter: ContentAdapter
    var contentData = ArrayList<Content>()

    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK 638dc203e2c1c41fc03b925e2d80613b"  // REST API 키
    }

    // 리스너로 처리
    inner class PopupListener : PopupMenu.OnMenuItemClickListener {
        override fun onMenuItemClick(p0: MenuItem?): Boolean {
            when (p0?.itemId) {
                R.id.searchplace_menu1 -> {
                    popup.text = "거리 순"
                    sort = "distance"
                    searchCategory(category_group_code, radius, sort)
                }

                R.id.searchplace_menu2 -> {
                    popup.text = "정확도 순"
                    sort = "accuracy"
                    searchCategory(category_group_code, radius, sort)
                }

            }
            return false
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_place)
        radius = 10000
        sort = "accuracy"
        category_group_code = "CE7"
        distancetxt = findViewById(R.id.distancetxt)
        distancebtn = findViewById(R.id.distance_btn)
        seekBar = findViewById(R.id.distancebar)
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                distancetxt.text = p1.toString()+"m"
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                distancetxt.text = p0!!.progress.toString()+"m"
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                distancetxt.text = p0!!.progress.toString()+"m"
                radius = p0!!.progress
                searchCategory(category_group_code, radius, sort)
            }

        })
        popup = findViewById(R.id.popbtn)
        popup.setOnClickListener {
            var pop = PopupMenu(this, popup)

            menuInflater.inflate(R.menu.searchplace_menu, pop.menu)

            //리스너로 처리
            var listener = PopupListener()
            pop.setOnMenuItemClickListener(listener)
            pop.show()
        }

        categoryData.add(Category_Menu("카페", R.drawable.ic_baseline_star_24))
        categoryData.add(Category_Menu("음식점", R.drawable.ic_baseline_star_24))
        categoryData.add(Category_Menu("편의점", R.drawable.ic_baseline_star_24))
        categoryData.add(Category_Menu("마트", R.drawable.ic_baseline_star_24))
        categoryData.add(Category_Menu("관광명소", R.drawable.ic_baseline_star_24))
        categoryData.add(Category_Menu("문화시설", R.drawable.ic_baseline_star_24))
        categoryData.add(Category_Menu("숙박", R.drawable.ic_baseline_star_24))

        val actionBar: ActionBar?
        actionBar=supportActionBar
        actionBar?.hide()

        var intent = intent
        lat = intent.getStringExtra("lat").toString()
        long = intent.getStringExtra("long").toString()
        address = intent.getStringExtra("address").toString()
        radius = 10000

        menuRecyclerView = findViewById(R.id.category_menu)
        menuAdapter = CategoryAdapter(this, categoryData)
        menuRecyclerView.adapter = menuAdapter

        contentRecyclerView = findViewById(R.id.searchplace_main_contents)
        contentRecyclerView.addItemDecoration(DividerItemDecoration(applicationContext, 1))
        searchCategory(category_group_code, radius, sort)


        menuAdapter.setItemClickListener(object: CategoryAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                rowindex = position
                sort = "accuracy"
                radius = 10000
                popup.text = "정확도 순"
                //Toast.makeText(v.context, "${categoryData[position].menu}", Toast.LENGTH_SHORT).show()
                if (categoryData[position].menu == "카페"){
                    category_group_code = "CE7"
                } else if(categoryData[position].menu == "음식점"){
                    category_group_code = "FD6"
                }else if(categoryData[position].menu == "편의점"){
                    category_group_code = "CS2"
                }else if(categoryData[position].menu == "마트"){
                    category_group_code = "MT1"
                }else if(categoryData[position].menu == "관광명소"){
                    category_group_code = "AT4"
                }else if(categoryData[position].menu == "문화시설"){
                    category_group_code = "CT1"
                }else if(categoryData[position].menu == "숙박"){
                    category_group_code = "AD5"
                }
                seekBar.progress = radius
                searchCategory(category_group_code, radius, sort)
            }
        })

        myplace = findViewById(R.id.myplace)
        myplace!!.text = address

        back = findViewById(R.id.out_btn)
        back.setOnClickListener {
            finish()
        }
    }
    // 카테고리 검색 함
    private fun searchCategory(category_group_code: String, radius: Int, sort: String) {
        val retrofit = Retrofit.Builder()   // Retrofit 구성
            .baseUrl(KaKaoTestActivity.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KaKaoAPI_category::class.java)   // 통신 인터페이스를 객체로 생성
        val call = api.getSearchKeyword(KaKaoTestActivity.API_KEY, category_group_code, this.long, this.lat, radius, sort)   // 검색 조건 입력

        // API 서버에 요청
        call.enqueue(object: Callback<ResultCategoryKeyword> {
            override fun onResponse(
                call: Call<ResultCategoryKeyword>,
                response: Response<ResultCategoryKeyword>
            ) {

                // 통신 성공 (검색 결과는 response.body()에 담겨있음)
//                Log.i("Test", "Raw: ${response.raw()}")
//                Log.i("Test", "Body: ${response.body()?.documents?.get(0)?.place_name}")
                //Toast.makeText(applicationContext, "${response.body()?.documents?.size}", Toast.LENGTH_SHORT).show()
                contentData = ArrayList<Content>()
                if (response.body()?.documents?.size!!.toInt() > 0){
                    for (i in 0..response.body()?.documents?.size!!.toInt()-1){
                        //var doc = Jsoup.connect(response.body()?.documents?.get(i)?.place_url.toString()).get()
                        contentData.add(Content(response.body()?.documents?.get(i)?.place_name.toString(), response.body()?.documents?.get(i)?.phone.toString(),
                            response.body()?.documents?.get(i)?.address_name.toString(), response.body()?.documents?.get(i)?.road_address_name.toString(),
                            response.body()?.documents?.get(i)?.distance.toString(), response.body()?.documents?.get(i)?.place_url.toString(),
                            response.body()?.documents?.get(i)?.category_name.toString()))
                    }
                }


//                if (contentData.isEmpty()){
//
//                }else{
//
//                }
//                try {
//                    var doc = Jsoup.connect(response.body()?.documents?.get(0)?.place_url.toString()).get()
//                    var elements = doc.select("div.details_present a span")
//                    for( e in elements){
//                        var url = e.absUrl("style")
//                        Toast.makeText(applicationContext, "${url.toString()}", Toast.LENGTH_SHORT).show()
//                    }
//                } catch (e: Error){
//                    Log.i("Error", "${e}")
//                }
                //var elements = doc.select("div.details_present a span")


                contentAdapter = ContentAdapter(applicationContext, contentData)
                contentRecyclerView.adapter = contentAdapter
                contentAdapter.setItemClickListener(object: ContentAdapter.OnItemClickListener{
                    override fun onClick(v: View, position: Int) {
                        //Toast.makeText(v.context, "${contentData[position].url}", Toast.LENGTH_SHORT).show()
                        val i = Intent(Intent.ACTION_VIEW)
                        i.data = Uri.parse(contentData[position].url.toString())
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
class Content(var placename: String, var phone: String, var addressname: String, var road_add: String, var distance: String, var url: String, var category: String)

class ContentAdapter(val context: Context, private val ContentData: ArrayList<Content>): RecyclerView.Adapter<ContentAdapter.ViewHolder>() {

    inner class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        val place_name = view!!.findViewById<TextView>(R.id.place_name)
        val phone = view!!.findViewById<TextView>(R.id.phone)
        val addressname = view!!.findViewById<TextView>(R.id.address)
        //val road_add = view!!.findViewById<TextView>(R.id.road_address)
        val distance = view!!.findViewById<TextView>(R.id.distance)
        val place_category = view!!.findViewById<TextView>(R.id.category_name)

        fun bind(content: Content, context: Context) {
            place_name!!.text = content.placename
            phone!!.text = content.phone
            place_category!!.text = content.category
            if(content.addressname != "" && content.road_add != ""){
                addressname!!.text = content.addressname +"\n" + content.road_add
            } else if(content.addressname != "" || content.road_add != ""){
                addressname!!.text = content.addressname + content.road_add
            }
            //addressname!!.text = content.addressname +"/n" + content.road_add
            //road_add!!.text = content.road_add
            distance!!.text = content.distance.toString()+"m"
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.searchplace_main_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContentAdapter.ViewHolder, position: Int) {
        holder.bind(ContentData[position], context)
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
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

    override fun getItemCount() = ContentData.size

}

class Category_Menu(var menu: String, var icon: Int)

class CategoryAdapter(val context: Context, private val CategoryData: ArrayList<Category_Menu>):
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>(){

    var rowindex : Int = 0

    inner class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!){
        val menu = view!!.findViewById<TextView>(R.id.category_text)
        val icon = view!!.findViewById<ImageView>(R.id.category_icon)

        fun bind(category: Category_Menu, context: Context, position: Int){
            menu!!.text = category.menu
            icon!!.setImageResource(category.icon)
            if(rowindex == position){
                menu!!.setTextColor(ContextCompat.getColor(context, R.color.black))
            } else {
                menu!!.setTextColor(ContextCompat.getColor(context, R.color.gray))
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.searchplace_category_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(CategoryData[position], context, position)
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

    override fun getItemCount() = CategoryData.size
}