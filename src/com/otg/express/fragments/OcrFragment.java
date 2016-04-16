package com.otg.express.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.otg.express.R;
import com.otg.express.activities.SenderImageActivity;
import com.otg.express.domain.ExpressLog;
import com.otg.express.utils.ImageThumbnail;

import java.io.*;

public class OcrFragment extends Fragment {
    private ImageView img;
    private ImageButton imageButton;
    private ImageButton imageButtonReset;
    private ExpressLog expressLog;
    private static final String jpgfile = "ocr.jpg";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(it, 1);*/
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        // TODO Auto-generated method stub
        super.onHiddenChanged(hidden);
        if (!hidden) {// 不在最前端界面显示
            getActivity().setTitle("寄件人证件拍照");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.tab_ocr, container, false);
        img = (ImageView) v.findViewById(R.id.img);
        imageButton = (ImageButton) v.findViewById(R.id.next);
        imageButtonReset = (ImageButton) v.findViewById(R.id.reset);
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

        imageButton = (ImageButton) v.findViewById(R.id.next);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (expressLog != null) {
                    Intent i = new Intent(getActivity(), SenderImageActivity.class);
                    i.putExtra("expressLog", expressLog);
                    startActivity(i);
//                    getActivity().finish();
                } else {
                    new AlertDialog.Builder(getActivity()).setTitle("提示")//设置对话框标题
                            .setMessage("请重新读取身份信息！")//设置显示的内容
                            .setNegativeButton("返回", new DialogInterface.OnClickListener() {//添加返回按钮
                                @Override
                                public void onClick(DialogInterface dialog, int which) {//响应事件
                                    Log.i("alert dialog", " 请重新读取身份信息！");
                                }
                            }).show();//在按键响应事件中显示此对话框
                }
            }
        });
        return v;
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
                    /*Bundle extras = data.getExtras();
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

                    // 将保存在本地的图片取出并缩小后显示在界面上
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
                                expressLog.setType("OCR");
                                expressLog.setBitmap(bytes);
                            } else {
                                expressLog = new ExpressLog();
                                expressLog.setType("OCR");
                                expressLog.setBitmap(bytes);
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
