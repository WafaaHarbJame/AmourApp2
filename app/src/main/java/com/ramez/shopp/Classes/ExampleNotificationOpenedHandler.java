package com.ramez.shopp.Classes;

import android.content.Intent;
import android.util.Log;

import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OneSignal;
import com.ramez.shopp.RootApplication;
import com.ramez.shopp.activities.MyOrderActivity;

import org.json.JSONObject;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ExampleNotificationOpenedHandler implements OneSignal.OSNotificationOpenedHandler {
    int order_id, user_id;
    String type = "";
    RootApplication rootApplication;


    public ExampleNotificationOpenedHandler(RootApplication rootApplication) {
        this.rootApplication = rootApplication;
    }

//    @Override
//    public void notificationOpened(OSNotificationOpenResult result) {
//        // Printing out the full OSNotification object to the logcat for easier debugging.
//        Log.i("OSNotification", "result.notification.toJSONObject(): " + result.notification.toJSONObject());
//
//        JSONObject data = result.notification.payload.additionalData;
//        if (data != null) {
//            user_id = data.optInt("user_id", 0);
//            order_id = data.optInt("order_id", 0);
//            type = data.optString("type", null);
//            if (user_id > 0) {
//                Log.i("OneSignalExample", "Log user_id set with value: " + user_id);
//                Log.i("OneSignalExample", "Log order_id set with value: " + order_id);
//                Log.i("OneSignalExample", "Log type set with value: " + type);
//            }
//            Intent intent;
//            if (type != null && type.equals("order")) {
//                intent = new Intent(getApplicationContext(), MyOrderActivity.class);
//            } else {
//                intent = new Intent(getApplicationContext(), Constants.INSTANCE.getMAIN_ACTIVITY_CLASS());
//            }
//
//            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
//            getApplicationContext().startActivity(intent);
//
//        }
//
//
//        OSNotificationAction.ActionType actionType = result.action.type;
//        if (actionType == OSNotificationAction.ActionType.ActionTaken) {
//            Log.i("OneSignalExample", "Log Button pressed with id: " + result.action.actionID);
//        }
//
//
//    }


    @Override
    public void notificationOpened(OSNotificationOpenedResult result) {
        // Printing out the full OSNotification object to the logcat for easier debugging.
        Log.i("OSNotification", "result.notification.toJSONObject(): " + result.getNotification().toJSONObject());

        JSONObject data = result.getNotification().getAdditionalData();
        if (data != null) {
            user_id = data.optInt("user_id", 0);
            order_id = data.optInt("order_id", 0);
            type = data.optString("type", null);
            if (user_id > 0) {
                Log.i("OneSignalExample", "Log user_id set with value: " + user_id);
                Log.i("OneSignalExample", "Log order_id set with value: " + order_id);
                Log.i("OneSignalExample", "Log type set with value: " + type);
            }
            Intent intent;
            if (type != null && type.equals("order")) {
                intent = new Intent(getApplicationContext(), MyOrderActivity.class);
            } else {
                intent = new Intent(getApplicationContext(), Constants.INSTANCE.getMAIN_ACTIVITY_CLASS());
            }

            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);

        }


        OSNotificationAction.ActionType actionType = result.getAction().getType();
        if (actionType == OSNotificationAction.ActionType.ActionTaken) {
            Log.i("OneSignalExample", "Log Button pressed with id: " + result.getAction().getActionId());
        }

    }
}
