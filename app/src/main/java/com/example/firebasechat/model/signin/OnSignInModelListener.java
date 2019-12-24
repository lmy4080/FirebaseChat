package com.example.firebasechat.model.signin;

/**
 * Class name: OnSignInModelListener
 * Description: SignInModel 의 Callback Listener
 * */

public interface OnSignInModelListener {
    void showSnackbar(String msg);
    void moveMainActivity();
}
