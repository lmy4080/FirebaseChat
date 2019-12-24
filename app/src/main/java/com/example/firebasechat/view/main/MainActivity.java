package com.example.firebasechat.view.main;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebasechat.R;
import com.example.firebasechat.view.signin.SignInActivity;
import com.example.firebasechat.adapter.main.FirebaseAdapter;
import com.example.firebasechat.contract.main.MainContract;
import com.example.firebasechat.presenter.main.MainPresenter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, MainContract.View, View.OnClickListener {

    // Presenter
    private MainContract.Presenter mPresenter;

    // UI Components
    private RecyclerView mMessageRecyclerView;
    private EditText mMessageEditText;

    // Adapter
    private FirebaseAdapter mFirebaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Presenter 및 설정 초기화
        mPresenter = new MainPresenter(this);
        mPresenter.initFirebaseAuth();
        mPresenter.checkCurrentUser();
        mPresenter.initGoogleApiClient(this, this, this);
        mPresenter.initFirebaseDatabase();

        // UI 초기화
        mMessageRecyclerView = findViewById(R.id.message_recycler_view);
        mMessageEditText = findViewById(R.id.message_edit);

        // UI 이벤트 설정
        findViewById(R.id.send_button).setOnClickListener(this);

        // Firebase 어댑터 초기화
        mFirebaseAdapter = new FirebaseAdapter(this, this, mPresenter.getFirebaseOptions());

        // 리사이클러뷰에 레이아웃 매니저 어댑터 설정
        mMessageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);

        // Firebase RemoteConfig 초기화 및 원격 구성 가져와 설정
        mPresenter.initFirebaseRemoteConfig();
        mPresenter.fetchFirebaseRemoteConfig();
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
                mPresenter.signOut();
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

    @Override
    public void moveSignInActivity() {
        startActivity(new Intent(this, SignInActivity.class));
        finish();
        return;
    }

    @Override
    public String getMessageEditText() {
        return mMessageEditText.getText().toString();
    }

    @Override
    public void clearMessageEditText() {
        mMessageEditText.setText("");
    }

    @Override
    public void setMessageLengthFilters(int messageLength) {
        mMessageEditText.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(messageLength)});
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_button:
                mPresenter.sendMessage();
                break;
        }
    }
}
