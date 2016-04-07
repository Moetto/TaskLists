package t3waii.tasklists;

/**
 * Created by matti on 4/7/16.
 */
public class Location {
    private String name;
    private double longitude, latitude;

    public Location(String name, double longitude, double latitude) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getName() { return this.name; }
    public double[] getCoordinates() { return new double[] {this.latitude, this.latitude}; }
}
