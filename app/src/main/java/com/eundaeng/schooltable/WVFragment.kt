package com.eundaeng.schooltable

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class WVFragment: Fragment() {

    lateinit var webView: MyWebView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        initWebView()

        return webView // 나타나는 뷰를 웹뷰로 설정한다.
    }

    //웹뷰 초기화 메서드.
    private fun initWebView() {
        webView = MyWebView(requireContext(), "http://comci.kr:4082/st#")
    }

}