package t3waii.tasklists;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by matti on 4/13/16.
 */

public class OpenTasksFragment extends TasksFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.todo_tasks_list, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        TAG = "OpenTasksFragment";
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
                if (task.getChildren().isEmpty()) {
                    convertView = createComplexTask(inflater, task);
                    TextView textView = (TextView) convertView.findViewById(R.id.complex_text);
                    textView.setText(task.getName());
                    return convertView;
                }

                convertView = getActivity().getLayoutInflater().inflate(R.layout.group_task_parent, null);
                hideUnnecessaryButtons(convertView);
                addListenerAndTag(convertView, task);
                TextView parentText = (TextView) convertView.findViewById(R.id.complex_text);
                parentText.setText(task.getName());

                LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.group_task_parent_layout);
                List<Task> children = task.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    View childView = createChildView(getActivity().getLayoutInflater(), (i == (children.size() - 1) ? true : false), children.get(i));
                    TextView textView = (TextView) childView.findViewById(R.id.complex_text);
                    textView.setText(children.get(i).getName());
                    linearLayout.addView(childView, (i + 1));
                }
                return convertView;
            }

            private View createComplexTask(LayoutInflater inflater, Task task) {
                View view = inflater.inflate(R.layout.complex_task, null);
                hideUnnecessaryButtons(view);
                addListenerAndTag(view, task);
                return view;
            }

            private View createChildView(LayoutInflater inflater, boolean lastChild, Task childTask) {
                View view = inflater.inflate(R.layout.child_complex_task, null);
                hideUnnecessaryButtons(view);
                if (lastChild) {
                    ImageView imageView = (ImageView) view.findViewById(R.id.arrow);
                    imageView.setImageResource(R.drawable.tree_end);
                }
                addListenerAndTag(view, childTask);
                return view;
            }

            private void addListenerAndTag(View v, Task t) {
                ImageButton imageButton = (ImageButton) v.findViewById(R.id.claim_button);
                imageButton.setOnClickListener(handleClick);
                imageButton.setTag(t);
            }

            private void hideUnnecessaryButtons(View v) {
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
        return task.getResponsibleMemberId() == 0 && !task.getCompleted();
    }
}
