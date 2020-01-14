package com.example.wifi_info;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.example.wifi_info.svghelper.IPMapView;
import com.example.wifi_info.svghelper.MeasurePoint;
import com.example.wifi_info.svghelper.IPMapView.ClickPointListener;
import com.example.wifi_info.tools.ApSet;
import com.example.wifi_info.tools.Infor;
import com.example.wifi_info.tools.Point;
import com.example.wifi_info.tools.Position;
import com.example.wifi_info.algorithm.Gaussian;
import com.example.wifi_info.algorithm.WKNN;
import com.example.wifi_info.database.WifiDao;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class PositionActivity extends Activity implements ClickPointListener {

	// ��ͼ�ļ���
	private static final String MAP_NAME = "1.svg";
	private static final int Thread_SlEEP_TIME = 1000/3;
	private static final int NUM_N = 30;// ȡƽ������

	private static HashSet<String> hs = (HashSet<String>) ApSet.getAps();

	private TextView tv_test;

	private WifiManager wifiManager;
	private WifiDao wifidao;

	private IPMapView mapView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_position);

		tv_test = (TextView) findViewById(R.id.tv_test);
		wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		wifidao = new WifiDao(this);

		// ����SVG��ͼ
		mapView = (IPMapView) findViewById(R.id.map_view);
		mapView.setMeasureMode(true);
		mapView.setClickPointListener(this);

		byte[] svgBytes = loadAssetsFile(this, MAP_NAME);

		ByteArrayInputStream bin = new ByteArrayInputStream(svgBytes);
		mapView.newMap(bin);

	}

	/**
	 * ��λ
	 */
	public void position(View view) {

		Map<String, double[]> hm = myThread("");// ��¼ʵʱwifi��Ϣ��

		List<Infor> infors = gauss(hm);// ��˹����

		Collections.sort(infors);// ��Levelֵ��AP����

		int[] apIndex = apChoose(infors);// ȷ��ѡ��AP������

		tv_test.setText(infors.toString());

		StringBuffer sql = linkedSql(infors, apIndex);// ƴ��sql

		List<Point> points = wifidao.returnTempTable(sql.toString());// ѵ������datas

		System.out.print("points" + points);

		Position position = new WKNN().method(points, infors);

		System.out.println("��λ�����" + position);
		Toast.makeText(this,
				"��λ�����X=" + position.getPosX() + " Y=" + position.getPosY(),
				Toast.LENGTH_LONG).show();

		display(position);// ��ʾ������

	}

	private List<Infor> gauss(Map<String, double[]> hm) {
		// TODO Auto-generated method stub

		List<Infor> infor = new ArrayList<Infor>();

		for (Map.Entry<String, double[]> entry : hm.entrySet()) {
			infor.add(new Infor(entry.getKey(),
					Gaussian.gauss(entry.getValue())));
		}

		return infor;

	}

	/**
	 * ��ʾ������
	 * 
	 * @param position
	 */

	private void display(Position position) {
		// TODO Auto-generated method stub

		DecimalFormat df = new DecimalFormat("######0.00");

		position.setPosX(Double.parseDouble(df.format(position.getPosX())));
		position.setPosY(Double.parseDouble(df.format(position.getPosY())));

		position.setPosX(position.getPosX() * 37.63 + 7);
		position.setPosY(2032 - position.getPosY() * 37.63);

		MeasurePoint point = new MeasurePoint();

		System.out.println(position);

		point.setPosx(position.getPosX());
		point.setPosy(position.getPosY());

		mapView.addOldPoint(point);
	}

	/**
	 * ƴ����ѡap��bssid��ʹ��\t\t��Ϊ�ָ���
	 * 
	 * @param infors
	 * @param apIndex
	 * @return
	 */
	private StringBuffer linkedSql(List<Infor> infors, int[] apIndex) {
		// TODO Auto-generated method stub
		StringBuffer sql = new StringBuffer();
		for (int i = 0; i < apIndex.length; i++) {
			sql.append(infors.get(apIndex[i]).getBssid());
			sql.append("\t\t");
		}
		sql.delete(sql.length() - 2, sql.length());

		// System.out.println("ѡ���AP:" + sql.toString());
		return sql;
	}

	/**
	 * �Ѽ�ʵʱAP��Ϣ��ȡ��ֵ�� �߳�δʵ��
	 * 
	 * @param str
	 */
	private Map<String, double[]> myThread(String str) {

		int i = 0;

		Map<String, double[]> hm = new HashMap<String, double[]>();

		// ѭ������ɨ�����
		while (i < NUM_N) {

			try {
				Thread.sleep(Thread_SlEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (!wifiManager.startScan())
				continue;

			i++;

			List<ScanResult> results = wifiManager.getScanResults();

			for (ScanResult scanResult : results) {

				if (myAps(scanResult.SSID)) {

					System.out.println(scanResult.SSID + "   "
							+ scanResult.level);

					if (!hm.containsKey(scanResult.BSSID)) {
						hm.put(scanResult.BSSID,
								new double[] { scanResult.level * 1.0 });
					} else {

						double[] arr = hm.get(scanResult.BSSID);

						double[] newArr = new double[arr.length + 1];

						for (int j = 0; j < arr.length; j++)
							newArr[j] = arr[j];

						newArr[newArr.length - 1] = scanResult.level;

						hm.put(scanResult.BSSID, newArr);
					}
				}
			}
		}
		return hm;

	}

	/**
	 * ȷ��ѡ�õ�apλ��
	 * 
	 * @param myResults
	 * @return
	 */
	private int[] apChoose(List<Infor> infors) {
		// TODO Auto-generated method stub

		int size = infors.size();

		int[] res = new int[size];

		for (int i = 0; i < size; i++) {
			res[i] = i;
		}

		return res;
	}

	/**
	 * 
	 */
	@Override
	public void onPointClick(double x, double y) {

		DecimalFormat df = new DecimalFormat("######0.00");

		// ��������
		x = (x - 7) / 37.63;
		y = (2032 - y) / 37.63;

		x = Double.parseDouble(df.format(x));
		y = Double.parseDouble(df.format(y));

		Toast.makeText(this, "ʵ������(" + x + ", " + y + ")",
				Toast.LENGTH_LONG).show();

		Log.e("", " ��� X��" + x + "  Y:" + y);
	}

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

	/***
	 * �ж�AP�Ƿ����嵥�� HashSet<String> hs = (HashSet<String>) ApSet.getAps();
	 */
	private boolean myAps(String str) {
		
		
		return true;
/*
		if (hs.contains(str))
			return true;

		return false;*/

	}
}
