package com.jskaleel.fbcommentssamp

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient

class MainActivity : AppCompatActivity() {

    private var fbCommentsWebView: WebView? = null
    private val appId = "YOUR_APP_ID"
    private val langCode = "en_US"
    private val mappingUrl = "https://your_mapping_url"
    private val numPosts = 10
    private val colorScheme = "dark"/*light*/

    @SuppressLint("SetJavaScriptEnabled", "AddJavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fbHtmlData1 = ("<html><head><meta name=\"viewport\" content=\"width=device-width, " +
                "initial-scale=1, maximum-scale=1, user-scalable=0\"></head><body style=\"background-color:#000000\">" +
                "<div id=\"fb-root\"></div><script>(function(d, s, id) {  var js, fjs = d.getElementsByTagName(s)[0];  " +
                "if (d.getElementById(id)) return;  js = d.createElement(s); js.id = id;  js.src = \"//connect.facebook.net/"
                + langCode + "/sdk.js#xfbml=1&version=v3.2&appId=" + appId + "\";  fjs.parentNode.insertBefore(js, fjs);}" +
                "(document, 'script', 'facebook-jssdk'));</script><div class=\"fb-comments\" data-href=\"")
        val fbHtmlData2 = "\" data-numposts=" + numPosts + " colorscheme=" + colorScheme + " data-mobile=\"true\" data-width=\"100%\"></div></body><script>window.fbAsyncInit = " +
                "function(){FB.Event.subscribe('xfbml.render', function(response){JSInterface.commentLoaded();});" +
                "FB.Event.subscribe('comment.create', function(response){JSInterface.commentAdded(response.commentID);});" +
                "FB.Event.subscribe('comment.remove', function(response){JSInterface.commentRemoved(response.commentID);});};</script></html>"
        val domainName = "YOUR_DOMAIN_NAME"

        fbCommentsWebView = findViewById(R.id.fbComments)

        fbCommentsWebView!!.webChromeClient = WebChromeClient()
        fbCommentsWebView!!.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
            }

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap) {
                super.onPageStarted(view, url, favicon)
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (url.contains("https://www.facebook.com/plugins/close_popup.php")) {
                    fbCommentsWebView!!.loadDataWithBaseURL(domainName,
                            fbHtmlData1 + mappingUrl + fbHtmlData2,
                            "text/html", null, null)
                } else if (url.contains("fb_comment_id")) {
                    return true
                } else {
                    view.loadUrl(url)
                }
                return true
            }

            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.loadUrl(request.url.toString())
                }
                return super.shouldOverrideUrlLoading(view, request)
            }
        }

        val webSettings = fbCommentsWebView!!.settings
        webSettings.javaScriptEnabled = true
        if (Build.VERSION.SDK_INT >= 21) {
            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            CookieManager.getInstance().setAcceptThirdPartyCookies(fbCommentsWebView, true)
        }
        val jsInterface = JavaScriptInterface()
        fbCommentsWebView!!.addJavascriptInterface(jsInterface, "JSInterface")

        fbCommentsWebView!!.loadDataWithBaseURL(domainName,
                fbHtmlData1 + mappingUrl + fbHtmlData2,
                "text/html", null, null)
    }

    override fun onDestroy() {
        super.onDestroy()
        fbCommentsWebView!!.clearCache(true)
    }

    private inner class JavaScriptInterface internal constructor() {

        @JavascriptInterface
        fun commentAdded(commentId: String) {
            //Triggered when comment added by user
        }

        @JavascriptInterface
        fun commentLoaded() {

        }

        @JavascriptInterface
        fun commentRemoved(commentId: String) {
            //Triggered when comment detelted by user
        }
    }


}
