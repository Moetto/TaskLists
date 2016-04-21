package t3waii.tasklists;

import android.util.Log;
import android.view.Menu;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

/**
 * Created by matti on 4/21/16.
 */
public class NetworkGroups {
    private final static String TAG = "NetworkGroups";

    public static void postNewGroup(String name, final Menu menu) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.addHeader("Authorization", "Token " + MainActivity.getApiId());

        RequestParams params = new RequestParams();
        params.put("name", name);

        asyncHttpClient.post(MainActivity.getServerAddress() + "groups/", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d(TAG, "Post new group succeeded");
                Log.d(TAG, new String(responseBody));
                NetworkGroups.setMainMenuGroupItemsVisibility(menu, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d(TAG, "Post new group failed");
                if (responseBody != null) {
                    Log.d(TAG, new String(responseBody));
                }
            }
        });
    }

    // Send leave group to server
    // on success: hide/show menu items, notify other groupmembers, clear users, locations and tasks that are not created by you
    public static void leaveGroup(final Menu menu) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.addHeader("Authorization", "Token " + MainActivity.getApiId());

        asyncHttpClient.delete(MainActivity.getServerAddress() + "groups/", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d(TAG, "Delete groupmember succeeded");
                NetworkGroups.setMainMenuGroupItemsVisibility(menu, false);
                for(User u : MainActivity.users) {
                    //TODO: notify I left the group
                }
                MainActivity.users.clear();
                MainActivity.locations.clear();
                //TODO: remove tasks that are not created by you
                //TODO: alter tasks that you have created to have no assigned or estimatedtime
                updateDatasets();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d(TAG, "Delete groupmember failed");
                if (responseBody != null) {
                    Log.d(TAG, new String(responseBody));
                }
            }
        });
    }

    public static void updateDatasets() {
        if(ManageGroupLocations.locationListAdapter != null) {
            ManageGroupLocations.locationListAdapter.notifyDataSetChanged();
        }
        //TODO: update manage groups members?
    }

    public static void setMainMenuGroupItemsVisibility(Menu menu, boolean isInGroup) {
        menu.findItem(R.id.dialog_newgroup_settings).setVisible(!isInGroup);
        menu.findItem(R.id.dialog_managegroup_settings).setVisible(isInGroup);
        menu.findItem(R.id.dialog_leavegroup_settings).setVisible(isInGroup);
        menu.findItem(R.id.dialog_managegrouplocations_settings).setVisible(isInGroup);
    }
}
