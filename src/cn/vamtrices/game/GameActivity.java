package cn.vamtrices.game;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import cn.vamtrices.tools.BlockMap;
import cn.vamtrices.tools.GameLogic;
import cn.vamtrices.tools.GameView;

/*
 * 软件1503 杨亚维
 * ID:315202060346
 * 
 * 感谢大佬提供的Handler，解决了大问题！
 * 
 */

public class GameActivity extends Activity implements Runnable {

	private final static int GAME_WIN_TIP = 0;
	private final static int GAME_OVER_TIP = 1;

	private static final String SP_WIN_LEVEL = "Level_Win";
	private static final String SP_PRE_LEVEL = "Level_Pre";

	private Thread th;

	private View gameWinPage;
	private View gameOverPage;
	private View gameLevelPage;
	private View gameHomePage;
	private View gameInfoPage;

	private GameView gameView;
	private Handler mHandler;
	private GameLogic state;

	private SharedPreferences sharedPreferences;
	private Editor editor;

	private TextView text_levelpage;
	private TextView text_nowlevel;

	private boolean runflag = true;
	private boolean freeMode = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_view);
		init();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		runflag = false;
		state.reset();
	}

	public void init() {

		gameView = (GameView) findViewById(R.id.game_view);
		gameOverPage = findViewById(R.id.game_over);
		gameWinPage = findViewById(R.id.game_win);
		gameLevelPage = findViewById(R.id.game_level);
		gameHomePage = findViewById(R.id.game_home);
		gameInfoPage = findViewById(R.id.game_info);

		text_levelpage = (TextView) findViewById(R.id.text_level);
		text_nowlevel = (TextView) findViewById(R.id.text_now_level);

		// ----------------
		sharedPreferences = this.getSharedPreferences("xcalculator", MODE_PRIVATE);
		editor = sharedPreferences.edit();

		// 游戏逻辑模块初始化
		state = new GameLogic(this, sharedPreferences.getInt(SP_PRE_LEVEL, 0));
		gameView.linkData(state);
		gameView.start();

		text_nowlevel.setText(String.valueOf(state.nowLevelID + 1));

		mHandler = new MyHandler();
		th = new Thread(this);
		th.start();

		Toast.makeText(this, "内测体验版，地图数据尚未完善。\r\n正式版即将上线,尽情期待！", Toast.LENGTH_LONG).show();
	}

	// 关闭所有页面
	private void closeAllPage() {
		gameWinPage.setVisibility(View.GONE);
		gameOverPage.setVisibility(View.GONE);
		gameHomePage.setVisibility(View.GONE);
		gameLevelPage.setVisibility(View.GONE);
	}

	// BUTTON: VR模式
	public void btnVR(View view) {
		state.vrMode = !state.vrMode;
		if (state.vrMode) {
			Toast.makeText(this, "VR模式处于开发阶段，暂时只支持手柄操作，旋转头部可控制视角。", Toast.LENGTH_LONG).show();
		}
	}

	// BUTTON: 游戏开始
	public void btnStart(View view) {
		closeAllPage();
		gameInfoPage.setVisibility(View.VISIBLE);
		state.start();
	}

	// BUTTON: 游戏结束
	public void btnExit(View view) {
		closeAllPage();
		runflag = false;
	}

	// BUTTON: 重新开始
	public void btnRestart(View view) {
		closeAllPage();
		gameInfoPage.setVisibility(View.VISIBLE);
		state.restart();
	}

	// BUTTON: 下一关
	public void btnNext(View view) {
		closeAllPage();
		gameInfoPage.setVisibility(View.VISIBLE);
		state.gameWinTip = false;
		state.selectLevel(state.nowLevelID + 1);
		text_nowlevel.setText(String.valueOf(state.nowLevelID + 1));
		state.restart();
		editor.putInt(SP_PRE_LEVEL, state.nowLevelID);
		editor.commit();
	}

	// BUTTON: 关卡选择页面
	public void btnLevel(View view) {
		closeAllPage();
		text_levelpage.setText(state.nowLevelID + 1 + "/" + state.blockMap.size);
		gameLevelPage.setVisibility(View.VISIBLE);
	}

	// BUTTON: 选择当前关卡
	public void btnLevelSelect(View view) {
		closeAllPage();
		gameInfoPage.setVisibility(View.VISIBLE);
		text_nowlevel.setText(String.valueOf(state.nowLevelID + 1));
		state.selectLevel(state.nowLevelID);
		state.restart();
		editor.putInt(SP_PRE_LEVEL, state.nowLevelID);
		editor.commit();
	}

	// 新增功能：点击某一关时加载那一关的地图
	// BUTTON: 选择关卡+
	public void btnLevelNext(View view) {
		if (state.nowLevelID < sharedPreferences.getInt(SP_WIN_LEVEL, 0)||freeMode) {
			if (state.nowLevelID + 1 >= state.blockMap.size) {
				return;
			}
			state.nowLevelID++;
			text_levelpage.setText(state.nowLevelID + 1 + "/" + state.blockMap.size);
		}
	}

	// BUTTON: 选择关卡-
	public void btnLevelPre(View view) {
		if (state.nowLevelID == 0) {
			return;
		}
		state.nowLevelID--;
		text_levelpage.setText(state.nowLevelID + 1 + "/" + state.blockMap.size);
	}

	// BUTTON: 自由模式
	public void btnFree(View view) {
		freeMode = !freeMode;
	}

	// BUTTON: 游戏帮助
	public void btnHelp(View view) {
		// TO-DO 未完成
	}

	@SuppressLint("HandlerLeak")
	private class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GAME_WIN_TIP:
				gameInfoPage.setVisibility(View.GONE);
				gameWinPage.setVisibility(View.VISIBLE);
				int win_level = sharedPreferences.getInt(SP_WIN_LEVEL, 0);
				if (state.nowLevelID + 1 > win_level) {
					editor.putInt(SP_WIN_LEVEL, state.nowLevelID + 1);
					editor.putInt(SP_PRE_LEVEL, state.nowLevelID + 1);
					editor.commit();
				}
				state.gameWinTip = false;
				break;
			case GAME_OVER_TIP:
				gameInfoPage.setVisibility(View.GONE);
				gameOverPage.setVisibility(View.VISIBLE);
				state.gameOverTip = false;
				break;
			}
		}
	}

	// 后台UI处理线程
	@Override
	public void run() {
		while (runflag) {
			if (state.gameWinTip) {
				mHandler.sendMessage(mHandler.obtainMessage(GAME_WIN_TIP));
			} else if (state.gameOverTip) {
				mHandler.sendMessage(mHandler.obtainMessage(GAME_OVER_TIP));
			}

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		finish();
	}
}
