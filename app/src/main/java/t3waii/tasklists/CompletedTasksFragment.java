package t3waii.tasklists;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
        TAG = "CompletedTasksFragment";
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
}
