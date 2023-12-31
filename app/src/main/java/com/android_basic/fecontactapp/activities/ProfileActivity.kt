package com.android_basic.fecontactapp.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.android_basic.fecontactapp.R
import com.android_basic.fecontactapp.adapter.ContactAdapter
import com.android_basic.fecontactapp.database.DatabaseHelper
import com.android_basic.fecontactapp.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding : ActivityProfileBinding
    private lateinit var dbHelper: DatabaseHelper

    companion object {
        const val CALL_PHONE_PERMISSION_REQUEST = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dbHelper = DatabaseHelper(this)
    }

    override fun onStart() {
        super.onStart()
        initView() //init view for activity
    }

    private fun initView() {
        //get extra
        val receiveId = intent.getLongExtra(ContactAdapter.KEY_ID,0)
        val receiveName = intent.getStringExtra(ContactAdapter.KEY_NAME)
        val receiveImage = intent.getStringExtra(ContactAdapter.KEY_IMAGE)
        val receivePhone = intent.getStringExtra(ContactAdapter.KEY_PHONE)
        val receiveEmail = intent.getStringExtra(ContactAdapter.KEY_EMAIL)

        //binding view
        binding.textContactName.text = receiveName
        binding.textPhoneNumber.text = receivePhone
        binding.textEmail.text = receiveEmail

        if(receiveImage != ""){
            binding.textFirstChar.visibility = View.GONE
            binding.imageProfileContact.setImageURI(receiveImage?.toUri())
        }else{
            binding.textFirstChar.visibility = View.VISIBLE
            binding.textFirstChar.text = receiveName?.first().toString()
        }

        //back button
        binding.imageBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        //call button
        binding.floatingButtonCall.setOnClickListener {
            val permission = Manifest.permission.CALL_PHONE

            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, request the permission
                ActivityCompat.requestPermissions(this, arrayOf(permission), CALL_PHONE_PERMISSION_REQUEST)
            } else {
                // Permission already granted, proceed to make a call
                Toast.makeText(this, "Calling....",Toast.LENGTH_SHORT).show()
                val intent = Intent(Intent.ACTION_CALL)
                intent.data = Uri.parse("tel:${receivePhone.toString().trim()}")
                startActivity(intent)
            }

        }

        //edit profile
        binding.textEdit.setOnClickListener {
            val intent = Intent(this,EditProfileActivity::class.java)
            intent.putExtra(ContactAdapter.KEY_ID,receiveId)
            intent.putExtra(ContactAdapter.KEY_NAME,receiveName)
            intent.putExtra(ContactAdapter.KEY_IMAGE,receiveImage)
            intent.putExtra(ContactAdapter.KEY_PHONE,receivePhone)
            intent.putExtra(ContactAdapter.KEY_EMAIL,receiveEmail)
            startActivity(intent)
            finish()
        }

        //remove profile
        binding.textRemove.setOnClickListener {
            removeDialog(receiveId)
        }
    }

    private fun removeDialog(id: Long){
        val removeDialog = Dialog(this)
        removeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        removeDialog.setContentView(R.layout.dialog_remove)

        val textRemove: TextView = removeDialog.findViewById(R.id.text_dialog_delete)
        val textCancel: TextView = removeDialog.findViewById(R.id.text_dialog_cancel)

        textRemove.setOnClickListener {
            Toast.makeText(this,"Remove..",Toast.LENGTH_SHORT).show()
            dbHelper.deleteContact(id)
            finish()
        }

        textCancel.setOnClickListener { removeDialog.dismiss() }
        removeDialog.show()
    }




}