package com.example.wifi_info.tools;

import java.util.List;

public class ItemLevels {

	private Position pos;

	private String bssid;

	private List<Double> list_double;

	public ItemLevels(Position pos, String bssid, List<Double> list_double) {
		super();
		this.pos = pos;
		this.bssid = bssid;
		this.list_double = list_double;
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

	public List<Double> getList_double() {
		return list_double;
	}

	public void setList_double(List<Double> list_double) {
		this.list_double = list_double;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub

		ItemLevels item = null;

		try {

			item = (ItemLevels) obj;

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("类型与ItemLevels不匹配，不相同");
			return false;
		}

		if (this.pos == item.pos && this.bssid == item.bssid)
			return true;
		else
			return false;

	}

}
