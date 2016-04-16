package com.otg.express.request;

import android.content.Context;
import android.os.Handler;
import android.telephony.TelephonyManager;
import com.otg.express.activities.FaceImageActivity;
import com.otg.express.activities.SenderImageActivity;
import com.otg.express.activities.UnpackImageActivity;
import com.otg.express.domain.ExpressLog;
import it.sauronsoftware.base64.Base64;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * 状态校验接口
 * <p/>
 * Created by yf on 2014-12-26.
 */
public class PostRequest {
    private Handler handler;
    private ExpressLog expressLog = null;
    private String ip = null;
    private String port = null;
    private Context context;
    TelephonyManager telephonyManager = null;

    public PostRequest(Context context, String ip, ExpressLog expressLog, String port) {
        handler = new Handler(context.getMainLooper());
        this.context = context;
        this.expressLog = expressLog;
        this.ip = ip;
        this.port = port;
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    public interface OnPostListener {
        public void onPostOk(String msg);

        public void onPostErr(String msg);
    }

    public boolean post(String url) throws Exception {
        HttpPost httpRequest = new HttpPost(url);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        String type = expressLog.getType().trim();
        if (type.equals("OTG")) {
            params.add(new BasicNameValuePair("name", expressLog.getName().trim()));
            params.add(new BasicNameValuePair("address", expressLog.getAddress().trim()));
            params.add(new BasicNameValuePair("birthday", expressLog.getBirthday().trim()));
            params.add(new BasicNameValuePair("dn", expressLog.getDN().trim()));
            params.add(new BasicNameValuePair("idCard", expressLog.getIdCard().trim()));
            params.add(new BasicNameValuePair("nation", expressLog.getNation().trim()));
            params.add(new BasicNameValuePair("sex", expressLog.getSex().trim()));
            params.add(new BasicNameValuePair("shapeCode", expressLog.getShapeCode().trim()));
            params.add(new BasicNameValuePair("signDepart", expressLog.getSignDepart().trim()));
            params.add(new BasicNameValuePair("validTime", expressLog.getValidTime().trim()));
            params.add(new BasicNameValuePair("latitude", expressLog.getLatitude()));
            params.add(new BasicNameValuePair("longitude", expressLog.getLongitude()));
            params.add(new BasicNameValuePair("sendTime", expressLog.getSendTime()));
            params.add(new BasicNameValuePair("phone", expressLog.getPhone().trim()));
            params.add(new BasicNameValuePair("contact", expressLog.getContact().trim()));
            params.add(new BasicNameValuePair("type", type));
        } else if (type.equals("OCR")) {
            params.add(new BasicNameValuePair("shapeCode", expressLog.getShapeCode().trim()));
            params.add(new BasicNameValuePair("latitude", expressLog.getLatitude()));
            params.add(new BasicNameValuePair("longitude", expressLog.getLongitude()));
            params.add(new BasicNameValuePair("sendTime", expressLog.getSendTime()));
            params.add(new BasicNameValuePair("phone", expressLog.getPhone().trim()));
            params.add(new BasicNameValuePair("contact", expressLog.getContact().trim()));
            params.add(new BasicNameValuePair("type", type));
        }
        if (expressLog.getBitmap() != null && expressLog.getBitmap().length > 0)
            params.add(new BasicNameValuePair("idimg", new String(Base64.encode(expressLog.getBitmap()))));

        if (expressLog.getFace_img() != null && expressLog.getFace_img().length > 0)
            params.add(new BasicNameValuePair("faceimg", new String(Base64.encode(expressLog.getFace_img()))));

        if (expressLog.getSender_img() != null && expressLog.getSender_img().length > 0)
            params.add(new BasicNameValuePair("senderimg", new String(Base64.encode(expressLog.getSender_img()))));

        if (expressLog.getUnpack_img() != null && expressLog.getUnpack_img().length > 0)
            params.add(new BasicNameValuePair("unpackimg", new String(Base64.encode(expressLog.getUnpack_img()))));

        httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
        HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
        if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String strResult = EntityUtils.toString(httpResponse.getEntity());
            JSONObject result = new JSONObject(strResult);
            boolean flag = result.getBoolean("success");
            return flag;
        }
        return false;
    }

    /*public boolean postJson(String url) throws Exception {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        String IMEI = telephonyManager.getDeviceId();
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"xm\":\"" + expressLog.getName().trim() + "\"").append(",");
        sb.append("\"xb\":\"" + expressLog.getSex().trim() + "\"").append(",");
        sb.append("\"mz\":\"" + expressLog.getNation().trim() + "\"").append(",");
        sb.append("\"csrq\":\"" + expressLog.getBirthday().trim() + "\"").append(",");
        sb.append("\"zz\":\"" + expressLog.getAddress().trim() + "\"").append(",");
        sb.append("\"gmsfhm\":\"" + expressLog.getIdCard().trim() + "\"").append(",");
        sb.append("\"fzjg\":\"" + expressLog.getSignDepart().trim() + "\"").append(",");
        sb.append("\"yxqx\":\"" + expressLog.getValidTime().trim() + "\"").append(",");
        sb.append("\"dn\":\"" + expressLog.getDN().trim() + "\"").append(",");
        sb.append("\"shapecode\":\"" + expressLog.getShapeCode().trim() + "\"").append(",");
        sb.append("\"longitude\":\"" + expressLog.getLongitude() + "\"").append(",");
        sb.append("\"latitude\":\"" + expressLog.getLatitude() + "\"").append(",");
        sb.append("\"sendtime\":\"" + expressLog.getSendTime().trim() + "\"").append(",");
        sb.append("\"express\":\"" + expressLog.getExpress_name().trim() + "\"").append(",");
        sb.append("\"expressNumber\":\"" + expressLog.getExpress_number().trim() + "\"").append(",");
        sb.append("\"expressCompany\":\"" + expressLog.getExpress_company().trim() + "\"").append(",");
        if (expressLog.getBitmap() != null && expressLog.getBitmap().length > 0)
            sb.append("\"idimg\":\"" + new String(Base64.encode(expressLog.getBitmap())) + "\"").append(",");
        sb.append("\"cjsbbh\":\"" + IMEI.trim() + "\"");
        sb.append("}");

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("style", "json"));
        params.add(new BasicNameValuePair("func", "addSFXX"));
        params.add(new BasicNameValuePair("datetime", format.format(new Date())));
        params.add(new BasicNameValuePair("content", sb.toString()));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        HttpContext context = new BasicHttpContext();
        CookieStore cookieStore = new BasicCookieStore();
        context.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        HttpResponse httpResponse = httpClient.execute(httpPost, context);
        int res = httpResponse.getStatusLine().getStatusCode();
        if (res == 200) {
            String strResult = EntityUtils.toString(httpResponse.getEntity());
            JSONObject result = new JSONObject(strResult);
            boolean flag = result.getBoolean("result");
            return flag;
        }
        return false;
    }*/


    public void postData(final OnPostListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String url = "http://" + ip + ":" + port + "/ExpressRealNameAction_upload.action";
//                String url_json = "http://59.172.104.98:8099/wlsmz/appData/postAppData";

                boolean flag = false;
                try {
                    flag = post(url);
                    if (flag) {
                        File senderFile = new File(SenderImageActivity.jpgfile);
                        if (senderFile.exists())
                            senderFile.delete();

                        File unpackFile = new File(UnpackImageActivity.jpgfile);
                        if (unpackFile.exists())
                            unpackFile.delete();

                        File faceFile = new File(FaceImageActivity.jpgfile);
                        if (faceFile.exists())
                            faceFile.delete();

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onPostOk("保存快递实名信息成功");
                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onPostErr("保存快递实名信息失败");
                            }
                        });
                    }
                } catch (Exception e1) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onPostErr("请求服务器异常！");
                        }
                    });
                }

               /* try {
                    flag = postJson(url_json);
                    if (flag) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onPostOk("保存快递实名JSON信息成功");
                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onPostErr("保存快递实名JSON信息失败");
                            }
                        });
                    }
                } catch (Exception e1) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onPostErr("请求JSON服务器异常！");
                        }
                    });
                }*/
            }
        }).start();
    }
}
