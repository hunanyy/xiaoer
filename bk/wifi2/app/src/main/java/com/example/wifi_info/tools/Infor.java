package com.example.wifi_info.tools;

/**
 * 一个AP信息(bssid,level)
 * 
 * @author nupt
 *
 */

public class Infor implements Comparable<Infor> {

	private String bssid;
	private double level;

	public Infor(String bssid, double level) {
		super();
		this.bssid = bssid;
		this.level = level;
	}

	public String getBssid() {
		return bssid;
	}

	public void setBssid(String bssid) {
		this.bssid = bssid;
	}

	public double getLevel() {
		return level;
	}

	public void setLevel(double level) {
		this.level = level;
	}

	/**
	 * 按level大小排序，精确到小数点后两位
	 */
	@Override
	public int compareTo(Infor another) {
		// TODO Auto-generated method stub
		return (int) ((another.level - this.level) * 1e2);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "(" + bssid + ", " + level + ")";
	}

}
