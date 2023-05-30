package com.example.imole;

import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFireBaseInstanceIDService extends FirebaseMessagingService {

    private static final String TAG = "EnhancedIntentService";


  /*  @Override
    public void onNewToken(@NonNull String token) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {


                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        // String msg = getString(R.string.msg_token_fmt, token);
                        // Log.d(TAG, msg);
                        //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        super.onNewToken(token);
                    };
                };
    } */
}



