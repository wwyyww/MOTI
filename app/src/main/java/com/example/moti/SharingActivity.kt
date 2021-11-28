package com.example.moti

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_hashtag.*
import kotlinx.android.synthetic.main.activity_sharing.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


@Suppress("DEPRECATION")
class SharingActivity: AppCompatActivity() {

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

    //db용
    private var auth : FirebaseAuth? = null
    private lateinit var database: DatabaseReference
    private val CurrentUser = FirebaseAuth.getInstance().currentUser
    val uid = CurrentUser?.uid
    private lateinit var pushRef:DatabaseReference

    var selectList=ArrayList<String>()
    var postHashList= hashMapOf<String, String>()

    //recycler view
    lateinit var sharingtagAdapter: SharingTagAdapter

    var myPost=Post()
    var recordKey=String()

    //달력
    var mCalendar = Calendar.getInstance()
    lateinit var todayDate:String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sharing)

        transparentStatusAndNavigation()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT


        //action bar 숨기기
        var actionBar: ActionBar?
        actionBar=supportActionBar
        actionBar?.hide()

        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference
        todayDate = (mCalendar.get(Calendar.YEAR)).toString() + "/" + (mCalendar.get(Calendar.MONTH) + 1).toString() +
                "/" + (mCalendar.get(Calendar.DAY_OF_MONTH)).toString()
        recordKey= intent.getStringExtra("pushKey")!!
        var sharedPlaces = intent.getParcelableExtra<Post>("sharing")

        myPost.recordId=recordKey

        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 EE요일")
        val formattedDate = current.format(formatter)

        //2021년 11월 26일 금요일
        sharing_date_textview.text=formattedDate

//        sharing_course_textview.text=sharedPlaces!!.title as Editable
        sharing_course_textview.setText("${sharedPlaces!!.title}")
        sharingtagAdapter= SharingTagAdapter(this, selectList)
        sharing_recyclerview.adapter=sharingtagAdapter


        val layout=LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        sharing_recyclerview.layoutManager = layout



        sharing_button.setOnClickListener {


            pushRef=database.child("community").push()
            pushRef.child("date").setValue(todayDate)
            for (i in postHashList){
                pushRef.child("hashtag/${i.key}").setValue(i.value)
            }
            pushRef.child("heart").setValue(0)
            pushRef.child("postId").setValue("${pushRef.key}")
            pushRef.child("sharedNum").setValue(0)
            pushRef.child("recordId").setValue(recordKey)
            pushRef.child("title").setValue(sharing_course_textview.text.toString())
            pushRef.child("riderId").setValue(uid)
            pushRef.child("photoUrl").setValue("imsiurl")
            pushRef.child("content").setValue(sharing_post_textview.text.toString())

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)


        }

        sharing_search_imgview.setOnClickListener {
            val intent = Intent(this, SelectHashtag::class.java)
            startActivityForResult(intent, 2000)
        }

        sharing_before_imgview.setOnClickListener {
            finish()
        }



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d("sharing", "onActivityResult")

        if (requestCode == 2000 && resultCode == RESULT_OK) {
            selectList= data?.getStringArrayListExtra("hashtagList")!!
            sharingtagAdapter.updateHashtagList(selectList)
            postHashList= data?.getSerializableExtra("mapHashList") as HashMap<String, String>
            Log.d("sharing", "receive posthash $postHashList")

        }else{
            Log.d("sharing", "no data")
        }

    }




}


public class SelectHashtag : Activity() {

    var hashtagList=ArrayList<hashtag>()
    var selectList=ArrayList<String>()
    var mapHashList= hashMapOf<String, String>()

    //달력
    var mCalendar = Calendar.getInstance()
    lateinit var todayDate:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_hashtag)

        setHashtag()


        hashtag_check_button.setOnClickListener{
            var myintent=Intent()

            myintent.putExtra("hashtagList", selectList)
            myintent.putExtra("mapHashList", mapHashList)

            setResult(RESULT_OK, myintent)
            finish()
        }





    }

    fun setHashtag(){

        for (i in 1..5){
            var tag = hashtag()
            var getID = resources.getIdentifier("hashtag_category${i}_btn", "id", "com.example.moti")
            var test=findViewById<Button>(getID)

            tag.text="${test.text}"
            hashtagList.add(tag)
            test.setOnClickListener {
                hashtagList[i-1].select=!hashtagList[i-1].select
                if (hashtagList[i-1].select){
                    test.background=ContextCompat.getDrawable(this, R.drawable.hashtag_select)
                    test.setTextColor(Color.parseColor("#0BE795"))
                    selectList.add("${test.text}")
                    mapHashList["c${i}"]="${test.text}"
                }else{
                    test.background=ContextCompat.getDrawable(this, R.drawable.hashtag)
                    selectList.remove("${test.text}")
                    test.setTextColor(Color.parseColor("#10111A"))
                    Log.d("sharing", "$selectList")
                    mapHashList.remove("c${i}")

                }
            }
//            Log.d("sharing", "check button id ${test.text}")
        }

        for (i in 1..3){
            var tag = hashtag()
            var getID = resources.getIdentifier("hashtag_mood${i}_btn", "id", "com.example.moti")
            var test=findViewById<Button>(getID)
            tag.text="${test.text}"
            hashtagList.add(tag)
            test.setOnClickListener {
                hashtagList[i+4].select=!hashtagList[i+4].select
                if (hashtagList[i+4].select){
                    test.background=ContextCompat.getDrawable(this, R.drawable.hashtag_select)
                    test.setTextColor(Color.parseColor("#0BE795"))
                    selectList.add("${test.text}")
                    mapHashList["m${i}"]="${test.text}"

                }else{
                    test.background=ContextCompat.getDrawable(this, R.drawable.hashtag)
                    selectList.remove("${test.text}")
                    mapHashList.remove("m${i}")
//                    Log.d("sharing", "$selectList")
                }
            }
        }




    }



}

data class hashtag(
    var text: String? = null,
    var select: Boolean = false,
)


class SharingTagAdapter(private val context: Context, private var hashtagList:ArrayList<String>)
    : RecyclerView.Adapter<SharingTagAdapter.CustomViewHolder>(){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SharingTagAdapter.CustomViewHolder {
        Log.d("firebaseMAdapter1",hashtagList.toString())
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sharing_hashtag_item,parent,false)
        return CustomViewHolder(view)

    }

    override fun onBindViewHolder(holder: SharingTagAdapter.CustomViewHolder, position: Int) {

        holder.sharingitem_category_text.text = hashtagList[position]
        Log.d("firebaseMAdapter2",hashtagList.get(position))

        holder.sharingitem_close_imgview.setOnClickListener {
            hashtagList.removeAt(position)
            updateHashtagList(hashtagList)
            Log.d("firebaseMAdapter2", "remove 확인 $hashtagList")

        }


    }



    override fun getItemCount(): Int {
        return hashtagList.size
    }

    class CustomViewHolder(itemView : View): RecyclerView.ViewHolder(itemView) {
        var sharingitem_category_text = itemView.findViewById<TextView>(R.id.sharingitem_category_text)
        var sharingitem_close_imgview = itemView.findViewById<ImageView>(R.id.sharingitem_close_imgview)

    }

    fun updateHashtagList(tagList : ArrayList<String>){
        hashtagList=tagList
        this.notifyDataSetChanged()
    }

}