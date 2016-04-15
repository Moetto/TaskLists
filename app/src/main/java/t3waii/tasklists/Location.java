package t3waii.tasklists;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by matti on 4/7/16.
 */
public class Location {
    private String name;
    private LatLng latlng;

    public Location(String name, LatLng latlng) {
        this.name = name;
        this.latlng = latlng;
    }

    @Override
    public String toString() { return this.name; }

    public LatLng getLatlng() { return this.latlng; }
}
