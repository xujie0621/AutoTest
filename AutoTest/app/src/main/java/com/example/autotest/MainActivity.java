package com.example.autotest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.example.autotest.AutoService;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Main";
    private Button weChatButton;
    private Button button1;
    private Button button2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        weChatButton = (Button) findViewById(R.id.weChatButton);
        button1=(Button)findViewById(R.id.button1);
        button2=(Button)findViewById(R.id.button2);
        startService(new Intent(MainActivity.this,NotiService.class));
        button1.setEnabled(false);
        //两个按钮的点击事件
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startService(new Intent(MainActivity.this,NotiService.class));
                button1.setEnabled(false);
                button2.setEnabled(true);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                stopService(new Intent(MainActivity.this,NotiService.class));
                button1.setEnabled(true);
                button2.setEnabled(false);
            }
        });
    }
    @Override
    protected void onResume(){
        super.onResume();
        weChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AutoService.isCaller = true;
                jumpApp("com.tencent.mm","com.tencent.mm.ui.LauncherUI");
            }
        });
}
    private void jumpApp(String pkgName,String clsName) {
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            ComponentName cmp = new ComponentName(pkgName, clsName);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.e(TAG, "未安装app");
        }
    }
}
