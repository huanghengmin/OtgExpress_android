package com.otg.express.handler;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import com.otg.express.activities.UserInfoActivity;
import com.otg.express.utils.PostUtils;
import com.otg.express.utils.Server;
import org.json.JSONObject;


/**
 * 状态校验接口
 * <p/>
 * Created by yf on 2014-12-26.
 */
public class ReadUserInfoHandler {
    private static final String TAG = "ReadUserInfoHandler";
    private Context context;
    private Handler handler;

    public ReadUserInfoHandler(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }

    void postData(String phone){
        //修改密码
        SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String remote_ip = mSharedPrefs.getString("remote_admin_ip", Server.admin_server);
        String remote_port = mSharedPrefs.getString("remote_admin_port",Server.admin_port);

        String url = "http://" + remote_ip + ":" + remote_port + "/UserAction_findUser.action";
        PostUtils postUtils = new PostUtils();

        String[][] params = new String[][]{
                {"phone", phone}
        };

        try {
            JSONObject object = postUtils.postJsonObject(url, params);
            boolean flag = object.getBoolean("success");
            if(flag){
                Message message = new Message();
                message.what = UserInfoActivity.RESULT_READ_SUCCESS;
                message.obj = object;
                handler.sendMessage(message);
            }else {
                Message message = new Message();
                message.what = UserInfoActivity.RESULT_READ_ERROR;
                message.obj = "查询用户信息失败";
                handler.sendMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Message message = new Message();
            message.what = UserInfoActivity.RESULT_READ_ERROR;
            message.obj = "请求用户数据失败";
            handler.sendMessage(message);
        }
    }

    public void handler(final String phone) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                postData(phone);
            }
        }).start();
    }




}
