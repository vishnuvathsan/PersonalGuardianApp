package com.aslan.personalguardianapp.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.util.Log;
import android.util.Patterns;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by gobinath on 10/29/15.
 */
public class Utility {
    private static String userId;
    private static List<String> otherNumbers;
    private static UserConfiguration conf;
    private static String signed_in_status;
    private static String deviceToken;

    public static SharedPreferences getSharedPreference(Context ctx) {
        SharedPreferences preferences = ctx.getSharedPreferences("com.aslan.personalguardianapp", Context.MODE_PRIVATE);
        return preferences;
    }

    public static boolean isFirstRun(Context ctx) {
        String userId = getUserId(ctx);
        String signed_in = isUserSignedIn(ctx);
        // If user-id is null, this is the first run
        boolean firstRun = userId == null || signed_in == null;
        return firstRun;
    }

    public static List<String> getAccountEmail(Context ctx) {
        List<String> emails = new ArrayList<>();
        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(ctx).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                String possibleEmail = account.name;
                emails.add(possibleEmail);
            }
        }
        return emails;
    }

    public static String getUserId(Context ctx) {
        if (userId == null) {
            SharedPreferences preferences = getSharedPreference(ctx);
            userId = preferences.getString(Constants.USER_ID, null);
        }
        return userId;
    }

    public static void saveUserId(Context ctx, String userID) {
        userId = userID;
        SharedPreferences preferences = getSharedPreference(ctx);
        preferences.edit().putString(Constants.USER_ID, userId).commit();
    }

    public static UserConfiguration getUserConf(Context ctx) {
        if (conf == null) {
            SharedPreferences preferences = getSharedPreference(ctx);
            try {
                conf = (UserConfiguration) deserialize(preferences.getString(Constants.USER_CONF, null));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return conf;
    }

    public static boolean saveUserConf(Context ctx, UserConfiguration con) {
        conf = con;
        SharedPreferences preferences = getSharedPreference(ctx);
        try {
            preferences.edit().putString(Constants.USER_CONF, serialize(conf)).apply();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<String> getOtherNumbers(Context ctx) {
        if (otherNumbers == null) {
            SharedPreferences preferences = getSharedPreference(ctx);
            try {
                otherNumbers = (ArrayList<String>) deserialize(preferences.getString(Constants.OTHER_NUMBERS, serialize(new ArrayList<String>())));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return otherNumbers;
    }

    public static void saveOtherNumbers(Context ctx, List<String> numbers) throws IOException {
        otherNumbers = numbers;
        SharedPreferences preferences = getSharedPreference(ctx);
        preferences.edit().putString(Constants.OTHER_NUMBERS, serialize((Serializable) otherNumbers)).apply();
    }

    public static String isUserSignedIn(Context ctx) {
        if (signed_in_status == null) {
            SharedPreferences preferences = getSharedPreference(ctx);
            signed_in_status = preferences.getString(Constants.SIGNED_IN_STATUS, null);
        }
        return signed_in_status;
    }

    public static void saveUserSignedIn(Context ctx, boolean status) {
        if (status) {
            signed_in_status = Constants.SIGNED_IN;
        } else {
            signed_in_status = Constants.SIGNED_OUT;
        }
        SharedPreferences preferences = getSharedPreference(ctx);
        preferences.edit().putString(Constants.SIGNED_IN_STATUS, signed_in_status).commit();
    }

    public static boolean getTrackingServiceState(Context ctx, String key) {
        SharedPreferences preferences = getSharedPreference(ctx);
        return preferences.getBoolean(key, false);
    }

    public static void saveTrackingServiceState(Context ctx, String key, boolean status) {
        SharedPreferences preferences = getSharedPreference(ctx);
        preferences.edit().putBoolean(key, status).commit();
    }

    public static String getDeviceToken(Context ctx) {
        if (deviceToken == null) {
            SharedPreferences preferences = getSharedPreference(ctx);
            deviceToken = preferences.getString(Constants.DEVICE_TOKEN, null);
        }
        return deviceToken;
    }

    public static void saveDeviceToken(Context ctx, String deviceToken) {
        SharedPreferences preferences = getSharedPreference(ctx);
        preferences.edit().putString(Constants.DEVICE_TOKEN, deviceToken).commit();
    }


    public static float getBatteryLevel(Context ctx) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = ctx.registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPercent = 100 * level / (float) scale;
        Log.i("BatteryLevel", "" + level);
        Log.i("BatteryScale", "" + scale);
        Log.i("Battery%", "" + batteryPercent);

        return batteryPercent;
    }


    /**
     * Check whether the network access is available or not.
     *
     * @param ctx
     * @return
     */
    public static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /*
     * From @url{https://github.com/apache/pig/blob/89c2e8e76c68d0d0abe6a36b4e08ddc56979796f/src/org/apache/pig/impl/util/ObjectSerializer.java}
     */
    public static String serialize(Serializable obj) throws IOException {
        if (obj == null) return "";
        try {
            ByteArrayOutputStream serialObj = new ByteArrayOutputStream();
            ObjectOutputStream objStream = new ObjectOutputStream(serialObj);
            objStream.writeObject(obj);
            objStream.close();
            return encodeBytes(serialObj.toByteArray());
        } catch (Exception e) {
//            throw WrappedIOException.wrap("Serialization error: " + e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    public static Object deserialize(String str) throws IOException {
        if (str == null || str.length() == 0) return null;
        try {
            ByteArrayInputStream serialObj = new ByteArrayInputStream(decodeBytes(str));
            ObjectInputStream objStream = new ObjectInputStream(serialObj);
            return objStream.readObject();
        } catch (Exception e) {
//            throw WrappedIOException.wrap("Deserialization error: " + e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    public static String encodeBytes(byte[] bytes) {
        StringBuffer strBuf = new StringBuffer();

        for (int i = 0; i < bytes.length; i++) {
            strBuf.append((char) (((bytes[i] >> 4) & 0xF) + ((int) 'a')));
            strBuf.append((char) (((bytes[i]) & 0xF) + ((int) 'a')));
        }

        return strBuf.toString();
    }

    public static byte[] decodeBytes(String str) {
        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < str.length(); i += 2) {
            char c = str.charAt(i);
            bytes[i / 2] = (byte) ((c - 'a') << 4);
            c = str.charAt(i + 1);
            bytes[i / 2] += (c - 'a');
        }
        return bytes;
    } //End of code from Apache pig
}
