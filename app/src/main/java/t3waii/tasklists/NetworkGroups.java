package t3waii.tasklists;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpRequest;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by matti on 4/21/16.
 */
public class NetworkGroups extends NetworkHandler {
    private final static String TAG = "NetworkGroups";

    private static String getUrl() {
        return getBaseUrl() + "/groups/";
    }

    public static void postNewGroup(final Context context, String name) {
        AsyncHttpClient asyncHttpClient = getAsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("name", name);

        asyncHttpClient.post(getUrl(), params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d(TAG, "Post new group succeeded");
                String response = new String(responseBody);
                Log.d(TAG, response);
                Intent intent = buildGroupIntent(response);
                if (intent != null) {
                    context.sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d(TAG, "Post new group failed");
                if (responseBody != null) {
                    Log.d(TAG, new String(responseBody));
                }
            }
        });
    }

    // Send leave group to server
    // on success: hide/show menu items, notify other groupmembers, clear users, locations and tasks that are not created by you
    public static void leaveGroup(final Context context) {

        getAsyncHttpClient().delete(getUrl(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d(TAG, "Delete groupmember succeeded");
                Intent intent = new Intent();
                intent.setAction(Group.ACTION_LEAVE_GROUP);
                context.sendBroadcast(intent);
                //TODO: remove tasks that are not created by you
                //TODO: alter tasks that you have created to have no assigned or estimatedtime
                //TODO: not here, server will take care hopefully
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d(TAG, "Delete groupmember failed");
                if (responseBody != null) {
                    Log.d(TAG, new String(responseBody));
                }
            }
        });
    }

    public static void getGroup(final Context context, int groupId) {
        getAsyncHttpClient().get(context, getUrl() + groupId, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Intent intent = buildGroupIntent(new String(responseBody));
                if (intent != null)
                    context.sendBroadcast(intent);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private static Intent buildGroupIntent(String response) {
        try {
            JSONObject group = new JSONObject(response);
            Intent intent = new Intent();
            intent.setAction(Group.ACTION_GET_GROUP);
            intent.putExtra(Group.EXTRA_GROUP_NAME, group.getString("name"));
            intent.putExtra(Group.EXTRA_GROUP_ID, group.getInt("id"));
            JSONArray members = group.getJSONArray("members");
            ArrayList<Integer> membersList = new ArrayList<>();
            for (int i = 0; i < members.length(); i++) {
                membersList.add((Integer) members.get(i));
            }
            intent.putIntegerArrayListExtra(Group.EXTRA_MEMBERS_IDS, membersList);
            return intent;
        } catch (JSONException ex) {
            Log.e(TAG, Log.getStackTraceString(ex));
        }
        return null;
    }
}
