package com.android_basic.fecontactapp.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.android_basic.fecontactapp.database.DatabaseHelper
import com.android_basic.fecontactapp.databinding.ActivityAddContactBinding
import com.android_basic.fecontactapp.model.Contact
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException

class AddContactActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddContactBinding
    private var encodedImage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        actions()
    }

    private fun actions() {
        //back button
        binding.imageBackToMain.setOnClickListener { finish() }

        //select image
        binding.layoutImage.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            pickImage.launch(intent)
        }

        //add contact
        binding.buttonAddContact.setOnClickListener {
            loading(true)
            val name = binding.inputName.text.toString()
            val phone = binding.inputPhone.text.toString()
            val email = binding.inputEmail.text.toString()
            val image = encodedImage.toString()
            val dbHelper = DatabaseHelper(this)
            if(image == null){
                val contact = Contact(id = 1, name = name,phone = phone,email = email,image = "")
                dbHelper.addContact(contact)
                Toast.makeText(this,"Add contact successfully!",Toast.LENGTH_SHORT).show()
                loading(false)
            }else{
                val contact = Contact(id = 1 ,name = name,phone = phone,email = email,image = image)
                dbHelper.addContact(contact)
                Toast.makeText(this,"Add contact successfully!",Toast.LENGTH_SHORT).show()
                loading(false)
            }
            finish()
        }
    }


    private fun encodeImage(bitmap: Bitmap): String {
        val previewWidth = 1000
        val previewHeight = bitmap.height * previewWidth / bitmap.width
        val previewBitMap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false)
        val byteArrayOutputStream = ByteArrayOutputStream()
        previewBitMap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        val bytes = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    private val pickImage = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            if (result.data != null) {
                val imageUri = result.data!!.data
                try {
                    val inputStream =
                        contentResolver.openInputStream(imageUri!!)
                    val bitmap =
                        BitmapFactory.decodeStream(inputStream)
                    binding.imageProfile.setImageBitmap(bitmap)
                    binding.textAddImage.visibility = View.GONE
                    encodedImage = encodeImage(bitmap)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun loading(isLoading: Boolean) {
        if (isLoading) {
            binding.buttonAddContact.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.INVISIBLE
            binding.buttonAddContact.visibility = View.VISIBLE
        }
    }

}