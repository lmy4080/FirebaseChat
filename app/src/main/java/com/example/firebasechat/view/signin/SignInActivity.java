package com.example.firebasechat.view.signin;

/**
 * Class name: SignInActivity
 * Description: 로그인 화면
 *              로그인 시 채팅방 입장
 * */

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.firebasechat.R;
import com.example.firebasechat.contract.signin.SignInContract;
import com.example.firebasechat.presenter.signin.SignInPresenter;
import com.example.firebasechat.view.main.MainActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.snackbar.Snackbar;

public class SignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, SignInContract.View {

    private static final String TAG = SignInActivity.class.getSimpleName();

    // Presenter
    private SignInContract.Presenter mPresenter;

    // StartActivityForResult Success Code
    private static final int RC_SIGN_IN = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Presenter 초기화
        mPresenter = new SignInPresenter(this);
        mPresenter.initFirebaseAuth();
        mPresenter.initGoogleApiClient(this, this, this);

        // UI 초기화
        findViewById(R.id.sign_in_button).setOnClickListener(this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Snackbar.make(getWindow().getDecorView().getRootView(), "Google Play Services Error", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                mPresenter.signIn();
                break;
        }
    }

    @Override
    public void signIn(GoogleApiClient googleApiClient) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN) {
            mPresenter.sendResult(data);
        }
    }

    @Override
    public void showSnackbar(String msg) {
        Snackbar.make(getWindow().getDecorView().getRootView(), msg, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void moveMainActivity() {
        startActivity(new Intent(SignInActivity.this, MainActivity.class));
        finish();
    }
}
