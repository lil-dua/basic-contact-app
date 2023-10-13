package com.android_basic.fecontactapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.android_basic.fecontactapp.adapter.ContactAdapter
import com.android_basic.fecontactapp.database.DatabaseHelper
import com.android_basic.fecontactapp.databinding.ActivitySearchBinding
import com.android_basic.fecontactapp.model.Contact
import java.util.Locale

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()  //init view
    }

    private fun initView() {
        //cancel text
        binding.textCancel.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        //binding recycle view
        val dbHelper = DatabaseHelper(this)
        val contacts = dbHelper.getAllContacts()
        val adapter = ContactAdapter(this, contacts)
        binding.recycleViewContacts.layoutManager = LinearLayoutManager(this)
        binding.recycleViewContacts.adapter = adapter

        binding.searchView.clearFocus()
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filterListContact(newText,contacts,adapter)
                return true
            }
        })
    }

    private fun filterListContact(text: String,contacts: List<Contact>, adapter: ContactAdapter) {
        val filterList = mutableListOf<Contact>()
        for (contact in contacts){
            if(contact.name.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault()))){
                filterList.add(contact)
            }
        }

        if (filterList.isEmpty()){
            Toast.makeText(this,"No information of data", Toast.LENGTH_SHORT).show()
        }else{
            adapter.setFilteredList(filterList)
        }
    }


}