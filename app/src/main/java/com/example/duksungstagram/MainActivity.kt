package com.example.duksungstagram

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.item_detail.view.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)

        add_button.setOnClickListener {
            val intent = Intent(this, AddPhotoActivity::class.java)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                startActivity(intent)
            }
        }

        notice_view.setOnClickListener {
            val intent = Intent(this, BulletinActivity::class.java)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                startActivity(intent)
            }
        }

        // Go to detailviewfragment
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.mainfragment, DetailViewFragment())
        transaction.commit()
    }

    fun mainButtonClick(view: View) {
        val intent = Intent(this, AddPhotoActivity::class.java)
        startActivity(intent)
    }

    fun mainButtonClick2(view: View) {
        val intent = Intent(this, BulletinActivity::class.java)
            startActivity(intent)
    }
}