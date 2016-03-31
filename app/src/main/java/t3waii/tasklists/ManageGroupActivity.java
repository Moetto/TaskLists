package t3waii.tasklists;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * Created by matti on 3/30/16.
 */
public class ManageGroupActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_manage);
    }

    public void clickSaveButton(View v) {
        //TODO: implement update group members
        finish();
    }
}
