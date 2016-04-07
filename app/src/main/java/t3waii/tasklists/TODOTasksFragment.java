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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.List;


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
        View.OnClickListener editClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: find the textview element nearby and gettag from that
                //int id = (int) v.getTag();
                //Log.d(TAG, Integer.toString(id));

                Log.d(TAG, "Halp, I've been clicked!");
                PopupMenu popupMenu = new PopupMenu(getActivity(), v);
                popupMenu.getMenuInflater().inflate(R.menu.edit_task, popupMenu.getMenu());
                popupMenu.show();
            }

        };

        @Override
        public int getCount() {
            return MainActivity.tasks.size();
        }

        @Override
        public Object getItem(int position) {
            return MainActivity.tasks.get(position);
        }

        @Override
        public long getItemId(int position) {
            return MainActivity.tasks.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            //TODO: FIX THIS TO WORK WITH TASK.JAVA
            Task task = MainActivity.tasks.get(position);
            if(task.getChildren().isEmpty()) {
                convertView = createComplexTask(inflater);
                TextView textView = (TextView) convertView.findViewById(R.id.complex_text);
                textView.setTag(task.getId());
                textView.setText(MainActivity.tasks.get(position).getName());
                return convertView;
            }

            convertView = getActivity().getLayoutInflater().inflate(R.layout.group_task_parent, null);
            convertView.findViewById(R.id.edit_button).setOnClickListener(editClickListener);
            TextView parentText = (TextView) convertView.findViewById(R.id.complex_text);
            parentText.setTag(task.getId());
            parentText.setText(task.getName());

            LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.group_task_parent_layout);
            List<Task> children = task.getChildren();
            for(int i = 0; i < children.size(); i++) {
                View childView = createChildView(getActivity().getLayoutInflater(), (i == (children.size()-1) ? true : false));
                TextView childText = (TextView) childView.findViewById(R.id.complex_text);
                childText.setTag(task.getChildren().get(i).getId());
                childText.setText(children.get(i).getName());
                linearLayout.addView(childView, (i+1));
            }
            return convertView;
        }

        private View createComplexTask(LayoutInflater inflater) {
            View view = inflater.inflate(R.layout.complex_task, null);
            ImageButton settingsButton = (ImageButton) view.findViewById(R.id.edit_button);
            settingsButton.setOnClickListener(editClickListener);
            return view;
        }

        private View createChildView(LayoutInflater inflater, boolean lastChild) {
            View view = inflater.inflate(R.layout.child_complex_task, null);
            if (lastChild){
                ImageView imageView = (ImageView)view.findViewById(R.id.arrow);
                imageView.setImageResource(R.drawable.tree_end);
            }
            ImageButton settingsButton = (ImageButton) view.findViewById(R.id.edit_button);
            settingsButton.setOnClickListener(editClickListener);
            return view;
        }
    };
}
