package com.ramez.shopp.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.ramez.shopp.R
import com.ramez.shopp.databinding.ActivityPayWebViewBinding

class PayWebViewActivity : ActivityBase() {
    var webUrl: String? = null
    var successUrl: String? = null
    var failUrl: String? = null

    lateinit var binding: ActivityPayWebViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayWebViewBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        title = getString(R.string.benefitpay)

        val bundle = intent.extras

        if (bundle != null) {
            webUrl = bundle.getString(KEY_WEB_URL)
            successUrl = bundle.getString(KEY_RETURN_SUCCESS_URL)
            failUrl = bundle.getString(KEY_RETURN_FAIL_URL)
        }

        Log.i(javaClass.simpleName, "Log webUrl $webUrl")
        Log.i(javaClass.simpleName, "Log returnSuccessUrl $successUrl")
        Log.i(javaClass.simpleName, "Log returnFailUrl $failUrl")

        binding.webView.webViewClient = Browser()
        binding.webView.settings.loadWithOverviewMode = true
        binding.webView.settings.useWideViewPort = true
        binding.webView.settings.loadsImagesAutomatically = true
        binding.webView.settings.javaScriptEnabled = true

        binding.webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {

                if (binding.progressLY.visibility == View.VISIBLE && progress == 100) {
                    binding.progressLY.visibility = View.GONE
                } else if (binding.progressLY.visibility == View.GONE && progress < 100) {
                    binding.progressLY.visibility = View.VISIBLE
                }
            }
        }
        binding.webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY

        binding.webView.loadUrl(webUrl!!)

        initListeners()

    }

    private fun initListeners() {

        binding.toolBar.backBtn.setOnClickListener {
            finish()

        }


    }

    private inner class Browser : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            if (successUrl != null && url.contains(successUrl!!)) {
                setResult(RESULT_OK)
                finish()
            } else if (failUrl != null && url.contains(failUrl!!)) {
                setResult(RESULT_CANCELED)
                finish()
            } else {
                setResult(RESULT_CANCELED)
                finish()
            }


            return false
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
        }


    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack())
            finish()

    }

    companion object {
        const val KEY_WEB_URL = "key_web_url"
        const val KEY_RETURN_SUCCESS_URL = "key_return_success_url"
        const val KEY_RETURN_FAIL_URL = "key_return_fail_url"
    }


}