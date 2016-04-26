package t3waii.tasklists;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

import cz.msebera.android.httpclient.Header;

/**
 * Created by moetto on 3/29/16.
 */
public class NetworkTasks {
    public final static String TAG = "NetworkTasks";

    public static void postNewTask(final Context context, Map<String, String> values) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.addHeader("Authorization", "Token " + MainActivity.getApiId());
        RequestParams params = new RequestParams();
        for (String key : values.keySet()) {
            params.add(key, values.get(key));
        }
        asyncHttpClient.post(MainActivity.getServerAddress() + "tasks/", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d(TAG, "Post new task succeeded");
                String response = new String(responseBody);
                Log.d(TAG, response);
                Intent intent = new Intent();
                intent.setAction(Task.ACTION_POST_TASK);
                intent.putExtra(Task.EXTRA_TASK_AS_JSON_STRING, response);
                context.sendBroadcast(intent);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d(TAG, "Post new task failed");
                if (responseBody != null) {
                    Log.d(TAG, new String(responseBody));
                }
            }
        });
    }

    public static void deleteTask(final Context context, final int taskId) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.addHeader("Authorization", "Token " + MainActivity.getApiId());
        asyncHttpClient.delete(MainActivity.getServerAddress() + "tasks/" + Integer.toString(taskId) + "/", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d(TAG, "Delete task succeeded");
                Intent intent = new Intent();
                intent.setAction(Task.ACTION_REMOVE_TASK_BY_ID);
                intent.putExtra(Task.EXTRA_TASK_ID, taskId);
                context.sendBroadcast(intent);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d(TAG, "Delete task failed");
                Log.d(TAG, "Status:" + statusCode);
                if (responseBody != null) {
                    Log.d(TAG, new String(responseBody));
                }
            }
        });
    }

    public static  void claimTask(final Context context, int taskId) {
        RequestParams params = new RequestParams();
        params.put("responsible_member", Integer.toString(MainActivity.getSelfGroupMemberId()));
        editTask(context, taskId, params);
    }

    public static  void unclaimTask(final Context context, int taskId) {
        RequestParams params = new RequestParams();
        params.put("responsible_member", 0);
        editTask(context, taskId, params);
    }

    public static  void editTask(final Context context, int taskId, RequestParams params) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.addHeader("Authorization", "Token " + MainActivity.getApiId());
        asyncHttpClient.patch(MainActivity.getServerAddress() + "tasks/" + taskId + "/", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d(TAG, "Edit task succeeded");
                String response = new String(responseBody);
                Log.d(TAG, response);
                Intent intent = new Intent();
                intent.setAction(Task.ACTION_UPDATE_TASK);
                intent.putExtra(Task.EXTRA_TASK_AS_JSON_STRING, response);
                context.sendBroadcast(intent);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d(TAG, "Edit task failed");
                if (responseBody != null) {
                    Log.d(TAG, new String(responseBody));
                }
            }
        });
    }

    public static void getTasks(final Context context) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.addHeader("Authorization", "Token " + MainActivity.getApiId());
        asyncHttpClient.get(MainActivity.getServerAddress() + "tasks/", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d(TAG, "Getting tasks succeeded");
                String response = new String(responseBody);
                Log.d(TAG, response);

                Intent intent = new Intent();
                intent.setAction(Task.ACTION_UPDATE_TASKS);
                intent.putExtra(Task.EXTRA_TASKS_AS_JSON_ARRAY, response);
                context.sendBroadcast(intent);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d(TAG, "Getting tasks failed");
                if (responseBody != null) {
                    Log.d(TAG, new String(responseBody));
                }
            }
        });
    }
}