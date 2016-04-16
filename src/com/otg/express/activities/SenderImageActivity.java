package com.otg.express.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.otg.express.R;
import com.otg.express.domain.ExpressLog;
import com.otg.express.utils.ImageThumbnail;
import com.otg.express.utils.Toaster;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class SenderImageActivity extends Activity {
    private ImageView img;
    private ImageButton imageButton;
    private ImageButton imageButtonReset;
    private ExpressLog expressLog;
    private Context context;
    public static final String jpgfile = "sender.jpg";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_ocr);
        this.context = this;
        //得到跳转到该Activity的Intent对象
        Intent intent = getIntent();
        expressLog = (ExpressLog) intent.getSerializableExtra("expressLog");
        if (expressLog == null) {
            Toaster.show(context, "请重新读取身份信息！");
            return;
        }

        img = (ImageView) findViewById(R.id.img);
        imageButton = (ImageButton) findViewById(R.id.next);
        imageButtonReset = (ImageButton) findViewById(R.id.reset);
        imageButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(it, 1);*/
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = new File("/sdcard/Image/");
                file.mkdirs();// 创建文件夹
                String fileName = "/sdcard/Image/" + jpgfile;
                File f = new File(fileName);
                if (f.exists()) {
                    f.delete();
                }
                Uri imageUri = Uri.fromFile(f);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(cameraIntent, 1);
            }
        });

        imageButton = (ImageButton) findViewById(R.id.next);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (expressLog != null /*&& expressLog.getSender_img() != null*/) {
                    Intent i = new Intent(context, UnpackImageActivity.class);
                    i.putExtra("expressLog", expressLog);
                    startActivity(i);
                    finish();
                } else {
                    new AlertDialog.Builder(context).setTitle("提示")
                            .setMessage("请重拍寄件人信息！")
                            .setNegativeButton("返回", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.i("alert dialog", " 请重拍寄件人信息！");
                                }
                            }).show();
                }
            }
        });

       /* Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(it, 1);*/
    }

    /**
     * 拍照上传
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 1:
                   /* Bundle extras = data.getExtras();
                    Bitmap b = (Bitmap) extras.get("data");
                    img.setImageBitmap(b);

                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    b.compress(Bitmap.CompressFormat.JPEG, 30, out);//30 是压缩率，表示压缩70%; 如果不压缩是100，表示压
                    try {
                        out.flush();
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
                    String fileName = "/sdcard/Image/" + jpgfile;
                    File f = new File(fileName);
                    if (f.exists()) {
                        Bitmap camorabitmap = BitmapFactory.decodeFile(fileName);
                        if (null != camorabitmap) {
                            // 下面这两句是对图片按照一定的比例缩放，这样就可以完美地显示出来。
                            int scale = ImageThumbnail.reckonThumbnail(camorabitmap.getWidth(), camorabitmap.getHeight(), 500, 600);
                            Bitmap bitMap = ImageThumbnail.PicZoom(camorabitmap, camorabitmap.getWidth() / scale, camorabitmap.getHeight() / scale);
                            img.setImageBitmap(bitMap);
                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            bitMap.compress(Bitmap.CompressFormat.JPEG, 30, out);//30 是压缩率，表示压缩70%; 如果不压缩是100，表示压
                            try {
                                out.flush();
                                out.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            byte[] bytes = out.toByteArray();
                            if (expressLog != null) {
                                expressLog.setSender_img(bytes);
                            } else {
                                expressLog = new ExpressLog();
                                expressLog.setSender_img(bytes);
                            }
                            // 由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
                            camorabitmap.recycle();
                            // 将处理过的图片显示在界面上，并保存到本地
                        }
                    }
                    break;
                default:
                    break;
            }
            ;
        }
    }
}
