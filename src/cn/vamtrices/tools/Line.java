package cn.vamtrices.tools;

/*
 * �߶��࣬�洢Coord������󹹳ɵĿռ��߶Ρ�
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

	public void setLine(float sx, float sy, float sz, float ex, float ey, float ez) { //�����߶ζ˵�����
		start.setCoord(sx, sy, sz);
		end.setCoord(ex, ey, ez);
	}
	
	public static void init(Line[] lines){ //���ٳ�ʼ����������
		for(int i=0;i<lines.length;i++){
			lines[i]=new Line();
		}
	}

}
