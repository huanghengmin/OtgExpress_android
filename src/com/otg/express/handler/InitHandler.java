package com.otg.express.handler;

import android.content.Context;
import android.util.Log;
import com.ivsign.android.IDCReader.IDCReaderSDK;
import com.otg.express.utils.StreamUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * 状态校验接口
 * <p/>
 * Created by yf on 2014-12-26.
 */
public class InitHandler {
    private static final String TAG = "InitHandler";
    private Context context;

    public InitHandler(Context context) {
        this.context = context;
    }

    public void handler() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream lic_data = context.getResources().getAssets().open("license.dat");
                    InputStream lic = context.getResources().getAssets().open("license.lic");
                    InputStream base = context.getResources().getAssets().open("base.dat");

                    String p = context.getFilesDir().getAbsolutePath();

                    StreamUtils.copyInputStream(lic_data, p + "/license.dat");
                    StreamUtils.copyInputStream(lic, p + "/license.lic");
                    StreamUtils.copyInputStream(base, p + "/base.dat");

                    String path = context.getFilesDir().getAbsolutePath();
                    //解码库初始化
                    if (0 == IDCReaderSDK.wltInit(path)) {
                        Log.e(TAG, "Main wltInit success");
                    } else {
                        Log.e(TAG, "Main wltInit failed");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
