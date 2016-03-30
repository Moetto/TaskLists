package t3waii.tasklists;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private final static int TAB_COUNT = 4;
    private Menu menu;
    FragmentPagerAdapter pagerAdapter;
    ViewPager pager;
    TabLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        //noinspection SimplifiableIfStatement
        switch(id) {
            case R.id.action_settings:
                //TODO: open settings
                return true;
            case R.id.dialog_newgroup_settings:
                createNewGroup();
                return true;
            case R.id.dialog_managegroup_settings:
                //TODO: open edit group dialog
                return true;
            case R.id.dialog_leavegroup_settings:
                leaveCurrentGroup();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // When user is in group, hide new group menu item and show manage group and leave group.
    private void setMainMenuGroupItemsVisibility(boolean isInGroup) {
        MenuItem item = (MenuItem) this.menu.findItem(R.id.dialog_newgroup_settings);
        item.setVisible(!isInGroup);
        item = (MenuItem) this.menu.findItem(R.id.dialog_managegroup_settings);
        item.setVisible(isInGroup);
        item = (MenuItem) this.menu.findItem(R.id.dialog_leavegroup_settings);
        item.setVisible(isInGroup);
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
            return "Tittelipittelipuu";
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            android.support.v4.app.Fragment fragment = new TODOTasksFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            return fragment;
        }
    }
}
