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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_manage);

        List<User> userList = new ArrayList<>(MainActivity.getUsers());
        //TODO: add other users to list

        ArrayAdapter<User> groupMembersAdapter = new ArrayAdapter<User>(this, R.layout.group_manage_single_user, userList) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.group_manage_single_user, null);
                User user = getItem(position);

                ImageView avatar = (ImageView) view.findViewById(R.id.user_avatar);
                //TODO: set avatar

                TextView name = (TextView) view.findViewById(R.id.user_name);
                name.setText(user.getName());

                CheckBox selected = (CheckBox) view.findViewById(R.id.user_checkbox);

                if(MainActivity.getUsers().contains(user)) {
                    selected.setChecked(true);
                }
                return view;
            }
        };
        ListView listElement = (ListView)findViewById(R.id.users_list);
        listElement.setAdapter(groupMembersAdapter);
    }

    public void clickSaveButton(View v) {
        ListView userList = (ListView)findViewById(R.id.users_list);

        //TODO: implement update group members
        finish();
    }
}
