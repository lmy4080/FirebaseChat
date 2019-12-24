package com.example.firebasechat.presenter.signin;

/**
 * Class name: SignInPresenter
 * Description: SignInActivity 의 View 를 가진 SignInContract.Presenter
 * */

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.FragmentActivity;

import com.example.firebasechat.contract.signin.SignInContract;
import com.example.firebasechat.model.signin.OnSignInModelListener;
import com.example.firebasechat.model.signin.SignInModel;
import com.google.android.gms.common.api.GoogleApiClient;

public class SignInPresenter implements SignInContract.Presenter, OnSignInModelListener {

    // View
    private SignInContract.View mView;
    // Model
    private SignInModel mModel;

    public SignInPresenter(SignInContract.View mView) {
        this.mView = mView;
        mModel = new SignInModel(this);
    }

    @Override
    public void initFirebaseAuth() {
        mModel.initFirebaseAuth();
    }

    @Override
    public void initGoogleApiClient(Context context, FragmentActivity fragmentActivity, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
        mModel.initGoogleApiClient(context, fragmentActivity, onConnectionFailedListener);
    }

    @Override
    public void signIn() {
        mView.signIn(mModel.getmGoogleApiClient());
    }

    @Override
    public void sendResult(Intent data) {
        mModel.sendResult(data);
    }


    @Override
    public void showSnackbar(String msg) {
        mView.showSnackbar(msg);
    }

    @Override
    public void moveMainActivity() {
        mView.moveMainActivity();
    }
}