package com.example.autotest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;


public class NotiService extends Service {
    private boolean isRun;// 线程是否继续的标志
    private Handler handler1; // 显示当前时间线程消息处理器。
    private Handler handler2;// 推送通知栏消息的线程消息处理器。
    private int notificationCounter;// 一个用于计算通知多少的计数器。

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRun = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isRun = true;
        final NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);// 注册通知管理器
        new Thread(new Runnable() {
            @Override
            // 在Runnable中，如果要让线程自己一直跑下去，必须自己定义while结构
            // 如果这个run()方法读完了，则整个线程自然死亡
            public void run() {
                // 定义一个线程中止标志
                while (isRun) {
                    try {
                        Thread.sleep(10000);// Java中线程的休眠，必须在try-catch结构中，每2s秒运行一次的意思
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!isRun) {
                        break;
                    }
                    Message msg = new Message(); // 在安卓中，不要在线程中直接现实方法，这样app容易崩溃，有什么要搞，扔到消息处理器中实现。
                    handler1.sendMessage(msg);
                }
            }
        }).start();// 默认线程不启动，必须自己start()
        // 不停在接受线程的消息，根据消息的参数，进行处理 ，这里没有传递过来的参数
        handler1 = new Handler(new Handler.Callback() {// 这样写，就不弹出什么泄漏的警告了
            @Override
            public boolean handleMessage(Message msg) {
                // 安卓显示当前时间的方法
//                Time time = new Time();
//                time.setToNow();
//                String currentTime = time.format("%Y-%m-%d %H:%M:%S");
                Toast.makeText(getApplicationContext(),
                        "打开微信", Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        });

        return START_STICKY;
    }

}

