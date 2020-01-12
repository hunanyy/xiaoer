package com.example.wifi_info.database;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.example.wifi_info.tools.Infor;
import com.example.wifi_info.tools.Item;
import com.example.wifi_info.tools.Point;
import com.example.wifi_info.tools.Position;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

public class WifiDao {
	private WifiSQLiteOpenHelper helper;
	private static String name;

	public WifiDao(Context context) {
		File dir = Environment.getExternalStoragePublicDirectory("wifidb");
		if(!dir.exists()){
			dir.mkdir();
		}
		name = dir + "/wifi.db";
		helper = new WifiSQLiteOpenHelper(context, name);
	}

	/**
	 * 插入数据
	 * 
	 * @param ssid
	 * @param bssid
	 * @param level
	 * @param posX
	 * @param posY
	 * @param frequency
	 * @param timestamp
	 * @return
	 */
	public long add(String ssid, String bssid, int level, double posX,
			double posY, int frequency, String timestamp) {
		SQLiteDatabase db = helper.getWritableDatabase();
		// db.execSQL("insert into wifi (ap_name, ap_level number) values (?,?)");
		ContentValues values = new ContentValues();

		values.put("ssid", ssid);
		values.put("bssid", bssid);
		values.put("level", level);
		values.put("posX", posX);
		values.put("posY", posY);
		values.put("frequency", frequency);
		values.put("timestamp", timestamp);

		long id = db.insert("wifi", null, values);

		db.close();

		return id;
	}

	/**
	 * 高斯计算
	 * 
	 * @param ssid
	 * @param bssid
	 * @param level
	 * @param posX
	 * @param posY
	 * @param frequency
	 * @param timestamp
	 * @return
	 */

	public long addGauss(String ssid, String bssid, double level, double posX,
			double posY, int frequency, String timestamp) {
		SQLiteDatabase db = helper.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put("ssid", ssid);
		values.put("bssid", bssid);
		values.put("level", level);
		values.put("posX", posX);
		values.put("posY", posY);
		values.put("frequency", frequency);
		values.put("timestamp", timestamp);

		long id = db.insert("wifi_gauss", null, values);

		db.close();

		return id;
	}

	/**
	 * 求level平均值
	 */
	public boolean avg() {
		SQLiteDatabase db = helper.getReadableDatabase();
		db.execSQL("delete from wifi_avg;");
		db.execSQL("insert into wifi_avg" + "(ssid,bssid,level,posX,posY)"
				+ " select ssid,bssid,round(avg(level),2) as level,posX,posY"
				+ " from wifi group by posX,posY,bssid;");

		db.close();
		return true;
	}

	/**
	 * 
	 * @param sql
	 *            bssid0\t\tbssid1\t\t...bssidn\t\t
	 * 
	 * @return 返回最强AP选择后的点集
	 */
	public List<Point> returnTempTable(String sql) {

		SQLiteDatabase db = helper.getWritableDatabase();

		StringBuffer sb = new StringBuffer();
		sb.append("posX\t\tposY\t\t");
		sb.append(sql.trim());

		String[] columns = sb.toString().split("\t\t");

		// 删除临时表
		try {

			db.execSQL("drop table temp;\n");

		} catch (Exception e) {
			// TODO: handle exception
		}

		// 建立临时表temp
		sb.delete(0, sb.length());
		sb.append("create table temp(id integer primary key autoincrement, posX real, posY real");
		for (int i = 2; i < columns.length; i++) {
			sb.append(", ");
			sb.append("'" + columns[i] + "'");
			sb.append(" real");
		}
		sb.append(");");
		db.execSQL(sb.toString());

		// 插入数据
		sb.delete(0, sb.length());
		sb.append("insert into temp(posX, posY");
		for (int i = 2; i < columns.length; i++) {
			sb.append(",");
			sb.append("'" + columns[i] + "'");
		}
		sb.append(")\n");
		sb.append("select  posX, posY");
		for (int i = 2; i < columns.length; i++) {
			sb.append(",");
			sb.append("avg(case bssid when ");
			sb.append("'" + columns[i] + "'");
			sb.append(" then level end) ");
			sb.append("'" + columns[i] + "'");
		}
		sb.append(" from wifi_gauss group by posX, posY;");

		// System.out.println(sb.toString());
		db.execSQL(sb.toString().trim());

		// 返回的临时表集
		List<Point> points = new ArrayList<Point>();

		Cursor cursor = db.rawQuery("select * from temp", null);

		while (cursor.moveToNext()) {

			double posX = Double.parseDouble(cursor.getString(cursor
					.getColumnIndex("posX")));
			double posY = Double.parseDouble(cursor.getString(cursor
					.getColumnIndex("posY")));

			String[] bssids = new String[columns.length - 2];
			double[] levels = new double[columns.length - 2];

			for (int i = 0; i < columns.length - 2; i++) {

				try {

					bssids[i] = cursor.getColumnName(i + 3);
					levels[i] = Double.parseDouble(cursor.getString(cursor
							.getColumnIndex(columns[i + 2])));

				} catch (Exception e) {
					// TODO: handle exception
					// levels为null时赋值-100.0
					levels[i] = -100.0;
				}

			}

			List<Infor> li = new ArrayList<Infor>();
			for (int i = 0; i < bssids.length; i++)
				li.add(new Infor(bssids[i], levels[i]));

			Point p = new Point(new Position(posX, posY), li);
			points.add(p);
		}

		cursor.close();
		db.close();

		return points;

	}

	/**
	 * 删除
	 * 
	 * @param ap_ssid
	 * @return
	 */
	public int delete(String ap_ssid) {
		SQLiteDatabase db = helper.getWritableDatabase();
		// db.execSQL("delete from person where name=?", new Object[]{name});
		int result = db.delete("WIFI", "ap_ssid=?", new String[] { ap_ssid });
		db.close();
		return result;
	}

	/**
	 * 返回未平均前全部数据
	 * 
	 * @return
	 */
	public List<Item> getAllItem() {

		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor cursor = db.rawQuery("select * from wifi", null);

		List<Item> list_item = new LinkedList<Item>();

		while (cursor.moveToNext()) {

			double posX = Double.parseDouble(cursor.getString(cursor
					.getColumnIndex("posX")));
			double posY = Double.parseDouble(cursor.getString(cursor
					.getColumnIndex("posY")));

			Position pos = new Position(posX, posY);

			String bssid = cursor.getString(cursor.getColumnIndex("bssid"));

			double level = Double.parseDouble(cursor.getString(cursor
					.getColumnIndex("level")));

			list_item.add(new Item(pos, bssid, level));

		}

		db.close();
		return list_item;

	}

}
