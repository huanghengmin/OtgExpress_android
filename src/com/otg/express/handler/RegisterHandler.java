package com.otg.express.handler;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import com.otg.express.activities.RegisterActivity;
import com.otg.express.utils.PostUtils;
import com.otg.express.utils.Md5Key;
import com.otg.express.utils.Server;

/**
 * 状态校验接口
 * <p/>
 * Created by yf on 2014-12-26.
 */
public class RegisterHandler {
    private static final String TAG = "RegisterHandler";
    private Context context;
    private Handler handler;

    public RegisterHandler(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }

    void postData(String phone,String pwd){
        //检测电话是否已经注册
        //注册
        //修改密码
        SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String remote_ip = mSharedPrefs.getString("remote_admin_ip", Server.admin_server);
        String remote_port = mSharedPrefs.getString("remote_admin_port",Server.admin_port);

        String check_url = "http://" + remote_ip +":" + remote_port + "/UserAction_check.action";
        String register_url = "http://" + remote_ip + ":" + remote_port + "/UserAction_register.action";

        PostUtils postUtils = new PostUtils();

        String[][] params = new String[][]{
                {"phone", phone},
                {"password", pwd}
        };

        try {
            boolean flag = postUtils.post(check_url,params);
            if(flag){//手机号未注册
                boolean register = postUtils.post(register_url,params);
                if(register){
                    Message message = new Message();
                    message.what = RegisterActivity.RESULT_SUCCESS;
                    message.obj = "注册成功！";
                    handler.sendMessage(message);
                }else {
                    Message message = new Message();
                    message.what = RegisterActivity.RESULT_ERROR;
                    message.obj = "注册失败！";
                    handler.sendMessage(message);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Message message = new Message();
            message.what = RegisterActivity.RESULT_ERROR;
            message.obj = "手机号已注册，请登陆！";
            handler.sendMessage(message);
        }
    }

    public void handler(final String phone, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //进行md5加密
                String md5_pwd = Md5Key.changeMd5Psd(password);
                postData(phone,md5_pwd);
            }
        }).start();
    }




}
