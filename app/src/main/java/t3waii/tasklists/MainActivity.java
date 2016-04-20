package t3waii.tasklists;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements SignInListener {

    //private final static int TAB_COUNT = 4;
    private static final String TAG = "MainActivity";
    private Menu menu;
    FragmentPagerAdapter pagerAdapter;
    ViewPager pager;
    TabLayout tabs;
    String ACCOUNT_MANAGER = "accountmanager";
    private String rest_api_id;

    public static List<User> users = new ArrayList<>();
    public static List<Location> locations = new ArrayList<>();
    private static List<Fragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fragments.add(new TODOTasksFragment());
        fragments.add(new CreatedTasksFragment());
        fragments.add(new OpenTasksFragment());
        fragments.add(new CompletedTasksFragment());

        //TODO: remove
        users.add(new User("Nimi1", Long.getLong("1")));
        users.add(new User("Nimi2", Long.getLong("2")));
        users.add(new User("Nimi3", Long.getLong("3")));
        users.add(new User("Nimi4", Long.getLong("4")));
        users.add(new User("Nimi5", Long.getLong("5")));
        locations.add(new Location("Paikka1", new LatLng(24, 23)));
        locations.add(new Location("Paikka2", new LatLng(34, 33)));
        locations.add(new Location("Paikka3", new LatLng(44, 43)));
        locations.add(new Location("Paikka4", new LatLng(54, 53)));
        locations.add(new Location("Paikka5", new LatLng(64, 63)));

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

    public static void updateDatasets() {
        for(Fragment f : fragments) {
            try {
                ListFragment lf = (ListFragment) f;
                ArrayAdapter adapter = (ArrayAdapter) lf.getListAdapter();
                adapter.notifyDataSetChanged();
            } catch (NullPointerException e) {
                continue;
            }
        }
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
        alertDialogBuilder.setView(promptView)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //TODO: unregister self from current group
                    setMainMenuGroupItemsVisibility(false);
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
                        // TODO: post new group, get name by input.getText()
                        setMainMenuGroupItemsVisibility(true);
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
                NetworkTasks.setApiId(rest_api_id);
                Log.d(TAG, rest_api_id);
                // called when response HTTP status is "200 OK"
                NetworkTasks.setServerAddress(getString(R.string.server_url));
                NetworkTasks.getTasks();
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
}
