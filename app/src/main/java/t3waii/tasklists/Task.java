package t3waii.tasklists;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by matti on 4/7/16.
 */
public class Task {
    private String name;
    private Date due;
    private List<Task> children;
    private User assignedTo;
    private long id;
    private int creator;

    public Task(long id, int creator) {
        this.id = id;
        this.creator = creator;
        this.name = "";
        this.children = new ArrayList<>();
    }

    public String getName() { return this.name; }
    public void setName(String newName) { this.name = newName; }

    public Date getDue() { return this.due; }
    public void setDue(Date newDue) { this.due = newDue; }

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

    public User getAssignedTo() { return this.assignedTo; }
    public void setAssignedTo(User newAssignedTo) { this.assignedTo = newAssignedTo; }

    public int getCreator() { return this.creator; }
    public long getId() { return this.id; }

    @Override
    public String toString() { return this.id + ":" + this.name; }
}
