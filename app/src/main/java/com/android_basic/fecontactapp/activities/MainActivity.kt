package com.android_basic.fecontactapp.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.android_basic.fecontactapp.R
import com.android_basic.fecontactapp.adapter.OuterContactAdapter
import com.android_basic.fecontactapp.database.DatabaseHelper
import com.android_basic.fecontactapp.databinding.ActivityMainBinding
import com.android_basic.fecontactapp.model.Contact
import com.android_basic.fecontactapp.model.ContactGroup

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: OuterContactAdapter
    private lateinit var dbHelper: DatabaseHelper

    companion object{
        const val READ_EXTERNAL_STORAGE = 102
        const val WRITE_EXTERNAL_STORAGE = 103
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkPermission()
    }

    override fun onStart() {
        super.onStart()
        initView()  //init view for activity
        actions()  //set action for even click
    }

    private fun initView() {
        dbHelper = DatabaseHelper(this)
        val data = dbHelper.getAllContacts()
        val dataGroupContact = groupContactByFirstCharacter(data)
        adapter = OuterContactAdapter(this,dataGroupContact)
        binding.recycleViewContacts.layoutManager = LinearLayoutManager(this)
        binding.recycleViewContacts.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun actions() {

        binding.imageSearch.setOnClickListener {
            startActivity(Intent(this,SearchActivity::class.java))
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }


        binding.floatingButtonAddContact.setOnClickListener {
            val intent = Intent(this,AddContactActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }
    }

    private fun groupContactByFirstCharacter(listContact: List<Contact>): List<ContactGroup> {
        val contactsMap = mutableMapOf <Char,MutableList<Contact>>()

        for (contact in listContact){
            val firstChar = contact.name.first().uppercaseChar()
            if(!contactsMap.containsKey(firstChar)){
                contactsMap[firstChar] = mutableListOf()
            }
            contactsMap[firstChar]?.add(contact)
        }

        // Sort keys (characters) in A-Z order
        val sortedKeys = contactsMap.keys.sorted()

        return sortedKeys.map { key ->
            ContactGroup(key, contactsMap[key] ?: emptyList())
        }
    }


    private fun checkPermission(){
        val permissionRead = Manifest.permission.READ_EXTERNAL_STORAGE
        val permissionWrite = Manifest.permission.WRITE_EXTERNAL_STORAGE

        if (ContextCompat.checkSelfPermission(this, permissionRead) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request the permission
            ActivityCompat.requestPermissions(this, arrayOf(permissionRead), READ_EXTERNAL_STORAGE)
        }

        if (ContextCompat.checkSelfPermission(this, permissionWrite) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request the permission
            ActivityCompat.requestPermissions(this, arrayOf(permissionWrite), WRITE_EXTERNAL_STORAGE)
        }
    }
}