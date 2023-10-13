package com.android_basic.fecontactapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android_basic.fecontactapp.R
import com.android_basic.fecontactapp.model.ContactGroup
import java.util.Locale

/***
 * Created by HoangRyan aka LilDua on 10/12/2023.
 */
class OuterContactAdapter(
    private val context: Context,
    private val contactsGroups: List<ContactGroup>
) : RecyclerView.Adapter<OuterContactAdapter.ViewHolder>(),Filterable {

    private var filteredContactsGroups: List<ContactGroup> = contactsGroups

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textHeader: TextView = itemView.findViewById(R.id.text_first_char_outer)
        val recyclerViewInnerContact: RecyclerView = itemView.findViewById(R.id.recycle_view_inner_contacts)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_outer_contact,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return contactsGroups.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = contactsGroups[position]

        //set data for header
        holder.textHeader.text = data.header.toString()

        //set data for inner contact
        val innerAdapter = ContactAdapter(context,data.contacts)
        holder.recyclerViewInnerContact.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.recyclerViewInnerContact.adapter = innerAdapter
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = mutableListOf<ContactGroup>()

                if (constraint.isNullOrBlank()) {
                    filteredList.addAll(contactsGroups)
                } else {
                    val filterPattern = constraint.toString().lowercase(Locale.getDefault()).trim()
                    for (group in contactsGroups) {
                        val filteredContacts = group.contacts.filter {
                            it.name.lowercase(Locale.getDefault()).startsWith(filterPattern)
                        }
                        if (filteredContacts.isNotEmpty()) {
                            filteredList.add(ContactGroup(group.header, filteredContacts))
                        }
                    }
                }

                val results = FilterResults()
                results.values = filteredList
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredContactsGroups = results?.values as List<ContactGroup>
                notifyDataSetChanged()
            }
        }
    }
}