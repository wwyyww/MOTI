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
import androidx.recyclerview.widget.RecyclerView
import com.skt.Tmap.TMapView
import kotlinx.android.synthetic.main.activity_main.*

class CommunityMain: AppCompatActivity() {

    lateinit var edit_searchBar : EditText
    
    var tmapview: TMapView? = null

    lateinit var searchPlace : PoiItem

    lateinit var hashtagRecyclerView: RecyclerView
    lateinit var hashtagAdapter: CategoryAdapter
    var hashtagData = ArrayList<Category_Menu>()

    lateinit var communityRecyclerView: RecyclerView
    lateinit var communityAdapter: ContentAdapter
    var communityData = ArrayList<Content>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community_main)

        hashtagRecyclerView = findViewById(R.id.hashtag)

        communityRecyclerView = findViewById(R.id.community_main_contents)

        edit_searchBar = findViewById(R.id.edit_searchBar)


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

        val actionBar: ActionBar?
        actionBar=supportActionBar
        actionBar?.hide()


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
