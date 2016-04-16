package com.otg.express.utils;

import android.app.ProgressDialog;
import android.os.Environment;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DownLoadManager {

    public static File getFileFromServer(String path, ProgressDialog pd,String version) throws Exception {
        HttpPost httpRequest = new HttpPost(path);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("os", "android"));
        params.add(new BasicNameValuePair("version", version));
        httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
        HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
        InputStream is = httpResponse.getEntity().getContent();
        File file = new File(Environment.getExternalStorageDirectory(), PropertiesUtils.APK_FILE);
        FileOutputStream fos = new FileOutputStream(file);
        BufferedInputStream bis = new BufferedInputStream(is);
        byte[] buffer = new byte[1024];
        int len;
        int total = 0;
        while ((len = bis.read(buffer)) != -1) {
            fos.write(buffer, 0, len);
            total += len;
            //获取当前下载量
            pd.setProgress(total);
        }
        fos.close();
        bis.close();
        is.close();
        return file;
    }
}
