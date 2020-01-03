package com.example.wifi_info.algorithm;

public class Gaussian {

	private final static double WEIGHT = 1.65;

	public static double gauss(double[] arr) {

		System.out.println("gauss前数组");

		for (int i = 0; i < arr.length; i++) {
			System.out.print(arr[i] + ",");
		}

		System.out.println();

		double avg = avg(arr);

		System.out.println("gauss计算前avg:" + avg);

		double o = o(arr);

		double min = avg - WEIGHT * o;
		double max = avg + WEIGHT * o;
		/*
		 * for (int i = 0; i < arr.length; i++) { if (arr[i] < min || arr[i] >
		 * max) arr[i] = 0; } return arr;
		 */

		int count = 0;

		for (int i = 0; i < arr.length; i++) {
			if (arr[i] < min || arr[i] > max)
				count++;
		}

		System.out.println("count" + count);

		double[] newArr = new double[arr.length - count];

		for (int i = 0, j = 0; i < arr.length; i++) {
			if (arr[i] < min || arr[i] > max)
				continue;
			newArr[j++] = arr[i];
		}

		System.out.println("gauss后数组");

		for (int i = 0; i < newArr.length; i++) {
			System.out.print(newArr[i] + ",");
		}

		System.out.println();
		
		double res = avg(newArr);

		System.out.println("gauss后avg:"+res);
		
		return res;
	}

	private static double o2(double[] arr) {
		double o2 = 0;

		double avg = avg(arr);

		for (int i = 0; i < arr.length; i++)
			o2 += (avg - arr[i]) * (avg - arr[i]);

		return o2 * 1.0 / arr.length;

	}

	private static double o(double[] arr) {
		double o2 = o2(arr);

		return Math.sqrt(o2);
	}

	private static double avg(double[] arr) {

		double sum = 0;

		for (int i = 0; i < arr.length; i++)
			sum += arr[i];

		return sum * 1.0 / arr.length;

	}

}