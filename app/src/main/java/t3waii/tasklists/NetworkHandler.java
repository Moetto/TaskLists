package t3waii.tasklists;

import com.loopj.android.http.AsyncHttpClient;

/**
 * Created by moetto on 23/04/16.
 */
public abstract class NetworkHandler {
    protected static AsyncHttpClient getAsyncHttpClient(){
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.addHeader("Authorization", "Token " + MainActivity.getApiId());
        return asyncHttpClient;
    }
    protected static String getBaseUrl() {
        return MainActivity.getServerAddress();
    }
}
