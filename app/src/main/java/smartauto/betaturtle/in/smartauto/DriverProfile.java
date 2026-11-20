package smartauto.betaturtle.in.smartauto;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Immutable driver profile model matching the Smart-Auto server schema.
 */
public class DriverProfile {

    public static final String KEY_DEVICE_ID = "deviceid";
    public static final String KEY_NAME = "name";
    public static final String KEY_VEHICLE_NO = "vehicleno";
    public static final String KEY_MOBILE_NO = "mobileno";

    private final String deviceId;
    private final String name;
    private final String vehicleNo;
    private final String mobileNo;

    public DriverProfile(String deviceId, String name, String vehicleNo, String mobileNo) {
        this.deviceId = trimOrEmpty(deviceId);
        this.name = trimOrEmpty(name);
        this.vehicleNo = normalizeVehicleNo(vehicleNo);
        this.mobileNo = trimOrEmpty(mobileNo);
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getName() {
        return name;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    /**
     * Trims and uppercases a vehicle registration number.
     */
    public static String normalizeVehicleNo(String vehicleNo) {
        if (vehicleNo == null) {
            return "";
        }
        return vehicleNo.trim().toUpperCase(Locale.US);
    }

    /**
     * Validates a mobile phone number (optional leading '+', 7 to 15 ASCII digits
     * after removing spaces, hyphens, and parentheses).
     */
    public static boolean isValidMobileNo(String mobileNo) {
        if (mobileNo == null) {
            return false;
        }
        String cleaned = mobileNo.trim().replaceAll("[\\s\\-()]", "");
        if (cleaned.length() == 0) {
            return false;
        }
        if (cleaned.startsWith("+")) {
            cleaned = cleaned.substring(1);
        }
        int len = cleaned.length();
        if (len < 7 || len > 15) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            char c = cleaned.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    /**
     * Serializes the driver profile fields into a JSON object for server sync.
     */
    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(KEY_DEVICE_ID, deviceId);
        json.put(KEY_NAME, name);
        json.put(KEY_VEHICLE_NO, vehicleNo);
        json.put(KEY_MOBILE_NO, mobileNo);
        return json;
    }

    private static String trimOrEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
