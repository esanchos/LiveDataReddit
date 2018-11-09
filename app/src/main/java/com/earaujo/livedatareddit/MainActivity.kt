package com.earaujo.livedatareddit

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
//import kotlinx.android.synthetic.main.auth_dialog.*
import kotlinx.android.synthetic.main.content_main.*

import org.json.JSONException

import java.util.UUID

class MainActivity : AppCompatActivity() {

//    internal var web: WebView
//    internal var auth: Button
    lateinit var pref: SharedPreferences
//    internal var Access: TextView
    lateinit var auth_dialog: Dialog
    internal var DEVICE_ID = UUID.randomUUID().toString()
    var authCode: String? = ""
    internal var authComplete = false

    internal var resultIntent = Intent()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)


        pref = getSharedPreferences("AppPref", Context.MODE_PRIVATE)
//        Access = findViewById(R.id.Access) as TextView
//        auth = findViewById(R.id.auth) as Button
        auth.setOnClickListener {
            // TODO Auto-generated method stub
            auth_dialog = Dialog(this@MainActivity)
            auth_dialog.setContentView(R.layout.auth_dialog)
            val webv = auth_dialog.findViewById(R.id.webv) as WebView
            webv.settings.javaScriptEnabled = true
            val url =
                "$OAUTH_URL?client_id=$CLIENT_ID&response_type=code&state=TEST&redirect_uri=$REDIRECT_URI&scope=$OAUTH_SCOPE"
            webv.loadUrl(url)
            Toast.makeText(applicationContext, "" + url, Toast.LENGTH_LONG).show()

            webv.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    view.loadUrl(url)
                    return true
                }

                override fun onPageStarted(view: WebView, url: String, favicon: Bitmap) {
                    super.onPageStarted(view, url, favicon)

                }

                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)

                    /*if (url.contains("?code=") || url.contains("&code=")) {

                        val uri = Uri.parse(url)
                        authCode = uri.getQueryParameter("code")
                        Log.i("NUNES", "CODE : " + authCode!!)
                        authComplete = true
                        resultIntent.putExtra("code", authCode)
                        this@MainActivity.setResult(Activity.RESULT_OK, resultIntent)
                        setResult(Activity.RESULT_CANCELED, resultIntent)
                        val edit = pref!!.edit()
                        edit.putString("Code", authCode)
                        edit.commit()
                        auth_dialog.dismiss()
                        Toast.makeText(
                            applicationContext,
                            "Authorization Code is: " + pref.getString("Code", "")!!,
                            Toast.LENGTH_SHORT
                        ).show()

                        try {
                            RedditRestClient(applicationContext).getToken(TOKEN_URL, GRANT_TYPE2, DEVICE_ID)
                            Toast.makeText(
                                applicationContext,
                                "Auccess Token: " + pref.getString("token", "")!!,
                                Toast.LENGTH_SHORT
                            ).show()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    } else if (url.contains("error=access_denied")) {
                        Log.i("", "ACCESS_DENIED_HERE")
                        resultIntent.putExtra("code", authCode)
                        authComplete = true
                        setResult(Activity.RESULT_CANCELED, resultIntent)
                        Toast.makeText(applicationContext, "Error Occured", Toast.LENGTH_SHORT).show()

                        auth_dialog.dismiss()
                    }*/
                }
            }
            auth_dialog.show()
            auth_dialog.setTitle("Authorize")
            auth_dialog.setCancelable(true)
        }

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener(View.OnClickListener { view ->
            Snackbar.make(view, "", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)

    }

    companion object {

        private val CLIENT_ID = "osHmUl_xltRuAQ"
        private val CLIENT_SECRET = ""
        private val REDIRECT_URI = "https://thankful-sprite.glitch.me/reddit"
        private val GRANT_TYPE = "https://oauth.reddit.com/grants/installed_client"
        private val GRANT_TYPE2 = "authorization_code"
        private val TOKEN_URL = "access_token"
        private val OAUTH_URL = "https://www.reddit.com/api/v1/authorize"
        private val OAUTH_SCOPE = "read"
        private val DURATION = "permanent"
    }
}


