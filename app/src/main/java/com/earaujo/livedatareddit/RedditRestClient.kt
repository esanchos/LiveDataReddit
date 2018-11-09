package com.earaujo.livedatareddit

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams

import org.json.JSONException
import org.json.JSONObject

import cz.msebera.android.httpclient.Header
import cz.msebera.android.httpclient.message.BasicHeader

/**
 * Created by Pratik Agrawal on 11/20/2015.
 */
class RedditRestClient internal constructor(internal var context: Context) {
    lateinit var pref: SharedPreferences
    lateinit var token: String

    @Throws(JSONException::class)
    fun getToken(relativeUrl: String, grant_type: String, device_id: String) {
        client.setBasicAuth(CLIENT_ID, CLIENT_SECRET)
        pref = context.getSharedPreferences("AppPref", Context.MODE_PRIVATE)
        val code = pref.getString("Code", "")

        val requestParams = RequestParams()
        requestParams.put("code", code)
        requestParams.put("grant_type", grant_type)
        requestParams.put("redirect_uri", REDIRECT_URI)

        post(relativeUrl, requestParams, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, response: JSONObject) {
                // If the response is JSONObject instead of expected JSONArray
                Log.i("response", response.toString())
                try {
                    token = response.getString("access_token").toString()
                    val edit = pref.edit()
                    edit.putString("token", token)
                    edit.commit()
                    Log.i("Access_token", pref.getString("token", ""))
                } catch (j: JSONException) {
                    j.printStackTrace()
                }

            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, throwable: Throwable, errorResponse: JSONObject) {
                //super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.i("statusCode", "" + statusCode)


            }
        })

    }


    fun revokeToken() {
        client.setBasicAuth(CLIENT_ID, CLIENT_SECRET)
        pref = context.getSharedPreferences("AppPref", Context.MODE_PRIVATE)
        val access_token = pref.getString("token", "")

        val requestParams = RequestParams()
        requestParams.put("token", access_token)
        requestParams.put("token_type_hint", "access_token")

        post("revoke_token", requestParams, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, response: JSONObject) {
                // If the response is JSONObject instead of expected JSONArray
                Log.i("response", response.toString())
                val edit = pref.edit()
                edit.remove(token)
                edit.commit()

            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, throwable: Throwable, errorResponse: JSONObject) {
                //super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.i("statusCode", "" + statusCode)
            }
        })
    }

    fun getUsername() {
        Log.i("token", pref.getString("token", ""))
        //  client.addHeader("Authorization", "bearer " + pref.getString("token", ""));
        // client.addHeader("User-Agent", "Redditsavedoffline/0.1 by pratik");

        val headers = arrayOfNulls<Header>(2)
        headers[0] = BasicHeader("User-Agent", "myRedditapp/0.1 by redditusername")
        headers[1] = BasicHeader("Authorization", "bearer " + pref.getString("token", "")!!)

        client.get(context, "https://oauth.reddit.com/api/v1/me", headers, null, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>, response: JSONObject) {
                Log.i("response", response.toString())
                try {
                    val username = response.getString("name").toString()
                    val edit = pref.edit()
                    edit.putString("username", username)
                    edit.commit()
                } catch (j: JSONException) {
                    j.printStackTrace()
                }

            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, throwable: Throwable, errorResponse: JSONObject) {
                Log.i("response", errorResponse.toString())
                Log.i("statusCode", "" + statusCode)
            }
        })
    }

    companion object {
        private val CLIENT_ID = "YOUR CLIENT_ID"
        private val CLIENT_SECRET = ""
        private val BASE_URL = "https://www.reddit.com/api/v1/"
        private val REDIRECT_URI = "YOUR reddit_uri(as per your reddit app preferences)"
        private val client = AsyncHttpClient()

        operator fun get(url: String, params: RequestParams, responseHandler: AsyncHttpResponseHandler) {
            client.get(getAbsoluteUrl(url), params, responseHandler)
        }

        fun post(url: String, params: RequestParams, responseHandler: AsyncHttpResponseHandler) {

            client.post(getAbsoluteUrl(url), params, responseHandler)
        }

        private fun getAbsoluteUrl(relativeUrl: String): String {
            return BASE_URL + relativeUrl
        }
    }

}
