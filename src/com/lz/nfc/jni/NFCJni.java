package com.lz.nfc.jni;

public class NFCJni {
    static {
        System.loadLibrary("NfcIdJni");
    }

    public static int ANDROID_OTG_DEV = 3;

    //获取设备序列号
    public native int GetDevSerialNo(byte[] var1, int len);

    //获取错误号
    public native int GetLastError();

    //本地设备设置
    public native void SetNFCLocal(int type, int devfd);

    //设置NFC服务器
    public native void SetNFCServer(String IP, int Port);

    //NFC读卡
    public native int NFCCardReader(byte[] msg);

}
