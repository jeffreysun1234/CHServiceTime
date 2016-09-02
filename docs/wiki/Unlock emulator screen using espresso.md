### Result 1:

This method is work.

http://riggaroo.co.za/setting-circle-ci-test-deploy-build-android-app/

### Result 2:

Goood method

http://stackoverflow.com/questions/33872777/unlock-emulator-screen-using-espresso

@UiThreadTest
@Before
public void setUp() throws Exception {
   final Activity activity = mActivityRule.getActivity();
    mActivityRule.runOnUiThread(new Runnable() {
        @Override
        public void run() {
          KeyguardManager mKG = (KeyguardManager) activity.getSystemService(Context.KEYGUARD_SERVICE);
          KeyguardManager.KeyguardLock mLock = mKG.newKeyguardLock(KEYGUARD_SERVICE);
          mLock.disableKeyguard();

        //turn the screen on
         activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
                   }
               });
}


src/debug/AndroidManifest.xml

<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android" >
    <uses-permission android:name="android.permission.DISABLE_KEYGUARD"/>
    <uses-permission android:name="android.permission.WAKE_LOCK"/>
</manifest>


### Result 3:

Use UIAnimation to 