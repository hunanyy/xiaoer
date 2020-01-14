package com.example.wifi_info.tools;

import java.util.List;

/**
 * 采集点类。包含一个位置信息(x,y)，一个AP特征信息List<Infor>
 * 
 * @author nupt
 *
 */
public class Point {

	private Position pos;
	private List<Infor> infors;

	public Point(Position pos, List<Infor> infors) {
		super();
		this.pos = pos;
		this.infors = infors;
	}

	public Position getPos() {
		return pos;
	}

	public void setPos(Position pos) {
		this.pos = pos;
	}

	public List<Infor> getInfors() {
		return infors;
	}

	public void setInfors(List<Infor> infors) {
		this.infors = infors;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub

		return pos.toString() + infors.toString();
	}

}
