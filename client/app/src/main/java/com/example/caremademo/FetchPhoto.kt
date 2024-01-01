package com.example.caremademo

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.time.LocalTime

class FetchPhoto:AppCompatActivity() {
    private fun fetchPhoto(){
        val fetchButton=findViewById<Button>(R.id.fetch_photo_button)
        fetchButton.setOnClickListener {
            val jsonArr= JSONArray(arrayOf(Profile.photoId))
            val jsonObject= JSONObject()
            jsonObject.put("method", "fetch image")
            jsonObject.put("args", jsonArr)
            val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
            val requestBody=jsonObject.toString().toRequestBody(mediaType)
            Thread{
                val request= Request.Builder()
                    .url(Profile.baseUrl)
                    .post(requestBody)
                    .build()

                val response: Response
                val client= OkHttpClient()
                val call=client.newCall(request)
                try {
                    response = call.execute()
                    if (response.isSuccessful) {
                        // Get the input stream of the image
                        val inputStream = response.body?.byteStream()

                        // Convert InputStream to Bitmap on a background thread
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        runOnUiThread {
                            val imageView = findViewById<ImageView>(R.id.photo_fetched)
                            imageView.setImageBitmap(bitmap)
                        }
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Request failed. Status code: ${response.code}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                catch (e: IOException){
                    e.printStackTrace()
                }
            }.start()
        }
    }
    private fun exit(){
        val exitButton=findViewById<Button>(R.id.exit_button)
        exitButton.setOnClickListener {
            val intent= Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
    private fun uploadPhoto(){
        val goUploadButton=findViewById<Button>(R.id.go_upload_button)
        goUploadButton.setOnClickListener {
            val intent=Intent(this, UploadPhoto::class.java)
            startActivity(intent)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fetch_photo)
        this.fetchPhoto()
        this.exit()
        this.uploadPhoto()
    }
}