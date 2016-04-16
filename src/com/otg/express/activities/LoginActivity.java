package com.otg.express.activities;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import com.otg.express.R;
import com.otg.express.handler.LoginHandler;
import com.otg.express.utils.*;
import com.otg.express.utils.des.DesUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends Activity {
    private static final String TAG = "LoginActivity";
    public static final int RESULT_SUCCESS = 1;
    public static final int RESULT_ERROR = 2;
    public static final int ENCRYPT_ERROR = 3; // 加密出错
    private static final int UPDATA_CLIENT = 5;
    private static final int UPDATA_NONEED = 6;
    private static final int GET_UNDATAINFO_ERROR = 7;

    private EditText phoneEdit;
    private EditText pswEdit;
    private Context context;
    ProgressDialog  dia =null;


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu_admin:
                startActivity(new Intent(context, SetAdminActivity.class));
                return true;
            case R.id.action_menu_otg:
                startActivity(new Intent(context, SetOtgActivity.class));
                return true;
        }
        return false;
    }



    /**
     * 设置menu显示icon
     */
    @Override
    public boolean onMenuOpened(int featureId, Menu menu){

        if (featureId == Window.FEATURE_ACTION_BAR && menu != null)
        {
            if (menu.getClass().getSimpleName().equals("MenuBuilder"))
            {
                try
                {
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        return super.onMenuOpened(featureId, menu);
    }


    /*
    * 获取当前程序的版本号
    */
    private String getVersionName(Context context) {
        //获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
        return packInfo.versionName;
    }

    public class CheckVersionTask implements Runnable {
        public void run() {
            SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            String remoteAdminIP = mSharedPrefs.getString("remote_admin_ip", Server.admin_server);
            String remoteAdminPort = mSharedPrefs.getString("remote_admin_port", Server.admin_port);
            String pathUrl = "http://" + remoteAdminIP + ":" + remoteAdminPort + PropertiesUtils.CHECK_UPGRADE;
            HttpPost httpRequest = new HttpPost(pathUrl);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("os", "android"));
            params.add(new BasicNameValuePair("version", getVersionName(context)));
            try {
                httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
                if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    String strResult = EntityUtils.toString(httpResponse.getEntity());
                    JSONObject result = new JSONObject(strResult);
                    boolean flag = result.getBoolean("flag");
                    if (flag) {
                        Log.i(TAG, "检测到有新版本，需要更新");
                        Message msg = new Message();
                        msg.what = UPDATA_CLIENT;
                        msg.obj = "检测到有新版本，需要更新 ";
                        mHandler.sendMessage(msg);
                    } else {
                        Log.i(TAG, "已经是最新版本！");
                        Message msg = new Message();
                        msg.what = UPDATA_NONEED;
                        msg.obj = "已经是最新版本";
                        mHandler.sendMessage(msg);
                    }
                }
            } catch (Exception e) {
                Message msg = new Message();
                msg.what = GET_UNDATAINFO_ERROR;
                msg.obj = "服务器读取数据出错";
                mHandler.sendMessage(msg);
            }
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_user_login);
        this.context = this;

        dia = new ProgressDialog(this);
        dia.setTitle("信息");
        dia.setMessage("正在登陆...");
        dia.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dia.setCancelable(false);

        CheckVersionTask cv = new CheckVersionTask();
        new Thread(cv).start();

        phoneEdit = (EditText) findViewById(R.id.phone);
        pswEdit = (EditText) findViewById(R.id.psw);
        CheckBox remBox = (CheckBox)findViewById(R.id.rember);


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean save = prefs.getBoolean("rember_pwd",false);
        if(save) {
            if(!remBox.isChecked()){
                remBox.setChecked(true);
            }
            String s_phone = prefs.getString("phone", null);
            //记住的md5密码，使用des,base64加密
            String s_pwd = prefs.getString("pwd",null);
            if(s_phone!=null&&s_pwd!=null){
                //提交数据
                Key key = DesUtils.getKey(this);
                if (key != null) {
                    try {
                        //base64解码
                        byte[] base64_pwd = Base64Util.decode(s_pwd);
                        //des解码
                        byte[] b_pwd = DesUtils.decrypt(base64_pwd, key);
                        //解码后的密码
                        String pwd = new String(b_pwd);
                        pswEdit.setText(pwd);
                        phoneEdit.setText(s_phone);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void onWelClick(View v){
        switch (v.getId()){
           /* case R.id.register:
                startActivity(new Intent(context,RegisterActivity.class));
                finish();
                break;*/
            case R.id.rember:
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                if (((CheckBox) v).isChecked()) {
                   boolean save = prefs.getBoolean("rember_pwd",false);
                    if(!save) {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("rember_pwd", true);
                        editor.commit();
                    }
                } else {
                    boolean save = prefs.getBoolean("rember_pwd",false);
                    if(save) {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("rember_pwd", false);
                        editor.commit();
                    }
                }
                break;
            case R.id.forget:
                startActivity(new Intent(context, ForgetPswActivity.class));
                finish();
                break;
            case R.id.login:
                if (!ValidUtils.isValidPhoneNumber(this.phoneEdit.getText().toString())){
                    Toaster.show(this, R.string.please_insert_success_phone_number);
                    return;
                }
                if (!ValidUtils.isValidPass(this.pswEdit.getText().toString()))
                {
                    Toaster.show(this, R.string.valid_pass);
                    return;
                }
                LoginHandler loginHandler = new LoginHandler(this,mHandler);
                loginHandler.handler(phoneEdit.getText().toString(),pswEdit.getText().toString());
                dia.show();
                break;
        }
    }

    /*
     * 弹出对话框通知用户更新程序
     * 弹出对话框的步骤：
     *  1.创建alertDialog的builder.
     *  2.要给builder设置属性, 对话框的内容,样式,按钮
     *  3.通过builder 创建一个对话框
     *  4.对话框show()出来
     */
    protected void showUpdataDialog(String msg) {
        AlertDialog.Builder builer = new AlertDialog.Builder(context);
        builer.setTitle("版本升级");
        builer.setMessage(msg);
        //当点确定按钮时从服务器上下载 新的apk 然后安装   װ
        builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "下载apk,更新");
                ApkUtils apkUtils = new ApkUtils(context,mHandler);
                SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                String remoteAdminIP = mSharedPrefs.getString("remote_admin_ip", Server.admin_server);
                String remoteAdminPort = mSharedPrefs.getString("remote_admin_port", Server.admin_port);
                String pathUrl = "http://" + remoteAdminIP + ":" + remoteAdminPort + PropertiesUtils.ACTION_UPGRADE;
                apkUtils.downLoadApk(pathUrl,getVersionName(context));
            }
        });

        builer.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });
        AlertDialog dialog = builer.create();
        dialog.show();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ENCRYPT_ERROR:
                    dia.dismiss();
                    Toaster.show(context, (String)msg.obj);
                    break;
                case RESULT_SUCCESS:
                    dia.dismiss();
                    Toaster.show(context, (String)msg.obj);
                    startActivity(new Intent(context,MainFragmentActivity.class));
                    finish();
                    break;
                case RESULT_ERROR:
                    dia.dismiss();
                    Toaster.show(context, (String)msg.obj);
                    break;
                case ApkUtils.DOWN_ERROR:
                    //下载apk失败
                    Toaster.show(context, (String) msg.obj);
                    break;
                case UPDATA_NONEED:
                    Toaster.show(context, (String) msg.obj);
                    break;
                case UPDATA_CLIENT:
                    //对话框通知用户升级程序
                    showUpdataDialog((String) msg.obj);
                    break;
                case GET_UNDATAINFO_ERROR:
                    Toaster.show(context, (String) msg.obj);
                    break;
            }
        }
    };


}
