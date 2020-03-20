package com.avon.spott.Notice

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.avon.spott.R
import kotlinx.android.synthetic.main.activity_webview.*
import kotlinx.android.synthetic.main.toolbar.view.*

class NoticeDetailActivity  : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        //툴바 타이틀 넣기
        include_toolbar_webview_a.text_title_toolbar.text = getString(R.string.text_notice)

        include_toolbar_webview_a.img_back_toolbar.setOnClickListener {
            onBackPressed()
        }

        webview_webview_a.loadUrl(getString(R.string.baseurl)+"/"+intent.getStringExtra("noticeRestUrl"))


    }
}