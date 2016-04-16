package com.otg.express.device;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.*;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class AndroidDriver {
	public static final String TAG = "AndroidDriver";
	private UsbManager mUsbmanager;
	private PendingIntent mPendingIntent;
	private UsbDevice mUsbDevice;
	private UsbInterface mInterface;
	private UsbDeviceConnection mDeviceConnection;
	private Context mContext;
	private String mString;
	private ArrayList<String> DeviceNum = new ArrayList();
	private int DeviceCount;
	private boolean BroadcastFlag = false;
	private UsbEndpoint mCtrlPoint;
	private UsbEndpoint mBulkInPoint;
	private UsbEndpoint mBulkOutPoint;

	public AndroidDriver(UsbManager manager, Context context, String AppName) {
		super();
		mUsbmanager = manager;
		mContext = context;
		mString = AppName;

		ArrayAddDevice("1a86:7523");
		ArrayAddDevice("1a86:5523");
	}
	
	private void ArrayAddDevice(String str)
	{
		DeviceNum.add(str);
		DeviceCount = DeviceNum.size();
	}

	public synchronized void OpenUsbDevice(UsbDevice mDevice)
	{
	
		Object localObject;
		UsbInterface intf;
		if(mDevice == null)
			return;
		intf = getUsbInterface(mDevice);
		if((mDevice != null) && (intf != null)) {
			localObject = this.mUsbmanager.openDevice(mDevice);
			if(localObject != null) {
				if(((UsbDeviceConnection)localObject).claimInterface(intf, true)) {
					this.mUsbDevice = mDevice;
					this.mDeviceConnection = ((UsbDeviceConnection)localObject);
					this.mInterface = intf;
					if(!enumerateEndPoint(intf))
						return;
					Toast.makeText(mContext, "设备已连接Android机器", Toast.LENGTH_LONG).show();
					return;
				}
			}
		}
		
	}


	private boolean enumerateEndPoint(UsbInterface sInterface)
	{
		if(sInterface == null)
			return false;
		for(int i = 0; i < sInterface.getEndpointCount(); ++i) {
			UsbEndpoint endPoint = sInterface.getEndpoint(i);
			if(endPoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK && endPoint.getMaxPacketSize() == 0x20) {
				if(endPoint.getDirection() == UsbConstants.USB_DIR_IN) {
					mBulkInPoint = endPoint;
				} else {
					mBulkOutPoint = endPoint;
				}
			} else if(endPoint.getType() == UsbConstants.USB_ENDPOINT_XFER_CONTROL) {
				mCtrlPoint = endPoint;
			}
		}
		return true;
	}

	public int GetFD(){
		if(isConnected())
			return mDeviceConnection.getFileDescriptor();
		else
			return -2;
	}
	
	public synchronized void OpenDevice(UsbDevice mDevice)
	{
		mPendingIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(mString), 0);
		if(mUsbmanager.hasPermission(mDevice)) {
			OpenUsbDevice(mDevice);
		} else {
			synchronized(mUsbReceiver) {
				mUsbmanager.requestPermission(mDevice, mPendingIntent);
			}
		}
	}
	
	public synchronized void CloseDevice()
	{
		try{
			Thread.sleep(10);}
		catch(Exception e){}
		
		if(this.mDeviceConnection != null)
		{
			if(this.mInterface != null) {
				this.mDeviceConnection.releaseInterface(this.mInterface);
				this.mInterface = null;
			}
			
			this.mDeviceConnection.close();
		}
		
		if(this.mUsbDevice != null) {
			this.mUsbDevice = null;
		}
		
		if(this.mUsbmanager != null) {
			this.mUsbmanager = null;
		}
		
		/*
		 * No need unregisterReceiver
		 */
		if(BroadcastFlag == true) {
			this.mContext.unregisterReceiver(mUsbReceiver);
			BroadcastFlag = false;
		}

//		System.exit(0);
	}
	
	public boolean UsbFeatureSupported()
	{
		boolean bool = this.mContext.getPackageManager().hasSystemFeature("android.hardware.usb.host");
		return bool;
	}
	
	public int ResumeUsbList(){
		mUsbmanager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
		mPendingIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(mString), 0);
		HashMap<String, UsbDevice> deviceList = mUsbmanager.getDeviceList();
//		Log.i("Usb Size", "size=" + deviceList.size());
		if(deviceList.isEmpty()) {
			Toast.makeText(mContext, "没有设备或设备不匹配", Toast.LENGTH_LONG).show();
			return 2;
		}
		Iterator<UsbDevice> localIterator = deviceList.values().iterator();
		while(localIterator.hasNext()) {
			UsbDevice localUsbDevice = localIterator.next();
			for(int i = 0; i < DeviceCount; ++i) {
//				 Log.d(TAG, "DeviceCount is " + DeviceCount);
				if(String.format("%04x:%04x", new Object[]{Integer.valueOf(localUsbDevice.getVendorId()),
						Integer.valueOf(localUsbDevice.getProductId())}).equals(DeviceNum.get(i))) {
					IntentFilter filter = new IntentFilter(mString);
					filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
					mContext.registerReceiver(mUsbReceiver, filter);
					BroadcastFlag = true;
					if(mUsbmanager.hasPermission(localUsbDevice)) {
						OpenUsbDevice(localUsbDevice);
					} else {
						synchronized(mUsbReceiver) {
							mUsbmanager.requestPermission(localUsbDevice, mPendingIntent);
						}
					}
				} else {
					Log.d(TAG, "String.format not match");
				}
			}
		}
		return 0;
	}

	public UsbDevice EnumerateDevice(){
		mUsbmanager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
		mPendingIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(mString), 0);
		HashMap<String, UsbDevice> deviceList = mUsbmanager.getDeviceList();
		if(deviceList.isEmpty()) {
			Toast.makeText(mContext, "没有设备或设备不匹配", Toast.LENGTH_LONG).show();
			return null;
		}
		Iterator<UsbDevice> localIterator = deviceList.values().iterator();
		while(localIterator.hasNext()) {
			UsbDevice localUsbDevice = localIterator.next();
			for(int i = 0; i < DeviceCount; ++i) {
//				 Log.d(TAG, "DeviceCount is " + DeviceCount);
				if(String.format("%04x:%04x", new Object[]{Integer.valueOf(localUsbDevice.getVendorId()),
						Integer.valueOf(localUsbDevice.getProductId())}).equals(DeviceNum.get(i))) {
					IntentFilter filter = new IntentFilter(mString);
					filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
					mContext.registerReceiver(mUsbReceiver, filter);
					BroadcastFlag = true;
					return localUsbDevice;
				} else {
					Log.d(TAG, "String.format not match");
				}
			}
		}
		return null;
	}
	
	public boolean isConnected(){
		return (this.mUsbDevice != null) && (this.mInterface != null) && (this.mDeviceConnection != null);
	}
	
	protected UsbDevice getUsbDevice()
	{
		return this.mUsbDevice;
	}
	
	private UsbInterface getUsbInterface(UsbDevice paramUsbDevice){
		if(this.mDeviceConnection != null) {
			if(this.mInterface != null) {
				this.mDeviceConnection.releaseInterface(this.mInterface);
				this.mInterface = null;
			}
			this.mDeviceConnection.close();
			this.mUsbDevice = null;
			this.mInterface = null;
		}
		if(paramUsbDevice == null)
			return null;
		
		for (int i = 0; i < paramUsbDevice.getInterfaceCount(); i++) {
			UsbInterface intf = paramUsbDevice.getInterface(i);
			if (intf.getInterfaceClass() == 0xff
					&& intf.getInterfaceSubclass() == 0x01
					&& intf.getInterfaceProtocol() == 0x02) {
				return intf;
			}
		}
		return null;
	}
	
	/***********USB broadcast receiver*******************************************/
	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent)
		{
			String action = intent.getAction();
			if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action))
				return;

			if(mString.equals(action))
			{
				synchronized(this) 
				{
					UsbDevice localUsbDevice = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
					if(intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false))
					{
						OpenUsbDevice(localUsbDevice);
					} else {
						Toast.makeText(AndroidDriver.this.mContext, "禁用USB权限", Toast.LENGTH_SHORT).show();
						Log.d(TAG, "permission denied");
					}
				}
			} else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
				Toast.makeText(AndroidDriver.this.mContext, "断开连接", Toast.LENGTH_SHORT).show();
				CloseDevice();
			} else {
				Log.d(TAG, "......");
			}
		}	
	};
}


