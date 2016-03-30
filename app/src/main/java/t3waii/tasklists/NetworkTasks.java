package t3waii.tasklists;

import android.os.AsyncTask;

/**
 * Created by moetto on 3/29/16.
 */
public class NetworkTasks {

    private void register() {
        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                return null;
            }
        }.execute();

    }

    public void update() {
        register();
    }

}
