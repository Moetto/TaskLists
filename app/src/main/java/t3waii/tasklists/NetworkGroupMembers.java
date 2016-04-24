package t3waii.tasklists;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by matti on 4/21/16.
 */
public class NetworkGroupMembers extends NetworkHandler{
    public final static String TAG = "TasksNetworkGroupMem",
            ACTION_UPDATE_USERS = "t3waii.tasklists.action_update_users",
            ACTION_UPDATE_GROUP_MEMBERS = "t3waii.tasklists.action_update_group_members",
            EXTRA_USER_JSON_OBJECT_STRING = "extraUser",
            EXTRA_USER_JSON_ARRAY_STRING = "extraUserList";

    protected static String getUrl(){
        return getBaseUrl()+"/groupmembers/";
    }

    public static void getAllUsers(Context context) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.addHeader("Authorization", "Token " + MainActivity.getApiId());
        asyncHttpClient.get(MainActivity.getServerAddress() + "groupmembers/", new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d(TAG, "Getting all users succeeded");
                Intent intent = new Intent();
                intent.setAction(ACTION_UPDATE_USERS);
                String response = new String(responseBody);
                intent.putExtra(EXTRA_USER_JSON_ARRAY_STRING, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d(TAG, "Getting users failed");
            }
        });
    }

    public static void deleteGroupMembers(List<User> users) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.addHeader("Authorization", "Token " + MainActivity.getApiId());
        for (User u : users) {
            final User user = u;
            asyncHttpClient.delete(MainActivity.getServerAddress() + "groupmembers/" + Long.toString(user.getId()), new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.d(TAG, "Delete groupmember succeeded");
                    MainActivity.removeGroupMember(user);
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
    }

    public static void getGroupMembers(final Context context) {
        getAsyncHttpClient().get(getUrl(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Intent intent = new Intent();
                intent.setAction(ACTION_UPDATE_GROUP_MEMBERS);
                intent.putExtra(EXTRA_USER_JSON_ARRAY_STRING, new String(responseBody));
                context.sendBroadcast(intent);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public static void joinGroup(final Context context, final int groupId) {
        Log.d(TAG, "Trying to join group");
        String groupIdAsString;
        if (groupId == 0){
            groupIdAsString = "";
        } else {
            groupIdAsString = ""+groupId;
        }
        RequestParams params = new RequestParams("group", groupIdAsString);
        getAsyncHttpClient().patch(getUrl() + MainActivity.getSelfGroupMember() +"/", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d(TAG, "Joined group");
                Intent joinIntent = new Intent();
                joinIntent.setAction(Group.ACTION_GET_GROUP);
                joinIntent.putExtra(Group.EXTRA_GROUP_ID, groupId);
                context.sendBroadcast(joinIntent);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d(TAG, "Joining group failed");
                Log.d(TAG, new String (responseBody));
            }
        });
    }
}
