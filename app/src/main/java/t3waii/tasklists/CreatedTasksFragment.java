package t3waii.tasklists;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matti on 4/13/16.
 */

public class CreatedTasksFragment extends TasksFragment implements PopupMenu.OnMenuItemClickListener {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.todo_tasks_list, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        TAG = "CreatedTasksFragment";
        ArrayAdapter<Task> taskListAdapter = new ArrayAdapter<Task>(getContext(), R.layout.complex_task, tasks) {
            View.OnClickListener handleClick = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Task t = null;

                    try {
                        t = (Task) v.getTag();
                    } catch (NullPointerException e) {
                        Log.d(TAG, "Unable to get Task from tag!");
                    }

                    switch (v.getId()) {
                        case R.id.claim_button:
                            Log.d(TAG, "claim clicked");
                            if(t != null) {
                                NetworkTasks.claimTask(getContext(), t.getId());
                            }
                            break;
                        case R.id.edit_button:
                            PopupMenu popupMenu = new PopupMenu(getActivity(), v);
                            popupMenu.getMenuInflater().inflate(R.menu.edit_task_created, popupMenu.getMenu());
                            Intent i = new Intent();
                            i.putExtra("taskId", t.getId());
                            for(int j = 0; j < popupMenu.getMenu().size(); j++) {
                                MenuItem menuItem = popupMenu.getMenu().getItem(j);
                                menuItem.setIntent(i);
                            }
                            popupMenu.setOnMenuItemClickListener(CreatedTasksFragment.this);
                            popupMenu.show();
                            break;
                        default:
                            Log.d(TAG, "Halp, I've been clicked but I don't know where!");
                    }
                }
            };

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                Task task = tasks.get(position);
                convertView = createComplexTask(inflater, task);
                TextView textView = (TextView) convertView.findViewById(R.id.complex_text);
                textView.setText(task.getName());
                return convertView;
            }

            private View createComplexTask(LayoutInflater inflater, Task task) {
                View view = inflater.inflate(R.layout.complex_task, null);
                View completeButton = view.findViewById(R.id.complete_button);
                completeButton.setVisibility(View.GONE);
                addListenerAndTag(view, task);
                return view;
            }

            private void addListenerAndTag(View v, Task t) {
                ImageButton imageButton = (ImageButton) v.findViewById(R.id.edit_button);
                imageButton.setOnClickListener(handleClick);
                imageButton.setTag(t);
                imageButton = (ImageButton) v.findViewById(R.id.claim_button);
                imageButton.setOnClickListener(handleClick);
                imageButton.setTag(t);
            }
        };

        super.onActivityCreated(savedInstanceState);
        setListAdapter(taskListAdapter);
    }

    public List<Task> getUnclaimedTasks() {
        List<Task> unclaimedTasks = new ArrayList<>();
        for(Task t : tasks) {
            if(t.getResponsibleMemberId() == 0) {
                unclaimedTasks.add(t);
            }
        }
        return unclaimedTasks;
    }

    @Override
    protected boolean affectThisFragment(Task task) {
        return task.getCreator() == MainActivity.getSelfGroupMemberId() && !task.getCompleted();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int taskId = item.getIntent().getIntExtra("taskId", 0);
        Log.d(TAG, "taskId:" + Integer.toString(taskId)); //getActionView().toString()

        switch (item.getItemId()) {
            case R.id.edit_task:
                Log.d(TAG, "edit clicked");
                return true;
            case R.id.remove_task:
                Log.d(TAG, "remove clicked");
                NetworkTasks.deleteTask(getContext(), taskId);
                return true;
            default:
                Log.d(TAG, "dunno what was clicked");
                return false;
        }
    }
}
