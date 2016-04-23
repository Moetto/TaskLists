package t3waii.tasklists;

import android.support.v4.app.ListFragment;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matti on 4/23/16.
 */
public class TasksFragment extends ListFragment {
    protected static List<Task> tasks = new ArrayList<>();


    // Add or update given task and update dataset
    public static void addTask(Task task) {
        for(Task t : tasks) {
            if(t.getId() == task.getId() && t.getCreator() == task.getCreator()) {
                t.updateTask(task);
                MainActivity.updateDatasets();
                return;
            }
        }

        tasks.add(task);
        MainActivity.updateDatasets();
    }

    // Remove given task from list and update dataset
    public static void removeTask(Task task) {
        for(Task t : tasks) {
            if(t.getId() == task.getId() && t.getCreator() == task.getCreator()) {
                tasks.remove(t);
                MainActivity.updateDatasets();
                return;
            }
        }
    }

    // Update tasks to match the given list
    public static void updateTasks(List<Task> updatedTasks) {
        // Remove tasks that do not exist in updated list
        for(Task existingTask : tasks) {
            if(!updatedTasks.contains(existingTask)) {
                tasks.remove(existingTask);
            }
        }

        // Loop updated list and either update or add task
        for(Task newTask : updatedTasks) {
            if(tasks.contains(newTask)) {
                for(Task existingTask : tasks) {
                    if(existingTask.equals(newTask)) {
                        existingTask.updateTask(newTask);
                        break;
                    }
                }
            } else {
                tasks.add(newTask);
            }
        }
    }
}
