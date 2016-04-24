package t3waii.tasklists;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by matti on 4/7/16.
 */
public class Location implements Serializable{
    private String name;
    private LatLng latlng;
    private int id;

    public Location(int id, String name, LatLng latlng) {
        this.id = id;
        this.name = name;
        this.latlng = latlng;
    }

    @Override
    public String toString() { return this.name; }

    public int getId() { return this.id; }

    public LatLng getLatlng() { return this.latlng; }
}
