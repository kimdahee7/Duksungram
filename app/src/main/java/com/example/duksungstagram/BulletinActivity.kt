package com.example.duksungstagram

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.my_bulletin.*


class BulletinActivity  : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_bulletin)

        back2.setOnClickListener {
            finish()
        }
    }
}