package com.main

import androidx.appcompat.app.AppCompatActivity
import com.example.alexeyglushkov.authorization.AuthActivityProxy

/**
 * Created by alexeyglushkov on 12.12.15.
 */
open class BaseActivity : AppCompatActivity() {
    override fun onResume() {
        super.onResume()
        AuthActivityProxy.setCurrentActivity(this)
    }
}