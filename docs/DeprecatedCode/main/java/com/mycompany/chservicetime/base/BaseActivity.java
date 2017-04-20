package com.mycompany.chservicetime.base;

/**
 * Created by szhx on 5/15/2016.
 */

import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.mycompany.chservicetime.BuildConfig;
import com.mycompany.chservicetime.R;
import com.mycompany.chservicetime.business.auth.FirebaseAuthAdapter;
import com.mycompany.chservicetime.data.preference.PreferenceSupport;

import static com.mycompany.chservicetime.util.LogUtils.LOGD;
import static com.mycompany.chservicetime.util.LogUtils.makeLogTag;

/**
 * BaseActivity class is used as a base class for all activities in the app
 * It enable "Logout" in all activities
 * and defines variables that are being shared across all activities
 */
public abstract class BaseActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = makeLogTag("BaseActivity");

    private static final int RC_SIGN_IN = 100;

    public static final String ANONYMOUS = "anonymous";

    private GoogleApiClient mGoogleApiClient;

    protected String mEncodedEmail;

    private FirebaseAuth.AuthStateListener mAuthListener;

    View mRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRootView = findViewById(android.R.id.content);

        FirebaseAuthAdapter.FIREBASE_AUTH = FirebaseAuth.getInstance();

        /**
         * Getting mEncodedEmail from SharedPreferences
         */
        mEncodedEmail = PreferenceSupport.getEncodedEmail(BaseActivity.this);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseAuthAdapter.FIREBASE_USER = firebaseAuth.getCurrentUser();
                if (FirebaseAuthAdapter.FIREBASE_USER != null) {
                    // User is signed in
                    LOGD(TAG, "onAuthStateChanged:signed_in:" + FirebaseAuthAdapter.getUserId());

                    showSnackbar(R.string.sign_in_successful);
                } else {
                    // User is signed out
                    LOGD(TAG, "onAuthStateChanged:signed_out");
                }

                // update Menu
                invalidateOptionsMenu();
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuthAdapter.FIREBASE_AUTH.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuthAdapter.FIREBASE_AUTH.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (FirebaseAuthAdapter.isSignIn())
            menu.add(Menu.NONE, R.id.menu_action_logout, 1000,
                    getResources().getString(R.string.action_logout));
        else
            menu.add(Menu.NONE, R.id.menu_action_login, 1000,
                    getResources().getString(R.string.action_login));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            super.onBackPressed();
            return true;
        }

        if (id == R.id.menu_action_logout) {
            signOut();
            return true;
        }

        if (id == R.id.menu_action_login) {
            signIn();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Logs out the user from their current session and starts LoginActivity.
     */
    protected void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //startActivity(AuthUiActivity.createIntent(SignedInActivity.this));
                            //finish();
                            showSnackbar(R.string.sign_out_successful);
                        } else {
                            showSnackbar(R.string.sign_out_failed);
                        }
                    }
                });

        //invalidateOptionsMenu();
    }

    private void signIn() {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setProviders(AuthUI.EMAIL_PROVIDER, AuthUI.GOOGLE_PROVIDER)
                        .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        LOGD(TAG, "onConnectionFailed:" + connectionResult);
    }

    @MainThread
    public void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(mRootView, errorMessageRes, Snackbar.LENGTH_LONG).show();
    }
}

