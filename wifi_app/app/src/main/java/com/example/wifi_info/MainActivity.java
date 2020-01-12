package com.example.wifi_info;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.example.wifi_info.algorithm.Gaussian;
import com.example.wifi_info.database.WifiDao;
import com.example.wifi_info.tools.Item;
import com.example.wifi_info.tools.ItemLevels;
import com.example.wifi_info.tools.Position;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	// 训练模式
	public void training(View view) {
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, TrainingActivity.class);
		startActivity(intent);
	}

	// 定位模式
	public void position(View view) {
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, PositionActivity.class);
		startActivity(intent);
	}

	// 求平均
	public void avg(View view) {
		WifiDao dao = new WifiDao(this);
		dao.avg();
		Toast.makeText(MainActivity.this, "success", Toast.LENGTH_LONG).show();
	}

	// 求高斯
	public void gauss(View v) {

		WifiDao dao = new WifiDao(this);
		LinkedList<Item> list_item = (LinkedList<Item>) dao.getAllItem();

		List<Position> list_pos = new ArrayList<Position>();// 全部位置
		List<String> list_bssid = new ArrayList<String>();// 全部AP

		/**
		 * 获取全部位置和AP
		 */
		for (Item item : list_item) {

			if (!list_pos.contains(item.getPos()))
				list_pos.add(item.getPos());

			if (!list_bssid.contains(item.getBssid()))
				list_bssid.add(item.getBssid());

		}

		List<ItemLevels> list_itemLevels = new ArrayList<ItemLevels>();

		/**
		 * 
		 */

		for (Item item : list_item) {

			for (Position pos : list_pos) {

				if (item.getPos().equals(pos)) {

					for (String bssid : list_bssid) {

						if (item.getBssid().equals(bssid)) {

							List<Double> list_double = new ArrayList<Double>();
							list_double.add(item.getLevel());

							ItemLevels newItemLevels = new ItemLevels(pos,
									bssid, list_double);

							Boolean bool = list_itemLevels
									.contains(newItemLevels);

							if (!bool)
								list_itemLevels.add(newItemLevels);

							else {
								int index = list_itemLevels
										.indexOf(newItemLevels);

								list_double = list_itemLevels.remove(index)
										.getList_double();

								list_double.add(item.getLevel());

								list_itemLevels.add(new ItemLevels(pos, bssid,
										list_double));

							}

						}
					}

				}

			}

		}

		Toast.makeText(this, "list_itemLevels:" + list_itemLevels.size(),
				Toast.LENGTH_LONG).show();

		for (ItemLevels itemLevels : list_itemLevels) {

			System.out.println("pos:" + itemLevels.getPos() + "bssid:"
					+ itemLevels.getBssid() + "\n" + "gauss前数组:"
					+ itemLevels.getList_double());

			List<Double> list_double = itemLevels.getList_double();

			double[] arr = new double[list_double.size()];

			int count = 0;
			for (Double d : list_double) {
				arr[count++] = d;
			}

			double gauss = Gaussian.gauss(arr);

			System.out.println("gauss计算后avg:" + gauss);

			dao.addGauss(null, itemLevels.getBssid(), gauss, itemLevels
					.getPos().getPosX(), itemLevels.getPos().getPosY(), 0, null);

		}

		Toast.makeText(MainActivity.this, "success", Toast.LENGTH_LONG).show();

	}
}
