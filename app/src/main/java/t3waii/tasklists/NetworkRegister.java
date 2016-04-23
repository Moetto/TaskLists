package t3waii.tasklists;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by moetto on 21/04/16.
 */
public class NetworkRegister {

    private static final String TAG = "TasksRegister";
    public static final String ACTION_REGISTERED = "t3waii.tasklists.action_registered",
            EXTRA_MEMBER_ID = "memberId", EXTRA_GROUP_ID = "groupId", EXTRA_AUTH_TOKEN = "authToken";

    public static void register(final Context context, final String googleToken, final int requestCode) {

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... parems) {
                InstanceID instanceID = InstanceID.getInstance(context);
                try {
                    String gcmToken = instanceID.getToken(context.getString(R.string.gcm_sender_ID),
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    Log.d(TAG, "GCMToken: " + gcmToken);
                    return gcmToken;
                } catch (IOException ex) {
                    Log.e(TAG, Log.getStackTraceString(ex));
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String gcmToken) {
                super.onPostExecute(gcmToken);
                if (gcmToken != null) {
                    NetworkRegister.sendRegisterRequest(context, googleToken, requestCode, gcmToken);
                }
            }
        }.execute();
    }

    private static void sendRegisterRequest(final Context context, String googleToken, final int requestCode, String gcmToken) {
        final AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams("token", googleToken, "gcm_token", gcmToken);//, "device_id", deviceId);
        Log.d(TAG, params.toString());

        client.post(MainActivity.getServerAddress() + "register/", params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                Log.d(TAG, "Successful request");
                String authToken;
                int selfGroupMemberId;
                int group_id;
                try {
                    JSONObject responseAsJson = new JSONObject(new String(response));
                    authToken = responseAsJson.getString("token");
                    selfGroupMemberId = responseAsJson.getInt("group_member_id");
                    try {
                        group_id = responseAsJson.getInt("group_id");
                    } catch (JSONException ex) {
                        group_id = 0;
                    }

                    Log.d(TAG, "I am " + selfGroupMemberId);
                } catch (JSONException ex) {
                    Log.e(TAG, "Invalid json from register");
                    return;
                }

                Intent intent = new Intent();
                intent.setAction(ACTION_REGISTERED);
                intent.putExtra(EXTRA_AUTH_TOKEN, authToken);
                intent.putExtra(EXTRA_MEMBER_ID, selfGroupMemberId);
                if(group_id != 0) {
                    intent.putExtra(EXTRA_GROUP_ID, group_id);
                }
                context.sendBroadcast(intent);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Log.d(TAG, "Failed request");
                Log.d(TAG, "Status: " + statusCode);
                if (errorResponse != null) {
                    Log.d(TAG, new String(errorResponse));
                }
                Log.d(TAG, Log.getStackTraceString(e));
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }
}
