package com.mycompany.chservicetime.presentation;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.mycompany.chservicetime.R;
import com.mycompany.chservicetime.presentation.timeslotlist.TimeSlotListActivity;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class InitialActivity extends AppCompatActivity {

    private final String PERMISSION_NAME_INITIAL_CHECK = "permission_initial_check";
    private final String PERMISSION_NAME_MAIN_ACTIVITY = "permission_main_activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // add this method at onStart(), so if returning from the setting, go the main activity,
        // and the Rationale dialog does not show repeatably.
        goToNextPermission(PERMISSION_NAME_INITIAL_CHECK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // for Getting Special Permissions
        InitialActivityPermissionsDispatcher.onActivityResult(this, requestCode);
    }

    /**
     * for getting the other runtime permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        InitialActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    /**
     * Permissions link, following the index. <br>
     * If no found the permissionName, go to the main activity of APP.
     *
     * @param permissionName from Manifest.permission.* <br>
     *                       PERMISSION_NAME_INITIAL_CHECK means the first permission check. <br>
     *                       PERMISSION_NAME_MAIN_ACTIVITY means to go to the main activity of APP.
     */
    private void goToNextPermission(String permissionName) {
        switch (permissionName) {
            case PERMISSION_NAME_INITIAL_CHECK:
                InitialActivityPermissionsDispatcher.getPermissionPhoneStateWithCheck(this);
                break;
            case PERMISSION_NAME_MAIN_ACTIVITY:
                goToMainActivity();
                break;
            case Manifest.permission.READ_PHONE_STATE:
                InitialActivityPermissionsDispatcher.getPermissionStorageWithCheck(this);
                break;
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                InitialActivityPermissionsDispatcher.writeSettingsPermissionWithCheck(this);
                break;
            case Manifest.permission.WRITE_SETTINGS:
                goToMainActivity();
                break;
            default:
                goToMainActivity();
        }
    }
    //*** permission.READ_PHONE_STATE ***//

    @NeedsPermission(Manifest.permission.READ_PHONE_STATE)
    void getPermissionPhoneState() {
        // NOTE: Perform action that requires the permission.
        // If this is run by PermissionsDispatcher, the permission will have been granted
        goToNextPermission(Manifest.permission.READ_PHONE_STATE);
    }

    @OnShowRationale(Manifest.permission.READ_PHONE_STATE)
    void showRationaleForPhoneState(PermissionRequest request) {
        // NOTE: Show a rationale to explain why the permission is needed, e.g. with a dialog.
        // Call proceed() or cancel() on the provided PermissionRequest to continue or abort
        showRationaleDialog(R.string.permission_phone_state_rationale, request);
    }

    @OnPermissionDenied(Manifest.permission.READ_PHONE_STATE)
    void onPhoneStateDenied() {
        // NOTE: Deal with a denied permission, e.g. by showing specific UI
        // or disabling certain functionality
        showSnackbar(R.string.permission_phone_state_denied);
        goToNextPermission(Manifest.permission.READ_PHONE_STATE);
    }

    @OnNeverAskAgain(Manifest.permission.READ_PHONE_STATE)
    void onPhoneStateNeverAskAgain() {
        showNeverAskAgainDialog(R.string.permission_phone_state_never_ask,
                Manifest.permission.READ_PHONE_STATE);
    }

    //*** permission.WRITE_EXTERNAL_STORAGE ***//

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void getPermissionStorage() {
        // NOTE: Perform action that requires the permission.
        // If this is run by PermissionsDispatcher, the permission will have been granted
        goToNextPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showRationaleForStorage(PermissionRequest request) {
        // NOTE: Show a rationale to explain why the permission is needed, e.g. with a dialog.
        // Call proceed() or cancel() on the provided PermissionRequest to continue or abort
        showRationaleDialog(R.string.permission_storage_rationale, request);
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void onStorageDenied() {
        // NOTE: Deal with a denied permission, e.g. by showing specific UI
        // or disabling certain functionality
        showSnackbar(R.string.permission_storage_denied);
        goToNextPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void onStorageNeverAskAgain() {
        showNeverAskAgainDialog(R.string.permission_storage_never_ask,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    //*** Special permission: permission.WRITE_SETTINGS ***//

    @NeedsPermission(Manifest.permission.WRITE_SETTINGS)
    void writeSettingsPermission() {
        goToNextPermission(Manifest.permission.WRITE_SETTINGS);
    }

    @OnShowRationale(Manifest.permission.WRITE_SETTINGS)
    void writeSettingsOnShowRationale(PermissionRequest request) {
        showRationaleDialog(R.string.permission_write_settings_rationale, request);
    }

    @OnPermissionDenied(Manifest.permission.WRITE_SETTINGS)
    void writeSettingsOnPermissionDenied() {
        showSnackbar(R.string.permission_write_settings_denied);
        goToNextPermission(Manifest.permission.WRITE_SETTINGS);
    }

    @OnNeverAskAgain(Manifest.permission.WRITE_SETTINGS)
    void writeSettingsOnNeverAskAgain() {
        showNeverAskAgainDialog(R.string.permission_write_settings_never_ask,
                Manifest.permission.WRITE_SETTINGS);
    }

    /****** Common methods ******/

    private void showRationaleDialog(@StringRes int messageResId, final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setPositiveButton(R.string.button_ok, (dialog, which) -> request.proceed())
                .setNegativeButton(R.string.button_cancel, (dialog, which) -> request.cancel())
                .setCancelable(false)
                .setMessage(messageResId)
                .show();
    }

    private void showNeverAskAgainDialog(@StringRes int messageResId, String nextPermissionName) {
        new AlertDialog.Builder(this)
                .setPositiveButton(R.string.button_go_settings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        goToSettings();
                    }
                })
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        goToNextPermission(nextPermissionName);
                    }
                })
                .setCancelable(false)
                .setMessage(messageResId)
                .show();
    }

    private void goToSettings() {
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        Intent settingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri);
        settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(settingsIntent);
    }


    private void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(findViewById(android.R.id.content), errorMessageRes, Snackbar.LENGTH_LONG)
                .show();
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, TimeSlotListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(intent);
        finish();
    }
}
