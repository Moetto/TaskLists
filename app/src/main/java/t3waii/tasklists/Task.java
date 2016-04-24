package t3waii.tasklists;

import android.util.Log;

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
    private List<Task> children;
    private int responsibleMember;
    private long id;
    private int creator;
    private double longitude;
    private double latitude;
    private boolean completed;
    private static final String TAG = "TaskListsTask";
    public static final String
    ACTION_UPDATE_TASKS = "t3waii.tasklists.action_update_task",
    ACTION_GET_TASK = "t3waii.tasklists.action_update_task",
    ACTION_POST_TASK = "t3waii.tasklists.action_update_task",
    ACTION_REMOVE_TASK = "t3waii.tasklists.action_remove_task",
    EXTRA_TASK_AS_JSON_STRING = "extraTask",
    EXTRA_TASKS_AS_JSON_ARRAY = "extraTasks";



    public Task(long id, int creator) {
        this.id = id;
        this.creator = creator;
        this.name = "";
        this.children = new ArrayList<>();
        this.completed = false;
    }

    public Task(JSONObject taskAsJson) throws JSONException {

        try {
            id = taskAsJson.getLong("id");
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
            int deadlineEpoch = taskAsJson.getInt("deadline");
            deadline = new Date(deadlineEpoch);
        } catch (JSONException e) {
        }
        try {
            int estimatedCompletionEpoch = taskAsJson.getInt("estimated_completion_time");
            estimatedCompletion = new Date(estimatedCompletionEpoch);
        } catch (JSONException e) {
        }
        children = new ArrayList<>();
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

    public List<Task> getChildren() {
        return this.children;
    }

    public void addChild(Task childTask) {
        this.children.add(childTask);
    }

    public void removeChild(Task childTask) {
        for (int i = 0; i < this.children.size(); i++) {
            if (this.children.get(i).getId() == childTask.getId()) {
                this.children.remove(i);
                return;
            }
        }
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
        this.children = newValues.getChildren();
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

    public long getId() {
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
