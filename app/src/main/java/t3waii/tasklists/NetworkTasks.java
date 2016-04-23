package t3waii.tasklists;

import android.os.AsyncTask;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

/**
 * Created by moetto on 3/29/16.
 */
public class NetworkTasks {
    private final static String TAG = "NetworkTasks";

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
        asyncHttpClient.addHeader("Authorization", "Token " + MainActivity.getApiId());
        RequestParams params = new RequestParams();
        for(String key : values.keySet()) {
            params.add(key, values.get(key));
        }
        asyncHttpClient.post(MainActivity.getServerAddress() + "tasks/", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d(TAG, "Post new task succeeded");
                Log.d(TAG, new String(responseBody));
                // TODO: add to created and todo if assigned to self
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
        asyncHttpClient.addHeader("Authorization", "Token " + MainActivity.getApiId());
        asyncHttpClient.get(MainActivity.getServerAddress() + "tasks/", new AsyncHttpResponseHandler() {
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

                Long ownId = MainActivity.getSelfGroupMember().getId();
                List<Task> todo = new ArrayList<>();
                List<Task> created = new ArrayList<>();
                List<Task> open = new ArrayList<>();
                List<Task> completed = new ArrayList<>();

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

                    if(task.getCompleted()) {
                        completed.add(task);
                    } else if(task.getAssignedTo() != null && task.getAssignedTo().getId() == ownId) {
                        todo.add(task);
                        if(task.getCreator().getId() == ownId) {
                            created.add(task);
                        }
                    } else if(task.getCreator().getId() == ownId) {
                        created.add(task);
                    } else {
                        open.add(task);
                    }
                }

                //TODOTasksFragment.updateTasks(todo);
                //CreatedTasksFragment.updateTasks(created);
                //OpenTasksFragment.updateTasks(open);
                //CompletedTasksFragment.updateTasks(completed);

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
        boolean completed = false;
        Long responsibleMember = null;
        String title = "";
        String description = "";
        Long creatorId;
        Date deadline = null;
        Date estimatedCompletion = null;

        User creator = null;
        try {
            id = jsonTask.getLong("id");
            creatorId = jsonTask.getLong("creator");
            if(creatorId == MainActivity.getSelfGroupMember().getId()) {
                creator = MainActivity.getSelfGroupMember();
            }
            for(User u : MainActivity.users) {
                if(u.getId() == creatorId) {
                    creator = u;
                    break;
                }
            }
        } catch (JSONException e) {
            Log.d(TAG, "unable to parse task id or creator id!");
            return null;
        }

        if(creator == null) {
            Log.d(TAG, "Creator not found!");
            return null;
        }

        try { title = jsonTask.getString("title"); } catch (JSONException e) { }
        try { description = jsonTask.getString("description"); } catch (JSONException e) { }
        try { responsibleMember = jsonTask.getLong("responsible_member"); } catch (JSONException e) { }
        try { int deadlineEpoch = jsonTask.getInt("deadline"); deadline = new Date(deadlineEpoch); } catch (JSONException e) { }
        try { int estimatedCompletionEpoch = jsonTask.getInt("estimated_completion_time"); estimatedCompletion = new Date(estimatedCompletionEpoch); } catch (JSONException e) { }
        try { completed = jsonTask.getBoolean("completed"); } catch (JSONException e) { }

        Task t = new Task(id, creator);
        t.setName(title);

        if (responsibleMember != null) {
            if(responsibleMember == MainActivity.getSelfGroupMember().getId()) {
                t.setAssignedTo(MainActivity.getSelfGroupMember());
            } else {
                for (User u : MainActivity.users) {
                    if (u.getId() == responsibleMember) {
                        t.setAssignedTo(u);
                        break;
                    }
                }
            }
        }

        if (completed) { t.complete(); }
        if (deadline != null) { t.setDue(deadline); }
        if (estimatedCompletion != null) { t.setEstimatedCompletion(estimatedCompletion); }

        return t;
    }

    public void update() {
        register();
    }
}