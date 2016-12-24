package cn.vamtrices.tools;

/*
 * 坐标类，存储三维坐标信息。
 * 
 */

public class Coord {
	public float x;
	public float y;
	public float z;
	
	public Coord() {
		x=y=z=0;
	}
	
	public Coord(float x, float y, float z) {
		setCoord( x,  y,  z);
	}
	
	public void setCoord(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Coord copy(Coord a){
		this.x = a.x;
		this.y = a.y;
		this.z = a.z;
		return this;
	}

}
