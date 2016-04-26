package t3waii.tasklists;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by moetto on 21/04/16.
 */
public class GcmListener extends GcmListenerService {
    public static final String TAG = "TaskGcmListener";

    private static final String
    GCM_ACTION_INVITE = "invite",
    GCM_ACTION_TASKS_CHANGED = "tasks_changed",
    GCM_ACTION_GROUP_CHANGED = "group_changed",
    GCM_ACTION_LOCATIONS_CHANGED = "locations_changed",
    GROUP_NAME = "group_name",
    GROUP_ID = "group_id",
    INVITER_NAME = "inviter_name",
    INVITE_ID = "id";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);
        Log.d(TAG, "From: " + from);
        String message = data.getString("message");
        Log.d(TAG, "Message: " + message);
        try {
            JSONObject messageAsJson = new JSONObject(message);
            switch (messageAsJson.getString("action")) {
                case (GCM_ACTION_INVITE):
                    String groupName = messageAsJson.getString(GROUP_NAME);
                    String inviterName = messageAsJson.getString(INVITER_NAME);
                    int id = messageAsJson.getInt(INVITE_ID);
                    int groupId = messageAsJson.getInt(GROUP_ID);
                    Intent intentOk = new Intent();
                    intentOk.setAction(Invite.ACTION_ACCEPT_INVITE);
                    intentOk.putExtra(Group.EXTRA_GROUP_ID, groupId);
                    intentOk.putExtra(Invite.EXTRA_INVITE_ID, id);
                    PendingIntent pendingOk = PendingIntent.getBroadcast(this, id, intentOk, PendingIntent.FLAG_ONE_SHOT);

                    Intent intentCancel = new Intent();
                    intentCancel.setAction(Invite.ACTION_CANCEL_INVITE);
                    intentCancel.putExtra(Invite.EXTRA_INVITE_ID, id);
                    PendingIntent pendingCancel = PendingIntent.getBroadcast(this, id, intentCancel, PendingIntent.FLAG_ONE_SHOT);

                    Notification notification = new Notification.Builder(this)
                            .setSmallIcon(android.R.drawable.sym_def_app_icon)
                            .setContentText(inviterName + " has invited you to a group called " + groupName)
                            .addAction(R.drawable.check_small, "Join", pendingOk)
                            .addAction(android.R.drawable.ic_delete, "Dismiss", pendingCancel)
                            .setAutoCancel(true)
                            .build();
                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.notify(id, notification);
                    break;
                case (GCM_ACTION_TASKS_CHANGED):
                    Intent intent = new Intent();
                    intent.setAction(Task.ACTION_TASKS_SHOULD_UPDATE);
                    sendBroadcast(intent);
                    Log.d(TAG, "Sent broadcast telling tasks should update");
                    break;
                case (GCM_ACTION_GROUP_CHANGED):
                    Intent groupChangeIntent = new Intent();
                    groupChangeIntent.setAction(Group.ACTION_UPDATE_GROUP);
                    sendBroadcast(groupChangeIntent);
                    Log.d(TAG, "Sent broadcast to update group");
                    break;
                case (GCM_ACTION_LOCATIONS_CHANGED):
                    Intent locationChangedIntent = new Intent();
                    locationChangedIntent.setAction(Location.ACTION_UPDATE_LOCATIONS);
                    sendBroadcast(locationChangedIntent);
                    Log.d(TAG, "Send broadcast to update locations");
                    break;
                default:
                    Log.e(TAG, "Unhandled message "+messageAsJson.getString("action"));
                    break;
            }
        } catch (JSONException ex) {
            Log.e(TAG, "Error parsing GCM json");
            Log.e(TAG, Log.getStackTraceString(ex));
        }
    }
}
