package com.example.autotest;


import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Path;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.List;

public class AutoService extends AccessibilityService {
    private static final String TAG = "Auto Service";

    private static final String WECHAT_PACKAGE_NAME = "com.tencent.mm";
    private static final String WECHAT_LAUCHER = "com.tencent.mm.ui.LauncherUI";
    private static final String WECHAT_CONTACT_FOUND = "com.tencent.mm.plugin.fts.ui.FTSMainUI";
    private static final String WECHAT_CONTACT_INFO = "com.tencent.mm.plugin.profile.ui.ContactInfoUI";
    private static final String WECHAT_CHAT = "com.tencent.mm.ui.chatting.ChattingUI";
    private static final String WECHAT_VIDEOCALL = "com.tencent.mm.plugin.voip.ui.VideoActivity";
    private static final String HICALL_PACKAGE_NAME = "";
    private static final String HICALL_LAUCHER = "";
    private static final String WECHAT_TEXT_KEY = "微信";
    private static final String HICALL_TEXT_KEY = "HICALL";
    private String callName = "AAA001";
    private int callTime;
    public static boolean isCaller = false;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        final int eventType = event.getEventType();
        switch (eventType) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                List<CharSequence> texts = event.getText();
                if (!texts.isEmpty()) {
                    for (CharSequence t : texts) {
                        String text = String.valueOf(t);
                        Log.i(TAG, "监控消息==" + text);
                        if(text.contains(WECHAT_TEXT_KEY)){
                            jumpApp(WECHAT_PACKAGE_NAME, WECHAT_LAUCHER);
                        }

                    }
                }
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                try {
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                //判断是否为微信
                if(WECHAT_PACKAGE_NAME.equals(event.getPackageName().toString())){
                    //自动接听
                    if(WECHAT_VIDEOCALL.equals(event.getClassName().toString())){
                        if(!isCaller){
                            wechatAutoAnswer();
                            try{
                                Thread.sleep(30000);
                            } catch (InterruptedException e){
                                e.printStackTrace();
                            }
                            wechatAutoHangUp();
                            try {
                                Thread.sleep(1000);
                            }catch (InterruptedException e){
                                e.printStackTrace();
                            }
                            wechatAutoHangUp();
                        }


                    } else if (WECHAT_LAUCHER.equals(event.getClassName().toString())){
                        //点击通讯录
                        if(isCaller){
                            clickContact();
                        } else {
                            clickBackKey();
                        }

                    } else if (WECHAT_CONTACT_FOUND.equals(event.getClassName().toString())){
                        if(isCaller){
                            inputContactName(callName);
                        } else {
                            clickBackKey();
                        }

                    } else if (WECHAT_CHAT.equals(event.getClassName().toString())){
                        if(isCaller){
                            isCaller = false;
                            videoCall();
                        } else {
                            clickBackKey();
                        }
//                        isCaller = false;
//                        clickBackKey();
                    }
                }
        }
    }


    /**
     * 跳转app
     * @param pkgName, clsName
     */
    private void jumpApp(String pkgName,String clsName){
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            ComponentName cmp = new ComponentName(pkgName,clsName);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // TODO: handle exception
            Log.e(TAG, "未安装app");
        }

    }
    /**
     *微信输入查找人姓名并点击
     */
    private void inputContactName(String name){
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        CharSequence cs = name;
        Bundle bundle = new Bundle();
        bundle.putCharSequence(AccessibilityNodeInfo .ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, cs);
        if (nodeInfo == null) {
            return;
        }
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/lh");
        if (list.isEmpty()) {
            return;
        }
        try {
            Thread.sleep(2000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        for (AccessibilityNodeInfo info : list) {
                info.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT,bundle);
        }
        try {
            Thread.sleep(2000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        AccessibilityNodeInfo nodeInfo1 = getRootInActiveWindow();
        if (nodeInfo == null) {
            return;
        }
        List<AccessibilityNodeInfo> list1 = nodeInfo1.findAccessibilityNodeInfosByText(callName);
        if (list1.isEmpty()) {
            return;
        }
        for (AccessibilityNodeInfo info : list1) {
            AccessibilityNodeInfo parent = info.getParent();
            if(parent.isClickable()){
                parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                break;
            }
        }
        try {
            Thread.sleep(2000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        videoCall();

    }
    /**
     *微信自动接听
     */
    private void wechatAutoAnswer(){
        Path path = new Path();
        path.moveTo(810, 1900);
        GestureDescription.Builder builder = new GestureDescription.Builder();
        GestureDescription gestureDescription = builder.addStroke(new GestureDescription.StrokeDescription(path, 200, 50)).build();
        dispatchGesture(gestureDescription, new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                Log.i("test","onCompleted");
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                Log.i("test","onCancelled");
            }
        }, null);


    }
    /**
     *微信自动挂断
     */
    private void wechatAutoHangUp(){
        Path path = new Path();
        path.moveTo(540, 1900);
        GestureDescription.Builder builder = new GestureDescription.Builder();
        GestureDescription gestureDescription = builder.addStroke(new GestureDescription.StrokeDescription(path, 200, 50)).build();
        dispatchGesture(gestureDescription, new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                Log.i("test","onCompleted");
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                Log.i("test","onCancelled");
            }
        }, null);
    }
    private void testClick(){
        Path path = new Path();
        path.moveTo(540, 1900);
        GestureDescription.Builder builder = new GestureDescription.Builder();
        GestureDescription gestureDescription = builder.addStroke(new GestureDescription.StrokeDescription(path, 50, 50)).build();
        dispatchGesture(gestureDescription, new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                Log.i("test","onCompleted");
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                Log.i("test","onCancelled");
            }
        }, null);
    }
    /**
     *微信打开联系人
     */
    private void clickContact(){
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            return;
        }
        List<AccessibilityNodeInfo> list1 = nodeInfo.findAccessibilityNodeInfosByText("通讯录");
        if (list1.isEmpty()) {
            return;
        }
        for (AccessibilityNodeInfo info : list1) {
            AccessibilityNodeInfo parent = info.getParent();
            if (parent != null) {
                parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                break;
            }

        }
        try{
            Thread.sleep(3000);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
//        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(callName);
//        if (list.isEmpty()) {
//            return;
//        }
//        for (AccessibilityNodeInfo info : list) {
//            AccessibilityNodeInfo parent = info.getParent();
//            if (parent != null) {
//                parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                break;
//            }
//        }
        AccessibilityNodeInfo nodeInfo1 = getRootInActiveWindow();
        if (nodeInfo1 == null) {
            return;
        }
        List<AccessibilityNodeInfo> list = nodeInfo1.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/c2");
        if (list.isEmpty()) {
            return;
        }
        for (AccessibilityNodeInfo info : list) {
            if(info.isClickable()){
                info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }
    /**
     *找到联系人并且点击
     */
    private void findContact(){
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            return;
        }
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(callName);
        if (list.isEmpty()) {
            return;
        }
        for (AccessibilityNodeInfo info : list) {
            AccessibilityNodeInfo parent = info.getParent();
            if (parent != null) {
                parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                break;
            }
        }
    }
    /**
     * 拨打视频通话
     */
    private void videoCall(){
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            return;
        }
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/aoe");
        if (list.isEmpty()) {
            return;
        }
        for (AccessibilityNodeInfo info : list) {
           if(info.isClickable()){
               info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
           }
        }

        try {
            Thread.sleep(2000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        AccessibilityNodeInfo nodeInfo2 = getRootInActiveWindow();
        List<AccessibilityNodeInfo> list2 = nodeInfo2.findAccessibilityNodeInfosByText("视频通话");
        if (list2.isEmpty()) {
            return;
        }
        for (AccessibilityNodeInfo info : list2) {
            AccessibilityNodeInfo parent2 = info.getParent();
            if (parent2 != null) {
                parent2.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                break;
            }
        }
        try {
            Thread.sleep(2000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        AccessibilityNodeInfo nodeInfo3 = getRootInActiveWindow();
        List<AccessibilityNodeInfo> list3 = nodeInfo3.findAccessibilityNodeInfosByText("视频通话");
        if (list3.isEmpty()) {
            return;
        }
        for (AccessibilityNodeInfo info : list3) {
            AccessibilityNodeInfo parent3 = info.getParent();
            if (parent3 != null) {
                parent3.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                break;
            }
        }

        try {
            Thread.sleep(2000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        AccessibilityNodeInfo nodeInfo4 = getRootInActiveWindow();
        if (nodeInfo4 == null) {
            return;
        }
        List<AccessibilityNodeInfo> list4 = nodeInfo4.findAccessibilityNodeInfosByText("取消");
        if (list.isEmpty()) {
            return;
        }

        for (AccessibilityNodeInfo info : list4) {
            AccessibilityNodeInfo parent = info.getParent();
            if (parent.isClickable()) {
                Log.i(TAG,"点击" +
                        "取消" );
                parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);

            }
        }
    }
    /**
     * 点击视频通话
     */
    private void clickVideoCall(){
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            return;
        }
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("音视频通话");
        if (list.isEmpty()) {
            return;
        }
        for (AccessibilityNodeInfo info : list) {
            AccessibilityNodeInfo parent = info.getParent();
            if (parent.isClickable()) {
                parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                break;
            }
        }
        try{
            Thread.sleep(3000);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        AccessibilityNodeInfo nodeInfo1 = getRootInActiveWindow();
        List<AccessibilityNodeInfo> list1 = nodeInfo1.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/l_");
        if (list.isEmpty()) {
            return;
        }
        for (AccessibilityNodeInfo info : list1) {
            if(info.isClickable()){
                info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }
    /**
     * 模拟返回键
     */
    private void clickBackKey() {
        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }
    /**
     * 服务连接
     */
    @Override
    protected void onServiceConnected() {
        Toast.makeText(this, "自动化服务开启", Toast.LENGTH_SHORT).show();
        super.onServiceConnected();
    }


    /**
     * 必须重写的方法：系统要中断此service返回的响应时会调用。在整个生命周期会被调用多次。
     */
    @Override
    public void onInterrupt() {
        Toast.makeText(this, "我快被终结了啊-----", Toast.LENGTH_SHORT).show();
    }

    /**
     * 服务断开
     */
    @Override
    public boolean onUnbind(Intent intent) {
        Toast.makeText(this, "自动化服务已被关闭", Toast.LENGTH_SHORT).show();
        return super.onUnbind(intent);
    }

    /**
     * 返回桌面
     */
    private void back2Home() {
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }

}
