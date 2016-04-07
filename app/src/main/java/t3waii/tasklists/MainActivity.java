package t3waii.tasklists;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements SignInListener {

    private final static int TAB_COUNT = 1; //TODO: 4
    private static final String TAG = "MainActivity";
    private Menu menu;
    FragmentPagerAdapter pagerAdapter;
    ViewPager pager;
    TabLayout tabs;
    String ACCOUNT_MANAGER = "accountmanager";
    private String rest_api_id;

    public static List<User> users = new ArrayList<>();
    public static List<Location> locations = new ArrayList<>();
    public static List<Task> tasks = new ArrayList<>();
    public static List<Fragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fragments.add(new TODOTasksFragment());

        //TODO: remove
        users.add(new User("Nimi1", "id1"));
        users.add(new User("Nimi2", "id2"));
        users.add(new User("Nimi3", "id3"));
        users.add(new User("Nimi4", "id4"));
        users.add(new User("Nimi5", "id5"));
        locations.add(new Location("Paikka1", 23.24, 35.23));
        locations.add(new Location("Paikka2", 23.24, 35.23));
        locations.add(new Location("Paikka3", 23.24, 35.23));
        locations.add(new Location("Paikka4", 23.24, 35.23));
        locations.add(new Location("Paikka5", 23.24, 35.23));
        tasks.add(new Task(1, 10));
        tasks.add(new Task(2, 10));
        tasks.add(new Task(3, 10));
        tasks.add(new Task(4, 10));
        tasks.add(new Task(5, 10));
        tasks.get(0).setName("Task1");
        tasks.get(1).setName("Task2");
        tasks.get(2).setName("Task3");
        tasks.get(3).setName("Task4");
        tasks.get(4).setName("Task5");
        Task childtask = new Task(346, 10);
        childtask.setName("Childtask1");
        tasks.get(1).addChild(childtask);
        childtask = new Task(347, 10);
        childtask.setName("Childtask2");
        tasks.get(1).addChild(childtask);
        childtask = new Task(348, 10);
        childtask.setName("Childtask3");
        tasks.get(1).addChild(childtask);


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


    // When user is in group, hide new group menu item and show manage group and leave group.
    private void setMainMenuGroupItemsVisibility(boolean isInGroup) {
        this.menu.findItem(R.id.dialog_newgroup_settings).setVisible(!isInGroup);
        this.menu.findItem(R.id.dialog_managegroup_settings).setVisible(isInGroup);
        this.menu.findItem(R.id.dialog_leavegroup_settings).setVisible(isInGroup);
        this.menu.findItem(R.id.dialog_managegrouplocations_settings).setVisible(isInGroup);
    }

    // Take new group name, show/hide menu elements and register new group
    private void leaveCurrentGroup() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.group_confirm_leave, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set group_new.xml to be the layout file of the alertdialog builder
        alertDialogBuilder.setView(promptView);
        // setup a dialog window
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // get user input and set it to result
                        //TODO: unregister self from current group
                        setMainMenuGroupItemsVisibility(false);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        // create an alert dialog
        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
    }

    // Take new group name, show/hide menu elements and register new group
    private void createNewGroup() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.group_new, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set group_new.xml to be the layout file of the alertdialog builder
        alertDialogBuilder.setView(promptView);
        final EditText input = (EditText) promptView.findViewById(R.id.newGroupName);
        // setup a dialog window
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // get user input and set it to result
                        Log.d("test", "value:" + input.getText());
                        setMainMenuGroupItemsVisibility(true);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        // create an alert dialog
        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
    }

    public static class TabAdapter extends FragmentPagerAdapter {
        public TabAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String[] titles = new String[]{
                    "TODO",
                    "Created",
                    "Open",
                    "Complete"
            };
            return titles[position];
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
        GoogleAccountManager accountManager = (GoogleAccountManager) getFragmentManager().findFragmentByTag(ACCOUNT_MANAGER);
        //Toast.makeText(this, accountManager.getGoogleId(), Toast.LENGTH_LONG).show();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams("token", accountManager.getGoogleToken());
        Log.d(TAG, params.toString());
        client.post(getString(R.string.server_url) + "register/", params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                Toast.makeText(MainActivity.this, "Successful request", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Successful request");
                rest_api_id = new String(response);
                Log.d(TAG, rest_api_id);
                // called when response HTTP status is "200 OK"
                getTasks();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                Toast.makeText(MainActivity.this, "Failed request", Toast.LENGTH_LONG).show();
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Log.d(TAG, "Failed request");
                Log.d(TAG, "Status: " + statusCode);
                if (errorResponse != null) {
                    Log.d(TAG, new String(errorResponse));
                }
                Log.d(TAG, Log.getStackTraceString(e));
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });

    }

    public void onLogOut() {
        finish();
    }

    public void getTasks() {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        //asyncHttpClient.setBasicAuth("Authorization: Token", rest_api_id,true);
        asyncHttpClient.addHeader("Authorization", "Token "+rest_api_id);
        asyncHttpClient.get(getString(R.string.server_url) + "tasks/", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d(TAG, "Getting tasks succeeded");
                Log.d(TAG, new String(responseBody));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d(TAG, "Getting tasks failed");
                if (responseBody != null) {
                    Log.d(TAG, new String(responseBody));
                }
            }
        });
    }
}
