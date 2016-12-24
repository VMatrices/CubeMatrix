package cn.vamtrices.tools;


/*
 * �����࣬�ṩ�洢������Ϣ����һЩ��̬����
 */

public class Block {
	public Line[] lines=new Line[12];
	public int style;
	public float size;
	public int x,y;
	public Coord mid=new Coord();
	public Boolean visibility=true;
	
	public Block(float blockSize) {
		this.size=blockSize;
		Line.init(lines);
	}
	
	public Block creat(int x,int y,int style) { //���췽�� λ��x,y style��ʽ
		this.x=x;
		this.y=y;
		this.style=style;

		if(style==0){
			setBlock(this.lines,x*size,y*size,0,size,size,2*size);
		} else if(style==1){
			setBlock(this.lines,x*size,y*size,0,2*size,size,size);
		} else if(style==2){
			setBlock(this.lines,x*size,y*size,0,size,2*size,size);
		}
		return this;
	}
	
	public float getPosZ(){ //��ȡ����߶�
		return lines[0].start.z;
	}
	
    public static Line[] setBlock(Line[] lines,float x,float y,float z,float L,float W,float H){ //���÷��飨ԭ�л����ϸ��ǣ�
    	//bottom
		lines[0].setLine(x,y,z,x+L,y,z);
		lines[1].setLine(x,y,z,x,y+W,z);
		lines[2].setLine(x+L,y+W,z,x,y+W,z);
		lines[3].setLine(x+L,y+W,z,x+L,y,z);
		
		//middle
		lines[4].setLine(x,y,z,x,y,z+H);
		lines[5].setLine(x+L,y,z,x+L,y,z+H);
		lines[6].setLine(x,y+W,z,x,y+W,z+H);
		lines[7].setLine(x+L,y+W,z,x+L,y+W,z+H);
		
		//Top
		lines[8].setLine(x,y,z+H,x+L,y,z+H);
		lines[9].setLine(x,y,z+H,x,y+W,z+H);
		lines[10].setLine(x+L,y+W,z+H,x,y+W,z+H);
		lines[11].setLine(x+L,y+W,z+H,x+L,y,z+H);
    	return lines;
    }

    public static Line[] creatBlock(float x,float y,float z,float L,float W,float H){ //��̬����������һ������ λ��x,y,z �����L W H
    	Line[] lines=new Line[12];
    	Line.init(lines);
    	return setBlock(lines,x, y, z, L, W, H);
    }
    
    public Coord getMidPoint(){
    	mid.setCoord(0,0,0);
    	mid.x=(lines[0].start.x+lines[11].start.x)/2;
    	mid.y=(lines[0].start.y+lines[11].start.y)/2;
    	mid.z=(lines[0].start.z+lines[11].start.z)/2;
    	return mid;
    }
}
