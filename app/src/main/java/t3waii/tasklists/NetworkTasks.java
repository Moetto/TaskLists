package t3waii.tasklists;

import android.os.AsyncTask;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

/**
 * Created by moetto on 3/29/16.
 */
public class NetworkTasks {
    private final static String TAG = "NetworkTasks";
    private static String API_ID = "";
    private static String SERVER_ADDRESS = "";

    public static void setApiId(String apiId) {
        API_ID = apiId;
    }
    public static void setServerAddress(String serverAddress) { SERVER_ADDRESS = serverAddress; }

    private void register() {
        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                return null;
            }
        }.execute();

    }

    public static void postNewTask(Map<String, String> values) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.addHeader("Authorization", "Token " + API_ID);
        RequestParams params = new RequestParams();
        for(String key : values.keySet()) {
            params.add(key, values.get(key));
        }
        asyncHttpClient.post(SERVER_ADDRESS + "tasks/", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d(TAG, "Post new task succeeded");
                Log.d(TAG, new String(responseBody));
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

    public static void getTasks() {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.addHeader("Authorization", "Token " + API_ID);
        asyncHttpClient.get(SERVER_ADDRESS + "tasks/", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d(TAG, "Getting tasks succeeded");
                String response = new String(responseBody);
                Log.d(TAG, response);

                JSONArray tasks;

                try {
                    tasks = new JSONArray(response);
                } catch (JSONException e) {
                    Log.d(TAG, "unable to parse tasks jsonarray");
                    return;
                }

                for (int i = 0; i < tasks.length(); i++) {
                    JSONObject jsonTask;
                    try {
                        jsonTask = (JSONObject) tasks.get(i);
                    } catch (JSONException e) {
                        Log.d(TAG, "unable to parse single task!");
                        continue;
                    }

                    Task task = parseTask(jsonTask);
                    if(task == null) {
                        Log.d(TAG, "parse task from json failed");
                        continue;
                    }

                    boolean taskUpdated = false;

                    for(int j = 0; j < OpenTasksFragment.openTasks.size(); j++) {
                        Task t = OpenTasksFragment.openTasks.get(j);
                        if(t.getId() == task.getId() && t.getCreator() == task.getCreator()) {
                            t.updateTask(task);
                            taskUpdated = true;
                            Log.d(TAG, "task updated!");
                            break;
                        }
                    }

                    if(!taskUpdated) {
                        OpenTasksFragment.openTasks.add(task);
                    }
                }
                MainActivity.updateDatasets();
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

    private static Task parseTask(JSONObject jsonTask) {
        Long id;
        Long responsibleMember = null;
        String title = "";
        String description = "";
        int creator;
        Date deadline = null;
        Date estimatedCompletion = null;

        try {
            id = jsonTask.getLong("id");
            creator = jsonTask.getInt("creator");
        } catch (JSONException e) {
            Log.d(TAG, "unable to parse task id or creator!");
            return null;
        }

        try { title = jsonTask.getString("title"); } catch (JSONException e) { }
        try { description = jsonTask.getString("description"); } catch (JSONException e) { }
        try { responsibleMember = jsonTask.getLong("responsible_member"); } catch (JSONException e) { }
        try { int deadlineEpoch = jsonTask.getInt("deadline"); deadline = new Date(deadlineEpoch); } catch (JSONException e) { }
        try { int estimatedCompletionEpoch = jsonTask.getInt("estimated_completion_time"); estimatedCompletion = new Date(estimatedCompletionEpoch); } catch (JSONException e) { }

        Task t = new Task(id, creator);
        t.setName(title);

        if (responsibleMember != null) {
            for (User u : MainActivity.users) {
                if (u.getId() == responsibleMember) {
                    t.setAssignedTo(u);
                    break;
                }
            }
        }

        if (deadline != null) { t.setDue(deadline); }
        if (estimatedCompletion != null) { t.setEstimatedCompletion(estimatedCompletion); }

        return t;
    }

    public void update() {
        register();
    }
}