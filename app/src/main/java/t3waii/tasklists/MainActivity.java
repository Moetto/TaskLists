package t3waii.tasklists;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MainActivity extends AppCompatActivity implements SignInListener {

    private static final int register = 100;
    private static final String TAG = "TasksMainActivity";
    private static String apiId = "";
    private static String serverAddress;
    private Menu menu;
    FragmentPagerAdapter pagerAdapter;
    ViewPager pager;
    TabLayout tabs;
    String ACCOUNT_MANAGER = "accountmanager";
    private static Set<User> users = new HashSet<>();
    private static Set<User> groupMembers = new HashSet<>();
    private static Set<Location> locations = new HashSet<>();
    private static List<Fragment> fragments = new ArrayList<>();
    private static int selfGroupMemberId, groupId;
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fragments.clear();
        fragments.add(new TODOTasksFragment());
        fragments.add(new CreatedTasksFragment());
        fragments.add(new OpenTasksFragment());
        fragments.add(new CompletedTasksFragment());

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        GoogleAccountManager googleAccountManager = new GoogleAccountManager();
        fragmentTransaction.add(googleAccountManager, ACCOUNT_MANAGER);
        fragmentTransaction.commit();
        fragmentManager.executePendingTransactions();
        googleAccountManager.setSignInListener(this);
        if (!googleAccountManager.isSignedIn()) {
            googleAccountManager.login();
        }
        pagerAdapter =
                new TabAdapter(
                        getSupportFragmentManager());
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(pagerAdapter);

        tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(pager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, NewTask.class));
            }
        });
        serverAddress = getString(R.string.server_url);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case NetworkRegister.ACTION_REGISTERED:
                        Log.d(TAG, "registered");
                        selfGroupMemberId = intent.getIntExtra(NetworkRegister.EXTRA_MEMBER_ID, 0);
                        apiId = intent.getStringExtra(NetworkRegister.EXTRA_AUTH_TOKEN);
                        int groupId = intent.getIntExtra(NetworkRegister.EXTRA_GROUP_ID, 0);
                        if (groupId != 0) {
                            NetworkGroups.getGroup(context, groupId);
                        }
                        NetworkGroupMembers.getAllUsers(context);
                        break;
                    case Group.ACTION_GET_GROUP:
                        Log.d(TAG, "Got group");
                        setMainMenuGroupItemsVisibility(true);
                        MainActivity.groupId = intent.getIntExtra(Group.EXTRA_GROUP_ID, 0);
                        NetworkTasks.getTasks(context);
                        NetworkLocations.getLocations(context);
                        break;
                    case NetworkGroupMembers.ACTION_UPDATE_USERS:
                        Log.d(TAG, "Got updated list of users");
                        JSONArray jsonUsers;
                        try {
                            jsonUsers = new JSONArray(intent.getStringExtra(NetworkGroupMembers.EXTRA_USER_JSON_ARRAY_STRING));
                        } catch (JSONException e) {
                            Log.e(TAG, "Error in users JSON");
                            Log.e(TAG, Log.getStackTraceString(e));
                            break;
                        }
                        updateUsers(jsonUsers);
                        Log.d(TAG, "users:" + users.size());
                        break;
                    case Invite.ACTION_INVITE_SENT:
                        Log.d(TAG, "Invite sent");
                        Toast.makeText(context, "Invite sent", Toast.LENGTH_SHORT).show();
                        break;
                    case Group.ACTION_LEAVE_GROUP:
                        Log.d(TAG, "Left group");
                        groupId = 0;
                        break;
                    case Location.ACTION_GET_LOCATIONS:
                        Log.d(TAG, "Got list of locations");
                        locations.clear();
                        try {
                            Log.d(TAG, intent.getStringExtra(Location.EXTRA_LOCATIONS_JSON));
                            for (Location location : Location.parseLocations(intent.getStringExtra(Location.EXTRA_LOCATIONS_JSON))) {
                                locations.add(location);
                            }
                       } catch (JSONException ex) {
                            Log.e(TAG, "Error in locations JSON");
                            Log.e(TAG, Log.getStackTraceString(ex));
                        }
                        break;
                    case Location.ACTION_NEW_LOCATION:
                        try {
                            addLocation(new Location(new JSONObject(intent.getStringExtra(Location.EXTRA_LOCATION))));
                        } catch (JSONException ex) {
                            Log.e(TAG, "Erronous location");
                            Log.e(TAG, Log.getStackTraceString(ex));
                        }
                        break;
                    case Location.ACTION_UPDATE_LOCATIONS:
                        NetworkLocations.getLocations(MainActivity.this);
                        break;
                    case Location.ACTION_LOCATION_REMOVED:
                        break;
                    case Task.ACTION_TASKS_SHOULD_UPDATE:
                        Log.d(TAG, "Should update tasks");
                        NetworkTasks.getTasks(context);
                        break;
                    case Group.ACTION_UPDATE_GROUP:
                        NetworkGroups.getGroup(MainActivity.this, selfGroupMemberId);
                    default:
                        Log.d(TAG, "Received non-handled action " + intent.getAction());
                        break;
                }
            }
        };
        for (String action : new String[]{
                Group.ACTION_GET_GROUP,
                Location.ACTION_NEW_LOCATION,
                Location.ACTION_GET_LOCATIONS,
                Location.ACTION_LOCATION_REMOVED,
                NetworkGroupMembers.ACTION_UPDATE_USERS,
                NetworkRegister.ACTION_REGISTERED,
                Task.ACTION_TASKS_SHOULD_UPDATE}) {
            IntentFilter intentFilter = new IntentFilter(action);
            registerReceiver(broadcastReceiver, intentFilter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            //noinspection SimplifiableIfStatement
            case R.id.action_settings:
                return true;
            case R.id.action_sign_out:
                GoogleAccountManager googleAccountManager = (GoogleAccountManager) getFragmentManager().findFragmentByTag(ACCOUNT_MANAGER);
                googleAccountManager.logout();
                return true;
            case R.id.dialog_newgroup_settings:
                createNewGroup();
                return true;
            case R.id.dialog_managegroup_settings:
                startActivity(new Intent(MainActivity.this, ManageGroupActivity.class));
                return true;
            case R.id.dialog_leavegroup_settings:
                leaveCurrentGroup();
                return true;
            case R.id.dialog_managegrouplocations_settings:
                startActivity(new Intent(MainActivity.this, ManageGroupLocations.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static int getSelfGroupMemberId() {
        return selfGroupMemberId;
    }

    public static int getGroupId() {
        return groupId;
    }

    public static void updateDatasets() {
        for (Fragment f : fragments) {
            try {
                ListFragment lf = (ListFragment) f;
                ArrayAdapter adapter = (ArrayAdapter) lf.getListAdapter();
                adapter.notifyDataSetChanged();
            } catch (NullPointerException e) {
                continue;
            }
        }
    }

    public static CreatedTasksFragment getCreatedTasksFragment() {
        for (Fragment f : fragments) {
            if (f instanceof CreatedTasksFragment) {
                return (CreatedTasksFragment) f;
            }
        }
        return null;
    }

    // When user is in group, hide new group menu item and show manage group and leave group.
    private void setMainMenuGroupItemsVisibility(boolean isInGroup) {
        this.menu.findItem(R.id.dialog_newgroup_settings).setVisible(!isInGroup);
        this.menu.findItem(R.id.dialog_managegroup_settings).setVisible(isInGroup);
        this.menu.findItem(R.id.dialog_leavegroup_settings).setVisible(isInGroup);
        this.menu.findItem(R.id.dialog_managegrouplocations_settings).setVisible(isInGroup);
    }

    private void updateUsers(JSONArray jsonUsers) {
        List<User> newUsers = new ArrayList<>();

        for(int i = 0; i < jsonUsers.length(); i++) {
            try {
                User u = new User(jsonUsers.get(i).toString());
                newUsers.add(u);
            } catch (JSONException e) {
                continue;
            }
        }

        for(User u : users) {
            if(!newUsers.contains(u)) {
                users.remove(u);
            }
        }

        for(User newUser : newUsers) {
            if(users.contains(newUser)) {
                for(User u : users) {
                    if(u.equals(newUser)) {
                        u.updateUser(newUser);
                    }
                }
            } else {
                users.add(newUser);
            }
        }
    }

    // Take new group name, show/hide menu elements and register new group
    private void leaveCurrentGroup() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.group_confirm_leave, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.NameDialogCustom);
        alertDialogBuilder.setView(promptView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        NetworkGroups.leaveGroup(getApplicationContext());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
    }

    // Take new group name, show/hide menu elements and register new group
    private void createNewGroup() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.group_new, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);
        final EditText input = (EditText) promptView.findViewById(R.id.newGroupName);
        alertDialogBuilder
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        NetworkGroups.postNewGroup(getApplicationContext(), input.getText().toString());

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
    }

    public static void removeGroupMember(User user) {
        groupMembers.remove(user);
    }

    public static class TabAdapter extends FragmentPagerAdapter {
        public TabAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String[] titles = new String[]{
                    "TODO",
                    "Created",
                    "Open",
                    "Complete"
            };
            try {
                return titles[position];
            } catch (IndexOutOfBoundsException ex) {
                return "Wappu";
            }
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            android.support.v4.app.Fragment fragment = MainActivity.fragments.get(position);
            Bundle args = new Bundle();
            fragment.setArguments(args);
            return fragment;
        }
    }

    public void onSignIn() {
        Log.d(TAG, "Signed in");
        //Toast.makeText(this, accountManager.getGoogleId(), Toast.LENGTH_LONG).show();
        register();
    }

    public void register() {
        GoogleAccountManager accountManager = (GoogleAccountManager) getFragmentManager().findFragmentByTag(ACCOUNT_MANAGER);
        NetworkRegister.register(this, accountManager.getGoogleToken(), register);
    }

    public void onLogOut() {
        finish();
    }

    public static String getApiId() {
        return apiId;
    }

    public static String getServerAddress() {
        return serverAddress;
    }

    public static void addLocation(Location location) {
        locations.add(location);
        updateDatasets();
    }

    public static List<Location> getLocations() {
        return new ArrayList<>(locations);
    }

    public static void removeLocation(Location location) {
        locations.remove(location);
    }

    public static void addUser(User user) {
        users.add(user);
    }

    public static void removeUser(User user) {
        users.remove(user);
    }

    public static List<User> getUsers() {
        return new ArrayList<>(users);
    }

    public static List<User> getNonMembers () {
        ArrayList<User> nonMembers = new ArrayList<>(getUsers());
        nonMembers.removeAll(getGroupMembers());
        return nonMembers;
    }

    public static List<User> getGroupMembers() {
        ArrayList<User> result = new ArrayList<>();
        for(User u : getUsers()) {
            if(u.getGroupId() == groupId) {
                result.add(u);
            }
        }
        return result;
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        Log.d(TAG, "resumed");
        if(intent.getAction().equals(GeofenceIntentService.ACTION_LOCATION_NEAR)) {
            pager.setCurrentItem(0);
        } else if (intent.getAction().equals(Task.ACTION_TASK_COMPLETE)) {
            pager.setCurrentItem(3);
        }
    }
}
