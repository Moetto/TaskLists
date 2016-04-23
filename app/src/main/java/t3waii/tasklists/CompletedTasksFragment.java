package t3waii.tasklists;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matti on 4/14/16.
 */

public class CompletedTasksFragment extends TasksFragment {
    public static final String TAG = "CompletedTasksFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.todo_tasks_list, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        ArrayAdapter<Task> taskListAdapter = new ArrayAdapter<Task>(getContext(), R.layout.complex_task, new ArrayList<Task>()) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                Task task = tasks.get(position);
                if(task.getChildren().isEmpty()) {
                    convertView = createComplexTask(inflater, task);
                    TextView textView = (TextView) convertView.findViewById(R.id.complex_text);
                    textView.setText(task.getName());
                    return convertView;
                }

                convertView = getActivity().getLayoutInflater().inflate(R.layout.group_task_parent, null);
                hideUnnecessaryButtons(convertView);
                TextView parentText = (TextView) convertView.findViewById(R.id.complex_text);
                parentText.setText(task.getName());

                LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.group_task_parent_layout);
                List<Task> children = task.getChildren();
                for(int i = 0; i < children.size(); i++) {
                    View childView = createChildView(getActivity().getLayoutInflater(), (i == (children.size()-1) ? true : false), children.get(i));
                    TextView textView = (TextView) childView.findViewById(R.id.complex_text);
                    textView.setText(children.get(i).getName());
                    linearLayout.addView(childView, (i+1));
                }
                return convertView;
            }

            private View createComplexTask(LayoutInflater inflater, Task task) {
                View view = inflater.inflate(R.layout.complex_task, null);
                hideUnnecessaryButtons(view);
                return view;
            }

            private View createChildView(LayoutInflater inflater, boolean lastChild, Task childTask) {
                View view = inflater.inflate(R.layout.child_complex_task, null);
                hideUnnecessaryButtons(view);
                if (lastChild){
                    ImageView imageView = (ImageView)view.findViewById(R.id.arrow);
                    imageView.setImageResource(R.drawable.tree_end);
                }
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

        Task t = new Task(1, 10);
        t.setName("Task1");
        taskListAdapter.add(t);
        t = new Task(2, 10);
        t.setName("Task2");
        Task t2 = new Task(100, 10);
        t2.setName("Child1");
        t.addChild(t2);;
        t2 = new Task(101, 10);
        t2.setName("Child2");
        t.addChild(t2);
        taskListAdapter.add(t);
        t = new Task(3, 10);
        t.setName("Task3");
        taskListAdapter.add(t);
        t = new Task(4, 10);
        t.setName("Task4");
        taskListAdapter.add(t);
    }
}
