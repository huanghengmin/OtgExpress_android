package com.otg.express.utils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 16-1-25.
 */
public class PostUtils {

    public boolean post(String url,String[][]   ps)throws Exception {
        HttpPost httpRequest =new HttpPost(url);
        List<NameValuePair> params=new ArrayList<NameValuePair>();
        for (String[] param : ps) {
            params.add(new BasicNameValuePair(param[0], param[1]));
        }
        httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
        HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
        if(httpResponse.getStatusLine().getStatusCode()== HttpStatus.SC_OK) {
            String strResult = EntityUtils.toString(httpResponse.getEntity());
            JSONObject result = new JSONObject(strResult);
            boolean flag = result.getBoolean("success");
            return flag;
        }
        return false;
    }


    public JSONObject postJsonObject(String url,String[][]   ps)throws Exception {
        HttpPost httpRequest =new HttpPost(url);
        List<NameValuePair> params=new ArrayList<NameValuePair>();
        for (String[] param : ps) {
            params.add(new BasicNameValuePair(param[0], param[1]));
        }
        httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
        HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
        if(httpResponse.getStatusLine().getStatusCode()== HttpStatus.SC_OK) {
            String strResult = EntityUtils.toString(httpResponse.getEntity(),"UTF-8");
            JSONObject result = new JSONObject(strResult);
           return result;
        }
        return null;
    }
}
