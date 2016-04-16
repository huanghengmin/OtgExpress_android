package com.otg.express.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import java.io.File;

/**
 * Created by Administrator on 16-3-15.
 */
public class ApkUtils {
    public static final int DOWN_ERROR = 4;
    private Context context;
    private Handler handler;

    public ApkUtils(Context context,Handler handler) {
        this.context = context;
        this.handler = handler;
    }

    /*
         * 从服务器中下载APK
         */
    public void downLoadApk(final String url, final String version) {
        final ProgressDialog pd;    //进度条对话框
        pd = new ProgressDialog(context);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在下载更新");
        pd.show();
        new Thread() {
            @Override
            public void run() {
                try {
                    File file = DownLoadManager.getFileFromServer(url, pd,version);
                    sleep(3000);
                    installApk(file);
                    pd.dismiss(); //结束掉进度条对话框
                } catch (Exception e) {
                    Message msg = new Message();
                    msg.what = DOWN_ERROR;
                    msg.obj="更新版本文件出错！";
                    handler.sendMessage(msg);
                    e.printStackTrace();
                }
            }
        }.start();

    }


    //安装apk
    public void installApk(File file) {
        Intent intent = new Intent();
        //执行动作
        intent.setAction(Intent.ACTION_VIEW);
        //执行的数据类型
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
}
