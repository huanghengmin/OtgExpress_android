package com.otg.express.handler;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import com.otg.express.activities.LoginActivity;
import com.otg.express.utils.PostUtils;
import com.otg.express.utils.Base64Util;
import com.otg.express.utils.Md5Key;
import com.otg.express.utils.Server;
import com.otg.express.utils.des.DesUtils;

import java.security.Key;

/**
 * 状态校验接口
 * <p/>
 * Created by yf on 2014-12-26.
 */
public class LoginHandler {
    private static final String TAG = "LoginHandler";
    private Context context;
    private Handler handler;


    public LoginHandler(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }

    void postData(String uploadPhone, String uploadPwd,String password) {
        //登陆
        SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String remote_ip = mSharedPrefs.getString("remote_admin_ip", Server.admin_server);
        String remote_port = mSharedPrefs.getString("remote_admin_port",Server.admin_port);

        String url = "http://" + remote_ip + ":" + remote_port + "/UserAction_login.action";


        PostUtils postUtils = new PostUtils();

        String[][] params = new String[][]{
                {"phone", uploadPhone},
                {"password", uploadPwd}
        };

        try {
            boolean flag = postUtils.post(url, params);
            if (flag) {
                Message message = new Message();
                message.what = LoginActivity.RESULT_SUCCESS;
                message.obj = "登陆成功";
                handler.sendMessage(message);

                /**
                 * 保存用户登陆信息
                 */
                SharedPreferences sharedPreferences = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor user_editor = sharedPreferences.edit();
                user_editor.putString("phone", uploadPhone);
                user_editor.commit();

                /**
                 * 保存密码
                 */
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                boolean save = prefs.getBoolean("rember_pwd", false);
                if (save) {
                    Key key = DesUtils.getKey(context);
                    if (key != null) {
                        try {
                            //先des加密
                            byte[] dest = DesUtils.encrypt(password.getBytes(), key);
                            //base64加密
                            String pwd = new String(Base64Util.encode(dest));

                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("phone", uploadPhone);
                            editor.putString("pwd", pwd);
                            editor.commit();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Message msg = new Message();
                            msg.what = LoginActivity.ENCRYPT_ERROR;
                            msg.obj = "加密密码出错";
                            handler.sendMessage(msg);
                        }
                    } else {
                        try {
                            DesUtils.saveDesKey(context);
                            Key new_key = DesUtils.getKey(context);
                            //先des加密
                            byte[] dest = DesUtils.encrypt(password.getBytes(), new_key);
                            //base64加密
                            String pwd = new String(Base64Util.encode(dest));

                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("phone", uploadPhone);
                            editor.putString("pwd", pwd);
                            editor.commit();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Message msg = new Message();
                            msg.what = LoginActivity.ENCRYPT_ERROR;
                            msg.obj = "加密密码出错";
                            handler.sendMessage(msg);
                        }
                    }
                }
            } else {
                Message message = new Message();
                message.what = LoginActivity.RESULT_ERROR;
                message.obj = "登陆失败，请确定手机号和密码填写正确！";
                handler.sendMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Message message = new Message();
            message.what = LoginActivity.RESULT_ERROR;
            message.obj = "登陆失败";
            handler.sendMessage(message);
        }
    }

    public void handler(final String phone, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //进行md5加密
                String md5_pwd = Md5Key.changeMd5Psd(password);
                postData(phone, md5_pwd,password);
            }
        }).start();
    }


}
