package t3waii.tasklists;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
public class NetworkLocations  {
    private final static String TAG = "TaskNetworkLocations";
    static final String ACTION_NEW_LOCATION = "t3waii.tasklists.action_new_location",
    ACTION_LOCATION_REMOVED = "removedLocation",
    EXTRA_LOCATION = "location";

    public static void postNewLocation(Map<String, String> values, final Context context) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.addHeader("Authorization", "Token " + MainActivity.getApiId());
        RequestParams params = new RequestParams();
        for(String key : values.keySet()) {
            params.add(key, values.get(key));
        }
        asyncHttpClient.post(MainActivity.getServerAddress() + "locations/", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d(TAG, "Post new location succeeded");
                Log.d(TAG, new String(responseBody));
                JSONObject jsonLocation;
                try {
                    jsonLocation = new JSONObject(new String(responseBody));
                } catch (JSONException e) {
                    Log.d(TAG, "Unable to parse post location response");
                    return;
                }
                Location l = parseLocation(jsonLocation);
                if(l != null) {
                    Intent intent = new Intent();
                    intent.setAction(ACTION_NEW_LOCATION);
                    intent.putExtra("test", new Task(1, 1));
                    context.sendBroadcast(intent);
                    //MainActivity.locations.add(l);
                    //updateDatasets();
                    //TODO: notify other group members of new location
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d(TAG, "Post new location failed");
                if (responseBody != null) {
                    Log.d(TAG, new String(responseBody));
                }
            }
        });
    }

    public static void getLocations(Context context) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.addHeader("Authorization", "Token " + MainActivity.getApiId());
        asyncHttpClient.get(MainActivity.getServerAddress() + "locations/", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d(TAG, "Getting locations succeeded");
                String response = new String(responseBody);
                Log.d(TAG, response);

                // get jsonarray
                JSONArray locations;
                try {
                    locations = new JSONArray(response);
                } catch (JSONException e) {
                    Log.d(TAG, "unable to parse locations jsonarray");
                    return;
                }

                // parse and store locatiosn from jsonarray
                List<Location> locationList = new ArrayList<>();
                for (int i = 0; i < locations.length(); i++) {
                    JSONObject jsonLocation;
                    try {
                        jsonLocation = (JSONObject) locations.get(i);
                    } catch (JSONException e) {
                        Log.d(TAG, "unable to parse single location!");
                        continue;
                    }

                    Location location = parseLocation(jsonLocation);
                    if (location == null) {
                        continue;
                    }

                    locationList.add(location);
                }

                // remove locations that do not exist anymore
                for (Location mainLocation : MainActivity.getLocations()) {
                    boolean stillExists = false;
                    for (Location l : locationList) {
                        if (mainLocation.getId() == l.getId()) {
                            stillExists = true;
                            break;
                        }
                    }
                    if (!stillExists) {
                        MainActivity.removeLocation(mainLocation);
                    }
                }

                // add locations that are not already in the list
                for (Location l : locationList) {
                    boolean alreadyExists = false;
                    for (Location mainLocation : MainActivity.getLocations()) {
                        if (mainLocation.getId() == l.getId()) {
                            alreadyExists = true;
                            break;
                        }
                    }
                    if (!alreadyExists) {
                        MainActivity.addLocation(l);
                    }
                }
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

    public static void deleteLocation(final Location location) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.addHeader("Authorization", "Token " + MainActivity.getApiId());
        asyncHttpClient.delete(MainActivity.getServerAddress() + "locations/" + Long.toString(location.getId()), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d(TAG, "Delete location succeeded");
                Intent intent = new Intent();
                intent.setAction(ACTION_LOCATION_REMOVED);
                intent.putExtra(EXTRA_LOCATION, location);
                MainActivity.removeLocation(location);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d(TAG, "Delete location failed");
                if (responseBody != null) {
                    Log.d(TAG, new String(responseBody));
                }
            }
        });
    }

    // Parse Location information from json and return it as Location object
    private static Location parseLocation(JSONObject jsonLocation) {
        Long id;
        String name;
        double latitude, longitude;

        try {
            id = jsonLocation.getLong("id");
            name = jsonLocation.getString("name");
            latitude = jsonLocation.getDouble("latitude");
            longitude = jsonLocation.getDouble("longitude");

        } catch (JSONException e) {
            Log.d(TAG, "Unable to parse at least one of the location parameters!");
            return null;
        }

        return new Location(id, name, new LatLng(latitude, longitude));
    }
}