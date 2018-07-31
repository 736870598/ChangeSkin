package com.sunxyaoyu.changeskin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.TextView
import com.sunxyaoyu.skincore.SkinManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        findViewById<TextView>(R.id.change_btn).setOnClickListener {
            SkinManager.getInstance().loadSkin(
                    "${Environment.getExternalStorageDirectory().absolutePath}/Android/sunxy_file/skin2.apk")
        }

        findViewById<Button>(R.id.reset_btn).setOnClickListener {
            SkinManager.getInstance().loadSkin("")
        }

    }

}
