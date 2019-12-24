package com.example.firebasechat.contract.signin;

/**
 * Interface name: SignInContract
 * Description: SignIn Activity 의 Contract 인터페이스
 * */

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.api.GoogleApiClient;

public interface SignInContract {

    interface View {
        void signIn(GoogleApiClient googleApiClient);
        void showSnackbar(String msg);
        void moveMainActivity();
    }

    interface Presenter {
        void initFirebaseAuth();
        void initGoogleApiClient(Context context, FragmentActivity fragmentActivity, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener);
        void signIn();
        void sendResult(Intent data);
    }
}
