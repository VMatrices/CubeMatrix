package cn.vamtrices.tools;

/*
 * 线段类，存储Coord坐标对象构成的空间线段。
 * 
 */
public class Line {

	public Coord start;
	public Coord end;

	public Line() {
		start =new Coord();
		end =new Coord();
	}

	public Line(float sx,float sy,float sz,float ex,float ey,float ez) {
		start =new Coord(sx, sy, sz);
		end =new Coord(ex, ey, ez);
	}

	public void setLine(float sx, float sy, float sz, float ex, float ey, float ez) { //设置线段端点坐标
		start.setCoord(sx, sy, sz);
		end.setCoord(ex, ey, ez);
	}
	
	public static void init(Line[] lines){ //快速初始化对象数组
		for(int i=0;i<lines.length;i++){
			lines[i]=new Line();
		}
	}

}
