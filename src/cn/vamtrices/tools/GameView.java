package cn.vamtrices.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

/*
 * 游戏绘图核心模块，触摸及传感器处理。
 * 
 */

public class GameView extends SurfaceView implements Callback, Runnable, SensorEventListener {
	private SurfaceHolder sfh;
	private Thread th;
	private Paint paint;
	private Canvas canvas;
	private Sensor accelerometerSensor;

	private boolean flag;
	private int screenW, screenH;

	private Line3D l3d;
	private GameLogic glc;

	private float TouchX;
	private float TouchY;

	private Line[] mapLines;

	public void linkData(GameLogic glc) { // 绑定游戏逻辑模块
		this.glc = glc;
	}

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);

		sfh = this.getHolder();
		sfh.addCallback(this);
		setFocusable(true);

		paint = new Paint();
		paint.setAntiAlias(true);
		l3d = new Line3D();
		mapLines = new Line[12];
		Line.init(mapLines);

		// 传感器初始化
		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_GAME);
		sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
	}

	public GameView(Context context) {
		super(context);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		screenW = this.getWidth();
		screenH = this.getHeight();
		l3d.setScreenSize(screenW, screenH); // 等比例适配
		l3d.setBasePoint(screenW / 4f, 0); // 设置绘图基准点
		l3d.enableRoate(true); // 开启视角旋转

		flag = true; 
		th = new Thread(this); 
		th.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		screenW = this.getWidth();
		screenH = this.getHeight();
		l3d.setScreenSize(screenW, screenH); // 等比例适配
		l3d.setBasePoint(screenW / 4f, 0); // 设置绘图基准点
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		flag = false;
	}

	/************************************ 绘制部分开始 ******************************************/
	public void autoView() { // 动态视角
		l3d.setViewRange(glc.rg);
		l3d.setViewHeight(glc.hg);
		l3d.setRoateCenter(glc.blockMap.H * BaseData.BlockSize / 2, glc.blockMap.W * BaseData.BlockSize / 2, 0);

		if (glc.vrMode) {
			l3d.setViewHeight(500);
			l3d.setBasePoint(screenW / 4f, 0);
			l3d.setSpaceOffset(0, 0, 0);
			l3d.setRoateAngle(0, 0, anglex + glc.ag);
		} else {
			l3d.setBasePoint(screenW / 4f, -100);
			l3d.setSpaceOffset(-glc.OffsetX - glc.OffsetY / 4, -glc.OffsetY / 1.4f, 0);
			l3d.setRoateAngle(0, 0, glc.ag);
		}

	}

	private void drawMyBlock() { // 绘制主方块
		if (glc.block.visibility) {
			paint.setStrokeWidth(3);
			paint.setColor(Color.WHITE);
			l3d.drawLine(canvas, paint, glc.block.lines);
		}
	}

	public void drawMap() { // 绘制地图
		float mapH = 0;
		glc.mapColor += 0.005;
		paint.setStrokeWidth(1);
		for (int j = 0; j < glc.blockMap.H; j++) {
			for (int i = 0; i < glc.blockMap.W; i++) {
				if (glc.blockMap.map[j][i] > 0) {

					if (glc.mapHeight < 5) { // 地图升降效果
						mapH = 200 * (i + j) + glc.mapHeight;
						if (mapH > 0)
							mapH = 0;
					}
					if (glc.gameWin)
						mapH = -mapH;

					float dc = (float) (i + j) / (glc.blockMap.W + glc.blockMap.H) + glc.mapColor; // 地图渐变色
					while (dc > 1)
						dc -= 1;

					if (glc.blockMap.EX == i && glc.blockMap.EY == j) {
						paint.setStrokeWidth(2);
						paint.setColor(glc.xColor(glc.goalBlockColor, 50, 255));
						l3d.drawLine(canvas, paint, glc.goalBlock, mapH);
						paint.setStrokeWidth(1);
					} else {
						paint.setColor(glc.xColor(dc, 120, 255));
						Block.setBlock(mapLines, j * BaseData.BlockSize + BaseData.BlockSpace,
								i * BaseData.BlockSize + BaseData.BlockSpace, -BaseData.BlockHeight + mapH,
								BaseData.BlockSize - 2 * BaseData.BlockSpace,
								BaseData.BlockSize - 2 * BaseData.BlockSpace, BaseData.BlockHeight);
						l3d.drawLine(canvas, paint, mapLines);
					}

				}
			}
		}
	}

	private void vrMode() { //VR模式
		l3d.enableVR(glc.vrMode);
		if (glc.vrMode) {
			l3d.setScreenOffset(-600, 0);
			paint.setColor(Color.WHITE);
			paint.setStrokeWidth(1);
			canvas.drawLine(screenW / 2, 0, screenW / 2, screenH, paint);
		} else {
			l3d.setScreenOffset(0, 0);
		}

	}

	private long pre_time = 0;

	public void myDraw() { // 绘图模块
		long now_time = System.currentTimeMillis();
		if (now_time - pre_time > 25) {
			pre_time = now_time;
			try {
				canvas = sfh.lockCanvas();
				if (canvas != null) {
					canvas.drawColor(Color.BLACK);
					autoView();
					drawMap();
					drawMyBlock();
					vrMode();
				}
			} catch (Exception e) {
				// TODO: handle exception
			} finally {
				if (canvas != null) {
					sfh.unlockCanvasAndPost(canvas);
				}
			}
		}
	}

	/************************************ 绘制部分结束 ******************************************/

	private long runtime_start = 0;

	@Override
	public void run() { //后台刷新线程
		while (flag) {
			long runtime_end = System.currentTimeMillis();
			if (runtime_end - runtime_start > 25 && ready) {
				runtime_start = runtime_end;
				myDraw();
				glc.logic();
			}
		}
	}

	private boolean ready = false;

	public void start() { //启动预备
		ready = true;
	}

	/***************************************** 事件监听部分 **********************************************/

	// 按键事件监听
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (glc.controllable()) {
			if (event.getRepeatCount() == 0) {
				if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
					glc.roateMode = BaseData.ROATE_RIGHT;
					glc.mapOffsetLogic();
					glc.isRoating = true;
				} else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
					glc.roateMode = BaseData.ROATE_LEFT;
					glc.mapOffsetLogic();
					glc.isRoating = true;
				} else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
					glc.roateMode = BaseData.ROATE_DOWN;
					glc.mapOffsetLogic();
					glc.isRoating = true;
				} else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
					glc.roateMode = BaseData.ROATE_UP;
					glc.mapOffsetLogic();
					glc.isRoating = true;
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	// 触屏事件监听
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!glc.gameOver && !glc.vrMode) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				TouchX = event.getX();
				TouchY = event.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				if (glc.controllable()) {
					float x = event.getX() - TouchX;
					float y = event.getY() - TouchY;
					if ((Math.abs(x) > 50 || Math.abs(y) > 50)) {
						if (y < 0 && -y > Math.abs(x)) {
							glc.roateMode = 0;
						} else if (y > 0 && y > Math.abs(x)) {
							glc.roateMode = 1;
						} else if (x < 0 && -x > Math.abs(y)) {
							glc.roateMode = 2;
						} else {
							glc.roateMode = 3;
						}
						glc.mapOffsetLogic();
						glc.isRoating = true;
					}
				}
				break;
			}
		}
		return true;
	}

	// 传感器监听
	private SensorManager sensorManager;
	private Sensor gyroscopeSensor;

	private static final float NS2S = 1.0f / 1000000000.0f;
	private float timestamp;
	private float angle[] = new float[3];

	private float anglex = 0;
	private float angley = 0;
	private float anglez = 0;

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
			if (timestamp != 0) {
				final float dT = (event.timestamp - timestamp) * NS2S;
				angle[0] += event.values[0] * dT;
				angle[1] += event.values[1] * dT;
				angle[2] += event.values[2] * dT;
				anglex = (float) Math.toDegrees(angle[0]);
				angley = (float) Math.toDegrees(angle[1]);
				anglez = (float) Math.toDegrees(angle[2]);
				gyroscopeSensor.getMinDelay();
			}
			timestamp = event.timestamp;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}

}