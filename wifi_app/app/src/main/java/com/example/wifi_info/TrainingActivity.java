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
/*1=====================================================================*/

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import android.Manifest;
import android.annotation.SuppressLint;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.app.Activity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.AudioRecord;
import android.media.AudioRecord.OnRecordPositionUpdateListener;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

/*import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;
import com.estimote.sdk.telemetry.EstimoteTelemetry;
import com.estimote.sdk.eddystone.Eddystone;
import com.estimote.sdk.eddystone.EddystoneTelemetry;
import com.estimote.sdk.MacAddress;
*/


/*2=====================================================================*/

public class TrainingActivity extends Activity implements SensorEventListener, ClickPointListener {

	private static final String MAP_NAME = "xiaoer.svg"; 	/* "1.svg";// 地图文件 */
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

	private StringBuffer stringBuffer;

	/*1=====================================================================*/
	
		private SensorManager mSensorManager;
		Sensor Sensor_Acc;
		OutputStreamWriter fout;
		private Context context=this;
		TextView obj_txtView0;// phone model, android version 
		TextView obj_txtView1;// ACCE: model
		TextView obj_txtView1b; // ACCE: data
		ToggleButton obj_ToggleButtonSave;
		String texto_Acc_Features;
	
		long tiempo_inicial_ns_raw=0;
		long timestamp_ns;
		double timestamp;
		long contador_Acce=0;
		double timestamp_Acce_last=0;
		double timestamp_Acce_last_update=0;
		float freq_medida_Acce=0;
		double deltaT_update=0.25;
		long contador_Posi=0;
		private Boolean flag_Trace=false;
		String phone_manufacturer;
		String phone_model;
		int phone_version;
		String phone_versionRelease;
		boolean primer_sensor_cambia=true;
		private TimerTask TaskReloj;					// Manejador Timer para pintar Reloj
		private final Handler handlerReloj = new Handler();
		private Timer timerReloj;		   // Timer
		Button obj_btnBotonMarkPosition;
		int savedb = 0;
	/*2=====================================================================*/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_training);

		tv_wifiinfo = (TextView) findViewById(R.id.tv_wifiinfo);
		//cb_saveData = (CheckBox) findViewById(R.id.cb_saveData);// dian ji bao cun

		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		wifidao = new WifiDao(this);


		// 加载SVG地图
		mapView = (IPMapView) findViewById(R.id.map_view);
		mapView.setMeasureMode(true);
		mapView.setClickPointListener(this);

		byte[] svgBytes = loadAssetsFile(this, MAP_NAME);

		ByteArrayInputStream bin = new ByteArrayInputStream(svgBytes);
		mapView.newMap(bin);

		
/*1=====================================================================*/

		if (flag_Trace) {// start tracing to "/sdcard/GetSensorData_Trace.trace"
			Debug.startMethodTracing("GetSensorData_Trace");
		}

		phone_manufacturer = android.os.Build.MANUFACTURER;
		phone_model = android.os.Build.MODEL;
		phone_version = android.os.Build.VERSION.SDK_INT;
		phone_versionRelease = android.os.Build.VERSION.RELEASE;

		Log.i("OnCreate", "Phone_manufacturer " + phone_manufacturer
				+ " \n Phone_model " + phone_model
				+ " \n Phone_version " + phone_version
				+ " \n Phone_versionRelease " + phone_versionRelease
		);

		Log.i("OnCreate", "Inicializando");
		//------------Inicializar UI---------------
		Log.i("OnCreate", "Poner manejadores botones");
		inicializar_objetos_UI();
		poner_manejador_botonSave();
		poner_manejador_boton_MarkPosition();


		obj_txtView0.setText("Phone: " + phone_manufacturer+"  "+ phone_model+"  API"+phone_version+"  Android_"+phone_versionRelease);
		//Log.i("OnCreate", "ver sensores internos disponibles");
		//System.out.println("11111111111111111");
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		Sensor_Acc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		// Mostrar datos generales del accelerometro:
		if (Sensor_Acc != null) {
			obj_txtView1.setText(" ACCE: "+Sensor_Acc.getName());
			/*texto_Acc_Features =" Manufacturer: "+Sensor_Acc.getVendor()
			+",\n Version: "+Sensor_Acc.getVersion()
			+", Type:"+Sensor_Acc.getType()
			+", \n Resolution: "+Sensor_Acc.getResolution()+" m/s^2"
			+", \n MaxRange: "+Sensor_Acc.getMaximumRange()+" m/s^2"
			+", \n Power consumption: "+Sensor_Acc.getPower()+" mA"
			+", \n MinDelay (0 means is not a streaming sensor): "+Sensor_Acc.getMinDelay();*/
		} else {
			obj_txtView1.setText(" ACCE: No Accelerometer detected");
			/*texto_Acc_Features =" No Features";*/
			obj_txtView1.setBackgroundColor(0xFFFF0000);  // red color
		}
		/*obj_txtView1a.setText(texto_Acc_Features);*/


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
							SimpleDateFormat sdf = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss:ms");
							date = sdf.format(new java.util.Date());
						}

						List<ScanResult> results = wifiManager.getScanResults();
						stringBuffer = new StringBuffer();
						// Collections.sort(results, new APLevelComparator());

						int pl_ay = 1;
						for (ScanResult scanResult : results) {

							Boolean bool = myAps(scanResult.SSID);

							if (bool) {
								stringBuffer.append("(" + position_X + ","
										+ position_Y + ")" + scanResult.SSID + ".."
										+ scanResult.level + "\n");
								// 存储数据
								if (savedb == 1) {
									if(pl_ay == 1)
									{
										//播放音乐
										Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
										Ringtone rt = RingtoneManager.getRingtone(getApplicationContext(), uri);
										rt.play();
										pl_ay = 0;
									}
									wifidao.add(scanResult.SSID, scanResult.BSSID,
											scanResult.level, position_X,
											position_Y, scanResult.frequency, date);
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


		
/*2=====================================================================*/

	}

	@Override
	protected void onStart() {

		super.onStart();
		Log.i("OnStart","Estoy en OnStart");
		
/*
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
						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss:ms");
						date = sdf.format(new java.util.Date());
					}

					List<ScanResult> results = wifiManager.getScanResults();
					stringBuffer = new StringBuffer();
					// Collections.sort(results, new APLevelComparator());

					int pl_ay = 1;
					for (ScanResult scanResult : results) {

						Boolean bool = myAps(scanResult.SSID);

						if (bool) {
							stringBuffer.append("(" + position_X + ","
									+ position_Y + ")" + scanResult.SSID + ".."
									+ scanResult.level + "\n");
							// 存储数据
							if (cb_saveData.isChecked()) {
								if(pl_ay == 1)
								{
									//播放音乐
									Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
									Ringtone rt = RingtoneManager.getRingtone(getApplicationContext(), uri);
									rt.play();
									pl_ay = 0;
								}
								wifidao.add(scanResult.SSID, scanResult.BSSID,
										scanResult.level, position_X,
										position_Y, scanResult.frequency, date);
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
*/
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
		
		/*// 调整比例
 		x = (x - 7) / 60;
	 	y = (2365 - y) / 43.45;*/

 		x = (x - 7) / 60;	//adapt new map
	 	y = (2365 - y) / 43.45;

		x = (int)x + 0.5;	//default click map center
		y = (int)y + 0.5;

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

	
	/*1=====================================================================*/
			@Override
		public final void onAccuracyChanged(Sensor sensor, int accuracy) {
			// Do something here if sensor accuracy changes.
		}
	
			
		@Override
		public final void onSensorChanged(SensorEvent event) {
			long accuracy = event.accuracy;
	
			// TimeStamp del Sensor (a poner en el log_file)
			long   SensorTimestamp_ns_raw =  event.timestamp;	   // in nano seconds
			double SensorTimestamp = ((double)(SensorTimestamp_ns_raw))*1E-9;  // de nano_s a segundos
	
			// Poner TimeStamp de la App (seg�n le llega el dato)
			long timestamp_ns_raw = System.nanoTime(); // in nano seconds
			if (timestamp_ns_raw>=tiempo_inicial_ns_raw)   // "tiempo_inicial_ns_raw" inicializado al dar al boton de grabar
			{
				timestamp_ns = timestamp_ns_raw - tiempo_inicial_ns_raw;
			} else {
				timestamp_ns = (timestamp_ns_raw - tiempo_inicial_ns_raw) + Long.MAX_VALUE;
			}
			timestamp = ((double)(timestamp_ns))*1E-9;	// de nano_s a segundos
	
			//Log.i("","timestamp (ns): "+timestamp_ns);
			//Log.i("","timestamp (s): "+timestamp);
	
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				contador_Acce++;
				//double resto=Math.IEEEremainder(contador_Acce, 10);
				if (SensorTimestamp - timestamp_Acce_last > 0) {
					freq_medida_Acce = (float) (0.9 * freq_medida_Acce + 0.1 / (SensorTimestamp - timestamp_Acce_last));
				} else {
					Log.e("ACCE SENSOR","timestamp<timestamp_Acce_last");
				}
				timestamp_Acce_last=SensorTimestamp;
	
				// Many sensors return 3 values, one for each axis.
				float[] Acc_data = event.values;
	
				// Do something with this sensor value.
				if (timestamp-timestamp_Acce_last_update>deltaT_update) // cada 0.5 segundos actualizo la pantalla
				{
					//Log.i(">>>>>>>>>>>>>>>", String.format(Locale.US,"\tAcc(X): \t%10.5f \tm/s^2\n\tAcc(Y): \t%10.5f \tm/s^2\n\tAcc(Z): \t%10.5f \tm/s^2\n\t\t\t\t\t\t\t\tFreq: %5.0f Hz",Acc_data[0],Acc_data[1],Acc_data[2],freq_medida_Acce));
					String cadena_display=String.format(Locale.US,"\tAcc(X): \t%10.5f \tm/s^2\n\tAcc(Y): \t%10.5f \tm/s^2\n\tAcc(Z): \t%10.5f \tm/s^2\n\tFreq: \t%10d \tHz",Acc_data[0],Acc_data[1],Acc_data[2],50/*,freq_medida_Acce*/);
					obj_txtView1b.setText(cadena_display);
					obj_txtView1b.setVisibility(View.VISIBLE);
					timestamp_Acce_last_update=timestamp;
				}
	
				if (obj_ToggleButtonSave.isChecked())  // Si grabando datos en log
				{
					try {
						String cadena_file=String.format(Locale.US,"\nACCE;%.3f;%.3f;%.5f;%.5f;%.5f;%d",timestamp,SensorTimestamp,Acc_data[0],Acc_data[1],Acc_data[2],accuracy);
						fout.write(cadena_file);
					} catch (IOException e) {// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
	
			} // end-if
		}
		
		private void timer_clock_handler() {
	
			// Timer handler to paint the clock
			TaskReloj = new TimerTask() {
				public void run() {
					handlerReloj.post(new Runnable() {
						public void run() {
								// ................Do something at Timer rate.....................
								// Averiguar los segundos trascurridos desde inicio de grabaci�n
								long timestamp_ns_raw = System.nanoTime(); // in nano seconds
								if (timestamp_ns_raw>=tiempo_inicial_ns_raw)   // "tiempo_inicial_ns_raw" inicializado al dar al boton de grabar
										{
											timestamp_ns = timestamp_ns_raw - tiempo_inicial_ns_raw;
										} else {
											timestamp_ns = (timestamp_ns_raw - tiempo_inicial_ns_raw) + Long.MAX_VALUE;
										}
								long segundos_trascurridos = (long)(((double)(timestamp_ns))*1E-9);  // de nano_s a segundos (segundos desde el inicio de la grabacion)
								// pintar los segundos en alg�n sitio d ela pantalla (p.ej. en el boton de grabar)
								if (obj_ToggleButtonSave.isChecked())  // Comenzar a grabar
								{
									obj_ToggleButtonSave.setText(/*"Stop Saving.\n "+*/Long.toString(segundos_trascurridos)+" s");
								}
						}
					} );
				}
			};
		}
	
		private void inicializar_objetos_UI() {
			// Instanciar objetos locales del interfaz
			obj_txtView0 = (TextView)findViewById(R.id.textView0);
			obj_txtView1 = (TextView)findViewById(R.id.textView1);
			//obj_txtView1.setBackgroundColor(0xFF00FF00);  // green color
			
			obj_txtView1b = (TextView)findViewById(R.id.textView1b);
			//obj_txtView1b.setBackgroundColor(0xFFAFAFAF);  // gray color
			obj_txtView1b.setVisibility(View.GONE);  // GONE 0x08
	
			obj_ToggleButtonSave = (ToggleButton)findViewById(R.id.togglebuttonsave);
			obj_ToggleButtonSave.setChecked(false);
			
			obj_btnBotonMarkPosition = (Button)findViewById(R.id.BtnBotonMarkPosition);
		}
	
		private void poner_manejador_botonSave() {
			if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
				finish();
			}
			//...................................................................
			// Manejador de ToggleButtonSave
			obj_ToggleButtonSave.setOnCheckedChangeListener(new ToggleButton.OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// Probar si es posible realizar almacenamiento externo 
					boolean mExternalStorageAvailable = false;
					boolean mExternalStorageWriteable = false;
					String state = Environment.getExternalStorageState();
	
					if (Environment.MEDIA_MOUNTED.equals(state)) {
						// We can read and write the media
						mExternalStorageAvailable = mExternalStorageWriteable = true;
					} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
						// We can only read the media
						mExternalStorageAvailable = true;
						mExternalStorageWriteable = false;
					} else {
						// Something else is wrong. It may be one of many other states, but all we need
						//	to know is we can neither read nor write
						mExternalStorageAvailable = mExternalStorageWriteable = false;
					}
					Log.i("OnCreate","ALMACENAMIENTO EXTERNO:"+mExternalStorageAvailable+mExternalStorageWriteable);
	
					// Intentar almacenar o cerrar
					if (obj_ToggleButtonSave.isChecked())  // Comenzar a grabar
					{
						savedb = 1;
						Log.i("OnCheckedchanged","Save button pressed !.I start recording ...");
	
						long CpuTimeStamp = System.nanoTime(); // in nano seconds
						if (primer_sensor_cambia && obj_ToggleButtonSave.isChecked()) {
							tiempo_inicial_ns_raw=CpuTimeStamp;  // en nano segundos
							Log.i("","Initial Time:"+tiempo_inicial_ns_raw+" ms");
							timestamp_Acce_last_update=0;
						}
	
						SimpleDateFormat sf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss",Locale.US);  // formato de la fecha
						Date fecha_actual = new Date();  // coger la fecha de hoy
						String str_fecha_actual = sf.format(fecha_actual);	// formatear fecha
	
						try {
							if (mExternalStorageAvailable) {
								File path = Environment.getExternalStoragePublicDirectory("LogFiles_GetSensorData");
								Log.i("OnCheckedChanged","Path where I save - ExternalStoragePublic:"+path);
								path.mkdirs();	// asegurarse que el directorio "./../LogFiles_GetSensorData" existe
								File fichero = new File(path.getAbsolutePath(), "logfile_"+str_fecha_actual+".txt");
								fout =	new OutputStreamWriter( new FileOutputStream(fichero));
								Log.i("OncheckedChanged","Open file 'External' to write");
							} else {
								fout=	new OutputStreamWriter( openFileOutput("logfile_"+str_fecha_actual+".txt", Context.MODE_PRIVATE));
								Log.i("OncheckedChanged","Open file 'Internal' to write");
							}
	
							Toast.makeText(getApplicationContext(), "Saving sensor data", Toast.LENGTH_SHORT).show();
	
							fout.write("% LogFile created by the 'GetSensorData' App for Android.");
							fout.write("\n% Date of creation: "+fecha_actual.toString());
							fout.write("\n% Developed by LOPSI research group at CAR-CSIC, Spain (http://www.car.upm-csic.es/lopsi)");
							fout.write("\n% Version 2.1 January 2018");
							fout.write("\n% The 'GetSensorData' program stores information from Smartphone/Tablet internal sensors (Accelerometers, Gyroscopes, Magnetometers, Pressure, Ambient Light, Orientation, Sound level, GPS/GNSS positio, WiFi RSS, Cellular/GSM/3G signal strength,...) and also from external devices (e.g. RFCode RFID reader, XSens IMU, LPMS-B IMU or MIMU22BT)");
							fout.write("\n%\n% Phone used for this logfile:");
							fout.write("\n% Manufacturer:			 \t"+phone_manufacturer);
							fout.write("\n% Model:					 \t"+phone_model);
							fout.write("\n% API Android version:	 \t"+phone_version);
							fout.write("\n% Android version Release: \t"+phone_versionRelease);
							fout.write("\n%\n% LogFile Data format:");
							fout.write("\n% Accelerometer data: \t'ACCE;AppTimestamp(s);SensorTimestamp(s);Acc_X(m/s^2);Acc_Y(m/s^2);Acc_Z(m/s^2);Accuracy(integer)'");
							fout.write("\n% Gyroscope data: 	\t'GYRO;AppTimestamp(s);SensorTimestamp(s);Gyr_X(rad/s);Gyr_Y(rad/s);Gyr_Z(rad/s);Accuracy(integer)'");
							fout.write("\n% Magnetometer data:	\t'MAGN;AppTimestamp(s);SensorTimestamp(s);Mag_X(uT);;Mag_Y(uT);Mag_Z(uT);Accuracy(integer)'");
							fout.write("\n% Pressure data:		\t'PRES;AppTimestamp(s);SensorTimestamp(s);Pres(mbar);Accuracy(integer)'");
							fout.write("\n% Light data: 		\t'LIGH;AppTimestamp(s);SensorTimestamp(s);Light(lux);Accuracy(integer)'");
							fout.write("\n% Proximity data: 	\t'PROX;AppTimestamp(s);SensorTimestamp(s);prox(?);Accuracy(integer)'");
							fout.write("\n% Humidity data:		\t'HUMI;AppTimestamp(s);SensorTimestamp(s);humi(Percentage);Accuracy(integer)'");
							fout.write("\n% Temperature data:	\t'TEMP;AppTimestamp(s);SensorTimestamp(s);temp(Celsius);Accuracy(integer)'");
							fout.write("\n% Orientation data:	\t'AHRS;AppTimestamp(s);SensorTimestamp(s);PitchX(deg);RollY(deg);YawZ(deg);Quat(2);Quat(3);Quat(4);Accuracy(int)'");
							fout.write("\n% GNSS/GPS data:		\t'GNSS;AppTimestamp(s);SensorTimeStamp(s);Latit(deg);Long(deg);Altitude(m);Bearing(deg);Accuracy(m);Speed(m/s);SatInView;SatInUse'");
							fout.write("\n% WIFI data:			\t'WIFI;AppTimestamp(s);SensorTimeStamp(s);Name_SSID;MAC_BSSID;Frequency;RSS(dBm);'"); // Added frequency by jtorres
							// fout.write("\n% WIFI data:		   \t'WIFI;AppTimestamp(s);SensorTimeStamp(s);Name_SSID;MAC_BSSID;RSS(dBm);'"); 			  original
							fout.write("\n% Bluetooth data: 	\t'BLUE;AppTimestamp(s);Name;MAC_Address;RSS(dBm);'");
							fout.write("\n% BLE 4.0 data:		\t'BLE4;AppTimestamp(s);iBeacon;MAC;RSSI(dBm);Power;MajorID;MinorID;UUID'"); // Added power and UUID by jtorres
							// fout.write("\n% BLE 4.0 data:	   \t'BLE4;AppTimestamp(s);iBeacon;MAC;RSSI(dBm);MajorID;MinorID;'");				original
							fout.write("\n% BLE 4.0 data:		\t'BLE4;AppTimestamp(s);Eddystone;MAC;RSSI(dBm);instanceID;OptionalTelemetry[voltaje;temperature;uptime;count]");
							fout.write("\n% Sound data: 		\t'SOUN;AppTimestamp(s);RMS;Pressure(Pa);SPL(dB);'");
							fout.write("\n% RFID Reader data:	\t'RFID;AppTimestamp(s);ReaderNumber(int);TagID(int);RSS_A(dBm);RSS_B(dBm);'");
							fout.write("\n% IMU XSens data: 	\t'IMUX;AppTimestamp(s);SensorTimestamp(s);Counter;Acc_X(m/s^2);Acc_Y(m/s^2);Acc_Z(m/s^2);Gyr_X(rad/s);Gyr_Y(rad/s);Gyr_Z(rad/s);Mag_X(uT);;Mag_Y(uT);Mag_Z(uT);Roll(deg);Pitch(deg);Yaw(deg);Quat(1);Quat(2);Quat(3);Quat(4);Pressure(mbar);Temp(Celsius)'");
							fout.write("\n% IMU LPMS-B data:	\t'IMUL;AppTimestamp(s);SensorTimestamp(s);Counter;Acc_X(m/s^2);Acc_Y(m/s^2);Acc_Z(m/s^2);Gyr_X(rad/s);Gyr_Y(rad/s);Gyr_Z(rad/s);Mag_X(uT);;Mag_Y(uT);Mag_Z(uT);Roll(deg);Pitch(deg);Yaw(deg);Quat(1);Quat(2);Quat(3);Quat(4);Pressure(mbar);Temp(Celsius)'");
							fout.write("\n% IMU MIMU22BT data:	\t'IMUI;AppTimestamp(s);Packet_count;Step_Counter;delta_X(m);delta_Y(m);delta_Z(m);delta_theta(degrees);Covariance4x4[1:10]'");
							fout.write("\n% POSI Reference: 	\t'POSI;Timestamp(s);Counter;Latitude(degrees); Longitude(degrees);floor ID(0,1,2..4);Building ID(0,1,2..3);'");
							fout.write("\n% ");
							fout.write("\n% Note that there are two timestamps: ");
							fout.write("\n%  -'AppTimestamp' is set by the Android App as data is read. It is not representative of when data is actually captured by the sensor (but has a common time reference for all sensors)");
							fout.write("\n%  -'SensorTimestamp' is set by the sensor itself (the delta_time=SensorTimestamp(k)-SensorTimestamp(k-1) between two consecutive samples is an accurate estimate of the sampling interval). This timestamp is better for integrating inertial data. \n");
						} catch (Exception ex) {
							Log.e("Ficheros", "Error writing file to device memory");
						}
						// Lanzar el Timer a 1Hz de Pintar los segundos trascurridos con timer
						timer_clock_handler();
						Log.i("debug.","Start timer...");
						timerReloj = new Timer("Hilo Timer Reloj");
						timerReloj.schedule(TaskReloj, 1000, 1000);  //call Timer every 1 second (with initial delay of 1s)

					} 
					else	// Parar de grabar
					{
						savedb = 0;
						// Parar el Timer a 1Hz de Pintar los segundos trascurridos con timer
						timerReloj.cancel();
	
						Log.i("Oncheckedchanged","Save button pressed to stop recording!I close the file");
						try {
							primer_sensor_cambia=true;	// resetear marca tiempo
							tiempo_inicial_ns_raw=0;
							fout.close();
							Toast.makeText(getApplicationContext(), "End of Saving", Toast.LENGTH_SHORT).show();

							contador_Posi = 0;
							obj_btnBotonMarkPosition.setText("Mark");
						} catch (Exception ex) {
							Log.e("Ficheros", "Error trying to close internal memory file");
						}
						//contador_Posi = 0;
					}
				}
			});
		}
	
		
	private void poner_manejador_boton_MarkPosition() {
			obj_btnBotonMarkPosition.setText("Mark");
	
			obj_btnBotonMarkPosition.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
	
					// Gestionar las pulsaciones del boton marcar
					if (obj_ToggleButtonSave.isChecked())  // Si grabando datos en log
					{
						Log.i("OnBotonMarkPosition", "Posicion marcada con botón mientras grabo fichero");
						contador_Posi=contador_Posi+1;	//incremento contador
						obj_btnBotonMarkPosition.setText("Mark #"+(contador_Posi+1));
					} else {
						Log.i("OnBotonMarkPosition", "Posicion no marcada pues no estoy grabando fichero");
						if (contador_Posi == 0) {
							obj_btnBotonMarkPosition.setText("Mark");
						} else {
							obj_btnBotonMarkPosition.setText("Mark #" + (contador_Posi + 1));
						}
						Toast.makeText(getApplicationContext(), "Not marked. Start saving first", Toast.LENGTH_SHORT).show();
						contador_Posi = 0;
					}
	
	
					if (obj_ToggleButtonSave.isChecked())  // Si grabando datos en log
					{
						// Poner TimeStamp de la App (seg�n le llega el dato)
						long timestamp_ns_raw = System.nanoTime(); // in nano seconds
						if (timestamp_ns_raw>=tiempo_inicial_ns_raw)   // "tiempo_inicial_ns_raw" inicializado al dar al boton de grabar
						{
							timestamp_ns = timestamp_ns_raw - tiempo_inicial_ns_raw;
						} else {
							timestamp_ns = (timestamp_ns_raw - tiempo_inicial_ns_raw) + Long.MAX_VALUE;
						}
						timestamp = ((double)(timestamp_ns))*1E-9;	// de nano_s a segundos
	
						// grabar en fichero
						try {
							// POSI;Timestamp(s);Counter;Latitude(degrees); Longitude(degrees);floor ID(0,1,2..4);Building ID(0,1,2..3)
							String cadena_file=String.format(Locale.US,"\nPOSI;%.3f;%d;%.8f;%.8f;%d;%d",timestamp,contador_Posi,0.0,0.0,0,0);
							fout.write(cadena_file);
						} catch (IOException e) {// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						tiempo_inicial_ns_raw = System.nanoTime();  // mark clear time;
					}
				}
			});
		}
	//======================onSaveInstanceState==================================
		// Called to save UI state changes at the
		// end of the active lifecycle.
		@Override
		public void onSaveInstanceState(Bundle savedInstanceState) {
			// Save UI state changes to the savedInstanceState.
			// This bundle will be passed to onCreate and
			// onRestoreInstanceState if the process is
			// killed and restarted by the run time.
			super.onSaveInstanceState(savedInstanceState);
		}
	
		//======================onRestoreInstanceState==================================
		// Called after onCreate has finished, use to restore UI state
		@Override
		public void onRestoreInstanceState(Bundle savedInstanceState) {
			super.onRestoreInstanceState(savedInstanceState);
			// Restore UI state from the savedInstanceState.
			// This bundle has also been passed to onCreate.
			// Will only be called if the Activity has been
			// killed by the system since it was last visible.
		}
	
		//----------showToast----------------------
		private void showToast(String msg)
		{
			Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
			error.show();
		}
	
		//======================onResume==================================
		@Override
		protected void onResume() {
			super.onResume();
			int delay=1;
	
			//SystemRequirementsChecker.checkWithDefaultDialogs(this);
	
			Log.i("OnResume","Estoy en OnResume");
	
	
			//.....register sensors............
			if (Sensor_Acc!=null)
			{
				mSensorManager.registerListener(this, Sensor_Acc, delay);
			}
	
			Log.i("OnResume","mSensorManager registered again");
	
			Log.i("OnResume","timerWifi Created");
		
		}
	
	
		//======================onReStart==================================
		@Override
		protected void onRestart() {
			super.onRestart();
			Log.i("OnRestart","Estoy en OnRestart");
		}
	
		//======================onPause==================================
		@Override
		protected void onPause() {
			super.onPause();
	
			Log.i("OnPause","INI: OnPause");
	
			//......unregister Sensors.................
			mSensorManager.unregisterListener(this);
	
			Log.i("OnPause","END: OnPause");
		}
	
		//======================onStop==================================
		@Override
		protected void onStop() {
			super.onStop();
			Log.i("OnStop","Estoy en OnStop");
		}
	
		//======================onDestroy==================================
		@Override
		protected void onDestroy() {
			super.onDestroy();
			
			if (flag_Trace)
			{	Debug.stopMethodTracing();	}
			Log.i("OnDestroy","END: OnDestroy");
		}

}
