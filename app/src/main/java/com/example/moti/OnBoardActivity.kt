package com.example.moti

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class Onboard(var keyword1: String, var image1: Int, var keyword2: String, var image2: Int)

class OnboardAdapter(val context: Context, private val OnboardData: ArrayList<Onboard>): RecyclerView.Adapter<OnboardAdapter.ViewHolder>() {


    var rowindex : Int = 0

    inner class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        val key1 = view!!.findViewById<TextView>(R.id.k1)
        val key2 = view!!.findViewById<TextView>(R.id.k2)
        val img1 = view!!.findViewById<ImageView>(R.id.onboard_img)
        val img2 = view!!.findViewById<ImageView>(R.id.onboard_img2)


        fun bind(onboard : Onboard, context: Context, position: Int) {
            key1!!.text = onboard.keyword1
            key2!!.text = onboard.keyword2
            img1!!.setImageResource(onboard.image1)
            img2!!.setImageResource(onboard.image2)

            if(rowindex == position) {
                img1!!.setOnClickListener {
                    key1!!.setTextColor(ContextCompat.getColor(context, R.color.green))

                }
                img2!!.setOnClickListener {
                    key2!!.setTextColor(ContextCompat.getColor(context, R.color.green))

                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.onboard_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: OnboardAdapter.ViewHolder, position: Int) {
        holder.bind(OnboardData[position], context, position)
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

    override fun getItemCount() = OnboardData.size

}


class OnBoardActivity : AppCompatActivity() {

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
            window.statusBarColor = Color.parseColor("#00000000")
            window.navigationBarColor = Color.parseColor("#00000000")
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


    lateinit var onboard_recycle : RecyclerView
    lateinit var onboard_adapter : OnboardAdapter
    var rowindex : Int = 0
    lateinit var start : Button
    var ondata = ArrayList<Onboard>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboard)
        transparentStatusAndNavigation()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val actionBar: ActionBar?
        actionBar=supportActionBar
        actionBar?.hide()

        onboard_recycle = findViewById(R.id.onboard_recycle)
        start = findViewById(R.id.startbtn)

        ondata.add(Onboard("시원시원한", R.drawable.onboard1, "조용한", R.drawable.onboard6))
        ondata.add(Onboard("분위기 있는", R.drawable.onboard3, "즐거운", R.drawable.onboard4))
        ondata.add(Onboard("컬러풀한", R.drawable.onboard5, "자연의", R.drawable.onboard2))

        onboard_adapter = OnboardAdapter(this, ondata)
        onboard_recycle.adapter = onboard_adapter
        onboard_adapter.setItemClickListener(object: OnboardAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                rowindex = position
            }
        })

        start.setOnClickListener {
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}