package t3waii.tasklists;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.IOException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by moetto on 21/04/16.
 */
public class NetworkRegister {

    private static final String TAG = "TasksRegister";
    private static final int REQUEST_READ_PHONE_STATE = 1000;

    public static void register(final Context context, final String googleToken, final int requestCode, final NetworkListener networkListener) {

        new AsyncTask<Void, Void, String>()

        {
            @Override
            protected String doInBackground(Void... parems) {
                InstanceID instanceID = InstanceID.getInstance(context);
                try {
                    String gcmToken = instanceID.getToken(context.getString(R.string.gcm_sender_ID),
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    Log.d(TAG, "GCMToken: "+gcmToken);
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
                    NetworkRegister.sendRegisterRequest(context, googleToken, requestCode, networkListener, gcmToken);
                }
            }
        }.execute();
    }

    private static void sendRegisterRequest(Context context, String googleToken, final int requestCode, final NetworkListener networkListener, String gcmToken) {
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
                networkListener.onNetworkOperationSuccess(requestCode, new String(response));
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
