package com.android_basic.fecontactapp.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.android_basic.fecontactapp.R
import com.android_basic.fecontactapp.activities.ProfileActivity
import com.android_basic.fecontactapp.model.Contact
import com.makeramen.roundedimageview.RoundedImageView

/***
 * Created by HoangRyan aka LilDua on 10/12/2023.
 */
class ContactAdapter(
    private val context: Context,
    private var listContact: List<Contact>
) : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    companion object{
        const val KEY_ID = "id"
        const val KEY_NAME = "name"
        const val KEY_IMAGE = "image"
        const val KEY_PHONE = "phone"
        const val KEY_EMAIL = "email"
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textContact: TextView = itemView.findViewById(R.id.text_name_contact)
        val textPhone: TextView = itemView.findViewById(R.id.text_phone_contact)
        val imageContact: RoundedImageView = itemView.findViewById(R.id.image_contact)
        val textImage: TextView = itemView.findViewById(R.id.text_first_char)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listContact.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listContact[position]
        holder.textContact.text = data.name
        holder.textPhone.text = data.phone
        holder.textImage.text = data.name[0].toString()
        if (data.image != ""){
            holder.imageContact.setImageURI(data.image.toUri())
            holder.textImage.visibility = View.GONE
        }else{
            holder.textImage.visibility = View.VISIBLE
        }

        //set action
        holder.itemView.setOnClickListener {
            val intent = Intent(context,ProfileActivity::class.java)
            intent.putExtra(KEY_ID,data.id)
            intent.putExtra(KEY_NAME,data.name)
            intent.putExtra(KEY_IMAGE,data.image)
            intent.putExtra(KEY_PHONE,data.phone)
            intent.putExtra(KEY_EMAIL,data.email)
            context.startActivity(intent)
        }
    }

    fun setFilteredList(filtered: List<Contact>){
        this.listContact = filtered
        notifyDataSetChanged()
    }
}