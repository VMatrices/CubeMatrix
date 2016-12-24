package cn.vamtrices.tools;

import android.graphics.Canvas;
import android.graphics.Paint;
/*
 * ����Canvas������ͼ�ļ���3D�߶λ�������
 * ֧�ּ���VRģʽ
 * 
 */

public class Line3D {
	final public static int ROATE_MODE_X=0;
	final public static int ROATE_MODE_Y=1;
	final public static int ROATE_MODE_Z=2;
	
	
	private float View_Range = 3000; // �Ӿ�
	private float View_Horizon = 1500; // ��Ұ
	private float View_Height = 800; // �ӵ�߶�
	private float Screen_DP = 1; // ���ű���
	
	private float Screen_Ox = 100; // ƽ���׼��
	private float Screen_Oy = 100;

	private float Screen_Dx = 0; // ƽ��ƫ����
	private float Screen_Dy = 0;

	private float Space_Dx = 0; // �ռ�ƫ����
	private float Space_Dy = 0;
	private float Space_Dz = 0;

	private boolean EnableRoateX = false; // ������ת�ռ�
	private boolean EnableRoateY = false; // ������ת�ռ�
	private boolean EnableRoateZ = false; // ������ת�ռ�
	
	private float Roate_x = 0; // �ռ���ת����
	private float Roate_y = 0;
	private float Roate_z = 0;

	private float SIN_X=0, COS_X=0;
	private float SIN_Y=0, COS_Y=0;
	private float SIN_Z=0, COS_Z=0;

	private Coord L_Start, L_End; 

	private boolean vr_Mode=false;
	private float vr_eye=120;
	private int screen_W=1920;
	private int screen_H=1080;
	
	public Line3D() {
		L_Start = new Coord();
		L_End = new Coord();
	}
	
	public void setScreenSize(int screenW,int screenH) {
		screen_W=screenW;
		screen_H=screenH;
		Screen_DP =(screen_H/1080f+screen_W/1920f)/2f;
	}
	
	public void setView(float Range, float Horizon, float Height) {
		View_Range = Range;
		View_Horizon = Horizon;
		View_Height = Height;
	}

	public float getViewRange() {
		return View_Range;
	}

	public float getViewHorizon() { 
		return View_Horizon;
	}

	public float getViewHeight() {
		return View_Height;
	}
	
	/************************��������*******************************/
	public void setViewRange(float view_Range) {  //�����Ӿ�
		View_Range = view_Range;
	}

	public void setViewHorizon(float view_Horizon) { //������Ұ��Χ
		View_Horizon = view_Horizon;
	}

	public void setViewHeight(float view_Height) { //�����ӵ�߶ȣ���������0�㣩
		View_Height = view_Height;
	}
	
	public void enableVR(boolean flag) {//����VRģʽ
		vr_Mode=flag;
	}
	
	public void setBasePoint(float x, float y) { // ���û�׼��
		Screen_Ox = x;
		Screen_Oy = y;
	}

	public void setScreenOffset(float x, float y) { // ������Ļƫ����
		Screen_Dx = x;
		Screen_Dy = y;
	}

	public void setSpaceOffset(float x, float y, float z) { // ���ÿռ�ƫ����
		Space_Dx = x ;
		Space_Dy = y;
		Space_Dz = z;
	}

	public void setRoateCenter(float x, float y, float z) { // �����ӽ���ת����
		Roate_x = x;
		Roate_y = y;
		Roate_z = z;
	}
	
	public void setRoateAngle(float angleX,float angleY,float angleZ) { // �����ӽ���ת�Ƕ�
		setRoateAngleX( angleX);
		setRoateAngleY( angleY);
		setRoateAngleZ( angleZ);
	}

	public void setRoateAngleX(float angle) {  // �����ӽ���ת�Ƕ�X
		SIN_X = (float) Math.sin(angle * Math.PI / 180f);
		COS_X = (float) Math.cos(angle * Math.PI / 180f);
	}
	
	public void setRoateAngleY(float angle) { // �����ӽ���ת�Ƕ�Y
		SIN_Y = (float) Math.sin(angle * Math.PI / 180f);
		COS_Y = (float) Math.cos(angle * Math.PI / 180f);
	}
	
	public void setRoateAngleZ(float angle) { // �����ӽ���ת�Ƕ�Z
		SIN_Z = (float) Math.sin(-angle * Math.PI / 180f);
		COS_Z = (float) Math.cos(-angle * Math.PI / 180f);
	}

	public void enableRoate(boolean flag) { // �����ռ���ת
		EnableRoateX = EnableRoateY = EnableRoateZ = flag;
	}

	
	/************************������*******************************/
	public void drawLine(Canvas canvas, Paint paint, Line[] lines,float dH) {  //��ͼ����

		for (int i = 0; i < lines.length; i++) {
			L_Start.copy(lines[i].start);
			L_End.copy(lines[i].end);
			L_Start.z+=dH;
			L_End.z+=dH;
			drawLine(canvas, paint, L_Start, L_End);
		}
	}
	
	public void drawLine(Canvas canvas, Paint paint, Line[] lines) { //��ͼ����
		for (int i = 0; i < lines.length; i++) {
			drawLine(canvas, paint, lines[i].start, lines[i].end);
		}
	}

	public void drawLine(Canvas canvas, Paint paint, Coord a, Coord b) { //��ͼ����
		L_Start.copy(a);
		L_End.copy(b);

		if (EnableRoateX) {
			Roate(L_Start,L_Start,0);
			Roate(L_End,L_End,0);
		}
		if (EnableRoateY) {
			Roate(L_Start,L_Start,1);
			Roate(L_End,L_End,1);
		}
		if (EnableRoateZ) {
			Roate(L_Start,L_Start,2);
			Roate(L_End,L_End,2);
		}
		
		if (L_Start.x + Space_Dx< View_Range && L_End.x + Space_Dx < View_Range) {
			float scale1 = View_Horizon / (View_Range - L_Start.x-Space_Dx);
			float scale2 = View_Horizon / (View_Range - L_End.x-Space_Dx);
			
			if(vr_Mode){ //VRģʽ
				//��������
				float x1=scale1 * ((L_Start.y +Space_Dy)/1.6f - vr_eye/2) + Screen_Ox + Screen_Dx/2;
				float y1=scale1 * (View_Height - (L_Start.z - Space_Dz)/1.6f)  + Screen_Oy + Screen_Dy;
				float x2=scale2 * ((L_End.y + Space_Dy)/1.6f - vr_eye/2)  + Screen_Ox + Screen_Dx/2;
				float y2=scale2 * (View_Height - (L_End.z - Space_Dz)/1.6f)  + Screen_Oy + Screen_Dy;
				if (x1 < screen_W / 2 || x2 < screen_W / 2) {
					float ny = (screen_W / 2 - x1) * (y1 - y2) / (x1 - x2) + y1;
					if (x1 > screen_W / 2 && x2 < screen_W / 2) {
						canvas.drawLine(screen_W / 2*Screen_DP, ny*Screen_DP, x2*Screen_DP, y2*Screen_DP, paint);
					} else if (x2 > screen_W / 2 && x1 < screen_W / 2) {
						canvas.drawLine(screen_W / 2*Screen_DP, ny*Screen_DP, x1*Screen_DP, y1*Screen_DP, paint);
					} else {
						canvas.drawLine(x1*Screen_DP, y1*Screen_DP, x2*Screen_DP, y2*Screen_DP, paint);
					}
				}
				
				//��������
				float x3 = x1 + scale1  * vr_eye + screen_W / 2;
				float y3 = y1;
				float x4 = x2 + scale2  * vr_eye + screen_W / 2;
				float y4 = y2;
				if (x3 > screen_W / 2 || x4 > screen_W / 2) {
					float ny = (screen_W / 2 - x3) * (y3 - y4) / (x3 - x4) + y3;
					if (x3 < screen_W / 2 && x4 > screen_W / 2) {
						canvas.drawLine(screen_W / 2*Screen_DP, ny*Screen_DP, x4*Screen_DP, y4*Screen_DP, paint);
					} else if (x4 < screen_W / 2 && x3 > screen_W / 2) {
						canvas.drawLine(screen_W / 2*Screen_DP, ny*Screen_DP, x3*Screen_DP, y3*Screen_DP, paint);
					} else {
						canvas.drawLine(x3*Screen_DP, y3*Screen_DP, x4*Screen_DP, y4*Screen_DP, paint);
					}
				}
			} else {
				//��VRģʽ
				float x1=scale1 * (L_Start.y + Space_Dy)+ Screen_Ox + Screen_Dx;
				float y1=scale1 * (View_Height - L_Start.z - Space_Dz) + Screen_Oy + Screen_Dy;
				float x2=scale2 * (L_End.y + Space_Dy) + Screen_Ox + Screen_Dx;
				float y2=scale2 * (View_Height - L_End.z - Space_Dz) + Screen_Oy + Screen_Dy;
				canvas.drawLine(x1*Screen_DP,y1*Screen_DP,x2*Screen_DP,y2*Screen_DP,paint);
			}
		}
		
	}
	
	private void Roate(Coord a , Coord b , int mode){ // ��ת�ռ��߶�
		switch (mode) {
		case ROATE_MODE_X:// ��x��
			Roate(a,b,0,Roate_x,Roate_y,Roate_z,SIN_X,COS_X);
			break;
		case ROATE_MODE_Y: // ��y��
			Roate(a,b,1,Roate_x,Roate_y,Roate_z,SIN_Y,COS_Y);
			break;
		case ROATE_MODE_Z:// ��z��;
			Roate(a,b,2,Roate_x,Roate_y,Roate_z,SIN_Z,COS_Z);
			break;
		}
	}
	
	public static void roateLines(Line[] lines, int mode, float x, float y, float z, float angle){ // ��ת�ռ��߶�
		float sin = (float) Math.sin(angle * Math.PI / 180f);
		float cos = (float) Math.cos(angle * Math.PI / 180f);
		for (int i = 0; i < lines.length; i++) {
			Roate(lines[i].start,lines[i].start,mode,x,y,z,sin,cos);
			Roate(lines[i].end,lines[i].end,mode,x,y,z,sin,cos);
		}
	}

	public static void Roate(Coord a, Coord b, int mode, float roate_x, float roate_y, float roate_z, float SIN,
			float COS) { // ��ת�ռ��߶�
		Coord tmp = new Coord();
		switch (mode) {
		case ROATE_MODE_X:// ��x��
			tmp.y = (float) ((a.y - roate_y) * COS + (roate_z - a.z) * SIN) + roate_y;
			tmp.z = (float) ((a.y - roate_y) * SIN - (roate_z - a.z) * COS) + roate_z;
			tmp.x = a.x;
			break;
		case ROATE_MODE_Y: // ��y��
			tmp.x = (float) ((a.x - roate_x) * COS + (roate_z - a.z) * SIN) + roate_x;
			tmp.z = (float) ((a.x - roate_x) * SIN - (roate_z - a.z) * COS) + roate_z;
			tmp.y = a.y;
			break;
		case ROATE_MODE_Z:// ��z��
			tmp.x = (float) ((a.x - roate_x) * COS + (roate_y - a.y) * SIN) + roate_x;
			tmp.y = (float) ((a.x - roate_x) * SIN - (roate_y - a.y) * COS) + roate_y;
			tmp.z = a.z;
			break;
		}
		b.copy(tmp);
	}
	
}
