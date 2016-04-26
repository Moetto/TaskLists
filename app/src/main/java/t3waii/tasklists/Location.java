package t3waii.tasklists;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by matti on 4/7/16.
 */
public class Location implements Serializable {
    private String name;
    private LatLng latlng;
    private int id;
    public final static String
            ACTION_GET_LOCATIONS = "get_locations",
            ACTION_NEW_LOCATION = "t3waii.tasklists.action_new_location",
            ACTION_LOCATION_REMOVED = "removedLocation",
            ACTION_UPDATE_LOCATIONS = "t3waii.tasklists.action_update_locations",
            EXTRA_LOCATION = "location",
            EXTRA_LOCATIONS_JSON = "extraLocations",
            EXTRA_REMOVED_ID = "extraLocationRemovedId";

    public Location(int id, String name, LatLng latlng) {
        this.id = id;
        this.name = name;
        this.latlng = latlng;
    }

    public Location(JSONObject JSONLocation) throws JSONException {
        id = JSONLocation.getInt("id");
        name = JSONLocation.getString("name");
        latlng = new LatLng(JSONLocation.getDouble("latitude"), JSONLocation.getDouble("longitude"));
    }

    @Override
    public String toString() {
        return this.name;
    }

    public int getId() {
        return this.id;
    }

    public LatLng getLatlng() {
        return this.latlng;
    }

    public static List<Location> parseLocations(String locationsAsJsonString) throws JSONException {
        List<Location> locationsList = new ArrayList<>();
        JSONArray locations = new JSONArray(locationsAsJsonString);
        for (int i = 0; i < locations.length(); i++) {
            locationsList.add(new Location(locations.getJSONObject(i)));
        }
        return locationsList;
    }
}
