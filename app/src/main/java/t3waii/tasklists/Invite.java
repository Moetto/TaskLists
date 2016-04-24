package t3waii.tasklists;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Network;
import android.util.Log;

import org.json.JSONException;

/**
 * Created by moetto on 24/04/16.
 */
public class Invite extends BroadcastReceiver {
    public static final String
            TAG = "TasksInviteReceiver",
            ACTION_INVITE_SENT = "t3waii.tasklists.action_invite_sent",
            ACTION_ACCEPT_INVITE = "t3waii.tasklists.action_accept_invite",
            ACTION_CANCEL_INVITE = "t3waii.tasklists.action_cancel_invite",
            EXTRA_INVITE = "extraInvite",
            EXTRA_INVITE_ID = "extraInviteId";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received intent");
        switch (intent.getAction()) {
            case ACTION_ACCEPT_INVITE:
                Log.d(TAG, "Accepted invite");
                int groupId = intent.getIntExtra(Group.EXTRA_GROUP_ID, 0);
                NetworkGroupMembers.joinGroup(context, groupId);
                NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(intent.getIntExtra(Invite.EXTRA_INVITE_ID, 0));
                break;
        }
    }
}
