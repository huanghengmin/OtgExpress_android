package com.otg.express.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import com.otg.express.R;
import com.otg.express.handler.ResetHandler;
import com.otg.express.utils.TextWatcherAdapter;
import com.otg.express.utils.Toaster;
import com.otg.express.utils.ValidUtils;

/**
 * Created by Administrator on 16-2-1.
 */
public class ResetPwdActivity extends Activity implements View.OnClickListener{
    public static final int RESULT_SUCCESS = 1;
    public static final int RESULT_ERROR = 2;
    private EditText oldEdit;
    private EditText newEdit;
    private EditText repeatEdit;
    private Button submit;
    private ImageButton back;
    private ProgressDialog dia = null;

    private boolean loginEnabled(){
        return (!TextUtils.isEmpty(this.newEdit.getText())) && (!TextUtils.isEmpty(this.repeatEdit.getText()));
    }

    private TextWatcher login = new TextWatcherAdapter(){
        public void afterTextChanged(Editable paramAnonymousEditable){
            ResetPwdActivity.this.submit.setEnabled(ResetPwdActivity.this.loginEnabled());
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_user_password);

        dia = new ProgressDialog(this);
        dia.setTitle("信息");
        dia.setMessage("正在更新...");
        dia.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dia.setCancelable(false);

        oldEdit = (EditText)findViewById(R.id.user_password_old_edit);
        newEdit = (EditText)findViewById(R.id.user_password_new_edit);


        repeatEdit = (EditText)findViewById(R.id.user_password_confirm_edit);
        back = (ImageButton)findViewById(R.id.header_left_small);
        back.setOnClickListener(this);


        submit = (Button)findViewById(R.id.user_password_submit_button);
        submit.setOnClickListener(this);

        newEdit.addTextChangedListener(login);
        repeatEdit.addTextChangedListener(login);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.user_password_submit_button:
                if (!ValidUtils.isValidPass(this.oldEdit.getText().toString())){
                    Toaster.show(this, R.string.valid_pass);
                    return;
                }
                if (!ValidUtils.isValidPass(this.newEdit.getText().toString())){
                    Toaster.show(this, R.string.valid_pass);
                    return;
                }
                if (!ValidUtils.isSame(this.newEdit.getText().toString(), this.repeatEdit.getText().toString()))
                {
                    Toaster.show(this, R.string.valid_pass_next);
                    return;
                }
                SharedPreferences sharedPreferences = ResetPwdActivity.this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                String phone = sharedPreferences.getString("phone","");
                ResetHandler resetHandler = new ResetHandler(this,mHandler);
                resetHandler.handler(phone,oldEdit.getText().toString(),newEdit.getText().toString());
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
                case RESULT_SUCCESS:
                    dia.dismiss();
                    Toaster.show(ResetPwdActivity.this, (String) msg.obj);
                    finish();
                    break;
                case RESULT_ERROR:
                    dia.dismiss();
                    Toaster.show(ResetPwdActivity.this, (String)msg.obj);
                    break;
            }
        }
    };
}
