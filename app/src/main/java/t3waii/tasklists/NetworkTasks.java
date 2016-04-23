package t3waii.tasklists;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;

import java.util.Map;

import cz.msebera.android.httpclient.Header;

/**
 * Created by moetto on 3/29/16.
 */
public class NetworkTasks {
    private final static String TAG = "NetworkTasks",
    ACTION_UPDATE_TASKS = "t3waii.tasklists.action_update_task",
    ACTION_GET_TASK = "t3waii.tasklists.action_update_task",
    ACTION_POST_TASK = "t3waii.tasklists.action_update_task",
    ACTION_REMOVE_TASK = "t3waii.tasklists.action_remove_task",
    EXTRA_TASK_AS_JSON_STRING = "extraTask",
    EXTRA_TASKS_AS_JSON_ARRAY = "extraTasks";

    public static void postNewTask(final Context context, Map<String, String> values) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.addHeader("Authorization", "Token " + MainActivity.getApiId());
        RequestParams params = new RequestParams();
        for(String key : values.keySet()) {
            params.add(key, values.get(key));
        }
        asyncHttpClient.post(MainActivity.getServerAddress() + "tasks/", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d(TAG, "Post new task succeeded");
                String response = new String(responseBody);
                Log.d(TAG, response);
                Intent intent = new Intent();
                intent.setAction(ACTION_POST_TASK);
                intent.putExtra(EXTRA_TASK_AS_JSON_STRING, response);
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

    public static void getTasks(final Context context) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.addHeader("Authorization", "Token " + MainActivity.getApiId());
        asyncHttpClient.get(MainActivity.getServerAddress() + "tasks/", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d(TAG, "Getting tasks succeeded");
                String response = new String(responseBody);
                Log.d(TAG, response);

                //JSONArray tasks;
                Intent intent = new Intent();
                intent.setAction(ACTION_UPDATE_TASKS);
                intent.putExtra(EXTRA_TASKS_AS_JSON_ARRAY, response);
                context.sendBroadcast(intent);
                /*
                    for(int j = 0; j < OpenTasksFragment.openTasks.size(); j++) {
                        Task t = OpenTasksFragment.openTasks.get(j);
                        if(t.getId() == task.getId() && t.getCreator() == task.getCreator()) {
                            t.updateTask(task);
                            taskUpdated = true;
                            Log.d(TAG, "task updated!");
                            break;
                        }
                    }
                }
                MainActivity.updateDatasets();
                */
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