package service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by christian on 16/03/18.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseInstanceIDService";

    @Override
    public void onTokenRefresh() {
        //super.onTokenRefresh();

        //getting registration token
        String refreshToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token in Logcat
        Log.d("TAG", "Refreshed Token : " + refreshToken);

        sendRegistrationToServer(refreshToken);
    }

    private void sendRegistrationToServer(String token) {
        //You can implement this method to store the token on your server
        //Not required for current project
    }
}
