package com.sunxyaoyu.changeskin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import com.sunxyaoyu.skincore.SkinManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        findViewById<Button>(R.id.change_btn).setOnClickListener {
            SkinManager.getInstance().loadSkin(Environment.getExternalStorageDirectory().getAbsolutePath() + "/360/skin1.apk")
        }

        findViewById<Button>(R.id.reset_btn).setOnClickListener {
            SkinManager.getInstance().loadSkin(" ")
        }

    }

    fun setting(view: View){
        startActivity(Intent(view.context, SecondActivity::class.java))
    }
}
