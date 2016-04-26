package t3waii.tasklists;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

/**
 * Created by moetto on 22/04/16.
 */
public class GeofenceIntentService extends IntentService {

    private static final String TAG = "TaskGeofenceService";
    public static final String ACTION_LOCATION_NEAR = "t3waii.tasklists.action_location_near";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public GeofenceIntentService(String name) {
        super(name);
    }

    public GeofenceIntentService() {
        super("GeofenceIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Received intent, hopefully location");
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.d(TAG, "Is error " + geofencingEvent.getErrorCode());
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        switch (geofenceTransition) {
            case Geofence.GEOFENCE_TRANSITION_DWELL:
            case Geofence.GEOFENCE_TRANSITION_ENTER: {
                // Get the geofences that were triggered. A single event can trigger
                // multiple geofences.
                List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                for (Geofence geofence : triggeringGeofences) {
                    Log.d(TAG, geofence.toString());
                    Intent notificationIntent = new Intent(this, MainActivity.class);
                    notificationIntent.setAction(ACTION_LOCATION_NEAR);
                    String[] taskDetails = geofence.getRequestId().split(";", 2);
                    int taskId = Integer.parseInt(taskDetails[0]);
                    String taskName = taskDetails[1];
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, taskId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    Notification notification = new Notification.Builder(this)
                            .setContentText("You are near " + taskName + " location")
                            .setSmallIcon(android.R.drawable.sym_def_app_icon)
                            .setContentIntent(pendingIntent)
                            .build();

                    notificationManager.notify(taskId, notification);
                    Log.d(TAG, taskName + " location is near");
                }
                break;
            }
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
                for (Geofence geofence : triggeringGeofences) {
                    String[] taskDetails = geofence.getRequestId().split(";", 2);
                    int taskId = Integer.parseInt(taskDetails[0]);
                    notificationManager.cancel(taskId);
                    Log.d(TAG, "Canceling notification for "+taskDetails[1]);
                    break;
                }

        }
    }
}
