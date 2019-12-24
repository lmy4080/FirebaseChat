package com.example.firebasechat.model.signin;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.example.firebasechat.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignInModel {

    private static final String TAG = "SignInModel";

    // Firebase 인스턴스 변수
    private FirebaseAuth mFirebaseAuth;
    private GoogleApiClient mGoogleApiClient;

    // Listener 등록
    private OnSignInModelListener mListener;

    public SignInModel(OnSignInModelListener onSignInModelListener) {
        this.mListener = onSignInModelListener;
    }

    public void initFirebaseAuth() {

        // FirebaseAuth 초기화
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    public void initGoogleApiClient(Context context, FragmentActivity fragmentActivity, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // GoogleApiClient 초기화
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .enableAutoManage(fragmentActivity, onConnectionFailedListener)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    public GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }

    public void sendResult(Intent data) {

        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

        // 구글 로그인에 성공하면 Firebase 인증
        if(result.isSuccess())
        {
            GoogleSignInAccount account = result.getSignInAccount();
            firebaseAuthWithGoogle(account);
        }
        // 구글 로그인 실패
        else
        {
            Log.e(TAG, "Google Sign-In failed");
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        Log.d(TAG, "firebaseAuthWithGoogle: " + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        // 구글 계정을 통해 Firebase 인증
        mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "SignInWithCredential:Complete: " + task.isSuccessful());

                        // 인증에 성공하면 MainActivity로 이동, 실패하면 에러 메시지 표시
                        if(!task.isSuccessful())
                        {
                            Log.w(TAG, "SignInWithCredential: ", task.getException());
                            mListener.showSnackbar("Authentication failed.");
                        }
                        else
                        {
                            mListener.moveMainActivity();

                        }
                    }
                });
    }
}
