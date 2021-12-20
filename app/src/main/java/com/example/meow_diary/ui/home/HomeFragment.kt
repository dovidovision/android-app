package com.example.meow_diary.ui.home

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.meow_diary.*
import com.facebook.stetho.Stetho
import kotlinx.android.synthetic.main.fragment_home.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private val pickImage = 100
    private var imageUri: Uri? = null

    lateinit var image_view: ImageView
    lateinit var load_image: TextView
    lateinit var text_view : TextView
    lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        root = inflater.inflate(R.layout.fragment_home, container, false)

        image_view = root.findViewById<ImageView>(R.id.image)
        load_image = root.findViewById<TextView>(R.id.load_image)
        text_view = root.findViewById<TextView>(R.id.text)
        val upload_image = root.findViewById<TextView>(R.id.upload_image)


        Stetho.initializeWithDefaults(root.context)
        (root.context.applicationContext as MasterApplication).createRetrofit()

        load_image.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }

        upload_image.setOnClickListener {
            imageUri?.let { uri ->
                uploadPost(uri);
            }
        }

        checkSelfPermission()
        return root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            image_view.setImageURI(imageUri)
        }
    }

    fun uploadPost(uri: Uri) {
        val bitmap = MediaStore.Images.Media.getBitmap(root.context.getContentResolver(), uri)
        // initialize byte stream
        val stream = ByteArrayOutputStream()
        // compress Bitmap
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        // Initialize byte array
        val bytes = stream.toByteArray()
        // get base64 encoded string
        val sImage = Base64.encodeToString(bytes, Base64.DEFAULT)

        val catImage = CatImage(image = sImage,text="")
        (root.context.applicationContext as MasterApplication).service.uploadImage(catImage)
            .enqueue(
                object : Callback<CatImage> {
                    override fun onResponse(
                        call: Call<CatImage>,
                        response: Response<CatImage>
                    ) {
                        if (response.isSuccessful) {
                            val obj = response.body() as CatImage
                            text_view.setText(obj.text)
                        }


                    }

                    override fun onFailure(call: Call<CatImage>, t: Throwable) {
                        Log.d("retrofit", "ERROR!")
                    }
                })
    }


    //권한에 대한 응답이 있을때 작동하는 함수
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
//권한을 허용 했을 경우
        if (requestCode == 1) {
            val length = permissions.size
            for (i in 0 until length) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
// 동의
                    Log.d("MainActivity", "권한 허용 : " + permissions[i])
                }
            }
        }
    }

    fun checkSelfPermission() {
        var temp = ""
        //파일 읽기 권한 확인
        if (ContextCompat.checkSelfPermission(
                root.context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            temp += Manifest.permission.READ_EXTERNAL_STORAGE.toString() + " "
        }
        //파일 쓰기 권한 확인
        if (ContextCompat.checkSelfPermission(
                root.context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            temp += Manifest.permission.WRITE_EXTERNAL_STORAGE.toString() + " "
        }
        if (TextUtils.isEmpty(temp) == false) {
// 권한 요청
            ActivityCompat.requestPermissions(
                root.context as Activity,
                temp.trim { it <= ' ' }.split(" ".toRegex()).toTypedArray(),
                1
            )
        } else {
// 모두 허용 상태
            Toast.makeText(root.context, "권한을 모두 허용", Toast.LENGTH_SHORT).show()
        }
    }



}



