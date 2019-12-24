package com.example.firebasechat.model.main;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;
import java.util.Map;

import static com.example.firebasechat.service.MyFcmService.TAG;

public class MainModel {

    // Firebase 인스턴스 변수
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    public static final String MESSAGE_CHILD = "messages";
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    // 사용자 이름, 사진
    private String mUsername;
    private String mPhotoUrl;

    // Google
    private GoogleApiClient mGoogleApiClient;

    // Listener 등록
    private OnMainModelListener mListener;

    public MainModel(OnMainModelListener onMainModelListener) {
        this.mListener = onMainModelListener;
    }

    public void initFirebaseAuth() {

        // Firebase 인증 초기화
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    public boolean checkCurrentUser() {

        // Firebase 현재 계정 가져오기
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser == null) {
            // 현재 계정 인증이 안 되었다면, 인증 화면으로 이동
            return false;
        }
        else {
            // 계정 인증이 되었다면, 계정의 이름 및 사진 저장
            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
            return true;
        }
    }

    public void initGoogleApiClient(Context context, FragmentActivity fragmentActivity, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {

        // GoogleApiClient 초기화
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .enableAutoManage(fragmentActivity, onConnectionFailedListener)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
    }

    public void initFirebaseDatabase() {

        // FirebaseDatabaseReference 초기화
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void sendMessage(String msg) {

        // Firebase 데이터베이스에 채팅 메시지 저장
        ChatMessage chatMessage = new ChatMessage(msg, mUsername, mPhotoUrl, null);
        mFirebaseDatabaseReference.child(MESSAGE_CHILD).push().setValue(chatMessage);
    }

    public FirebaseRecyclerOptions<ChatMessage> getFirebaseOptions() {

        // 쿼리 수행 위치
        Query query = mFirebaseDatabaseReference.child(MESSAGE_CHILD);
        // 옵션 초기화
        FirebaseRecyclerOptions<ChatMessage> options = new FirebaseRecyclerOptions.Builder<ChatMessage>()
                .setQuery(query, ChatMessage.class)
                .build();

        return options;
    }

    public void initFirebaseRemoteConfig() {

        // Firebase RemoteConfig 초기화
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
    }

    public void fetchFirebaseRemoteConfig() {

        // Firebase Remote Config 설정
        FirebaseRemoteConfigSettings firebaseRemoteConfigSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(true)
                .build();

        // 인터넷 연결이 안 되었을 때 기본값 정의
        Map<String, Object> defaultConfigMap = new HashMap<>();
        defaultConfigMap.put("message_length", 50L);

        // 설정과 기본값 설정
        mFirebaseRemoteConfig.setConfigSettings(firebaseRemoteConfigSettings);
        mFirebaseRemoteConfig.setDefaults(defaultConfigMap);

        // 1시간
        long cacheExpiration = 3600;

        // 개발자 모드라면 0초로 하기
        if(mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        mFirebaseRemoteConfig.fetch(cacheExpiration).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // 원격 구성 가져오기 성공
                mFirebaseRemoteConfig.activateFetched();
                applyRetrievedLengthLimit();
            }
        });

        mFirebaseRemoteConfig.fetch(cacheExpiration).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // 원격 구성 가져오기 실패
                Log.w(TAG, "Error fetching config" + e.getMessage());
                applyRetrievedLengthLimit();
            }
        });
    }

    public void applyRetrievedLengthLimit() {

        // 채팅 메시지 길이 설정
        Long messageLength = mFirebaseRemoteConfig.getLong("message_length");
        mListener.setMessageFilter(messageLength.intValue());
    }

    public void signOut() {
        mFirebaseAuth.signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        mUsername = "";
    }
}
