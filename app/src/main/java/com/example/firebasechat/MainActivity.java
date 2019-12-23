package com.example.firebasechat;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    // firebase 인스턴스 변수
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    // Google
    private GoogleApiClient mGoogleApiClient;

    // 사용자 이름과 사진
    private String mUsername;
    private String mPhotoUrl;

    // UI Components
    private RecyclerView mMessageRecyclerView;
    private EditText mMessageEditText;
    public static final String MESSAGE_CHILD = "messages";

    // 어댑터
    private FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder> mFirebaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        // firebase 인증 초기화
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser == null) {
            // 인증이 안 되었다면 인증 화면으로 이동
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            if(mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }

        // firebase database 초기화
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        // UI 초기화
        mMessageRecyclerView = findViewById(R.id.message_recycler_view);
        mMessageEditText = findViewById(R.id.message_edit);
        findViewById(R.id.send_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("OnClick", "onClick: called.");
                ChatMessage chatMessage = new ChatMessage(mMessageEditText.getText().toString(), mUsername, mPhotoUrl, null);
                mFirebaseDatabaseReference.child(MESSAGE_CHILD).push().setValue(chatMessage);
                mMessageEditText.setText("");
            }
        });

        // 쿼리 수행 위치
        Query query = mFirebaseDatabaseReference.child(MESSAGE_CHILD);
        // 옵션
        FirebaseRecyclerOptions<ChatMessage> options = new FirebaseRecyclerOptions.Builder<ChatMessage>()
                .setQuery(query, ChatMessage.class)
                .build();

        // 어댑터
        mFirebaseAdapter = new FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MessageViewHolder holder, int position, @NonNull ChatMessage model) {

                holder.messageTextView.setText(model.getText());
                holder.messengerTextView.setText(model.getName());

                if(model.getPhotoUrl() != null) {
                    holder.messengerImageView.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_account_circle_black_24dp));
                } else {
                    Glide.with(MainActivity.this)
                            .load(model.getPhotoUrl())
                            .into(holder.messengerImageView);
                }
            }

            @NonNull
            @Override
            public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
                return new MessageViewHolder(view);
            }
        };

        // 리사이클러뷰에 레이아웃 매니저 어댑터 설정
        mMessageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);

        // Firebase Remote Config 초기화
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        // Firebase Remote Config 설정
        FirebaseRemoteConfigSettings firebaseRemoteConfigSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(true)
                .build();

        // 인터넷 연결이 안 되었을 때 기본값 정의
        Map<String, Object> defaultConfigMap = new HashMap<>();
        defaultConfigMap.put("message_length", 10L);

        // 설정과 기본값 설정
        mFirebaseRemoteConfig.setConfigSettings(firebaseRemoteConfigSettings);
        mFirebaseRemoteConfig.setDefaults(defaultConfigMap);

        // 원격 구성 가져오기
        fetchConfig();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mUsername = "";
                startActivity(new Intent(this, SignInActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Snackbar.make(getWindow().getDecorView().getRootView(), "Google Play Services Error", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        // FirebaseRecyclerAdapter 실시간 쿼리 시작
        mFirebaseAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        // FirebaseRecyclerAdapter 실시간 쿼리 중지
        mFirebaseAdapter.stopListening();
    }

    // 원격 구성 가져오기
    public void fetchConfig() {
        long cacheExpiration = 3600; // 1시간

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
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // 원격 구성 가져오기 실패
                Log.w(TAG, "Error fetching config" + e.getMessage());
                applyRetrievedLengthLimit();
            }
        });
    }

    /**
     *  서버에서 가져 오거나 캐시된 값을 가져 옴
     */
    public void applyRetrievedLengthLimit() {
        Long messageLength = mFirebaseRemoteConfig.getLong("message_length");
        mMessageEditText.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(messageLength.intValue())});
        Log.d(TAG, "메시지 길이 : " + messageLength);
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        ImageView messageImageView;
        TextView messengerTextView;
        CircleImageView messengerImageView;

        public MessageViewHolder(View v) {
            super(v);
            messageTextView = v.findViewById(R.id.messageTextView);
            messageImageView = v.findViewById(R.id.messageImageView);
            messengerTextView = v.findViewById(R.id.messengerTextView);
            messengerImageView = v.findViewById(R.id.messengerImageView);
        }
    }
}
