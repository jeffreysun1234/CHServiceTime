package com.mycompany.chservicetime.business.auth;

import android.support.design.widget.Snackbar;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mycompany.chservicetime.CHApplication;
import com.mycompany.chservicetime.R;
import com.mycompany.chservicetime.util.CHLog;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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

    /**
     * This is a asynchronous operation.
     *
     * @return
     */
    public static String getAuthToken() {
        final StringBuilder token = new StringBuilder();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        CHLog.d("Current user(Display name): " + user.getDisplayName());

        if (user != null) {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            user.getToken(true)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            CHLog.d("Auth token: " + task.getResult().getToken());
                            token.append(task.getResult().getToken());
                        }
                        countDownLatch.countDown();
                    });
            try {
                int i = 0;
                // wait 5*20=100 seconds
                while (countDownLatch.getCount() != 0 && i < 20) {
                    countDownLatch.await(5L, TimeUnit.SECONDS);
                    i++;
                }
            } catch (InterruptedException ie) {
                return null;
            }
        }

        return token.toString();
    }

    public static String getUserId() {
        return FIREBASE_USER.getUid();
    }

    public static String getUserEmail() {
        CHLog.d("Test-Tag", FIREBASE_AUTH.getCurrentUser().getEmail());
        return FIREBASE_USER.getEmail();
    }
}
