package com.eundaeng.schooltable

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast

class AFragment : Fragment()
{
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        val view = inflater.inflate(R.layout.webview, container, false)
        val myWebView = view.findViewById<WebView>(R.id.webView)
        myWebView.apply {
            webViewClient = object : WebViewClient() {

                override fun onReceivedHttpError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    errorResponse: WebResourceResponse?
                ) {
                    super.onReceivedHttpError(view, request, errorResponse)
                    Toast.makeText(
                        activity,
                        "haha",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            settings.javaScriptEnabled = true
            settings.builtInZoomControls = true
            settings.setSupportZoom(true)
            settings.displayZoomControls = false
        }
        myWebView.loadUrl("http://xn--s39aj90b0nb2xw6xh.kr/")
        return view
    }
}