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
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by moetto on 3/11/16.
 */
public class TODOTasksFragment extends ListFragment {
    public static final String TAG = "TODOTasksFragment";
    public static ArrayAdapter<Task> taskListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.todo_tasks_list, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        taskListAdapter = new ArrayAdapter<Task>(getContext(), R.layout.complex_task, new ArrayList<Task>()) {
            View.OnClickListener handleClick = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Task t;

                    try {
                        t = (Task) v.getTag();
                        Log.d(TAG, t.toString());
                    } catch (NullPointerException e) {
                        Log.d(TAG, "NO TAGGED TASK");
                    }

                    switch (v.getId()) {
                        case R.id.complete_button:
                            Log.d(TAG, "complete clicked");
                            break;
                        case R.id.edit_button:
                            Log.d(TAG, "edit clicked");
                            PopupMenu popupMenu = new PopupMenu(getActivity(), v);
                            popupMenu.getMenuInflater().inflate(R.menu.edit_task, popupMenu.getMenu());
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
                Task task = taskListAdapter.getItem(position);
                if(task.getChildren().isEmpty()) {
                    convertView = createComplexTask(inflater, task);
                    TextView textView = (TextView) convertView.findViewById(R.id.complex_text);
                    textView.setText(task.getName());
                    return convertView;
                }

                convertView = getActivity().getLayoutInflater().inflate(R.layout.group_task_parent, null);
                addListenerAndTag(convertView, task);
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
                addListenerAndTag(view, task);
                return view;
            }

            private View createChildView(LayoutInflater inflater, boolean lastChild, Task childTask) {
                View view = inflater.inflate(R.layout.child_complex_task, null);
                if (lastChild){
                    ImageView imageView = (ImageView)view.findViewById(R.id.arrow);
                    imageView.setImageResource(R.drawable.tree_end);
                }
                addListenerAndTag(view, childTask);
                return view;
            }

            private void addListenerAndTag(View v, Task t) {
                ImageButton imageButton = (ImageButton) v.findViewById(R.id.edit_button);
                imageButton.setOnClickListener(handleClick);
                imageButton.setTag(t);
                imageButton = (ImageButton) v.findViewById(R.id.complete_button);
                imageButton.setOnClickListener(handleClick);
                imageButton.setTag(t);
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
