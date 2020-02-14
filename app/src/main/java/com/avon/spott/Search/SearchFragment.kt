package com.avon.spott.Search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.avon.spott.Data.SearchRecent
import com.avon.spott.Data.SearchResult
import com.avon.spott.Main.MainActivity
import com.avon.spott.Main.MainActivity.Companion.mToolbar
import com.avon.spott.Main.controlToolbar
import com.avon.spott.R
import com.avon.spott.Utils.logd
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.toolbar.view.*
import org.w3c.dom.Text

class SearchFragment: Fragment(), SearchContract.View, View.OnClickListener {

    private val TAG = "forSearchFragment"

    private lateinit var searchPresenter: SearchPresenter
    override lateinit var presenter: SearchContract.Presenter

    //검색 결과 recyclerview
    private lateinit var resultAdapter: ResultAdapter
    private lateinit var resultLayoutManager: LinearLayoutManager

    //최근 검색 recyclerview
    private lateinit var recentAdapter: RecentAdapter
    private lateinit var recentLayoutManager: LinearLayoutManager

    val searchInterListener = object : searchInter{
        override fun hashItemClick(hash: String) {}
        override fun recentTextClick(recent: String) {}
        override fun userItemClick(userId: Int) {}

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //검색 결과 recyclerview
        resultLayoutManager = LinearLayoutManager(context!!)
        resultAdapter = ResultAdapter(context!!, searchInterListener)

        //최근 검색 recyclerview
        recentLayoutManager = LinearLayoutManager(context!!)
        recentAdapter = RecentAdapter(context!!, searchInterListener)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_search, container, false)

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        init()

        recycler_result_search_f.layoutManager = resultLayoutManager
        recycler_result_search_f.adapter = resultAdapter

        recycler_recent_search_f.layoutManager = recentLayoutManager
        recycler_recent_search_f.adapter = recentAdapter
    }

    override fun onStart() {
        super.onStart()

        //툴바 처리 (뒤로가기 + 검색edit)
        controlToolbar(View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, View.VISIBLE)
        MainActivity.mToolbar.visibility = View.VISIBLE

    }

    override fun onStop() {
        super.onStop()
        val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        recycler_result_search_f.layoutManager = null
        recycler_recent_search_f.layoutManager = null
    }

    fun init(){
        searchPresenter = SearchPresenter(this)

        mToolbar.edit_search_toolbar.addTextChangedListener {
            if(it!!.trim().length>0){
                mToolbar.edit_search_toolbar.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.ic_search_black_24dp, 0, R.drawable.ic_close_black_24dp,0)
                scroll_recent_search_f.visibility = View. GONE
                const_result_search_f.visibility = View.VISIBLE

                presenter.getSearching(getString(R.string.baseurl), it.toString())

            }else{
                mToolbar.edit_search_toolbar.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.ic_search_black_24dp, 0, 0,0)

                scroll_recent_search_f.visibility = View.VISIBLE
                const_result_search_f.visibility = View.GONE
            }
        }

        mToolbar.edit_search_toolbar.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                val DRAWABLE_RIGHT = 2

                when(event?.action){
                    MotionEvent.ACTION_UP ->{
                        if(mToolbar.edit_search_toolbar.compoundDrawables[DRAWABLE_RIGHT]!=null){
                            if(event.rawX >= (mToolbar.edit_search_toolbar.right - mToolbar.edit_search_toolbar.compoundDrawables[DRAWABLE_RIGHT].bounds.width())){
                                mToolbar.edit_search_toolbar.setText("")
                                return v?.onTouchEvent(event) ?: true
                            }
                        }

                    }
                }
                return v?.onTouchEvent(event) ?: false
            }
        })

    }

    override fun onClick(v: View?) {
        when(v?.id){

        }
    }

    override fun addResultItems(resultItems:ArrayList<SearchResult>){
        resultAdapter.addItemsAdapter(resultItems)
        resultAdapter.notifyDataSetChanged()
    }

    override fun clearResultItems(){
        resultAdapter.clearItemsAdapter()
        resultAdapter.notifyDataSetChanged()
    }

    override fun addRecentItems(recentItems:ArrayList<SearchRecent>){
        recentAdapter.addItemsAdapter(recentItems)
        recentAdapter.notifyDataSetChanged()
    }

    override fun clearRecentItems(){
        recentAdapter.clearItemsAdapter()
        recentAdapter.notifyDataSetChanged()
    }

    interface searchInter{
        fun hashItemClick(hash:String)
        fun userItemClick(userId:Int)
        fun recentTextClick(recent:String)
    }

    inner class ResultAdapter(val context: Context, val searchInterListnener:searchInter):RecyclerView.Adapter<ResultAdapter.ViewHolder>(){

        private var itemsList = ArrayList<SearchResult>()

        override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): ResultAdapter.ViewHolder {
            val view =  LayoutInflater.from(context).inflate(R.layout.item_search_result, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return itemsList.size
        }

        fun addItemsAdapter(searchItems:ArrayList<SearchResult>){
            itemsList.addAll(searchItems)
        }

        fun clearItemsAdapter(){
            itemsList.clear()
        }

        override fun onBindViewHolder(holder: ResultAdapter.ViewHolder, position: Int) {
            itemsList[position].let{
                if(itemsList[position].isHash){
                    holder.userPhoto.visibility = View.GONE
                    holder.bigHash.visibility = View.VISIBLE
                    holder.resultText.text = it.tag

                    holder.itemView.setOnClickListener{
                        searchInterListnener.hashItemClick(itemsList[position].tag!!)
                    }

                }else{
                    holder.userPhoto.visibility = View.VISIBLE
                    holder.bigHash.visibility = View.GONE
                    holder.resultText.text = it.userName

                    Glide.with(holder.itemView.context)
                        .load(it.userPhoto)
                        .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                        .error(android.R.drawable.stat_notify_error)
                        .into(holder.userPhoto)

                    holder.itemView.setOnClickListener{
                        searchInterListnener.userItemClick(itemsList[position].userId!!)
                    }

                }
            }

        }

        inner class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
            val userPhoto =  itemView.findViewById<ImageView>(R.id.img_result_search_i)
            val resultText = itemView.findViewById<TextView>(R.id.text_result_search_i)
            val bigHash = itemView.findViewById<TextView>(R.id.text_hash_result_search_i)
        }


    }

    inner class RecentAdapter(val context: Context, val searchInterListnener: searchInter):RecyclerView.Adapter<RecentAdapter.ViewHolder>(){
        private var itemsList = ArrayList<SearchRecent>()

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): RecentAdapter.ViewHolder {
            val view =  LayoutInflater.from(context).inflate(R.layout.item_search_recent, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return itemsList.size
        }

        fun addItemsAdapter(searchItems:ArrayList<SearchRecent>){
            itemsList.addAll(searchItems)
        }

        fun clearItemsAdapter(){
            itemsList.clear()
        }

        override fun onBindViewHolder(holder: RecentAdapter.ViewHolder, position: Int){
            itemsList[position].let{
                holder.recentText.text = it.recentString

                holder.close.setOnClickListener {
                    // 최근 검색어 삭제 눌렀을 때
                }

                holder.itemView.setOnClickListener{
                    // 최근 검색어 아이템 눌렀을 때
                }
            }
        }

        inner class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
            val recentText = itemView.findViewById<TextView>(R.id.text_recent_search_i)
            val close = itemView.findViewById<ImageButton>(R.id.imgbtn_delete_recent_search_i)
        }

    }
}