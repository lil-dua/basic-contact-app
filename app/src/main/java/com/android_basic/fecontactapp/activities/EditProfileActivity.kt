package com.android_basic.fecontactapp.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.android_basic.fecontactapp.R
import com.android_basic.fecontactapp.adapter.ContactAdapter
import com.android_basic.fecontactapp.database.DatabaseHelper
import com.android_basic.fecontactapp.databinding.ActivityEditProfileBinding
import com.android_basic.fecontactapp.model.Contact
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private var encodedImage: String? = null

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
        if (getBitmapFromEncodedString(receiveImage) != null){
            binding.textFirstChar.visibility = View.GONE
            binding.imageProfileContact.setImageBitmap(getBitmapFromEncodedString(receiveImage))
        }else{
            binding.textFirstChar.text = receiveName?.first().toString()
            binding.textFirstChar.visibility = View.VISIBLE
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
            val intent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            pickImage.launch(intent)
        }

        //save button
        binding.buttonSave.setOnClickListener {
            val name = binding.textEditName.text.toString()
            val phone = binding.textEditPhone.text.toString()
            val email = binding.textEditEmail.text.toString()
            val image = encodedImage.toString()

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

    private fun getBitmapFromEncodedString(encodedImage: String?): Bitmap? {
        return if (encodedImage != null) {
            val bytes = Base64.decode(encodedImage, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } else {
            null
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
                    binding.imageProfileContact.setImageBitmap(bitmap)
                    binding.textFirstChar.visibility = View.GONE
                    encodedImage = encodeImage(bitmap)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        overridePendingTransition(R.anim.slide_out,R.anim.slide_out)
    }
}