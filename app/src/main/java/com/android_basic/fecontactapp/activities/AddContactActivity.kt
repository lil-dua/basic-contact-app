package com.android_basic.fecontactapp.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Patterns
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
    private var imageContact: String? = null
    companion object{
        const val PICK_IMAGE_REQUEST = 1
    }
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
            chooseImageFromDevice(this)
        }

        //add contact
        binding.buttonAddContact.setOnClickListener {
            loading(true)
            if(validateInput()){    //check validate for input
                val name = binding.inputName.text.toString()
                val phone = binding.inputPhone.text.toString()
                val email = binding.inputEmail.text.toString()
                val image = imageContact
                val dbHelper = DatabaseHelper(this)
                if(image == null){
                    val contact = Contact(id = 1, name = name,phone = phone,email = email,image = "")
                    dbHelper.addContact(contact)
                    Toast.makeText(this,"Add contact successfully!",Toast.LENGTH_SHORT).show()
                }else{
                    val contact = Contact(id = 1 ,name = name,phone = phone,email = email,image = image)
                    dbHelper.addContact(contact)
                    Toast.makeText(this,"Add contact successfully!",Toast.LENGTH_SHORT).show()
                }
                loading(false)
                finish()
            }else{
                Toast.makeText(this,"Add contact failed!",Toast.LENGTH_SHORT).show()
                loading(false)
            }
        }
    }
    private fun validateInput(): Boolean {
        if(binding.inputName.text.isNullOrEmpty()){
            binding.inputName.error = "User name is required!"
            binding.inputName.requestFocus()
            return false
        }else if (binding.inputPhone.text.isNullOrEmpty()){
            binding.inputPhone.error = "Phone number is required!"
            binding.inputPhone.requestFocus()
            return false
        }else if (binding.inputEmail.text.isNullOrEmpty()){
            binding.inputEmail.error = "Email is required!"
            binding.inputEmail.requestFocus()
            return false
        }else if(!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text).matches()) {
            binding.inputEmail.error = "Input must be a email!"
            binding.inputEmail.requestFocus()
            return false
        }
        return true
    }

    private fun chooseImageFromDevice(activity: Activity) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        activity.startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            // Image selected, save the URI to the database
            val selectedImageUri = data?.data
            selectedImageUri?.let { uri ->
                binding.imageProfile.setImageURI(uri)
                binding.textAddImage.visibility = View.GONE
                imageContact = getImageUri(uri)
            }
        }
    }
    private fun getImageUri(uri: Uri): String{
        return uri.toString()
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

    private val pickImage = registerForActivityResult(
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