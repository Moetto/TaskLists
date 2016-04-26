package t3waii.tasklists;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matti on 3/30/16.
 */
public class ManageGroupActivity extends Activity {
    private static final String TAG = "TaskManageGroup";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_manage);

        List<User> userList = new ArrayList<>();
        userList.addAll(MainActivity.getGroupMembers());
        userList.addAll(MainActivity.getNonMembers());

        //TODO: add other users to list

        ArrayAdapter<User> groupMembersAdapter = new ArrayAdapter<User>(this, R.layout.group_manage_single_user, userList) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.group_manage_single_user, null);
                User user = getItem(position);
                view.setTag(R.id.userTag, user);
                ImageView avatar = (ImageView) view.findViewById(R.id.user_avatar);
                //TODO: set avatar

                TextView name = (TextView) view.findViewById(R.id.user_name);
                name.setText(user.getName());

                CheckBox selected = (CheckBox) view.findViewById(R.id.user_checkbox);

                if (MainActivity.getGroupMembers().contains(user)) {
                    selected.setChecked(true);
                }
                return view;
            }
        };
        ListView listElement = (ListView) findViewById(R.id.users_list);
        listElement.setAdapter(groupMembersAdapter);
    }

    public void clickSaveButton(View v) {
        ListView userList = (ListView) findViewById(R.id.users_list);
        List<User> removed = new ArrayList<>(), invited = new ArrayList<>(), inCurrentGroup = MainActivity.getGroupMembers(), notInGroup = MainActivity.getNonMembers();

        for (int i = 0; i < userList.getAdapter().getCount(); i++) {
            View userView = userList.getChildAt(i);
            User user = (User) userView.getTag(R.id.userTag);
            CheckBox checkBox = (CheckBox) userView.findViewById(R.id.user_checkbox);
            boolean inGroup = checkBox.isChecked();
            if (inGroup && notInGroup.contains(user)) {
                invited.add(user);
                Log.d(TAG, "Invited user " + user.getName());
            } else if (!inGroup && inCurrentGroup.contains(user)) {
                removed.add(user);
                Log.d(TAG, "Removed user " + user.getName());
            }
            NetworkInvites.postInvites(this, invited);
            NetworkGroupMembers.deleteGroupMembers(this, removed);
        }

        findViewById(R.id.user_checkbox);
        //TODO: implement update group members
        finish();
    }
}
