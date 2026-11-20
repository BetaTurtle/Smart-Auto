package smartauto.betaturtle.in.smartauto;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Unit tests for {@link DriverProfile}.
 */
public class DriverProfileTest extends TestCase {

    public void testNormalizationAndGetters() {
        DriverProfile profile = new DriverProfile(
                "  abc123def  ",
                "  Junaid Babu  ",
                "  kl 01 ab 1234  ",
                "  +91 98765-43210  ");

        assertEquals("abc123def", profile.getDeviceId());
        assertEquals("Junaid Babu", profile.getName());
        assertEquals("KL 01 AB 1234", profile.getVehicleNo());
        assertEquals("+91 98765-43210", profile.getMobileNo());

        DriverProfile emptyProfile = new DriverProfile(null, null, null, null);
        assertEquals("", emptyProfile.getDeviceId());
        assertEquals("", emptyProfile.getName());
        assertEquals("", emptyProfile.getVehicleNo());
        assertEquals("", emptyProfile.getMobileNo());
        assertEquals("", DriverProfile.normalizeVehicleNo(null));
    }

    public void testToJson() throws JSONException {
        DriverProfile profile = new DriverProfile(
                "dev-001",
                "Sudev",
                "kl07cd5678",
                "9876543210");

        JSONObject json = profile.toJson();
        assertEquals("dev-001", json.getString(DriverProfile.KEY_DEVICE_ID));
        assertEquals("Sudev", json.getString(DriverProfile.KEY_NAME));
        assertEquals("KL07CD5678", json.getString(DriverProfile.KEY_VEHICLE_NO));
        assertEquals("9876543210", json.getString(DriverProfile.KEY_MOBILE_NO));
        assertEquals(4, json.length());
    }

    public void testValidMobileNumbers() {
        assertTrue(DriverProfile.isValidMobileNo("9876543210"));
        assertTrue(DriverProfile.isValidMobileNo("+91 98765-43210"));
        assertTrue(DriverProfile.isValidMobileNo("(0484) 234-5678"));
        assertTrue(DriverProfile.isValidMobileNo("1234567"));
        assertTrue(DriverProfile.isValidMobileNo("123456789012345"));
    }

    public void testInvalidMobileNumbers() {
        assertFalse(DriverProfile.isValidMobileNo(null));
        assertFalse(DriverProfile.isValidMobileNo(""));
        assertFalse(DriverProfile.isValidMobileNo("   "));
        assertFalse(DriverProfile.isValidMobileNo("123456"));
        assertFalse(DriverProfile.isValidMobileNo("1234567890123456"));
        assertFalse(DriverProfile.isValidMobileNo("98765abcde"));
        assertFalse(DriverProfile.isValidMobileNo("++919876543210"));
        assertFalse(DriverProfile.isValidMobileNo("+"));
    }
}
