package t3waii.tasklists;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

/**
 * Created by matti on 4/21/16.
 */
public class NetworkGroupMembers {
    private final static String TAG = "NetworkGroupMembers";

    public static void getGroupMembers() {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.addHeader("Authorization", "Token " + MainActivity.getApiId());
        asyncHttpClient.get(MainActivity.getServerAddress() + "groupmembers/", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d(TAG, "Getting groupmembers succeeded");
                String response = new String(responseBody);
                Log.d(TAG, response);

                // get jsonarray
                JSONArray groupMembers;
                try {
                    groupMembers = new JSONArray(response);
                } catch (JSONException e) {
                    Log.d(TAG, "unable to parse groupmembers jsonarray");
                    return;
                }

                // parse and store groupmembers from jsonarray
                List<User> groupMembersList = new ArrayList<>();
                for (int i = 0; i < groupMembers.length(); i++) {
                    JSONObject jsonGroupMember;
                    try {
                        jsonGroupMember = (JSONObject) groupMembers.get(i);
                    } catch (JSONException e) {
                        Log.d(TAG, "unable to parse single jsonGroupMember!");
                        continue;
                    }

                    User groupMember = parseUser(jsonGroupMember);
                    if (groupMember == null) {
                        continue;
                    }

                    groupMembersList.add(groupMember);
                }

                // remove groupmembers that do not exist anymore
                for (User mainUser : MainActivity.users) {
                    boolean stillExists = false;
                    for (User u : groupMembersList) {
                        if (mainUser.getId() == u.getId()) {
                            stillExists = true;
                            break;
                        }
                    }
                    if (!stillExists) {
                        MainActivity.users.remove(mainUser);
                    }
                }

                // add groupmembers that are not already in the list
                for (User u : groupMembersList) {
                    boolean alreadyExists = false;
                    for (User mainUser : MainActivity.users) {
                        if (mainUser.getId() == u.getId()) {
                            alreadyExists = true;
                            break;
                        }
                    }
                    if (!alreadyExists) {
                        MainActivity.users.add(u);
                    }
                }
                updateDatasets();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d(TAG, "Getting locations failed");
                if (responseBody != null) {
                    Log.d(TAG, new String(responseBody));
                }
            }
        });
    }

    public static void postNewGroupMembers(List<User> users) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.addHeader("Authorization", "Token " + MainActivity.getApiId());
        for(User u : users) {
            final User user = u;
            RequestParams params = new RequestParams();
            params.put("id", Long.toString(user.getId())); //TODO: determine what to post
            asyncHttpClient.post(MainActivity.getServerAddress() + "groupmembers/", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.d(TAG, "Post new groupmember succeeded");
                    Log.d(TAG, new String(responseBody));
                    JSONObject jsonUser;
                    try {
                        jsonUser = new JSONObject(new String(responseBody));
                    } catch (JSONException e) {
                        Log.d(TAG, "Unable to parse post groupmember response");
                        return;
                    }
                    User responseUser = parseUser(jsonUser);
                    if (responseUser != null) {
                        MainActivity.users.add(responseUser);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.d(TAG, "Post new groupmember failed");
                    if (responseBody != null) {
                        Log.d(TAG, new String(responseBody));
                    }
                }
            });
        }
        updateDatasets();
        //TODO: notify other group members of new group members
    }

    public static void deleteGroupMembers(List<User> users) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.addHeader("Authorization", "Token " + MainActivity.getApiId());
        for(User u : users) {
            final User user = u;
            asyncHttpClient.delete(MainActivity.getServerAddress() + "groupmembers/" + Long.toString(user.getId()), new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.d(TAG, "Delete groupmember succeeded");
                    MainActivity.users.remove(user);
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
        updateDatasets();
        //TODO: notify other group members
    }

    private static void updateDatasets() {
        //TODO: implement and enable
        /*
        if (ManageGroupActivity.groupMemberListAdapter != null) {
            ManageGroupActivity.groupMemberListAdapter.notifyDataSetChanged();
        }
        */
    }

    // Parse User information from json and return it as User object
    private static User parseUser(JSONObject jsonUser) {
        Long id;
        String name;

        try {
            id = jsonUser.getLong("id");
            name = jsonUser.getString("name");

        } catch (JSONException e) {
            Log.d(TAG, "Unable to parse at least one of the user parameters!");
            return null;
        }

        return new User(name, id);
    }
}
