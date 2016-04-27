package t3waii.tasklists;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matti on 4/23/16.
 */
public abstract class TasksFragment extends ListFragment {
    protected String TAG = "TasksFragment";
    protected List<Task> tasks = new ArrayList<>();
    protected BroadcastReceiver broadcastReceiver;
    protected List<IntentFilter> intentFilters = new ArrayList<>();

    // Add or update given task and update dataset
    public void addTask(Task task) {
        for (Task t : tasks) {
            if (t.getId() == task.getId() && t.getCreator() == task.getCreator()) {
                t.updateTask(task);
                MainActivity.updateDatasets();
                Log.d(TAG, "Updated existing task");
                return;
            }
        }
        Log.d(TAG, "Add task");
        reallyAddTaskThisTime(task);
    }

    public void reallyAddTaskThisTime(Task task ){
        tasks.add(task);
        MainActivity.updateDatasets();
    }

    // Remove given task from list and update dataset
    public void removeTask(Task task) {
        for (Task t : tasks) {
            if (t.getId() == task.getId() && t.getCreator() == task.getCreator()) {
                tasks.remove(t);
                MainActivity.updateDatasets();
                return;
            }
        }
    }

    // Remove task with given id from list and update dataset
    public void removeTask(int id) {
        for(Task t : tasks) {
            if(t.getId() == id) {
                tasks.remove(t);
                MainActivity.updateDatasets();
                return;
            }
        }
    }

    // Update tasks to match the given list
    public void updateTasks(List<Task> updatedTasks) {
        // Remove tasks that do not exist in updated list
        ArrayList<Task> remove = new ArrayList<>();
        for (Task existingTask : tasks) {
            if (!updatedTasks.contains(existingTask)) {
                remove.add(existingTask);
            }
        }
        tasks.removeAll(remove);

        // Loop updated list and either update or add task
        for (Task newTask : updatedTasks) {
            if (tasks.contains(newTask)) {
                for (Task existingTask : tasks) {
                    if (existingTask.equals(newTask)) {
                        existingTask.updateTask(newTask);
                        break;
                    }
                }
            } else {
                addTask(newTask);
            }
        }
        MainActivity.updateDatasets();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Received broadcast");
                switch (intent.getAction()) {
                    case Task.ACTION_UPDATE_TASKS:
                        try {
                            JSONArray tasksArray = new JSONArray(intent.getStringExtra(Task.EXTRA_TASKS_AS_JSON_ARRAY));
                            List<Task> updatedTasks = new ArrayList<>();
                            for (int i = 0; i < tasksArray.length(); i++) {
                                Task task = new Task(tasksArray.getJSONObject(i));
                                if (affectThisFragment(task)) {
                                    updatedTasks.add(task);
                                }
                            }
                            updateTasks(updatedTasks);
                        } catch (JSONException ex) {
                            Log.d(TAG, Log.getStackTraceString(ex));
                        }
                        break;

                    case Task.ACTION_POST_TASK:
                        try {
                            JSONObject taskJson = new JSONObject(intent.getStringExtra(Task.EXTRA_TASK_AS_JSON_STRING));
                            Task task = new Task(taskJson);
                            if (affectThisFragment(task)) {
                                addTask(task);
                            }
                        } catch (JSONException ex) {
                            Log.d(TAG, Log.getStackTraceString(ex));
                        }
                        break;

                    case Task.ACTION_REMOVE_TASK:
                        try {
                            JSONObject taskJson = new JSONObject(intent.getStringExtra(Task.EXTRA_TASK_AS_JSON_STRING));
                            Task task = new Task(taskJson);
                            removeTask(task);
                        } catch (JSONException ex) {
                            Log.d(TAG, Log.getStackTraceString(ex));
                        }
                        break;

                    case Task.ACTION_REMOVE_TASK_BY_ID:
                        int taskId = intent.getIntExtra(Task.EXTRA_TASK_ID, 0);
                        removeTask(taskId);
                        break;

                    case Task.ACTION_UPDATE_TASK:
                        try {
                            JSONObject taskJson = new JSONObject(intent.getStringExtra(Task.EXTRA_TASK_AS_JSON_STRING));
                            Task task = new Task(taskJson);
                            if(handleEditTask(task)) {
                                MainActivity.updateDatasets();
                            }
                        } catch (JSONException ex) {
                            Log.d(TAG, Log.getStackTraceString(ex));
                        }
                        break;
                }
            }
        };

        for (String action : new String[]{
                Task.ACTION_GET_TASK,
                Task.ACTION_POST_TASK,
                Task.ACTION_UPDATE_TASK,
                Task.ACTION_UPDATE_TASKS,
                Task.ACTION_REMOVE_TASK,
                Task.ACTION_REMOVE_TASK_BY_ID}) {
            intentFilters.add(new IntentFilter(action));
        }

        for (IntentFilter intentFilter : intentFilters) {
            getActivity().registerReceiver(broadcastReceiver, intentFilter);
        }

    }

    protected boolean handleEditTask(Task task) {
        for(Task t : tasks) {
            if(t.equals(task)) {
                if(affectThisFragment(task)) {
                    t.updateTask(task);
                } else {
                    tasks.remove(t);
                }
                return true;
            }
        }
        if(affectThisFragment(task)) {
            reallyAddTaskThisTime(task);
            return true;
        }
        return false;
    }

    protected boolean affectThisFragment(Task task) {
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            try {
                getActivity().unregisterReceiver(broadcastReceiver);
            } catch (IllegalArgumentException ex) {

            }
        }
    }

    public List<Task> getTasks() {
        return new ArrayList<>(tasks);
    }
}
