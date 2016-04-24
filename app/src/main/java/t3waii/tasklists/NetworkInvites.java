package t3waii.tasklists;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by moetto on 23/04/16.
 */
public class NetworkInvites extends NetworkHandler {
    public static final String TAG = "TaskNetworkInvites";


    protected static String getUrl() {
        return getBaseUrl() + "/invites/";
    }

    public static final String ACTION_GET_INVITES = "t3waii.tasklists.action_get_invites";

    public static void getInvites(final Context context) {
        AsyncHttpClient asyncHttpClient = getAsyncHttpClient();
        asyncHttpClient.get(getUrl(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public static void postInvites(final Context context, List<User> invited) {
        for (User user : invited) {
            RequestParams params = new RequestParams("invited", user.getId(), "inviter", MainActivity.getSelfGroupMember().getId());
            getAsyncHttpClient().post(getUrl(), params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.d(TAG, "Sent invite");
                    Intent intent = new Intent();
                    intent.setAction(Invite.ACTION_INVITE_SENT);
                    intent.putExtra(Invite.EXTRA_INVITE, new String(responseBody));
                    context.sendBroadcast(intent);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                }
            });
        }
    }
}
