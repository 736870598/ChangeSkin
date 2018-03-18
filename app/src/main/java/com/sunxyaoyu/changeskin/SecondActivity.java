package com.sunxyaoyu.changeskin;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.sunxyaoyu.skincore.SkinManager;

/**
 * Created by Sunxy on 2018/3/17.
 */

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_layout);

        findViewById(R.id.change_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SkinManager.getInstance().loadSkin(Environment.getExternalStorageDirectory().getAbsolutePath() + "/360/skin1.apk");
            }
        });
        findViewById(R.id.reset_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SkinManager.getInstance().loadSkin("");
            }
        });
    }
}
