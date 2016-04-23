package t3waii.tasklists;

import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.List;

/**
 * Created by matti on 4/7/16.
 */
public class Task {
    private String name;
    private Date due, estimatedCompletion;
    private List<Task> children;
    private User assignedTo;
    private long id;
    private User creator;
    private double longitude;
    private double latitude;
    private boolean completed;

    public Task(long id, User creator) {
        this.id = id;
        this.creator = creator;
        this.name = "";
        this.children = new ArrayList<>();
        this.completed = false;
    }

    public String getName() { return this.name; }
    public void setName(String newName) { this.name = newName; }

    public Date getDue() { return this.due; }
    public void setDue(Date newDue) { this.due = newDue; }

    public Date getEstimatedCompletion() { return this.estimatedCompletion; }
    public void setEstimatedCompletion(Date newEstimatedCompletion) { this.estimatedCompletion = newEstimatedCompletion; }

    public List<Task> getChildren() { return this.children; }
    public void addChild(Task childTask) { this.children.add(childTask); }

    public void removeChild(Task childTask) {
        for(int i = 0; i < this.children.size(); i++) {
            if(this.children.get(i).getId() == childTask.getId()) {
                this.children.remove(i);
                return;
            }
        }
    }

    public boolean getCompleted() { return this.completed; }
    public void complete() { this.completed = true; }

    public void updateTask(Task newValues) {
        this.name = newValues.getName();
        this.due = newValues.getDue();
        this.children = newValues.getChildren();
        this.assignedTo = newValues.getAssignedTo();
    }

    public User getAssignedTo() { return this.assignedTo; }
    public void setAssignedTo(User newAssignedTo) { this.assignedTo = newAssignedTo; }

    public User getCreator() { return this.creator; }
    public long getId() { return this.id; }

    @Override
    public String toString() { return this.name; }

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
        if(!(o instanceof Task)) {
            return false;
        }

        Task t = (Task) o;

        if(this.getId() == t.getId() && this.getCreator() == t.getCreator()) {
            return true;
        }

        return false;
    }
}
