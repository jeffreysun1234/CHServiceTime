package com.mycompany.chservicetime.presentation;

/**
 * Created by szhx on 1/25/2017.
 */


import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
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
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;

import net.grandcentrix.thirtyinch.TiActivity;

import java.util.Arrays;
import java.util.List;

import static com.mycompany.chservicetime.util.LogUtils.LOGD;
import static com.mycompany.chservicetime.util.LogUtils.makeLogTag;

/**
 * BaseActivity class is used as a base class for all activities in the app
 * It enable "Logout" in all activities
 * and defines variables that are being shared across all activities
 */
public abstract class BaseTiActivity extends TiActivity<TimeSlotListPresenter, TimeSlotListView> implements
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = makeLogTag("BaseActivity");

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

        // TODO: runtime error
        // Permission: WRITE_SETTING
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(this)) {
                // Do stuff here
            } else {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + this.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, RC_PERMISSION_WRITE_SETTING);
            }
        }

        // Permission Setting for Android 6.0
        AndPermission.with(this)
                .requestCode(RC_PERMISSION_PHONE_STATE)
                .permission(Manifest.permission.READ_PHONE_STATE)
                // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框，避免用户勾选不再提示。
                .rationale((requestCode, rationale) ->
                        // 这里的对话框可以自定义，只要调用rationale.resume()就可以继续申请。
                        AndPermission.rationaleDialog(BaseTiActivity.this, rationale).show()
                )
                .send();

        mRootView = findViewById(android.R.id.content);

//        FileInputStream serviceAccount = null;
//        try {
//            serviceAccount = new FileInputStream("path/to/service_account_keyey.json");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
        //InputStream serviceAccount = getResources().openRawResource(R.raw.service_account_key);
//        FirebaseOptions options = new FirebaseOptions.Builder()
//                .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
//                .setDatabaseUrl("https://chservicetime.firebaseio.com/")
//                .build();

//        FirebaseOptions options = new FirebaseOptions.Builder()
//                .setServiceAccount(serviceAccount)
//                .setDatabaseUrl("https://chservicetime.firebaseio.com")
//                .build();
//        FirebaseApp.initializeApp(options);

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
                    LOGD(TAG, "onAuthStateChanged:signed_in:" + FirebaseAuthAdapter.getUserId());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RC_PERMISSION_WRITE_SETTING: {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.System.canWrite(this)) {
                        showSnackbar(R.string.permission_write_setting_yes);
                    } else {
                        showSnackbar(R.string.permission_write_setting_no);
                    }

                }
                return;
            }
            case RC_PERMISSION_SETTING: {
                showSnackbar(R.string.back_from_system_setting);
                return;
            }
            // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
            case RC_SIGN_IN: {
                handleSignInResponse(resultCode, data);
                return;
            }
            default:
                showSnackbar(R.string.unknown_response);
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
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setProviders(Arrays.asList(
                                new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
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

    //----------------------------------Phone_State读写权限----------------------------------//

    /**
     * <p>权限全部申请成功才会回调这个方法，否则回调失败的方法。</p>
     *
     * @param grantedPermissions AndPermission回调过来的申请成功的权限。
     */
    @PermissionYes(RC_PERMISSION_PHONE_STATE)
    private void getPermissionPhoneStateYes(List<String> grantedPermissions) {
        showSnackbar(R.string.get_permission_phone_state_yes);
    }

    /**
     * <p>只要有一个权限申请失败就会回调这个方法，并且不会回调成功的方法。</p>
     *
     * @param deniedPermissions AndPermission回调过来的申请失败的权限。
     */
    @PermissionNo(RC_PERMISSION_PHONE_STATE)
    private void getPermissionPhoneStateNo(List<String> deniedPermissions) {
        showSnackbar(R.string.get_permission_phone_state_no);
        // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
        if (AndPermission.hasAlwaysDeniedPermission(this, deniedPermissions)) {
            AndPermission.defaultSettingDialog(this, RC_PERMISSION_SETTING).show();
        }
    }

    //----------------------------------权限回调处理----------------------------------//

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AndPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

}

