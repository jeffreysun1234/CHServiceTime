package com.mycompany.chservicetime.presentation;

/**
 * Created by szhx on 1/25/2017.
 */


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.mycompany.chservicetime.BuildConfig;
import com.mycompany.chservicetime.R;
import com.mycompany.chservicetime.business.auth.FirebaseAuthAdapter;
import com.mycompany.chservicetime.data.preference.PreferenceSupport;
import com.mycompany.chservicetime.presentation.timeslotlist.TimeSlotListPresenter;
import com.mycompany.chservicetime.presentation.timeslotlist.TimeSlotListView;
import com.mycompany.chservicetime.util.CHLog;

import net.grandcentrix.thirtyinch.TiActivity;

import java.util.Arrays;

import static com.mycompany.chservicetime.util.CHLog.makeLogTag;

/**
 * BaseActivity class is used as a base class for all activities in the app
 * It enable "Logout" in all activities
 * and defines variables that are being shared across all activities
 */
public abstract class BaseTiActivity extends TiActivity<TimeSlotListPresenter, TimeSlotListView>
        implements GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = makeLogTag("BaseActivity");

    // The Request_code should be greater 100, for avoid to conflict with the value of
    // PermissionDispatcher generator.
    private static final int RC_SIGN_IN = 100;
    private static final int RC_PERMISSION_PHONE_STATE = 101;
    private static final int RC_PERMISSION_WRITE_SETTING = 102;
    private static final int RC_PERMISSION_SETTING = 300;

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
        mEncodedEmail = PreferenceSupport.getEncodedEmail(BaseTiActivity.this);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseAuthAdapter.FIREBASE_USER = firebaseAuth.getCurrentUser();
                if (FirebaseAuthAdapter.FIREBASE_USER != null) {
                    // User is signed in
                    CHLog.d(TAG, "onAuthStateChanged:signed_in:" + FirebaseAuthAdapter.getUserEmail());
                } else {
                    // User is signed out
                    CHLog.d(TAG, "onAuthStateChanged:signed_out");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
            case RC_SIGN_IN: {
                handleSignInResponse(resultCode, data);
                break;
            }
        }
    }

    @MainThread
    private void handleSignInResponse(int resultCode, Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);

        // Successfully signed in
        if (resultCode == ResultCodes.OK) {
            showSnackbar(R.string.sign_in_successful);
            return;
        } else {
            // Sign in failed
            if (response == null) {
                // User pressed back button
                showSnackbar(R.string.sign_in_cancelled);
                return;
            }

            if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                showSnackbar(R.string.no_internet_connection);
                return;
            }

            if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                showSnackbar(R.string.unknown_error);
                return;
            }
        }

        showSnackbar(R.string.unknown_sign_in_response);
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
    }

    private void signIn() {
        AuthUI.IdpConfig googleIdp = new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER)
                .setPermissions(Arrays.asList(Scopes.GAMES))
                .build();

        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setProviders(Arrays.asList(
                                new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build()
//                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()
                        ))
                        .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        CHLog.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @MainThread
    public void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(mRootView, errorMessageRes, Snackbar.LENGTH_LONG).show();
    }

}

