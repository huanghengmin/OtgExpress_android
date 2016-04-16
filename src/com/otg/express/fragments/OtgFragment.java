package com.otg.express.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.otg.express.R;
import com.otg.express.activities.SenderImageActivity;
import com.otg.express.device.AndroidDriver;
import com.otg.express.domain.IdentityCard;
import com.otg.express.domain.ExpressLog;
import com.otg.express.handler.InitHandler;
import com.otg.express.handler.ReadHandler;

public class OtgFragment extends Fragment
{
	/**
	 * otg
	 */
	private TextView name;
	private TextView nametext;
	private TextView sex;
	private TextView sextext;
	private TextView mingzu;
	private TextView mingzutext;
	private TextView birthday;
	private TextView birthdaytext;
	private TextView address;
	private TextView addresstext;
	private TextView number;
	private TextView numbertext;
	private TextView qianfa;
	private TextView qianfatext;
	private TextView start;
	private TextView starttext;
//	private TextView dncodetext;
//	private static Button onredo;
	private static ImageButton readIdCardBt;
//	private TextView dncode;
	private TextView Readingtext;
	private ImageView idimg;
	private ImageButton imageButton;
	private ExpressLog expressLog;
	public static final int NFC_NET_NETCONNECT_ERROR = 1; // 网络连接错误
	public static final int NFC_NET_NETRECV_ERROR = 2;// 网络接收错误
	public static final int NFC_NET_NETSEND_ERROR = 3;// 网络发送错误
	public static final int NFC_NET_NFCOPEN_ERROR = 4;// NFC打开错误
	public static final int NFC_NET_NFCREADCARD_ERROR = 5; // NFC读卡错误
	public static final int NFC_NET_AUTHENTICATION_FAIL = 6;// 认证失败
	public static final int NFC_NET_SEARCHCARD_FAIL = 7;// NFC寻卡失败
	public static final int NFC_NET_STARTNETREADCARD_FAIL = 8; // 启动网络读卡失败
	public static final int NFC_NET_INVALIDNETCMD = 9;// 无效的网络命令
	public static final int NFC_NET_UNKNOWN_ERROR = 10;// 未知错误
	public static final int READ_IDCARD_SUCCESS = 11; //读取身份信息成功
	public static final int READ_PHOTO_SUCCESS = 12; //读取照片成功
	public static final int PLASE_INIT_SERVER = 13; //请先初始化服务器
	public static final int NOT_FOUND_DEVICE = 14; //未找到读卡设备
	public static final int READ_PHOTO_ERROR = 16; //读取照片出错


	private static final String ACTION_USB_PERMISSION = "com.lz.nfc.USB_PERMISSION";
	private AndroidDriver androidDriver;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Context context = this.getActivity();
		UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
		androidDriver = new AndroidDriver(usbManager, context, ACTION_USB_PERMISSION);

		InitHandler initHandler = new InitHandler(context);
		initHandler.handler();


	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.tab_otg, container,false);

		/**
		 * otg
		 */
		/*onredo = (Button) v.findViewById(R.id.scale);
		onredo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onredo.setEnabled(false);
				onredo.setFocusable(false);
				readIdCardBt.setEnabled(false);
				readIdCardBt.setFocusable(false);
				expressLog = null;
				nametext.setText("");
				sextext.setText("");
				mingzutext.setText("");
				birthdaytext.setText("");
				addresstext.setText("");
				numbertext.setText("");
				qianfatext.setText("");
				starttext.setText("");
				dncodetext.setText("");
				idimg.setImageBitmap(null);
				Readingtext.setText("      正在读卡，请稍候...");
				Readingtext.setVisibility(View.VISIBLE);
				int cfd = androidDriver.GetFD();
				ReadHandler threadHandler = new ReadHandler(mHandler, getActivity(), cfd);
				threadHandler.postData();
			}
		});*/


		readIdCardBt = (ImageButton) v.findViewById(R.id.readIdCard);
		readIdCardBt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				readIdCardBt.setEnabled(false);
				readIdCardBt.setFocusable(false);
//				onredo.setEnabled(false);
//				onredo.setFocusable(false);
				expressLog = null;
				nametext.setText("");
				sextext.setText("");
				mingzutext.setText("");
				birthdaytext.setText("");
				addresstext.setText("");
				numbertext.setText("");
				qianfatext.setText("");
				starttext.setText("");
//				dncodetext.setText("");
				idimg.setImageBitmap(null);
				Readingtext.setText("      正在读卡，请稍候...");
				Readingtext.setVisibility(View.VISIBLE);
				int cfd = androidDriver.GetFD();
				ReadHandler threadHandler = new ReadHandler(mHandler, getActivity(), cfd);
				threadHandler.postData();
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
//					getActivity().finish();
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

		name = (TextView) v.findViewById(R.id.name);
		sex = (TextView) v.findViewById(R.id.sex);
		nametext = (TextView) v.findViewById(R.id.nametext);
		sextext = (TextView) v.findViewById(R.id.sextext);
		mingzu = (TextView) v.findViewById(R.id.mingzu);
		mingzutext = (TextView) v.findViewById(R.id.mingzutext);
		birthday = (TextView) v.findViewById(R.id.birthday);
		birthdaytext = (TextView) v.findViewById(R.id.birthdaytext);
		address = (TextView) v.findViewById(R.id.address);
		addresstext = (TextView) v.findViewById(R.id.addresstext);
		number = (TextView) v.findViewById(R.id.number);
		numbertext = (TextView) v.findViewById(R.id.numbertext);
		qianfa = (TextView) v.findViewById(R.id.qianfa);
		qianfatext = (TextView) v.findViewById(R.id.qianfatext);
		start = (TextView) v.findViewById(R.id.start);
		starttext = (TextView) v.findViewById(R.id.starttext);
		Readingtext = (TextView) v.findViewById(R.id.Readingtext);
//		dncodetext = (TextView) v.findViewById(R.id.dncodetext);
//		dncode = (TextView) v.findViewById(R.id.dncode);

		name.setText("姓名：");
		sex.setText("性别：");
		mingzu.setText("民族：");
		birthday.setText("出生年月：");
		address.setText("地址：");
		number.setText("身份证号码：");
		qianfa.setText("签发机关：");
		start.setText("有效时间：");
//		dncode.setText("DN码：");
		idimg = (ImageView) v.findViewById(R.id.idimg);
		Readingtext.setVisibility(View.GONE);
		Readingtext.setText("      正在读卡，请稍候...");
		Readingtext.setTextColor(Color.RED);

		return v;
	}

	@Override
	public void onDestroy() {
		if (androidDriver != null) {
			if (androidDriver.isConnected()) {
				androidDriver.CloseDevice();
			}
			androidDriver = null;
		}

		super.onDestroy();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if (!hidden) {// 不在最前端界面显示
			getActivity().setTitle("寄件人证件读卡");
		}
	}

	@Override
	public void onResume() {
		if (2 == androidDriver.ResumeUsbList()) {
			androidDriver.CloseDevice();
		}
		super.onResume();
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case READ_IDCARD_SUCCESS:
					expressLog = new ExpressLog();
					Readingtext.setText("      读卡成功");
					Readingtext.setVisibility(View.GONE);
//					onredo.setEnabled(true);
//					onredo.setFocusable(true);
					readIdCardBt.setEnabled(true);
					readIdCardBt.setFocusable(true);
//					onredo.setBackgroundResource(R.drawable.sfz_dq);
					IdentityCard identityCard = (IdentityCard) msg.obj;
					nametext.setText(identityCard.getName());
					expressLog.setName(identityCard.getName());
					sextext.setText(identityCard.getSex());
					expressLog.setSex(identityCard.getSex());
					mingzutext.setText(identityCard.getMz());
					expressLog.setNation(identityCard.getMz());
					birthdaytext.setText(identityCard.getBirth());
					expressLog.setBirthday(identityCard.getBirth());
					addresstext.setText(identityCard.getAddress());
					expressLog.setAddress(identityCard.getAddress());
					numbertext.setText(identityCard.getIdCard());
					expressLog.setIdCard(identityCard.getIdCard());
					qianfatext.setText(identityCard.getSign());
					expressLog.setSignDepart(identityCard.getSign());
					starttext.setText(identityCard.getValidity());
					expressLog.setValidTime(identityCard.getValidity());
//					dncodetext.setText(identityCard.getDN());
					expressLog.setDN(identityCard.getDN());

					expressLog.setType("OTG");


//					onredo.setEnabled(true);
//					onredo.setFocusable(true);
					readIdCardBt.setEnabled(true);
					readIdCardBt.setFocusable(true);
//					onredo.setBackgroundResource(R.drawable.sfz_dq);
					Readingtext.setVisibility(View.GONE);
					break;
				case READ_PHOTO_SUCCESS:
					Readingtext.setText("      读照片成功");
					Readingtext.setVisibility(View.GONE);
					byte[] cardbmp = (byte[]) msg.obj;
					Bitmap bm = BitmapFactory.decodeByteArray(cardbmp, 0, cardbmp.length);
					idimg.setImageBitmap(bm);
					if (expressLog == null)
						expressLog = new ExpressLog();
					expressLog.setBitmap(cardbmp);
					expressLog.setType("OTG");
					break;
				case NFC_NET_NETCONNECT_ERROR:
					Readingtext.setText("      网络连接错误！");
//					onredo.setEnabled(true);
//					onredo.setFocusable(true);
					readIdCardBt.setEnabled(true);
					readIdCardBt.setFocusable(true);
//					onredo.setBackgroundResource(R.drawable.sfz_dq);
					break;
				case NFC_NET_NETRECV_ERROR:
					Readingtext.setText("      网络接收错误！");
//					onredo.setEnabled(true);
//					onredo.setFocusable(true);
					readIdCardBt.setEnabled(true);
					readIdCardBt.setFocusable(true);
//					onredo.setBackgroundResource(R.drawable.sfz_dq);
					break;
				case NFC_NET_NETSEND_ERROR:
					Readingtext.setText("      网络发送错误！");
//					onredo.setEnabled(true);
//					onredo.setFocusable(true);
					readIdCardBt.setEnabled(true);
					readIdCardBt.setFocusable(true);
//					onredo.setBackgroundResource(R.drawable.sfz_dq);
					break;
				case NFC_NET_NFCOPEN_ERROR:
					Readingtext.setText("      NFC打开错误！");
//					onredo.setEnabled(true);
//					onredo.setFocusable(true);
					readIdCardBt.setEnabled(true);
					readIdCardBt.setFocusable(true);
//					onredo.setBackgroundResource(R.drawable.sfz_dq);
					break;
				case NFC_NET_NFCREADCARD_ERROR:
					Readingtext.setText("      NFC读卡错误！");
//					onredo.setEnabled(true);
//					onredo.setFocusable(true);
					readIdCardBt.setEnabled(true);
					readIdCardBt.setFocusable(true);
//					onredo.setBackgroundResource(R.drawable.sfz_dq);
					break;
				case NFC_NET_AUTHENTICATION_FAIL:
					Readingtext.setText("      认证失败！");
//					onredo.setEnabled(true);
//					onredo.setFocusable(true);
					readIdCardBt.setEnabled(true);
					readIdCardBt.setFocusable(true);
//					onredo.setBackgroundResource(R.drawable.sfz_dq);
					break;
				case NFC_NET_SEARCHCARD_FAIL:
					Readingtext.setText("      NFC寻卡失败！");
//					onredo.setEnabled(true);
//					onredo.setFocusable(true);
					readIdCardBt.setEnabled(true);
					readIdCardBt.setFocusable(true);
//					onredo.setBackgroundResource(R.drawable.sfz_dq);
					break;
				case NFC_NET_STARTNETREADCARD_FAIL:
					Readingtext.setText("      启动网络读卡失败！");
//					onredo.setEnabled(true);
//					onredo.setFocusable(true);
					readIdCardBt.setEnabled(true);
					readIdCardBt.setFocusable(true);
//					onredo.setBackgroundResource(R.drawable.sfz_dq);
					break;
				case NFC_NET_INVALIDNETCMD:
					Readingtext.setText("      无效的网络命令！");
//					onredo.setEnabled(true);
//					onredo.setFocusable(true);
					readIdCardBt.setEnabled(true);
					readIdCardBt.setFocusable(true);
//					onredo.setBackgroundResource(R.drawable.sfz_dq);
					break;
				case NFC_NET_UNKNOWN_ERROR:
					Readingtext.setText("      未知错误！");
//					onredo.setEnabled(true);
//					onredo.setFocusable(true);
					readIdCardBt.setEnabled(true);
					readIdCardBt.setFocusable(true);
//					onredo.setBackgroundResource(R.drawable.sfz_dq);
					break;
				case PLASE_INIT_SERVER:
					Readingtext.setText("      请初始化服务器配置！");
//					onredo.setEnabled(true);
//					onredo.setFocusable(true);
					readIdCardBt.setEnabled(true);
					readIdCardBt.setFocusable(true);
//					onredo.setBackgroundResource(R.drawable.sfz_dq);
					break;
				case NOT_FOUND_DEVICE:
					Readingtext.setText("      未找到读卡设备！");
//					onredo.setEnabled(true);
//					onredo.setFocusable(true);
					readIdCardBt.setEnabled(true);
					readIdCardBt.setFocusable(true);
//					onredo.setBackgroundResource(R.drawable.sfz_dq);
					if (2 == androidDriver.ResumeUsbList()) {
						androidDriver.CloseDevice();
					}
					break;
				case READ_PHOTO_ERROR:
//					String m = (String) msg.obj;
//					Toast.makeText(getActivity(), m, Toast.LENGTH_SHORT).show();
					break;
			}
		}
	};

}
