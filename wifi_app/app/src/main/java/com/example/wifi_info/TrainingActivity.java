package com.example.wifi_info;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;
import android.net.Uri;
import android.media.RingtoneManager;
import android.media.Ringtone;

import com.example.wifi_info.database.WifiDao;
import com.example.wifi_info.svghelper.IPMapView;
import com.example.wifi_info.svghelper.IPMapView.ClickPointListener;
import com.example.wifi_info.tools.ApSet;

public class TrainingActivity extends Activity implements ClickPointListener {

	private static String MAP_NAME = null;// 地图文件
	private static final int Thread_SlEEP_TIME = 1000;

	private static final int SCANF_COUNT = 30;// 存储次数

	private static HashSet<String> hs = (HashSet<String>) ApSet.getAps();

	private TextView tv_wifiinfo;
	private CheckBox cb_saveData;

	private IPMapView mapView;
	private WifiManager wifiManager;
	private WifiDao wifidao;

	private double position_X = 0.0;
	private double position_Y = 0.0;
	private double position_Z = 0.0;

	private StringBuffer stringBuffer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_training);

		tv_wifiinfo = (TextView) findViewById(R.id.tv_wifiinfo);
		cb_saveData = (CheckBox) findViewById(R.id.cb_saveData);

		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		wifidao = new WifiDao(this);

		Intent intent = getIntent();
		String value = intent.getStringExtra("floor");
		
		MAP_NAME = value+".svg";
		
		position_Z = Double.parseDouble(value);

		// 加载SVG地图
		mapView = (IPMapView) findViewById(R.id.map_view);
		mapView.setMeasureMode(true);
		mapView.setClickPointListener(this);

		byte[] svgBytes = loadAssetsFile(this, MAP_NAME);

		ByteArrayInputStream bin = new ByteArrayInputStream(svgBytes);
		mapView.newMap(bin);

	}

	@Override
	protected void onStart() {

		super.onStart();

		// 扫描wifi
		new Thread(new Runnable() {

			@SuppressLint("SimpleDateFormat")
			public void run() {

				while (true) {

					try {
						Thread.sleep(Thread_SlEEP_TIME);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					String date = "0000-00-00 00:00:00:0000";
					// 扫描时间
					if (wifiManager.startScan()) {
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:ms");
						date = sdf.format(new java.util.Date());
					}

					List<ScanResult> results = wifiManager.getScanResults();
					stringBuffer = new StringBuffer();
					// Collections.sort(results, new APLevelComparator());

					for (ScanResult scanResult : results) {

						Boolean bool = myAps(scanResult.SSID);

						if (bool) {
							stringBuffer.append("(" + position_X + "," + position_Y + ","+position_Z + ")"
									+ scanResult.SSID + ".." + scanResult.level + "\n");

							// 存储数据
							if (cb_saveData.isChecked()) {
								//播放音乐
								Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
								Ringtone rt = RingtoneManager.getRingtone(getApplicationContext(), uri);
								rt.play();
								/*Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
								Ringtone rt = RingtoneManager.getRingtone(getApplicationContext(), uri);
								rt.play();*/

								wifidao.add(scanResult.SSID, scanResult.BSSID, scanResult.level, position_X, position_Y,
										position_Z, scanResult.frequency, date);
							}

						}

					}
					// 显示文本
					runOnUiThread(new Runnable() {
						public void run() {
							tv_wifiinfo.setText(stringBuffer);
						}

					});
				}
			}
		}).start();

	}

	/***
	 * 判断AP是否在清单中 HashSet<String> hs = (HashSet<String>) ApSet.getAps();
	 */
	private boolean myAps(String str) {
		
		if (hs.contains(str))
			return true;

		return false;

	}

	/**
	 * 屏幕坐标
	 */
	@Override
	public void onPointClick(double x, double y) {

		DecimalFormat df = new DecimalFormat("######0.00");

		// 调整比例
		x = (x - 7) / 37.63;
		y = (2032 - y) / 37.63;

		position_X = Double.parseDouble(df.format(x));
		position_Y = Double.parseDouble(df.format(y));

		// Log.e("", " 点击 X：" + position_X + " Y:" + position_Y);
	}

	/**
	 * 获取Assets文件字节流
	 * 
	 */
	public static byte[] loadAssetsFile(Context context, String fileName) {

		byte[] bytes = null;
		InputStream stream;
		try {
			stream = context.getAssets().open(fileName);
			bytes = new byte[stream.available()];
			stream.read(bytes, 0, bytes.length);
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bytes;
	}

}
