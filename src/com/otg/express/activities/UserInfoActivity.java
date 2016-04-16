package com.otg.express.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import com.otg.express.R;
import com.otg.express.handler.ReadUserInfoHandler;
import com.otg.express.handler.WriteUserInfoHandler;
import com.otg.express.utils.Toaster;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 16-2-1.
 */
public class UserInfoActivity extends Activity implements View.OnClickListener{
    public static final int RESULT_WRITE_SUCCESS = 1;
    public static final int RESULT_WRITE_ERROR = 2;
    public static final int RESULT_READ_SUCCESS = 3;
    public static final int RESULT_READ_ERROR = 4;
    private EditText phoneEdit;
    private EditText id_cardEdit;
    private EditText express_nameEdit;
    private EditText express_numberEdit;
    private EditText express_companyEdit;
    private EditText modify_timeEdit;
    private EditText register_timeEdit;
    private Button submit;
    private ImageButton back;
    private ProgressDialog dia = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_user_setting_info);
        dia = new ProgressDialog(this);
        dia.setTitle("信息");
        dia.setMessage("正在修改...");
        dia.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dia.setCancelable(false);
        phoneEdit = (EditText) findViewById(R.id.phone);
        id_cardEdit = (EditText) findViewById(R.id.id_card);
        express_nameEdit = (EditText) findViewById(R.id.express_name);
        express_numberEdit = (EditText) findViewById(R.id.express_number);
        express_companyEdit = (EditText) findViewById(R.id.express_company);
        modify_timeEdit = (EditText) findViewById(R.id.modify_time);
        register_timeEdit = (EditText) findViewById(R.id.register_time);
        submit = (Button) findViewById(R.id.user_info_submit_button);
        submit.setOnClickListener(this);

        back = (ImageButton)findViewById(R.id.header_left_small);
        back.setOnClickListener(this);

        SharedPreferences sharedPreferences = UserInfoActivity.this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String phone = sharedPreferences.getString("phone","");
        ReadUserInfoHandler readUserInfoHandler = new ReadUserInfoHandler(this,mHandler);
        readUserInfoHandler.handler(phone);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.user_info_submit_button:
                SharedPreferences sharedPreferences = UserInfoActivity.this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                String phone = sharedPreferences.getString("phone","");
                WriteUserInfoHandler writeUserInfoHandler = new WriteUserInfoHandler(this,mHandler);
                writeUserInfoHandler.handler(phone,id_cardEdit.getText().toString(),express_nameEdit.getText().toString(),express_numberEdit.getText().toString(),express_companyEdit.getText().toString());
                dia.show();
                break;
            case R.id.header_left_small:
                finish();
                break;
        }
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RESULT_WRITE_SUCCESS:
                    dia.dismiss();
                    Toaster.show(UserInfoActivity.this, (String) msg.obj);
                    finish();
                    break;
                case RESULT_WRITE_ERROR:
                    dia.dismiss();
                    Toaster.show(UserInfoActivity.this, (String)msg.obj);
                    break;
                case RESULT_READ_SUCCESS:
                    JSONObject jsonObject = (JSONObject)msg.obj;
                    try {
                        String express_name = jsonObject.getString("express_name");
                        String express_company = jsonObject.getString("express_company");
                        String express_number = jsonObject.getString("express_number");
                        String phone = jsonObject.getString("phone");
                        String register_time = jsonObject.getString("register_time");
                        String idCard = jsonObject.getString("idCard");
                        String modify_time = jsonObject.getString("modify_time");
                        phoneEdit.setText(phone);
                        express_nameEdit.setText(express_name);
                        express_numberEdit.setText(express_number);
                        express_companyEdit.setText(express_company);
                        register_timeEdit.setText(register_time);
                        modify_timeEdit.setText(modify_time);
                        id_cardEdit.setText(idCard);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                case RESULT_READ_ERROR:
                    Toaster.show(UserInfoActivity.this, (String)msg.obj);
                    break;
            }
        }
    };
}
