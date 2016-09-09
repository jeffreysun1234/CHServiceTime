package com.mycompany.chservicetime.auth;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.mycompany.chservicetime.CHApplication;
import com.mycompany.chservicetime.R;

/**
 * Created by szhx on 9/7/2016.
 */
public class FirebaseAuthAdapter {

    public static FirebaseAuth FIREBASE_AUTH;
    public static FirebaseUser FIREBASE_USER;

    public static boolean isSignIn() {
        return FIREBASE_USER != null;
    }

    public static void showLoginHintInSnackbar(View view) {
        if (!isSignIn())
            Snackbar.make(view, CHApplication.getContext().getString(R.string.sign_in_hint), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
    }

    public static String getAuthToken() {
        final String[] authToken = new String[1];

        if (isSignIn()) {
            FIREBASE_USER.getToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    if (task.isSuccessful()) {
                        authToken[0] = task.getResult().getToken();
                    }
                }
            });
        } else {
            return authToken[0] = null;
        }

        return authToken[0];
    }
}
