package com.ganna.uber.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.ganna.uber.R;
import com.ganna.uber.util.ScreenManager;
import com.google.firebase.auth.FirebaseAuth;

import static com.firebase.ui.auth.ui.AcquireEmailHelper.RC_SIGN_IN;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (FirebaseAuth.getInstance().getCurrentUser()!=null){
            ScreenManager.launchMainScreen(this);
        }
    }

    public void login(View view) {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder().build(),
                RC_SIGN_IN);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // user is signed in!
                ScreenManager.launchMainScreen(this);
            }
        }
    }
}
