package com.avon.spott

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.avon.spott.main.MainActivity
import com.avon.spott.main.MainActivity.Companion.mToolbar
import com.avon.spott.main.controlToobar
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_mypage.*
import kotlinx.android.synthetic.main.fragment_mypage.view.*
import kotlinx.android.synthetic.main.toolbar.view.*


class MypageFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root =  inflater.inflate(R.layout.fragment_mypage, container, false)

        val topButtonsListner = View.OnClickListener {
            if(it.id == R.id.imgbtn_grid_mypage_f) {
                root.imgbtn_grid_mypage_f.isSelected= true
                root.imgbtn_map_mypage_f.isSelected = false
            }else{
                root.imgbtn_grid_mypage_f.isSelected=false
                root.imgbtn_map_mypage_f.isSelected = true
            }
        }

        root.imgbtn_grid_mypage_f.setOnClickListener(topButtonsListner)
        root.imgbtn_map_mypage_f.setOnClickListener(topButtonsListner)
        root.imgbtn_grid_mypage_f.performClick()

        /////임시 테스트////////
        root.btn_photo_mypage_f.setOnClickListener {
            findNavController().navigate(R.id.action_mypageFragment_to_photo)
        }

        mToolbar.img_noti_toolbar.setOnClickListener {
            findNavController().navigate(R.id.action_mypageFragment_to_alarmFragment)
        }

        ///////////////////////

        return root
    }

    override fun onStart() {
        super.onStart()
             //-----임시 데이터-----------------------------
        Glide.with(this)
             .load(R.mipmap.ic_launcher)
             .into(mToolbar.img_profile_toolbar)

        mToolbar.text_name_toolbar.text="MyNickName"
        //--------------------------------------------
        controlToobar(View.GONE, View.VISIBLE, View.VISIBLE, View.GONE, View.GONE, View.VISIBLE, View.VISIBLE)
        mToolbar.visibility = View.VISIBLE

    }

}





