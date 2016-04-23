package t3waii.tasklists;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by matti on 4/7/16.
 */
public class User implements Serializable{
    private String name;
    private int id;
    public final static String TAG = "TaskUser";
    public User(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public User(String userString) throws JSONException{
        JSONObject jsonUser = new JSONObject(userString);
        try {
            id = jsonUser.getInt("id");
            name = jsonUser.getString("name");

        } catch (JSONException e) {
            Log.d(TAG, "Unable to parse at least one of the user parameters!");
            throw e;
        }
    }

    public String getName() { return this.name; }
    public int getId() { return this.id; }

    @Override
    public String toString() {
        return this.name;
    }
}
