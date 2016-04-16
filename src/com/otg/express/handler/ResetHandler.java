package com.otg.express.handler;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import com.otg.express.activities.ResetPwdActivity;
import com.otg.express.utils.Md5Key;
import com.otg.express.utils.PostUtils;
import com.otg.express.utils.Server;
import org.json.JSONObject;


/**
 * 状态校验接口
 * <p/>
 * Created by yf on 2014-12-26.
 */
public class ResetHandler {
    private static final String TAG = "ResetHandler";
    private Context context;
    private Handler handler;

    public ResetHandler(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }

    void postData(String phone,String oldPwd,String pwd){
        //修改密码

        SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String remote_ip = mSharedPrefs.getString("remote_admin_ip", Server.admin_server);
        String remote_port = mSharedPrefs.getString("remote_admin_port",Server.admin_port);

        String url = "http://" + remote_ip + ":" + remote_port + "/UserAction_modifyPassword.action";
        PostUtils postUtils = new PostUtils();

        String[][] params = new String[][]{
                {"phone", phone},
                {"oldPwd", oldPwd},
                {"password", pwd}
        };

        try {
            JSONObject object = postUtils.postJsonObject(url, params);
            boolean flag = object.getBoolean("success");
            String msg = object.getString("msg");
            if(flag){
                Message message = new Message();
                message.what = ResetPwdActivity.RESULT_SUCCESS;
                message.obj = msg;
                handler.sendMessage(message);
            }else {
                Message message = new Message();
                message.what = ResetPwdActivity.RESULT_ERROR;
                message.obj = msg;
                handler.sendMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Message message = new Message();
            message.what = ResetPwdActivity.RESULT_ERROR;
            message.obj = "修改失败";
            handler.sendMessage(message);
        }
    }

    public void handler(final String phone,final String oldPwd, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //进行md5加密
                String md5_old_pwd = Md5Key.changeMd5Psd(oldPwd);
                String md5_pwd = Md5Key.changeMd5Psd(password);
                postData(phone,md5_old_pwd,md5_pwd);
            }
        }).start();
    }




}
