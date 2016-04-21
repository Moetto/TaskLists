package t3waii.tasklists;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by moetto on 21/04/16.
 */
public class GcmListener extends GcmListenerService {
    private static final String TAG = "TaskGcmListener";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: "+data.getString("message"));
    }
}
