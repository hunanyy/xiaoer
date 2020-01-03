package com.example.wifi_info.tools;

/**
 * 为平均前，数据库中一条数据
 * 
 * @author nupt
 *
 */
public class Item {

	private Position pos;

	private String bssid;

	private double level;

	public Item(Position pos, String bssid, double level) {
		super();
		this.pos = pos;
		this.bssid = bssid;
		this.level = level;
	}

	public Position getPos() {
		return pos;
	}

	public void setPos(Position pos) {
		this.pos = pos;
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

}
