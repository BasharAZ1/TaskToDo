package com.example.tasktodo.BrodcastHolder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import com.example.tasktodo.ItemsRecycleAdapter;

public class BroadcastReceiverListner extends BroadcastReceiver {

    private static final String TAG = BroadcastReceiverListner.class.getSimpleName();
    public static boolean SendEnable = true;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("com.TaskRemainder.CUSTOM_INTENT")) {
            ItemsRecycleAdapter.listner.UpdateTaskList();
            Log.d("Intent", " Detected.");
        } else if (intent.getAction().equals(Intent.ACTION_AIRPLANE_MODE_CHANGED)) {
            // Airplane mode state has changed
            boolean airplaneModeOn = Settings.Global.getInt(context.getContentResolver(),
                    Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
            Log.d("airplane", String.valueOf(airplaneModeOn));
            if (airplaneModeOn) {
                // Airplane mode turned on
                Log.d("airplane", "Airplane mode turned on");
                SendEnable = false;
            } else {
                // Airplane mode turned off
                Log.d("airplane", "Airplane mode turned off");
                SendEnable = true;
            }
        }
    }
}






