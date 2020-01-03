package com.example.wifi_info.algorithm;

import java.text.DecimalFormat;
import java.util.List;

import com.example.wifi_info.tools.Infor;
import com.example.wifi_info.tools.Point;
import com.example.wifi_info.tools.Position;

public class WKNN {

	public Position method(List<Point> points, List<Infor> infors) {

		double[][] datas = datasToArr(points);// points转换为二维数组

		// print训练数组
		print(datas, "训练数组");

		double[] test = testToArr(infors); // infors转换为一维数组

		// print测试数组
		print(test, "测试数据");

		double[] distance = getDistance(datas, test);// 计算datas[i]与test的欧式距离

		// println欧式距离
		println(distance, "欧式距离");

		int[] order = getOrder(distance);// 由小到大排序的,order[0]的值为最小欧式距离所在角标

		int k = kValue(distance);// 设置K值

		int[] index = getIndex(order, k);// 获取k个角标

		// println index
		println(index, "index");

		Position[] pos = getPos(points, index);// K个坐标

		// println前k个坐标
		for (Position p : pos)
			System.out.print(p);

		double[] weight = getWeight(distance, index);// 权值
		
		println(weight,"权值：");

		Position centre = calculate(pos, weight);// 定位坐标

		DecimalFormat df = new DecimalFormat("######0.00");

		double position_X = Double.parseDouble(df.format(centre.getPosX()));
		double position_Y = Double.parseDouble(df.format(centre.getPosY()));

		Position res = new Position(position_X, position_Y);
		// println定位坐标
		System.out.print("WKNN结果："+res);

		return res;

	}

	private double[][] datasToArr(List<Point> points) {
		// TODO Auto-generated method stub
		int pointsSize = points.size();
		int inforsSize = points.get(0).getInfors().size();

		double[][] datas = new double[pointsSize][inforsSize];

		for (int j = 0; j < pointsSize; j++) {
			List<Infor> infData = points.get(j).getInfors();
			for (int i = 0; i < infData.size(); i++) {
				datas[j][i] = infData.get(i).getLevel();
			}
		}
		return datas;
	}

	private double[] testToArr(List<Infor> infors) {
		// TODO Auto-generated method stub
		int size = infors.size();
		double[] test = new double[size];
		for (int i = 0; i < size; i++) {
			test[i] = infors.get(i).getLevel();
		}
		return test;
	}

	private double[] getDistance(double[][] datas, double[] test) {
		// TODO Auto-generated method stub
		int length = datas.length;

		double[] res = new double[length];
		for (int i = 0; i < length; i++) {
			res[i] = getDistance(datas[i], test);
		}
		return res;
	}

	private double getDistance(double[] data, double[] test) {

		// 如果arr1与arr2的长度不同，距离值为负
		if (data.length != test.length)
			return -1.0;

		double result = 0;

		for (int i = 0; i < data.length; i++) {
			double temp = data[i] - test[i];
			temp *= temp;
			result += temp;
		}

		return Math.sqrt(result);

	}

	private int[] getOrder(double[] arr) {
		int length = arr.length;
		// 克隆一个数组
		double[] clone = arr.clone();

		int[] res = new int[length];

		for (int i = 0; i < length; i++) {

			double min = clone[0];
			res[i] = 0;

			for (int j = 1; j < length; j++) {

				if (clone[j] < Double.MAX_VALUE && clone[j] < min) {
					min = clone[j];
					res[i] = j;
				}

			}

			clone[res[i]] = Double.MAX_VALUE;

		}

		return res;
	}

	private int kValue(double[] distance) {
		// TODO Auto-generated method stub
		return 4;
	}

	private int[] getIndex(int[] order, int k) {
		// TODO Auto-generated method stub

		int[] res = new int[k];
		for (int i = 0; i < k; i++) {
			res[i] = order[i];
		}
		return res;
	}

	private double[] getWeight(double[] distance, int[] index) {
		// TODO Auto-generated method stub
		int length = index.length;
		// 克隆一个double[] distance
		double[] clone = distance.clone();

		double[] res = new double[length];

		double sum = 0;
		for (int i = 0; i < length; i++) {
			clone[index[i]] = 1 / (clone[index[i]] + 0.01);
			sum += clone[index[i]];
		}

		for (int i = 0; i < length; i++) {
			res[i] = clone[index[i]] / sum;
		}

		return res;
	}

	private Position[] getPos(List<Point> points, int[] index) {
		// TODO Auto-generated method stub
		int length = index.length;

		Position[] pos = new Position[length];
		for (int i = 0; i < length; i++) {
			pos[i] = points.get(index[i]).getPos();
		}

		return pos;
	}

	private Position calculate(Position[] pos, double[] weight) {
		// TODO Auto-generated method stub

		double x = 0, y = 0;
		for (int i = 0; i < pos.length; i++) {
			x += pos[i].getPosX() * weight[i];
			y += pos[i].getPosY() * weight[i];
		}

		return new Position(x, y);
	}

	/**
	 * println
	 */

	private void print(double[] arr) {
		// TODO Auto-generated method stub
		int length = arr.length;
		for (int i = 0; i < length; i++)
			System.out.print(arr[i] + "\t");
		System.out.println();
	}

	private void print(double[] arr, String str) {
		// TODO Auto-generated method stub
		System.out.println(str);
		print(arr);
	}

	private void print(double[][] arr, String str) {
		// TODO Auto-generated method stub
		System.out.println(str);
		int length = arr.length;
		for (int i = 0; i < length; i++)
			print(arr[i]);
	}

	private void println(double[] arr, String str) {
		// TODO Auto-generated method stub
		System.out.println(str);
		int length = arr.length;
		for (int i = 0; i < length; i++)
			System.out.println("[" + i + "]=" + arr[i]);
	}

	private void println(int[] arr, String str) {
		// TODO Auto-generated method stub
		System.out.println(str);
		int length = arr.length;
		for (int i = 0; i < length; i++)
			System.out.println("[" + i + "]=" + arr[i]);
	}

}
