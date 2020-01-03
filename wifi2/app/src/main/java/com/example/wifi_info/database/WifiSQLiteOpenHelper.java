package com.example.wifi_info.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WifiSQLiteOpenHelper extends SQLiteOpenHelper {

	public WifiSQLiteOpenHelper(Context context) {
		super(context, "wifi.db", null, 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("create table wifi("
				+ "id integer primary key autoincrement,"
				+ "ssid vachar(20), bssid vachar(20), posX real, posY real, level real, frequency real, timestamp vachar(20))");

		db.execSQL("create table wifi_avg("
				+ "id integer primary key autoincrement,"
				+ "ssid vachar(20), bssid vachar(20), posX real, posY real, level real, frequency real, timestamp vachar(20))");
		db.execSQL("create table wifi_gauss("
				+ "id integer primary key autoincrement,"
				+ "ssid vachar(20), bssid vachar(20), posX real, posY real, level real, frequency real, timestamp vachar(20))");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("");
	}

}
