package com.example.wifi_info.tools;

/**
 * 位置信息类(x,y)
 * 
 * @author nupt
 *
 */

public class Position {

	private double posX;
	private double posY;
	
	private double posZ;
	
	

	public Position(double posX, double posY) {
		super();
		this.posX = posX;
		this.posY = posY;
	}

	public double getPosX() {
		return posX;
	}

	public void setPosX(double posX) {
		this.posX = posX;
	}

	public double getPosY() {
		return posY;
	}

	public void setPosY(double posY) {
		this.posY = posY;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "\n(posX, posY) = (" + posX + ", " + posY + ")";
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub

		Position pos = null;

		try {

			pos = (Position) obj;

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("类型与Position不匹配，不相同");
			return false;
		}

		double x = Math.abs(this.getPosX() - pos.posX);
		double y = Math.abs(this.getPosY() - pos.posY);

		if (x < 0.001 && y < 0.001)
			return true;
		else
			return false;

	}

}
