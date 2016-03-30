package t3waii.tasklists;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.GooglePlayServicesUtil;


/**
 * Created by moetto on 3/22/16.
 */
public class LoginActivity extends Activity {
    private static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1001;
    public static final String TOKEN = "TOKEN";
    String mEmail;
    String SCOPE = "oauth2:https://www.googleapis.com/auth/plus.login";
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        token = "kikkel";
        startMainActivity();

    /*
        if (isValidToken(token)) {
            startMainActivity();
        }
        View view = getLayoutInflater().inflate(R.layout.login_screen, null);
        setContentView(view);
    */
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(TOKEN, token);
        startActivity(intent);
        finish();
    }

    private boolean isValidToken(String token) {
        if (token != null) {
            return true;
        }
        return false;
    }

    public void loginButtonClick(View view) {
        if (mEmail != null) {
            pickAccount();
        } else {
            getToken();
        }
    }

    public void getToken() {
        if (mEmail != null) {
            new GetUsernameTask(this, mEmail, SCOPE).execute();
        } else {
            pickAccount();
        }
    }

    public void pickAccount() {
        String[] accountTypes = new String[]{"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, null, null, null, null);
        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
            // Receiving a result from the AccountPicker
            if (resultCode == RESULT_OK) {
                mEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                Log.d(TAG, mEmail);
                // With the account name acquired, go get the auth token
                //getUsername();
                getToken();
            } else if (resultCode == RESULT_CANCELED) {
                // The account picker dialog closed without selecting an account.
                // Notify users that they must pick an account to proceed.
                Toast.makeText(this, R.string.need_account, Toast.LENGTH_SHORT).show();
            }
        } else if ((requestCode == REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR)
                && resultCode == RESULT_OK) {
            // Receiving a result that follows a GoogleAuthException, try auth again
            //getUsername();
            pickAccount();
        }
    }

    public void handleException(final Exception e) {
        Log.d(TAG, Log.getStackTraceString(e));
        // Because this call comes from the AsyncTask, we must ensure that the following
        // code instead executes on the UI thread.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (e instanceof GooglePlayServicesAvailabilityException) {
                    // The Google Play services APK is old, disabled, or not present.
                    // Show a dialog created by Google Play services that allows
                    // the user to update the APK
                    int statusCode = ((GooglePlayServicesAvailabilityException) e).getConnectionStatusCode();
                    Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode,
                            LoginActivity.this,
                            REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                    dialog.show();
                } else if (e instanceof UserRecoverableAuthException) {
                    // Unable to authenticate, such as when the user has not yet granted
                    // the app access to the account, but the user can fix this.
                    // Forward the user to an activity in Google Play services.
                    Intent intent = ((UserRecoverableAuthException) e).getIntent();
                    startActivityForResult(intent,
                            REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                }
            }
        });
    }

    public void login(String token) {
        this.token = token;
        if (isValidToken(token)) {
            startMainActivity();
        }
    }
}
