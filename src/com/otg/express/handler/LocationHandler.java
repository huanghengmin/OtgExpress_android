package com.otg.express.handler;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.location.*;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import com.otg.express.activities.QRCodeActivity;

public class LocationHandler{

    private final static int CHECK_POSITION_INTERVAL = 2 * 1000;
    private final static int CHECK_POSITION_DISTANCE = 10;
    private LocationManager locationManager;
    private Location location;
    private Context mContext;
    private Handler mHandler;
    private ListenerGPS listenGPS;

    public LocationHandler(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
        locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
    }

    /*
     * 获取GPS开关状态
     */
    public boolean getGPSState() {
        ContentResolver resolver = mContext.getContentResolver();
        boolean open = Settings.Secure.isLocationProviderEnabled(resolver, LocationManager.GPS_PROVIDER);
        return open;
    }

    /*
     * 切换GPS开关状态̬
     */
    public void toggleGPS() {
        Intent gpsIntent = new Intent();
        gpsIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        gpsIntent.addCategory("android.intent.category.ALTERNATIVE");
        gpsIntent.setData(Uri.parse("custom:3"));

        try {
            PendingIntent.getBroadcast(mContext, 0, gpsIntent, 0).send();
        } catch (CanceledException e) {
            e.printStackTrace();
        }
    }


    // 获取Location Provider
    private String getProvider() {
        // 构建位置查询条件
        Criteria criteria = new Criteria();
        // 查询精度：高
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 是否查询海拨：否
        criteria.setAltitudeRequired(false);
        // 是否查询方位角 : 否
        criteria.setBearingRequired(false);
        // 是否允许付费：是
        criteria.setCostAllowed(true);
        // 电量要求：低
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        // 返回最合适的符合条件的provider，第2个参数为true说明 , 如果只有一个provider是有效的,则返回当前provider
        return locationManager.getBestProvider(criteria, true);
    }


    public Location getLocationByGps() {
        Location location = locationManager.getLastKnownLocation(getProvider());
        return location;
    }


    public void registerListen() {
        if (listenGPS == null) {
            listenGPS = new ListenerGPS();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, CHECK_POSITION_INTERVAL, CHECK_POSITION_DISTANCE, listenGPS);
        }
    }

    public void unRegisterListen() {
        if (listenGPS != null) {
            locationManager.removeUpdates(listenGPS);
            listenGPS = null;
        }
    }

    class ListenerGPS implements LocationListener {
        private static final String TAG = "GPSListener";

        // 位置发生改变后调用
        @Override
        public void onLocationChanged(Location l) {
            // TODO Auto-generated method stub
            if (l != null) {
                location = l;
                Message msg = mHandler.obtainMessage(QRCodeActivity.REFRESH_LOCATION, location);
                msg.sendToTarget();
            }
        }

        // provider 被用户关闭后调用
        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
            location = null;
        }

        // provider 被用户开启后调用
        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
            Location l = locationManager.getLastKnownLocation(provider);
            if (l != null) {
                location = l;
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub
            switch (status) {
                //GPS状态为可见时
                case LocationProvider.AVAILABLE:
                    Log.i(TAG, "当前GPS状态为可见状态");
                    break;
                //GPS状态为服务区外时
                case LocationProvider.OUT_OF_SERVICE:
                    Log.i(TAG, "当前GPS状态为服务区外状态");
                    break;
                //GPS状态为暂停服务时
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.i(TAG, "当前GPS状态为暂停服务状态");
                    break;
            }
        }
    }
}
