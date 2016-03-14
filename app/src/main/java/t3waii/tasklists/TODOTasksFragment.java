package t3waii.tasklists;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


/**
 * Created by moetto on 3/11/16.
 */
public class TODOTasksFragment extends ListFragment{
    public static final String TAG = "TODOTasksFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.todo_tasks_list, container, false);
        Bundle args = getArguments();

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //ArrayAdapter adapter =  new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, new String[]{"1", "2","3","4","5","6","7","8"});
        setListAdapter(taskListAdapter);
        //getListView().setOnItemClickListener(this);
    }
    /*
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "Halp, I've been clicked");
    }
    */
    BaseAdapter taskListAdapter = new BaseAdapter(){
        private final String[] dummyTasks = new String[]{"a","b","c","d"};
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
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.complex_task, null);
            }
            TextView textView = (TextView)convertView.findViewById(R.id.complex_text);
            textView.setText(dummyTasks[position]);
            return convertView;
        }
    };
}
