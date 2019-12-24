package com.example.firebasechat.presenter.main;

import android.content.Context;

import androidx.fragment.app.FragmentActivity;

import com.example.firebasechat.model.main.ChatMessage;
import com.example.firebasechat.contract.main.MainContract;
import com.example.firebasechat.model.main.MainModel;
import com.example.firebasechat.model.main.OnMainModelListener;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainPresenter implements MainContract.Presenter, OnMainModelListener {

    // View
    private MainContract.View mView;
    // Model
    private MainModel mModel;

    public MainPresenter(MainContract.View mView) {
        this.mView = mView;
        this.mModel = new MainModel(this);
    }

    @Override
    public void initFirebaseAuth() {
        mModel.initFirebaseAuth();
    }

    @Override
    public void checkCurrentUser() {
        if(!mModel.checkCurrentUser()) {
            mView.moveSignInActivity();
        }
    }

    @Override
    public void initGoogleApiClient(Context context, FragmentActivity fragmentActivity, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
        mModel.initGoogleApiClient(context, fragmentActivity, onConnectionFailedListener);
    }

    @Override
    public void initFirebaseDatabase() {
        mModel.initFirebaseDatabase();
    }

    @Override
    public void sendMessage() {
        mModel.sendMessage(mView.getMessageEditText());
        mView.clearMessageEditText();
    }

    @Override
    public FirebaseRecyclerOptions<ChatMessage> getFirebaseOptions() {
        return mModel.getFirebaseOptions();
    }

    @Override
    public void initFirebaseRemoteConfig() {
        mModel.initFirebaseRemoteConfig();
    }

    @Override
    public void fetchFirebaseRemoteConfig() {
        mModel.fetchFirebaseRemoteConfig();
    }

    @Override
    public void signOut() {
        mModel.signOut();
        mView.moveSignInActivity();
    }

    @Override
    public void setMessageFilter(int messageLength) {
        mView.setMessageLengthFilters(messageLength);
    }
}
