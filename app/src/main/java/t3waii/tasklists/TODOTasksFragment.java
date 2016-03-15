package t3waii.tasklists;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Created by moetto on 3/11/16.
 */
public class TODOTasksFragment extends ListFragment {
    public static final String TAG = "TODOTasksFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.todo_tasks_list, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(taskListAdapter);
    }

    BaseAdapter taskListAdapter = new BaseAdapter() {
        private final String[] dummyTasks = new String[]{"a", "b", "c", "d"};
        View.OnClickListener editClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Halp, I've been clicked!");
            }
        };

        @Override
        public int getCount() {
            return dummyTasks.length;
        }

        @Override
        public Object getItem(int position) {
            return dummyTasks[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            if (position == 1) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.group_task_parent, null);
                convertView.findViewById(R.id.edit_button).setOnClickListener(editClickListener);
                TextView parentText = (TextView)convertView.findViewById(R.id.complex_text);
                parentText.setText("parent text");
                LinearLayout linearLayout = (LinearLayout)convertView.findViewById(R.id.group_task_parent_layout);
                View childView = createComplexTask(getActivity().getLayoutInflater());
                TextView childText = (TextView)childView.findViewById(R.id.complex_text);
                childText.setText("child text");
                linearLayout.addView(childView, 1);
                return convertView;
            } else {
                convertView = createComplexTask(inflater);
                TextView textView = (TextView) convertView.findViewById(R.id.complex_text);
                textView.setText(dummyTasks[position]);
                return convertView;
            }
        }

        private View createComplexTask(LayoutInflater inflater) {
            View view = inflater.inflate(R.layout.complex_task, null);
            ImageButton settingsButton = (ImageButton)view.findViewById(R.id.edit_button);
            settingsButton.setOnClickListener(editClickListener);
            return view;
        }
    };
}
