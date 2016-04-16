package com.otg.express.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.otg.express.R;
import com.otg.express.domain.ExpressLog;
import com.otg.express.handler.LocationHandler;
import com.otg.express.request.PostRequest;
import com.otg.express.utils.Server;
import com.otg.express.utils.ValidUtils;
import com.zxing.activity.CaptureActivity;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

public class QRCodeActivity extends Activity implements OnClickListener {

    private static final int PHOTO_PIC = 1;
    private ExpressLog expressLog = null;
    private static final int SETTING_ADMIN_IP = 11;
    private TextView qr_code;
    private EditText phone_et;
    public String remoteAdminIP;
    SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
    private String remoteAdminPort;
    private LocationHandler locationHandler;
    private Context context;
    public static final int REFRESH_LOCATION = 15; //gps信息改变
    private Location location = null;
    private ProgressDialog dia = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_qr_code);
        // 默认软键盘不弹出
        getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        this.context = this;
        dia = new ProgressDialog(this);
        dia.setTitle("信息");
        dia.setMessage("正在提交...");
        dia.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dia.setCancelable(false);
        //    得到跳转到该Activity的Intent对象
        Intent intent = getIntent();
        expressLog = (ExpressLog) intent.getSerializableExtra("expressLog");
        //        findViewById(R.id.read_camera).setOnClickListener(this);
        findViewById(R.id.success).setOnClickListener(this);
        qr_code = (TextView) findViewById(R.id.qr_code);
        phone_et = (EditText) findViewById(R.id.phone);
        SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        remoteAdminIP = mSharedPrefs.getString("remote_admin_ip", Server.admin_server);
        remoteAdminPort = mSharedPrefs.getString("remote_admin_port", Server.admin_port);
        expressLog.setShapeCode(null);
        qr_code.setText("");

        locationHandler = new LocationHandler(context, mHandler);
        boolean open = locationHandler.getGPSState();
        if (open) {
            locationHandler.registerListen();
        } else {
            locationHandler.toggleGPS();
        }

        //跳转到拍照界面扫描二维码
        Intent i = new Intent(this, CaptureActivity.class);
        startActivityForResult(i, PHOTO_PIC);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        this.getMenuInflater().inflate(R.menu.option_menu_readcode, menu);
        return true;
    }

    /**
     * 设置menu显示icon
     */
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {

        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setadminip:
                Intent intent = new Intent(this, SetAdminActivity.class);
                startActivityForResult(intent, SETTING_ADMIN_IP);
                return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PHOTO_PIC:
                    String result = data.getExtras().getString("result");
                    expressLog.setShapeCode(result);
                    if (location != null) {
                        expressLog.setLongitude(String.valueOf(location.getLongitude()));
                        expressLog.setLatitude(String.valueOf(location.getLatitude()));
                    } else {
                        Location l = locationHandler.getLocationByGps();
                        if (l != null) {
                            location = l;
                            expressLog.setLongitude(String.valueOf(location.getLongitude()));
                            expressLog.setLatitude(String.valueOf(location.getLatitude()));
                        }
                    }
                    String date = format.format(new Date());
                    expressLog.setSendTime(date);
                    qr_code.setText(result);
                    break;
                default:
                    break;
            }
        } else if (requestCode == SETTING_ADMIN_IP) {
            SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            remoteAdminIP = mSharedPrefs.getString("remote_admin_ip", Server.admin_server);
            remoteAdminPort = mSharedPrefs.getString("remote_admin_port", Server.admin_port);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.success:
                SharedPreferences sharedPreferences = QRCodeActivity.this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                String phone = sharedPreferences.getString("phone", "");
                expressLog.setPhone(phone);
                boolean contact = ValidUtils.isValidPhoneNumber(phone_et);
                if (contact) {
                    expressLog.setContact(phone_et.getText().toString());
                    if (expressLog.getShapeCode() != null) {
                        //提交数据到后台服务器
                        if (remoteAdminIP == null || "".equals(remoteAdminIP)) {
                            new AlertDialog.Builder(this).setTitle("提示")//设置对话框标题
                                    .setMessage("请先配置管理服务器地址！")//设置显示的内容
                                    .setNegativeButton("返回", new DialogInterface.OnClickListener() {//添加返回按钮
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {//响应事件
                                            Log.i("alert dialog", " 请先配置管理服务器地址！");
                                        }
                                    }).show();//在按键响应事件中显示此对话框
                            break;
                        }
                        dia.show();
                        PostRequest postRequest = new PostRequest(this, remoteAdminIP, expressLog, remoteAdminPort);
                        postRequest.postData(new PostRequest.OnPostListener() {
                            @Override
                            public void onPostOk(String msg) {
                                dia.dismiss();
                                Toast.makeText(QRCodeActivity.this, msg, Toast.LENGTH_LONG).show();
                                startActivity(new Intent(context,MainFragmentActivity.class));
                                finish();
                            }

                            @Override
                            public void onPostErr(String msg) {
                                dia.dismiss();
                                Toast.makeText(QRCodeActivity.this, msg, Toast.LENGTH_LONG).show();
                            }
                        });

//                        QRCodeActivity.this.finish();
                    } else {
                        new AlertDialog.Builder(this).setTitle("提示")//设置对话框标题
                                .setMessage("请重新扫描条形码信息！")//设置显示的内容
                                .setNegativeButton("返回", new DialogInterface.OnClickListener() {//添加返回按钮
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {//响应事件
                                        Log.i("alert dialog", " 请重新扫描条形码信息！");
                                    }
                                }).show();//在按键响应事件中显示此对话框
                    }
                } else {
                    new AlertDialog.Builder(this).setTitle("提示")//设置对话框标题
                            .setMessage("请输入正确的手机号码！")//设置显示的内容
                            .setNegativeButton("返回", new DialogInterface.OnClickListener() {//添加返回按钮
                                @Override
                                public void onClick(DialogInterface dialog, int which) {//响应事件
                                    Log.i("alert dialog", " 请输入正确的手机号码！");
                                }
                            }).show();//在按键响应事件中显示此对话框
                }
                break;
            default:
                break;
        }

    }

    @Override
    protected void onDestroy() {
        if (locationHandler != null) {
            locationHandler.unRegisterListen();
            locationHandler = null;
        }
        super.onDestroy();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_LOCATION:
                    Location l = (Location) msg.obj;
                    if (l != null) {
                        location = l;
                    }
                    break;
            }
        }
    };
}
