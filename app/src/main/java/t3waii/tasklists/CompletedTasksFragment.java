package t3waii.tasklists;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matti on 4/14/16.
 */

public class CompletedTasksFragment extends TasksFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.todo_tasks_list, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        TAG = "TaskCompletedFragment";
        ArrayAdapter<Task> taskListAdapter = new ArrayAdapter<Task>(getContext(), R.layout.complex_task, tasks) {

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
                hideUnnecessaryButtons(view);
                return view;
            }

            private void hideUnnecessaryButtons(View v) {
                View claimButton = v.findViewById(R.id.claim_button);
                claimButton.setVisibility(View.GONE);
                View completeButton = v.findViewById(R.id.complete_button);
                completeButton.setVisibility(View.GONE);
                View editButton = v.findViewById(R.id.edit_button);
                editButton.setVisibility(View.GONE);
            }
        };

        super.onActivityCreated(savedInstanceState);
        setListAdapter(taskListAdapter);
    }

    @Override
    protected boolean affectThisFragment(Task task) {
        return task.getCompleted();
    }

    @Override
    public void reallyAddTaskThisTime(Task task) {
        if (task.getCreator() == MainActivity.getSelfGroupMemberId() && task.getResponsibleMemberId() != MainActivity.getSelfGroupMemberId()) {
            Intent onClickIntent = new Intent(getContext(), MainActivity.class);
            onClickIntent.setAction(Task.ACTION_TASK_COMPLETE);
            PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), task.getId(), onClickIntent, PendingIntent.FLAG_ONE_SHOT);

            NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = new Notification.Builder(getContext())
                    .setContentText("Your task "+task.getName() + " has been completed")
                    .setSmallIcon(android.R.drawable.sym_def_app_icon)
                    .setContentIntent(pendingIntent)
                    .build();
            notificationManager.notify(task.getId(), notification);
        }
        super.reallyAddTaskThisTime(task);
    }
}
