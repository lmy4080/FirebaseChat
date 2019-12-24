package com.example.firebasechat.contract.main;

import android.content.Context;

import androidx.fragment.app.FragmentActivity;

import com.example.firebasechat.model.main.ChatMessage;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.api.GoogleApiClient;

public interface MainContract {

    interface View {
        void moveSignInActivity();
        String getMessageEditText();
        void clearMessageEditText();
        void setMessageLengthFilters(int messageLength);
    }

    interface Presenter {
        void initFirebaseAuth();
        void checkCurrentUser();
        void initGoogleApiClient(Context context, FragmentActivity fragmentActivity, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener);
        void initFirebaseDatabase();
        void sendMessage();
        FirebaseRecyclerOptions<ChatMessage> getFirebaseOptions();
        void initFirebaseRemoteConfig();
        void fetchFirebaseRemoteConfig();
        void signOut();
    }
}
