package com.android_basic.fecontactapp.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.core.net.toUri
import com.android_basic.fecontactapp.R
import com.android_basic.fecontactapp.adapter.ContactAdapter
import com.android_basic.fecontactapp.database.DatabaseHelper
import com.android_basic.fecontactapp.databinding.ActivityEditProfileBinding
import com.android_basic.fecontactapp.model.Contact

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private var imageContact: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
    }

    override fun onStart() {
        super.onStart()
        initView()  //set view
    }

    private fun initView() {
        //get extra
        val receiveId = intent.getLongExtra(ContactAdapter.KEY_ID,0)
        val receiveName = intent.getStringExtra(ContactAdapter.KEY_NAME)
        val receiveImage = intent.getStringExtra(ContactAdapter.KEY_IMAGE)
        val receivePhone = intent.getStringExtra(ContactAdapter.KEY_PHONE)
        val receiveEmail = intent.getStringExtra(ContactAdapter.KEY_EMAIL)

        //binding view
        binding.textEditName.setText(receiveName.toString())
        binding.textEditPhone.setText(receivePhone.toString())
        binding.textEditEmail.setText(receiveEmail.toString())
        if(receiveImage != ""){
            binding.textFirstChar.visibility = View.GONE
            binding.imageProfileContact.setImageURI(receiveImage?.toUri())
        }else{
            binding.textFirstChar.visibility = View.VISIBLE
            binding.textFirstChar.text = receiveName?.first().toString()
        }

        //back button
        binding.imageBackToProfile.setOnClickListener {
            //start activity
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra(ContactAdapter.KEY_ID,receiveId)
            intent.putExtra(ContactAdapter.KEY_NAME,receiveName)
            intent.putExtra(ContactAdapter.KEY_PHONE,receivePhone)
            intent.putExtra(ContactAdapter.KEY_EMAIL,receiveEmail)
            intent.putExtra(ContactAdapter.KEY_IMAGE,receiveImage)
            startActivity(intent)

            finish()
        }

        //upload image
        binding.imageUploadImage.setOnClickListener {
            chooseImageFromDevice(this)
        }

        //save button
        binding.buttonSave.setOnClickListener {
            val name = binding.textEditName.text.toString()
            val phone = binding.textEditPhone.text.toString()
            val email = binding.textEditEmail.text.toString()
            val image = imageContact

            val dbHelper = DatabaseHelper(this)
            if(image != null){
                val contact = Contact(receiveId,name,phone,email,image)
                dbHelper.updateContact(contact)
                Toast.makeText(this,"Contact information updated!",Toast.LENGTH_SHORT).show()
            }else{
                val contact = Contact(receiveId,name,phone,email,"")
                dbHelper.updateContact(contact)
                Toast.makeText(this,"Contact information updated!",Toast.LENGTH_SHORT).show()
            }

            //start activity
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra(ContactAdapter.KEY_ID,receiveId)
            intent.putExtra(ContactAdapter.KEY_NAME,name)
            intent.putExtra(ContactAdapter.KEY_PHONE,phone)
            intent.putExtra(ContactAdapter.KEY_EMAIL,email)
            intent.putExtra(ContactAdapter.KEY_IMAGE,image)
            startActivity(intent)

            finish()

        }
    }


    private fun chooseImageFromDevice(activity: Activity) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        activity.startActivityForResult(intent, AddContactActivity.PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AddContactActivity.PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            // Image selected, save the URI to the database
            val selectedImageUri = data?.data
            selectedImageUri?.let { uri ->
                binding.imageProfileContact.setImageURI(uri)
                binding.textFirstChar.visibility = View.GONE
                imageContact = getImageUri(uri)
            }
        }
    }
    private fun getImageUri(uri: Uri): String{
        return uri.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        overridePendingTransition(R.anim.slide_out,R.anim.slide_out)
    }
}