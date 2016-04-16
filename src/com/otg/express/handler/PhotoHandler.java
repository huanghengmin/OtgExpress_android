package com.otg.express.handler;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import com.ivsign.android.IDCReader.IDCReaderSDK;
import com.otg.express.fragments.OtgFragment;
import com.otg.express.utils.StreamUtils;

/**
 * Created by Administrator on 16-1-15.
 */
public class PhotoHandler {
    private static final String TAG = "PhotoHandler";
    private Context context;
    private Handler handler;
    private byte[] bytes;
    byte[] localObject2 = new byte[]{5, 0, 1, 0, 91, 3, 51, 1, 90, -77, 30};


    public PhotoHandler(Handler handler, Context context, byte[] bytes) {
        this.handler = handler;
        this.context = context;
        this.bytes = bytes;
    }

    public void handler() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<=5;i++){
                    int ret = IDCReaderSDK.wltGetBMP(bytes, localObject2);
                    if(ret==1){
                        String p1 = context.getFilesDir().getAbsolutePath() + "/zp.bmp";
                        byte[] b = StreamUtils.input2byte(p1);
                        Message message = new Message();
                        message.what = OtgFragment.READ_PHOTO_SUCCESS;
                        message.obj = b;
                        handler.sendMessage(message);
                        break;
                    }else {
                        if(i==5){
                            Message message = new Message();
                            message.what = OtgFragment.READ_PHOTO_ERROR;
                            message.obj = "读取照片返回错误码" + ret;
                            handler.sendMessage(message);
                            break;
                        }
                    }
                }
            }
        }).start();
    }
}
