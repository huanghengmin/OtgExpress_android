package com.otg.express.handler;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import com.otg.express.domain.IdentityCard;
import com.otg.express.fragments.OtgFragment;
import com.otg.express.utils.MessageUtils;
import com.lz.nfc.jni.NFCJni;
import com.otg.express.utils.Server;
import com.otg.express.utils.UncodeUtils;
import com.otg.express.utils.ByteHex;


/**
 * 状态校验接口
 * <p/>
 * Created by yf on 2014-12-26.
 */
public class ReadHandler {
    private static final String TAG = "ThreadHandler";
    private Context context;
    private Handler handler;
    NFCJni nfcJni = new NFCJni();
    private int cfd;
    private UncodeUtils uncodeUtils = new UncodeUtils();

    public ReadHandler(Handler handler, Context context,int cfd) {
        this.handler = handler;
        this.context = context;
        this.cfd = cfd;
    }




    /*
    char szCardNo[38];         //身份证号
	char szName[32];           //姓名
	char szSex[4];             //性别
	char szNationality[6];     //民族
	char szBirth[18];          //出生
	char szAddress[92];        //住址
	char szAuthority[92];      //签发机关
	char szPeriod[40];         //有效期限
	char szPhoto[1024];        //身份证照片, wlf格式
	char szDn[16];		       //DN码
	char Reserve[48];          //保留
    */
    private void parseReadIDCard(byte[] bytes) throws Exception {
        IdentityCard identityCard = new IdentityCard();
        // 身份证：38
        byte[] idcard_bytes = new byte[38];
        for (int i = 0; i <= 37; i++) {
            idcard_bytes[i - 0] = bytes[i];
        }
        String idCard = uncodeUtils.Byte2Unicode(idcard_bytes);
        identityCard.setIdCard(idCard);
        // 姓名:32
        byte[] name_bytes = new byte[32];
        for (int i = 38; i <= 69; i++) {
            name_bytes[i - 38] = bytes[i];
        }
        String name = uncodeUtils.Byte2Unicode(name_bytes);
        identityCard.setName(name);
        // 性别:4
        byte[] xb_bytes = new byte[4];
        for (int i = 70; i <= 73; i++) {
            xb_bytes[i - 70] = bytes[i];
        }
        String xb = uncodeUtils.Byte2Unicode(xb_bytes);
        identityCard.setSex(MessageUtils.getSex(xb));
        // 民族:6
        byte[] mz_bytes = new byte[6];
        for (int i = 74; i <= 79; i++) {
            mz_bytes[i - 74] = bytes[i];
        }
        String mz = uncodeUtils.Byte2Unicode(mz_bytes);
        identityCard.setMz(MessageUtils.getMinZu(mz));
        // 出生:18
        byte[] birth_bytes = new byte[18];
        for (int i = 80; i <= 97; i++) {
            birth_bytes[i - 80] = bytes[i];
        }
        String birth = uncodeUtils.Byte2Unicode(birth_bytes);
        identityCard.setBirth(birth);
        // 住址:92
        byte[] zz_bytes = new byte[92];
        for (int i = 98; i <= 189; i++) {
            zz_bytes[i - 98] = bytes[i];
        }
        String zz = uncodeUtils.Byte2Unicode(zz_bytes);
        identityCard.setAddress(zz);
        // 签发机关:92
        byte[] jf_bytes = new byte[92];
        for (int i = 190; i <= 281; i++) {
            jf_bytes[i - 190] = bytes[i];
        }
        String jftext = uncodeUtils.Byte2Unicode(jf_bytes);
        identityCard.setSign(jftext);
        // 有效期限:40
        byte[] start_bytes = new byte[40];
        for (int i = 282; i <= 321; i++) {
            start_bytes[i - 282] = bytes[i];
        }
        String validity = uncodeUtils.Byte2Unicode(start_bytes);
        identityCard.setValidity(validity);

        // 身份证照片, wlf格式:1024
        byte[] photo_bytes = new byte[1024];
        for (int i = 322; i <= 1345; i++) {
            photo_bytes[i - 322] = bytes[i];
        }

        PhotoHandler photoHandler = new PhotoHandler(handler,context,photo_bytes);
        photoHandler.handler();

        byte[] dn_bytes = new byte[16];
        for (int i = 1346; i <= 1361; i++) {
            dn_bytes[i - 1346] = bytes[i];
        }
        String dn = ByteHex.bytesToHexString(dn_bytes).toUpperCase();
        identityCard.setDN(dn);

        Message message = new Message();
        message.what = OtgFragment.READ_IDCARD_SUCCESS;
        message.obj = identityCard;
        handler.sendMessage(message);
    }


    public void postData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (cfd > 0) {
                    nfcJni.SetNFCLocal(nfcJni.ANDROID_OTG_DEV, cfd);
                    SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                    String remote_ip = mSharedPrefs.getString("remote_ip", Server.otg_server);
                    String remote_port = mSharedPrefs.getString("remote_port",Server.otg_port);
                    if(remote_ip!=null&&remote_ip.length()>0&&remote_port!=null&&remote_port.length()>0) {
                        nfcJni.SetNFCServer(remote_ip, Integer.parseInt(remote_port));
                        byte[] nfc_data = new byte[2048];
                        int iret = nfcJni.NFCCardReader(nfc_data);
                        if (iret == 0) {
                            try {
                                parseReadIDCard(nfc_data);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            int i = nfcJni.GetLastError();
                            Message message = new Message();
                            message.what = i;
                            message.obj = MessageUtils.getMsg(i);
                            handler.sendMessage(message);
                        }
                    }else {
                        Message message = new Message();
                        message.what = OtgFragment.PLASE_INIT_SERVER;
                        message.obj = "请先初始化服务器配置";
                        handler.sendMessage(message);
                    }
                }else {
                    Message message = new Message();
                    message.what = OtgFragment.NOT_FOUND_DEVICE;
                    message.obj = "未找到读卡设备";
                    handler.sendMessage(message);
                }
            }
        }).start();
    }
}
