package com.example.imole;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MyNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        MainActivity mainActivity = MainActivity.getInstance(); // Obtain the instance of MainActivity


        double dailyPowerConsumption = mainActivity.getTextViewValue();




        // Construct the notification payload
        String title = "Daily Power Consumption";
        String message = "Your daily power consumption is: " + dailyPowerConsumption + " kWh";

        // Send the notification using Firebase Cloud Messaging
        sendNotificationToFirebase(context, title, message);
    }


    private void sendNotificationToFirebase(Context context, String title, String message) {
        // Use your Server Key obtained from the Firebase console
        String serverKey = "AAAA4tNXWYM:APA91bEjXWtR9sa-OoHwGm_JV5RMChBG0eJJ_DEYJdaciw2H1lVStcrwJDX7ZwDEuasnpT3QfPtGXsDLZZpPIy8Swv3bsQ35npxtBuKRscpT736dC-AnyRrbhvWjgSUuJ-X2jOCfR1e_";

        // Construct the FCM notification payload
        JSONObject payload = new JSONObject();
        try {
            payload.put("to", "/topics/Daily_Power_Notification"); // Replace with your topic name or individual device token
            payload.put("priority", "high");

            JSONObject notificationData = new JSONObject();
            notificationData.put("title", title);
            notificationData.put("body", message);

            payload.put("notification", notificationData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Send the FCM notification using Volley or any other network library
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, "https://fcm.googleapis.com/fcm/send", payload,
                response -> {
                    // Notification sent successfully
                },
                error -> {
                    // Error occurred while sending notification
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "key=" + serverKey);
                return headers;
            }
        };

        // Add the request to the RequestQueue
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }


}
