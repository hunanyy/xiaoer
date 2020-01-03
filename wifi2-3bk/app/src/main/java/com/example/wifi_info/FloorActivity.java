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

public class FloorActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_floor);
	}

	// 1¥
	public void floor_1(View view) {
		Intent intent = new Intent();
		intent.putExtra("floor", "1");
		intent.setClass(FloorActivity.this, TrainingActivity.class);
		startActivity(intent);
	}

	
	public void floor_2(View view) {
		Intent intent = new Intent();
		intent.putExtra("floor", "2");
		intent.setClass(FloorActivity.this, TrainingActivity.class);
		startActivity(intent);
	}
	
	public void floor_3(View view) {
		Intent intent = new Intent();
		intent.putExtra("floor", "3");
		intent.setClass(FloorActivity.this, TrainingActivity.class);
		startActivity(intent);
	}
	
}
