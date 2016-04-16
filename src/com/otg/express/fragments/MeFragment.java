package com.otg.express.fragments;

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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.otg.express.R;
import com.otg.express.activities.AboutActivity;
import com.otg.express.activities.LoginActivity;
import com.otg.express.activities.ResetPwdActivity;
import com.otg.express.activities.UserInfoActivity;
import com.otg.express.utils.ApkUtils;
import com.otg.express.utils.PropertiesUtils;
import com.otg.express.utils.Server;
import com.otg.express.utils.Toaster;
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

import java.util.ArrayList;
import java.util.List;

public class MeFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "MeFragment";
    private static final int UPDATA_NONEED = 1;
    private static final int UPDATA_CLIENT = 2;
    private static final int GET_UNDATAINFO_ERROR = 3;
    private Button exitLogin;
    private RelativeLayout layoutUserInfo;
    private RelativeLayout layoutPwdReset;
    private RelativeLayout layoutVersion;
    private RelativeLayout layoutAbout;
    private ProgressDialog dia = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dia = new ProgressDialog(getActivity());
        dia.setTitle("信息");
        dia.setMessage("正在检测...");
        dia.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dia.setCancelable(false);
        /*Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(it, 1);*/
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        // TODO Auto-generated method stub
        super.onHiddenChanged(hidden);
        if (!hidden) {// 不在最前端界面显示
            getActivity().setTitle("个人信息");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.tab_me, container, false);

        layoutUserInfo = (RelativeLayout) v.findViewById(R.id.fragment_my_personal_layout);
        layoutUserInfo.setClickable(true);
        layoutUserInfo.setOnClickListener(this);

        layoutPwdReset = (RelativeLayout) v.findViewById(R.id.fragment_my_password_layout);
        layoutPwdReset.setClickable(true);
        layoutPwdReset.setOnClickListener(this);


        layoutVersion = (RelativeLayout) v.findViewById(R.id.fragment_my_version_layout);
        layoutVersion.setClickable(true);
        layoutVersion.setOnClickListener(this);

        layoutAbout = (RelativeLayout) v.findViewById(R.id.fragment_my_about_layout);
        layoutAbout.setClickable(true);
        layoutAbout.setOnClickListener(this);

        exitLogin = (Button) v.findViewById(R.id.fragment_my_signout_button);
        exitLogin.setOnClickListener(this);
        return v;
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);

            switch (msg.what) {
                case ApkUtils.DOWN_ERROR:
                    dia.dismiss();
                    Toaster.show(getActivity(), (String) msg.obj);
                    break;
                case UPDATA_NONEED:
                    dia.dismiss();
                    Toaster.show(getActivity(), (String) msg.obj);
                    break;
                case UPDATA_CLIENT:
                    dia.dismiss();
                    //对话框通知用户升级程序
                    showUpdataDialog((String) msg.obj);
                    break;
                case GET_UNDATAINFO_ERROR:
                    dia.dismiss();
                    Toaster.show(getActivity(), (String) msg.obj);
                    break;
            }

        }

    };

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
            SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String remoteAdminIP = mSharedPrefs.getString("remote_admin_ip", Server.admin_server);
            String remoteAdminPort = mSharedPrefs.getString("remote_admin_port", Server.admin_port);
            String pathUrl = "http://" + remoteAdminIP + ":" + remoteAdminPort + PropertiesUtils.CHECK_UPGRADE;
            HttpPost httpRequest = new HttpPost(pathUrl);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("os", "android"));
            params.add(new BasicNameValuePair("version", getVersionName(getActivity())));
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
                        handler.sendMessage(msg);
                    } else {
                        Log.i(TAG, "已经是最新版本！");
                        Message msg = new Message();
                        msg.what = UPDATA_NONEED;
                        msg.obj = "已经是最新版本";
                        handler.sendMessage(msg);
                    }
                }
            } catch (Exception e) {
                Message msg = new Message();
                msg.what = GET_UNDATAINFO_ERROR;
                msg.obj = "服务器读取数据出错";
                handler.sendMessage(msg);
            }
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
        AlertDialog.Builder builer = new AlertDialog.Builder(getActivity());
        builer.setTitle("版本升级");
        builer.setMessage(msg);
        //当点确定按钮时从服务器上下载 新的apk 然后安装   װ
        builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "下载apk,更新");
                ApkUtils apkUtils = new ApkUtils(getActivity(),handler);
                SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String remoteAdminIP = mSharedPrefs.getString("remote_admin_ip", Server.admin_server);
                String remoteAdminPort = mSharedPrefs.getString("remote_admin_port", Server.admin_port);
                String pathUrl = "http://" + remoteAdminIP + ":" + remoteAdminPort + PropertiesUtils.ACTION_UPGRADE;
                apkUtils.downLoadApk(pathUrl,getVersionName(getActivity()));
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fragment_my_personal_layout:
//				Toast.makeText(getActivity(),"personInfo",Toast.LENGTH_LONG).show();
                startActivity(new Intent(getActivity(), UserInfoActivity.class));
                break;
            case R.id.fragment_my_password_layout:
//				Toast.makeText(getActivity(),"passwordInfo",Toast.LENGTH_LONG).show();
                startActivity(new Intent(getActivity(), ResetPwdActivity.class));
                break;
            case R.id.fragment_my_version_layout:
                dia.show();
                CheckVersionTask cv = new CheckVersionTask();
                new Thread(cv).start();
                break;
            case R.id.fragment_my_about_layout:
                startActivity(new Intent(getActivity(), AboutActivity.class));
                break;
            case R.id.fragment_my_signout_button:
//				Toast.makeText(getActivity(),"exitLogin",Toast.LENGTH_LONG).show();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                sharedPreferences.edit().clear().commit();
                getActivity().finish();
                break;
            default:
                break;
        }
    }
}
