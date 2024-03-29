package com.example.firebasechat.service;

/**
 * Class name: MyFcmService
 * Description: PCM(Firebase Cloud Messaging)을 위한 서비스
 * */

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFcmService extends FirebaseMessagingService {
    public static final String TAG = MyFcmService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // 여기서 FCM 메시지를 처리
        Log.d(TAG, "onMessageReceived ID: " + remoteMessage.getMessageId());
        Log.d(TAG, "onMessageReceived DATA: " + remoteMessage.getData());
    }
}
