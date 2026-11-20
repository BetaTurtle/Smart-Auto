package smartauto.betaturtle.in.smartauto;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Activity for viewing and updating the driver profile details.
 */
public class ProfileActivity extends Activity {

    public static final String PREFS_NAME = "smart_auto_profile";
    public static final String PROFILE_UPDATE_URL = "http://better-brett.gopagoda.com/driver";

    private static final String TAG = "ProfileActivity";

    private TextView deviceIdView;
    private EditText nameInput;
    private EditText vehicleNoInput;
    private EditText mobileNoInput;
    private Button saveButton;
    private ProgressBar progressBar;

    private String deviceId;
    private AsyncPost activePost;
    private boolean destroyed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        deviceIdView = (TextView) findViewById(R.id.profile_device_id);
        nameInput = (EditText) findViewById(R.id.profile_name);
        vehicleNoInput = (EditText) findViewById(R.id.profile_vehicle_no);
        mobileNoInput = (EditText) findViewById(R.id.profile_mobile_no);
        saveButton = (Button) findViewById(R.id.profile_save_button);
        progressBar = (ProgressBar) findViewById(R.id.profile_progress);

        String rawId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        if (TextUtils.isEmpty(rawId)) {
            deviceId = "";
            deviceIdView.setText(R.string.profile_device_id_unavailable);
        } else {
            deviceId = rawId;
            deviceIdView.setText(deviceId);
        }

        loadSavedProfile();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitProfile();
            }
        });

        mobileNoInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    submitProfile();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        destroyed = true;
        if (activePost != null) {
            activePost.cancel(true);
            activePost = null;
        }
        super.onDestroy();
    }

    private void loadSavedProfile() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        nameInput.setText(prefs.getString(DriverProfile.KEY_NAME, ""));
        vehicleNoInput.setText(prefs.getString(DriverProfile.KEY_VEHICLE_NO, ""));
        mobileNoInput.setText(prefs.getString(DriverProfile.KEY_MOBILE_NO, ""));
    }

    private void saveProfileLocally(DriverProfile profile) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit()
                .putString(DriverProfile.KEY_NAME, profile.getName())
                .putString(DriverProfile.KEY_VEHICLE_NO, profile.getVehicleNo())
                .putString(DriverProfile.KEY_MOBILE_NO, profile.getMobileNo())
                .apply();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    private void setSubmitting(boolean submitting) {
        nameInput.setEnabled(!submitting);
        vehicleNoInput.setEnabled(!submitting);
        mobileNoInput.setEnabled(!submitting);
        saveButton.setEnabled(!submitting);
        saveButton.setText(submitting ? R.string.profile_saving : R.string.profile_save);
        progressBar.setVisibility(submitting ? View.VISIBLE : View.GONE);
    }

    private void submitProfile() {
        nameInput.setError(null);
        vehicleNoInput.setError(null);
        mobileNoInput.setError(null);

        String name = nameInput.getText().toString().trim();
        String vehicleNo = vehicleNoInput.getText().toString().trim();
        String mobileNo = mobileNoInput.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            nameInput.setError(getString(R.string.error_name_required));
            nameInput.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(vehicleNo)) {
            vehicleNoInput.setError(getString(R.string.error_vehicle_no_required));
            vehicleNoInput.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(mobileNo)) {
            mobileNoInput.setError(getString(R.string.error_mobile_no_required));
            mobileNoInput.requestFocus();
            return;
        }
        if (!DriverProfile.isValidMobileNo(mobileNo)) {
            mobileNoInput.setError(getString(R.string.error_mobile_no_invalid));
            mobileNoInput.requestFocus();
            return;
        }

        DriverProfile profile = new DriverProfile(deviceId, name, vehicleNo, mobileNo);
        vehicleNoInput.setText(profile.getVehicleNo());
        saveProfileLocally(profile);

        if (!isNetworkAvailable()) {
            Toast.makeText(this, R.string.profile_saved_offline, Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject json;
        try {
            json = profile.toJson();
        } catch (JSONException e) {
            Log.e(TAG, "Failed to serialize driver profile", e);
            Toast.makeText(this, R.string.profile_saved_sync_failed, Toast.LENGTH_LONG).show();
            return;
        }

        setSubmitting(true);
        if (activePost != null) {
            activePost.cancel(true);
        }
        activePost = new AsyncPost(PROFILE_UPDATE_URL, json, new AsyncPost.OnPostCompleteListener() {
            @Override
            public void onPostComplete(boolean success, int statusCode, String response) {
                activePost = null;
                if (destroyed || isFinishing()) {
                    return;
                }
                setSubmitting(false);
                if (success) {
                    Toast.makeText(ProfileActivity.this,
                            R.string.profile_update_success, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ProfileActivity.this,
                            R.string.profile_saved_sync_failed, Toast.LENGTH_LONG).show();
                }
            }
        });
        activePost.execute();
    }
}
