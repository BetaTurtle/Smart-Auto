package smartauto.betaturtle.in.smartauto;


import java.util.Calendar;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class Main extends Activity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button start = (Button) findViewById(R.id.start);
        Button end = (Button) findViewById(R.id.end);
        start.setOnClickListener(this);
        end.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.start:
                startService(new Intent(this, MyService.class));
                Calendar cal = Calendar.getInstance();
                Intent intent = new Intent(this, MyService.class);
                PendingIntent pintent = PendingIntent
                        .getService(this, 0, intent, 0);

                AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                // Start service every hour
                alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                        30*1000, pintent);

                break;
            case R.id.end:
                stopService(new Intent(this, MyService.class));
                break;
            default:
                break;
        }
    }

}