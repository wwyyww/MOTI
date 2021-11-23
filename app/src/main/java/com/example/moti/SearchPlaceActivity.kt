package com.example.moti

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_ready_ticket.view.*

class SearchPlaceActivity : AppCompatActivity() {

    lateinit var back : Button
    lateinit var lat : String
    lateinit var long : String
    lateinit var address : String
    lateinit var myplace : TextView

    lateinit var menuRecyclerView: RecyclerView
    lateinit var menuAdapter: CategoryAdapter
    var categoryData = ArrayList<Category_Menu>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_place)

        categoryData.add(Category_Menu("카페", R.drawable.ic_baseline_star_24))
        categoryData.add(Category_Menu("음식점", R.drawable.ic_baseline_star_24))
        categoryData.add(Category_Menu("편의점", R.drawable.ic_baseline_star_24))
        categoryData.add(Category_Menu("마트", R.drawable.ic_baseline_star_24))
        categoryData.add(Category_Menu("관광명소", R.drawable.ic_baseline_star_24))
        categoryData.add(Category_Menu("문화시설", R.drawable.ic_baseline_star_24))
        categoryData.add(Category_Menu("기타", R.drawable.ic_baseline_star_24))

        val actionBar: ActionBar?
        actionBar=supportActionBar
        actionBar?.hide()

        var intent = intent
        lat = intent.getStringExtra("lat").toString()
        long = intent.getStringExtra("long").toString()
        address = intent.getStringExtra("address").toString()

        menuRecyclerView = findViewById(R.id.category_menu)
        menuAdapter = CategoryAdapter(this, categoryData)
        menuRecyclerView.adapter = menuAdapter

        menuAdapter.setItemClickListener(object: CategoryAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {

                Toast.makeText(v.context, "${categoryData[position].menu}", Toast.LENGTH_SHORT).show()
            }
        })

        myplace = findViewById(R.id.myplace)
        myplace!!.text = address

        back = findViewById(R.id.out_btn)
        back.setOnClickListener {
            finish()
        }
    }
}

class Category_Menu(var menu: String, var icon: Int)

class CategoryAdapter(val context: Context, private val CategoryData: ArrayList<Category_Menu>):
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>(){

    inner class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!){
        val menu = view!!.findViewById<TextView>(R.id.category_text)
        val icon = view!!.findViewById<ImageView>(R.id.category_icon)

        fun bind(category: Category_Menu, context: Context){
            menu!!.text = category.menu
            icon!!.setImageResource(category.icon)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.searchplace_category_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(CategoryData[position], context)
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

    override fun getItemCount() = CategoryData.size
}