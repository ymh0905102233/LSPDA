package com.xx.chinetek.util.deviceInfo;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @ Des:
 * @ Created by yangyiqing on 2021/1/19.
 */
public class DeviceUtils {
    private static final String              TAG      = "DeviceInfo";
    private static       DeviceUtils         INSTANCE = new DeviceUtils();
    //用来存储设备信息和异常信息
    private        Map<String, String> infos    = new HashMap<String, String>();

    //保证只有一个DeviceUtils实例
    private DeviceUtils() {
        //构造函数
    }

    //获取DeviceUtils实例,单例模式
    public static DeviceUtils getInstance() {
        return INSTANCE;
    }

    public static DeviceInfo getDeviceInfo() {
        DeviceInfo deviceInfo = new DeviceInfo();
        Log.e(TAG, "MANUFACTURER=" + Build.MANUFACTURER);
        Log.e(TAG, "BRAND=" + Build.BRAND);
        Log.e(TAG, "MODEL=" + Build.MODEL);
        Log.e(TAG, "VERSION.RELEASE=" + Build.VERSION.RELEASE);
        Log.e(TAG, "VERSION.SDK_INT=" + Build.VERSION.SDK_INT);
        Log.e(TAG, "DEVICE=" + Build.DEVICE);
        Log.e(TAG, "HOST=" + Build.HOST);
        Log.e(TAG, "ID=" + Build.ID);
        Log.e(TAG, "TIME=" + Build.TIME);
        Log.e(TAG, "TYPE=" + Build.TYPE);
        Log.e(TAG, "PRODUCT=" + Build.PRODUCT);
        Log.e(TAG, "BOARD=" + Build.BOARD);
        Log.e(TAG, "DISPLAY=" + Build.DISPLAY);
        Log.e(TAG, "FINGERPRINT=" + Build.FINGERPRINT);
        Log.e(TAG, "HARDWARE=" + Build.HARDWARE);
        Log.e(TAG, "BOOTLOADER=" + Build.BOOTLOADER);
        Log.e(TAG, "TAGS=" + Build.TAGS);
        Log.e(TAG, "UNKNOWN=" + Build.UNKNOWN);
        Log.e(TAG, "USER=" + Build.USER);
        return deviceInfo;
    }
  /**
   * @desc:  获取设备信息
   * @param:
   * @return:
   * @author: Nietzsche
   * @time 2021/1/19 14:06
   */
    public  DeviceInfo getDeviceInfo(String loginUser) {
        DeviceInfo deviceInfo = new DeviceInfo(loginUser, infos);
        return deviceInfo;
    }

    public static class DeviceInfo {
        private String versionName;
        private String versionCode;
        private String serialNo;
        private String loginUser;
        private String SDKVersion;
        private String ID;
        private String model;
        private String manufacturer;

        public DeviceInfo() {
        }

        public DeviceInfo(String loginUser, Map<String, String> deviceInfoMap) {
            if (deviceInfoMap != null) {
                this.versionName = deviceInfoMap.get("versionName");
                this.versionCode = deviceInfoMap.get("versionCode");
                this.serialNo = deviceInfoMap.get("SERIAL");
                this.SDKVersion = deviceInfoMap.get("VERSION.SDK_INT");
                this.ID = deviceInfoMap.get("ID");
                this.model = deviceInfoMap.get("MODEL");
                this.manufacturer = deviceInfoMap.get("MANUFACTURER");

            }
            if (loginUser != null) {
                this.loginUser = loginUser;
            }

        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        public String getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(String versionCode) {
            this.versionCode = versionCode;
        }

        public String getSerialNo() {
            return serialNo;
        }

        public void setSerialNo(String serialNo) {
            this.serialNo = serialNo;
        }

        public String getLoginUser() {
            return loginUser;
        }

        public void setLoginUser(String loginUser) {
            this.loginUser = loginUser;
        }

        public String getSDKVersion() {
            return SDKVersion;
        }

        public void setSDKVersion(String SDKVersion) {
            this.SDKVersion = SDKVersion;
        }

        public String getID() {
            return ID;
        }

        public void setID(String ID) {
            this.ID = ID;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getManufacturer() {
            return manufacturer;
        }

        public void setManufacturer(String manufacturer) {
            this.manufacturer = manufacturer;
        }
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
                Log.d(TAG, "versionName" + " : " + versionName);
                Log.d(TAG, "versionCode" + " : " + versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
                Log.d(TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                Log.e(TAG, "an error occured when collect crash info", e);
            }
        }
        infos.put("VERSION.SDK_INT", Build.VERSION.SDK_INT + "");
        Log.e(TAG, "VERSION.SDK_INT=" + Build.VERSION.SDK_INT);
    }
}
