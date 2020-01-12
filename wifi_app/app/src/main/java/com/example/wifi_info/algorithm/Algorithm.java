package com.example.wifi_info.algorithm;

import java.util.List;

import com.example.wifi_info.tools.Infor;
import com.example.wifi_info.tools.Point;
import com.example.wifi_info.tools.Position;

/**
 * 
 * @author nupt
 *
 */
public class Algorithm {

	/**
	 * 匹配算法
	 * 
	 * @param points
	 * @param p
	 * @return
	 */
	public Position method(List<Point> points, Point p) {

		return method(points, p.getInfors());

	}

	/**
	 * 匹配算法
	 * 
	 * @param points
	 * @param infors
	 * @return
	 */
	public Position method(List<Point> points, List<Infor> infors) {
		// TODO Auto-generated method stub

		// 初始条件判断
		if (initialCondition(points, infors))
			System.out.println("满足初始条件。");
		else {
			System.out.println("不满足初始条件。");
			return null;
		}

		double[][] datas = datasToArr(points);// points转换为二维数组
		// print训练数组
		print(datas, "训练数组");

		double[] test = testToArr(infors); // infors转换为一维数组
		// print测试数组
		print(test, "测试数据");

		double[] distance = getDistance(datas, test);//  计算datas[i]与test的欧式距离

		while (true) {
			// println欧式距离
			println(distance, "欧式距离");

			int[] order = getOrder(distance);// 由小到大排序的,order[0]的值为最小欧式距离所在角标
			// println大小顺序
			println(order, "distance由小到大的index");

			int k = kValue(distance);// 根据欧式距离确定k值
			System.out.println("k = " + k);

			int[] index = getIndex(order, k);// 获取k个角标
			// println index
			println(index, "index");

			Position[] pos = getPos(points, index);// K个坐标
			// println前k个坐标
			for (Position p : pos)
				System.out.print(p);

			Position mathCentre = getMathCentre(pos, pos.length);// K个坐标的几何中心点
			// println几何中心点
			System.out.println("几何中心点" + mathCentre);

			double[] mathCentreDis = getMathCentreDis(pos, mathCentre);// k个坐标与几何中心点距离
			// println k个坐标与几何中心点距离
			println(mathCentreDis, "k个坐标与几何中心点距离");

			int maxIndex = getMaxIndex(mathCentreDis);// k个坐标中距几何中心点最远元素角标
			// ptintln maxIndex
			System.out.println("maxIndex = " + maxIndex);

			if (mathCentreDis[maxIndex] > 12) {
				distance[index[maxIndex]] = Double.MAX_VALUE;// 抛弃maxIndex所指向的Position
				continue;
			}

			double[] weight = getWeight(distance, index, Kinds.WKNN);// 权值
			// println权值
			println(weight, "权重");

			Position centre = calculate(pos, weight);// 中心点

			return centre;
		}

	}

	/**
	 * 返回arr[]中元素最大值所在角标
	 * 
	 * @param mathCentreDis
	 * @return
	 */
	private int getMaxIndex(double[] arr) {
		// TODO Auto-generated method stub
		int length = arr.length;
		int index = -1;

		double max = 0;

		for (int i = 0; i < length; i++) {
			if (arr[i] > max) {
				max = arr[i];
				index = i;
			}

		}

		return index;
	}

	/**
	 * 计算各坐标与几何中心点距离
	 * 
	 * @param pos
	 * @param mathCentre
	 * @return
	 */
	private double[] getMathCentreDis(Position[] pos, Position mathCentre) {
		// TODO Auto-generated method stub
		int length = pos.length;
		double[] arr = new double[length];

		for (int i = 0; i < length; i++)
			arr[i] = Math.sqrt((pos[i].getPosX() - mathCentre.getPosX())
					* (pos[i].getPosX() - mathCentre.getPosX())
					+ (pos[i].getPosY() - mathCentre.getPosY())
					* (pos[i].getPosY() - mathCentre.getPosY()));

		return arr;
	}

	/**
	 * 计算几何中心点
	 * 
	 * @param pos
	 * @param length
	 * @return
	 */
	private Position getMathCentre(Position[] pos, int length) {
		// TODO Auto-generated method stub
		double posX = 0, posY = 0;
		for (Position p : pos) {
			posX += p.getPosX();
			posY += p.getPosY();
		}

		return new Position(posX * 1.0 / length, posY * 1.0 / length);
	}

	/**
	 * 计算(x,y)
	 * 
	 * @param pos
	 * @param weight
	 * @return
	 */
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
	 * 计算加权值。KNN权重为1；WKNN权重为distance的倒数之和占比。默认KNN
	 * 
	 * @param distance
	 * @param index
	 * @param knn
	 * @param str
	 * @return
	 */
	private double[] getWeight(double[] distance, int[] index, Kinds kind) {
		// TODO Auto-generated method stub
		int length = index.length;
		// 克隆一个double[] distance
		double[] clone = distance.clone();

		double[] res = new double[length];
		switch (kind) {
		case KNN:
			for (int i = 0; i < length; i++)
				res[i] = 1 / (length * 1.0);
			break;
		case WKNN: {
			double sum = 0;
			for (int i = 0; i < length; i++) {
				clone[index[i]] = 1 / (clone[index[i]] + 0.01);
				sum += clone[index[i]];
			}

			for (int i = 0; i < length; i++) {
				res[i] = clone[index[i]] / sum;
			}
			break;
		}

		default:
			for (int i = 0; i < length; i++)
				res[i] = 1 / (length * 1.0);
			break;
		}

		return res;
	}

	/**
	 * 获取前K个坐标
	 * 
	 * @param points
	 * @param index
	 * @return
	 */
	private Position[] getPos(List<Point> points, int[] index) {
		// TODO Auto-generated method stub
		int length = index.length;

		Position[] pos = new Position[length];
		for (int i = 0; i < length; i++) {
			pos[i] = points.get(index[i]).getPos();
		}

		return pos;
	}

	/**
	 * 初始条件：List<Infor> infors 中的BSSID个数、位置相同
	 * 
	 * @param datas
	 * @param test
	 * @return
	 */
	private boolean initialCondition(List<Point> datas, List<Infor> test) {
		if (datas == null || test == null)
			return false;

		List<Infor> infData = datas.get(0).getInfors();

		// 要求BSSID个数相同
		if (infData.size() != test.size())
			return false;

		int size = test.size();

		// 要求BSSID顺序相同
		for (int i = 0; i < size; i++) {

			String str1 = infData.get(i).getBssid();
			String str2 = test.get(i).getBssid();

			if (!str1.equals(str2))
				return false;
		}

		return true;
	}

	/**
	 * eleDatas转数组
	 * 
	 * @param eleDatas
	 * @return
	 */
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

	/**
	 * eleTest转数组
	 * 
	 * @param eleTest
	 * @return
	 */
	private double[] testToArr(List<Infor> infors) {
		// TODO Auto-generated method stub
		int size = infors.size();
		double[] test = new double[size];
		for (int i = 0; i < size; i++) {
			test[i] = infors.get(i).getLevel();
		}
		return test;
	}

	/**
	 * 
	 * 欧式距离
	 * 
	 * @param datas
	 * @param test
	 * @return
	 */
	private double[] getDistance(double[][] datas, double[] test) {
		// TODO Auto-generated method stub
		int length = datas.length;

		double[] res = new double[length];
		for (int i = 0; i < length; i++) {
			res[i] = getDistance(datas[i], test);
		}
		return res;
	}

	/**
	 * 欧式距离
	 * 
	 * @param arr1
	 * @param arr2
	 * @return
	 */
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

	/**
	 * 不改变元素位置，由小到大的顺序，指定各自所在位置。 a[0]=5表示欧式距离最小值在角标为5处
	 * 
	 * @param arr
	 * @return
	 */
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

	/**
	 * 根据欧式距离，确定K值
	 */

	private int kValue(double[] distance) {
		// TODO Auto-generated method stub
		int length = distance.length;
		int[] order = getOrder(distance);

		try {

			int min = 3, max = length;

			return 6;

		} catch (Exception e) {
			// TODO: handle exception
			return 1;
		}

	}

	/**
	 * 前K个角标
	 * 
	 * @param order
	 * @param k
	 * @return
	 */
	private int[] getIndex(int[] order, int k) {
		// TODO Auto-generated method stub
		int[] res = new int[k];
		for (int i = 0; i < k; i++) {
			res[i] = order[i];
		}
		return res;
	}

	/**
	 * 
	 * @author nupt
	 *
	 */
	private enum Kinds {
		KNN, WKNN
	}

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
