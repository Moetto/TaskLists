package t3waii.tasklists;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by matti on 4/7/16.
 */
public class Task implements Serializable {
    private String name, description;
    private Date deadline, estimatedCompletion;
    private int responsibleMember;
    private int id;
    private int creator;
    private double longitude;
    private double latitude;
    private boolean completed;
    private static final String TAG = "TaskListsTask";
    public static final String
            ACTION_UPDATE_TASKS = "t3waii.tasklists.action_update_tasks",
            ACTION_GET_TASK = "t3waii.tasklists.action_get_task",
            ACTION_POST_TASK = "t3waii.tasklists.action_post_task",
            ACTION_UPDATE_TASK = "t3waii.tasklists.action_update_task",
            ACTION_REMOVE_TASK = "t3waii.tasklists.action_remove_task",
            ACTION_REMOVE_TASK_BY_ID = "t3waii.tasklists.action_remove_task_id",
            ACTION_TASKS_SHOULD_UPDATE = "t3waii.tasklists.action_should_update_tasks",
            ACTION_TASK_COMPLETE = "t3waii.tasklists.action_task_complete",
            EXTRA_TASK_AS_JSON_STRING = "extraTask",
            EXTRA_TASKS_AS_JSON_ARRAY = "extraTasks",
            EXTRA_TASK_ID = "extraTaskId";


    public Task(int id, int creator) {
        this.id = id;
        this.creator = creator;
        this.name = "";
        this.completed = false;
    }

    public Task(JSONObject taskAsJson) throws JSONException {

        try {
            id = taskAsJson.getInt("id");
            creator = taskAsJson.getInt("creator");
        } catch (JSONException e) {
            Log.d(TAG, "unable to parse task id or creator!");
            throw e;
        }

        try {
            name = taskAsJson.getString("title");
        } catch (JSONException e) {
        }
        try {
            description = taskAsJson.getString("description");
        } catch (JSONException e) {
        }
        try {
            responsibleMember = taskAsJson.getInt("responsible_member");
        } catch (JSONException e) {
        }
        try {
            Long deadlineEpoch = taskAsJson.getLong("deadline");
            deadline = new Date(deadlineEpoch);
        } catch (JSONException e) {
        }
        try {
            int estimatedCompletionEpoch = taskAsJson.getInt("estimated_completion_time");
            estimatedCompletion = new Date(estimatedCompletionEpoch);
        } catch (JSONException e) {
        }
        int locationId = 0;
        try {
            locationId = taskAsJson.getInt("location");
            Log.d(TAG, "Should add location for " + name);
        } catch (JSONException ex) {
        }
        if (locationId != 0) {
            List<Location> locations = MainActivity.getLocations();
            for (Location location : locations) {
                if (location.getId() == locationId) {
                    LatLng latLng = location.getLatlng();
                    latitude = latLng.latitude;
                    longitude = latLng.longitude;
                    Log.d(TAG, "Added location for task");
                    break;
                }
            }
        }
        try {
            completed = taskAsJson.getBoolean("completed");
        } catch (JSONException e) {

        }
    }

    public String getName() {
        return this.name;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public Date getDeadline() {
        return this.deadline;
    }

    public void setDeadline(Date newDue) {
        this.deadline = newDue;
    }

    public Date getEstimatedCompletion() {
        return this.estimatedCompletion;
    }

    public void setEstimatedCompletion(Date newEstimatedCompletion) {
        this.estimatedCompletion = newEstimatedCompletion;
    }

    public boolean getCompleted() {
        return this.completed;
    }

    public void complete() {
        this.completed = true;
    }

    public void updateTask(Task newValues) {
        this.name = newValues.getName();
        this.deadline = newValues.getDeadline();
        this.responsibleMember = newValues.getResponsibleMemberId();
    }

    public int getResponsibleMemberId() {
        return this.responsibleMember;
    }

    public void setAssignedTo(User newAssignedTo) {
        this.responsibleMember = newAssignedTo.getId();
    }

    public int getCreator() {
        return this.creator;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Task)) {
            return false;
        }

        Task t = (Task) o;

        if (this.getId() == t.getId()) {
            return true;
        }
        return false;
    }

}
