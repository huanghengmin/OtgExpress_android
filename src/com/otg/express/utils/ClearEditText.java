package com.otg.express.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import com.otg.express.R;

public class ClearEditText extends EditText {
    private Drawable imgClearButton = null;
    private Drawable imgValidButton = null;
    private boolean isDisplayValid = false;

    public ClearEditText(Context paramContext) {
        super(paramContext);
        init();
    }

    public ClearEditText(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        init();
    }

    public ClearEditText(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        init();
    }

    public void diplayValidButton() {
        this.isDisplayValid = true;
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], this.imgValidButton, getCompoundDrawables()[3]);
    }

    void handleClearButton() {
        Log.i("phone", "handleClearButton");
        if (getText().toString().equals("")) {
            setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], null, getCompoundDrawables()[3]);
            if (this.isDisplayValid) {
                setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], this.imgValidButton, getCompoundDrawables()[3]);
                this.isDisplayValid = false;
            }
            return;
        }
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], this.imgClearButton, getCompoundDrawables()[3]);
    }

    public void hideClearButton() {
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], null, getCompoundDrawables()[3]);
    }

    void init() {
        try {
            this.imgValidButton = getResources().getDrawable(R.drawable.img_valid_empty);
            this.imgValidButton.setBounds(0, 0, this.imgValidButton.getIntrinsicWidth(), this.imgValidButton.getIntrinsicHeight());
            this.imgClearButton = getResources().getDrawable(R.drawable.icon_clear);
            this.imgClearButton.setBounds(0, 0, this.imgClearButton.getIntrinsicWidth(), this.imgClearButton.getIntrinsicHeight());
            handleClearButton();
            setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent) {
                    if (ClearEditText.this.getCompoundDrawables()[2] == null) {
                    }
                    while ((paramAnonymousMotionEvent.getAction() != 1) || (paramAnonymousMotionEvent.getX() <= paramAnonymousView.getWidth() - paramAnonymousView.getPaddingRight() - ClearEditText.this.imgClearButton.getIntrinsicWidth())) {
                        return false;
                    }
                    ClearEditText.this.setText("");
                    ClearEditText.this.handleClearButton();
                    return false;
                }
            });
            addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable paramAnonymousEditable) {
                }

                public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {
                }

                public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {
                    ClearEditText.this.handleClearButton();
                }
            });
            return;
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }
}