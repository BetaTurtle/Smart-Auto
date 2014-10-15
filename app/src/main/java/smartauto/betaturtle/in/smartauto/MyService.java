package smartauto.betaturtle.in.smartauto;

/**
 * Created by jun on 11/10/14.
 *
 */
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.provider.Settings;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MyService extends Service {

    int count = 0;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, " MyService Created ", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        count++;
        final String android_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Toast.makeText(this, android_id+" MyService Started. Now "+ count, Toast.LENGTH_SHORT).show();
        new MyLocation().getLocation(getApplicationContext(), new MyLocation.LocationResult(){
            @Override
            public void gotLocation (Location location) {

                JSONObject json = new JSONObject();

                try {
                    json.put("deviceid",android_id);
                    json.put("lat", location.getLatitude());
                    json.put("lng", location.getLongitude());
                    json.put("alt", location.getAltitude());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new AsyncPost("http://better-brett.gopagoda.com/location", json).execute();
            }
        });

    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        Toast.makeText(this, "Servics Stopped", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

}