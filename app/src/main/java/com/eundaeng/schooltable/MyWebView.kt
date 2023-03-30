package com.eundaeng.schooltable

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient


/*
* 웹뷰 클래스
* url과 context를 매개변수로 받아서 웹뷰를 띄워준다.
* */

@SuppressLint("SetJavaScriptEnabled", "ViewConstructor")
class MyWebView(context: Context, url: String): WebView(context) {

    init {
        settings.apply {
            javaScriptEnabled = true //자바스크립트 허용.
            //javaScriptCanOpenWindowsAutomatically = true // 웹 뷰에서 창을 자동으로 열 수 있도록 한다.
            setSupportMultipleWindows(true) //웹뷰에서 새 창을 열 수 있도록 허용하는 메서드.
            webChromeClient = WebChromeClient()
        }

        webViewClient = object: WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return false // 브라우저를 사용하지 않고, 웹뷰에서 URL을 열도록 한다.
            }
        }

        loadUrl(url) // 입력받은 url을 웹뷰에서 로드한다.
    }
}