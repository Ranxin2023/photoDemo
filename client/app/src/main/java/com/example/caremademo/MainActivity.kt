package com.example.caremademo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private val baseUrl="http://192.168.108.1:80"
    private lateinit var photoImageView:ImageView

    private fun takePhoto(){
        val takePhotoButton=findViewById<Button>(R.id.photoButton)
        this.photoImageView=findViewById(R.id.photoView)
        if(ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.CAMERA)!=
            PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(
                Manifest.permission.CAMERA
            ), 100)
        }
        takePhotoButton.setOnClickListener{
//            runOnUiThread{
//                Toast.makeText(applicationContext, "Start taking photo", Toast.LENGTH_SHORT).show()
//            }
            val intent=Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, 100)
        }
    }

    private fun uploadPhoto(){
        val uploadButton=findViewById<Button>(R.id.upload_photo_button)
        uploadButton.setOnClickListener {
//            runOnUiThread{
//                Toast.makeText(applicationContext, "Start upload", Toast.LENGTH_SHORT).show()
//            }
            val profilePhotoBitmap = (this.photoImageView.drawable as BitmapDrawable).bitmap
            // Convert bitmap to byte array
            val byteArrayOutputStream = ByteArrayOutputStream()
            profilePhotoBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            // Create request body
            val MEDIA_TYPE_JPEG = "image/jpeg".toMediaTypeOrNull()
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "image.jpg", RequestBody.create(MEDIA_TYPE_JPEG, byteArray))
                .build()


            Thread{
                // Create request
                val request = Request.Builder()
                    .url(this.baseUrl)
                    .post(requestBody)
                    .build()
                val response: Response
                val client= OkHttpClient()
                val call=client.newCall(request)
                try{
//                    runOnUiThread {
//                        Toast.makeText(applicationContext, "Start response", Toast.LENGTH_SHORT).show()
//                    }
                    response=call.execute()
                    if(response.isSuccessful){
//                        execute the code
                        val serverResponse: String? = response.body?.string()

                        val jsonObjectFromServer = JSONObject(serverResponse?: "")
                        val success = jsonObjectFromServer.getString("success").toBoolean()
                        val failureInformation=jsonObjectFromServer.getString("error msg").toString()
                        if(success){
                            val photoId=jsonObjectFromServer.getString("photo id").toString()
                            Profile.photoId=photoId
                            runOnUiThread {
                                Toast.makeText(
                                    applicationContext,
                                    "Photo Id is: ${Profile.photoId}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        else{
                            runOnUiThread{
                                Toast.makeText(applicationContext, "error message: $failureInformation", Toast.LENGTH_SHORT).show()

                            }
                        }
                    }
                    else{
                        println("Request failed. Status code: ${response.code}")
                    }
                }
                catch (e: IOException){
                    e.printStackTrace()
                }
            }.start()
        }
    }
    private fun fetchPhoto(){
        val enterPhoto=findViewById<Button>(R.id.go_fetch_button)
        enterPhoto.setOnClickListener {
            val intent=Intent(this, FetchPhoto::class.java)
            startActivity(intent)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.takePhoto()
        this.uploadPhoto()
        this.fetchPhoto()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==100 && resultCode == RESULT_OK && data!=null){
            val bundle:Bundle?=data.extras
            val finalPhoto: Bitmap = bundle?.get("data") as Bitmap
            if(finalPhoto!=null){
                photoImageView.setImageBitmap(finalPhoto)
            }
        }
    }
}