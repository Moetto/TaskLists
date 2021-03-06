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
public class NetworkLocations {
    private final static String TAG = "TaskNetworkLocations";

    public static void postNewLocation(Map<String, String> values, final Context context) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.addHeader("Authorization", "Token " + MainActivity.getApiId());
        RequestParams params = new RequestParams();
        for (String key : values.keySet()) {
            params.add(key, values.get(key));
        }
        asyncHttpClient.post(MainActivity.getServerAddress() + "locations/", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d(TAG, "Post new location succeeded");
                String response = new String(responseBody);
                Log.d(TAG, response);
                JSONObject jsonLocation;
                Location l;
                try {
                    jsonLocation = new JSONObject(new String(responseBody));
                    l = new Location(jsonLocation);
                } catch (JSONException e) {
                    Log.d(TAG, "Unable to parse post location response");
                    return;
                }
                Intent intent = new Intent();
                intent.setAction(Location.ACTION_NEW_LOCATION);
                intent.putExtra(Location.EXTRA_LOCATION, response);
                intent.putExtra("test", new Task(1, 1));
                context.sendBroadcast(intent);
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

    public static void getLocations(final Context context) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.addHeader("Authorization", "Token " + MainActivity.getApiId());
        asyncHttpClient.get(MainActivity.getServerAddress() + "locations/", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                Log.d(TAG, "Getting locations succeeded");
                String response = new String(responseBody);
                Log.d(TAG, response);

                Intent intent = new Intent();
                intent.setAction(Location.ACTION_GET_LOCATIONS);
                intent.putExtra(Location.EXTRA_LOCATIONS_JSON, response);
                context.sendBroadcast(intent);
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

    public static void deleteLocation(final Context context, final Location location) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.addHeader("Authorization", "Token " + MainActivity.getApiId());
        asyncHttpClient.delete(MainActivity.getServerAddress() + "locations/" + Integer.toString(location.getId()) + "/", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d(TAG, "Delete location succeeded");
                Intent intent = new Intent();
                intent.setAction(Location.ACTION_LOCATION_REMOVED);
                intent.putExtra(Location.EXTRA_REMOVED_ID, location.getId());
                context.sendBroadcast(intent);
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
}