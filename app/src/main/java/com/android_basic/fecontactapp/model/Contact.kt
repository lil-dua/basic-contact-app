package com.android_basic.fecontactapp.model

/***
 * Created by HoangRyan aka LilDua on 10/12/2023.
 */
data class Contact(
    val id: Long,
    val name: String,
    val phone: String,
    val email: String,
    val image: String
)