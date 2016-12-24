package cn.vamtrices.tools;

import cn.vamtrices.game.R;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;

/*
 * 游戏逻辑处理核心。
 * 
 */

public class GameLogic extends BaseData {

	public Line[] goalBlock;
	public BlockMap blockMap;
	
	public boolean isOK = false;
	public boolean vrMode = false;
	public boolean isRoating = false;
	public int roateMode = 0;
	public float roateAngle = 0;
	
	public float OffSetStepX;
	public float OffSetStepY;
	public float OffsetX = 0;
	public float OffsetY = 0;
	public float OffsetEndY;
	public float OffsetEndX;
	
	public boolean isDrop = false;
	public boolean gameOver = false;
	public int dropRoateX;
	public int dropRoateY;
	
	public boolean IsdropRoate;
	public int dropRoateMode;
	public boolean isdropRoate = false;
	public boolean isShowOver = false;
	public float goalBlockColor = 0;
	
	public Block block;
	
	public float mapHeight = 0;
	public float mapColor = 0;
	
	public float rg = 0;
	public float hg = 0;
	public float ag = 0;
	
	public boolean gameWin = false;
	public boolean gameStart = false;
	public boolean isBlockPut = false;
	public boolean isViewRoate=false;
	private boolean readyStart = false;
	public boolean animeMode = true;
	
	private SoundPool audio;
	private  int audio_res;
	
	public boolean gameOverTip = false; // 游戏结束提示标记
	public boolean gameWinTip = false; // 游戏结束提示标记
	public boolean gameEndRoate = true; // 游戏结束提示标记
	private boolean isChangeLevel = false; // 标记
	public int nowLevelID = 0;
	
	public GameLogic(Context context, int ID) {
		blockMap = new BlockMap(context);
		blockMap.selectMap(ID);
		nowLevelID=ID;
		
		audio=new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		audio_res=audio.load(context, R.raw.tick,1);
		initBlock();
	}

	private void initBlock() { //初始化
		goalBlock = Block.creatBlock(BlockSize * blockMap.EY + 30, BlockSize * blockMap.EX + 30, 40, BlockSize - 60,
				BlockSize - 60, BlockSize - 60);
		block = new Block(BlockSize);
		//block.creat(blockMap.SY, blockMap.SX, 0);
		Block.setBlock(block.lines, blockMap.SY * BlockSize, blockMap.SX * BlockSize, 1250, BlockSize,
				BlockSize, BlockSize * 2);
		// OffsetX = blockMap.SY*BlockSize;
		// OffsetY = blockMap.SY*BlockSize;
	}

	public void reset() { //重置游戏
		block.creat(blockMap.SY, blockMap.SX, 0);
		resetOffset();
		isOK = false;
		vrMode = false;
		isRoating = false;
		roateMode = 0;
		roateAngle = 0;
		isDrop = false;
		gameOver = false;
		dropRoateX = 0;
		dropRoateY = 0;
		IsdropRoate = false;
		dropRoateMode = 0;
		isdropRoate = false;
		isShowOver = false;
	}

	public void restart() { //重新开始游戏
		gameWinTip = false;
		gameOverTip = false;
		gameStart = true;
		//resetOffset();
	}
	
	public void start(){ //开始游戏
		gameEndRoate=false;
		animeMode=false;
		isBlockPut=true;
	}
	
	private void resetOffset(){ //重置地图偏移
		OffsetX = 0;
		OffsetY = 0;
		OffsetEndY = 0;
		OffsetEndX = 0;
	}

	public int xColor(float index, int min, int max) { // 连续渐变色生成器
		if (max > 255 || min > 255 || max < 0 || min < 0 || min >= max)
			return Color.BLACK;
		int d = max - min;

		int n = (int) (3f * d * index);
		if (n < d) {
			return Color.rgb(d - n + min, n + min, min);
		}
		if (n < d * 2) {
			n -= d;
			return Color.rgb(min, d - n + min, n + min);
		}
		if (n < d * 3) {
			n -= 2 * d;
			return Color.rgb(n + min, min, d - n + min);
		}
		return xColor(0, min, max);
	}

	private boolean isInside(int x, int y) { // 判断方块示是否越界
		if (x < 0 || x >= blockMap.H || y < 0 || y >= blockMap.W || blockMap.map[x][y] == 0) {
			return false;
		}
		return true;
	}

	void dropSet() { // 下落判断
		switch (block.style) {
		case 0:
			if (isInside(block.x, block.y))
				return;
			switch (roateMode) {
			case ROATE_UP:
				dropRoateX = block.x;
				break;
			case ROATE_DOWN:
				dropRoateX = block.x + 1;
				break;
			case ROATE_LEFT:
				dropRoateY = block.y;
				break;
			case ROATE_RIGHT:
				dropRoateY = block.y + 1;
				break;
			}
			isdropRoate = true;
			gameOver = true;
			dropRoateMode = roateMode;
			break;
		case 1:
			if (isInside(block.x + 1, block.y) && isInside(block.x, block.y))
				return;

			if (isInside(block.x, block.y) && !isInside(block.x + 1, block.y)) {
				dropRoateX = block.x + 1;
				dropRoateMode = ROATE_UP;
			} else if (!isInside(block.x, block.y) && isInside(block.x + 1, block.y)) {
				dropRoateX = block.x + 1;
				dropRoateMode = ROATE_DOWN;

			} else {
				dropRoateMode = roateMode;
				switch (roateMode) {
				case ROATE_UP:
					dropRoateX = block.x;
					break;
				case ROATE_DOWN:
					dropRoateX = block.x + 2;
					break;
				case ROATE_LEFT:
					dropRoateY = block.y;
					break;
				case ROATE_RIGHT:
					dropRoateY = block.y + 1;
					break;
				}
			}
			gameOver = true;
			isdropRoate = true;
			break;
		case 2:
			if (isInside(block.x, block.y) && isInside(block.x, block.y + 1))
				return;

			if (isInside(block.x, block.y) && !isInside(block.x, block.y + 1)) {
				dropRoateY = block.y + 1;
				dropRoateMode = ROATE_LEFT;
				IsdropRoate = true;
			} else if (!isInside(block.x, block.y) && isInside(block.x, block.y + 1)) {
				dropRoateY = block.y + 1;
				dropRoateMode = ROATE_RIGHT;
				IsdropRoate = true;

			} else {
				dropRoateMode = roateMode;
				switch (roateMode) {
				case ROATE_UP:
					dropRoateX = block.x;
					break;
				case ROATE_DOWN:
					dropRoateX = block.x + 1;
					break;
				case ROATE_LEFT:
					dropRoateY = block.y;
					break;
				case ROATE_RIGHT:
					dropRoateY = block.y + 2;
					break;
				}
			}
			gameOver = true;
			isdropRoate = true;
			break;
		}
	}

	private void moveBlock(Line[] lines, int step) { //移动方块
		for (int i = 0; i < lines.length; i++) {
			lines[i].start.z += step;
			lines[i].end.z += step;
		}
	}

	public boolean controllable() { //判断当前状态是否允许操作方块

		if (isRoating || isDrop || isBlockPut || readyStart ||animeMode ||isViewRoate) {
			return false;
		}
		return true;

	}

	public void home() {

	}

	private void gameAnimeLogic() { // 游戏过场逻辑判断

		if (isDrop) {
			Coord mid = block.getMidPoint();
			switch (dropRoateMode) {
			case 0:
				Line3D.roateLines(block.lines, Line3D.ROATE_MODE_Y, mid.x, mid.y, mid.z, ROATE_STEP * 1.2f);
				break;
			case 1:
				Line3D.roateLines(block.lines, Line3D.ROATE_MODE_Y, mid.x, mid.y, mid.z, -ROATE_STEP * 1.2f);
				break;
			case 2:
				Line3D.roateLines(block.lines, Line3D.ROATE_MODE_X, mid.x, mid.y, mid.z, ROATE_STEP * 1.2f);
				break;
			case 3:
				Line3D.roateLines(block.lines, Line3D.ROATE_MODE_X, mid.x, mid.y, mid.z, -ROATE_STEP * 1.2f);
				break;
			}

			moveBlock(block.lines, -40);
			if (block.getPosZ() < -1000) {
				if (gameOver) {
					gameOverTip = true;
				} else {
					gameWinTip = true;
				}
			}

			if (block.getPosZ() < -1600) {
				isDrop = false;
				gameEndRoate = true;
				block.visibility = false;
			}
		}

		if (gameEndRoate) {
			ag = (ag + 0.7f) % 360;
			if (rg < 2000)
				rg += 10;
		} else {
			if (ag < 390){
				isViewRoate=true;
				ag += 2;
			} else{
				isViewRoate=false;
			}
		}

		if (gameStart) {
			mapHeight -= 36;
			if (mapHeight < -200 * (blockMap.W + blockMap.H) - 50) {
				if (isChangeLevel) {
					blockMap.selectMap(nowLevelID);
					mapHeight = -200 * (blockMap.W + blockMap.H) - 50;
				}
				resetOffset();
				initBlock();
				Block.setBlock(block.lines, blockMap.SY * BlockSize, blockMap.SX * BlockSize, 1250, BlockSize,
						BlockSize, BlockSize * 2);
				block.visibility = true;
				ag = 45;
				isOK = true;
				gameStart = false;
				gameWin = false;
				gameOver = false;
				readyStart = true;
				gameEndRoate = false;
			}
		} else {
			if (rg < 1800)
				rg += 10;
			if (mapHeight > 0) {
				if (!gameEndRoate && rg > 1800 && mapHeight > 0)
					rg -= 1;
				if (isOK){
					if(ag>340){
						isBlockPut = true;
					}
				}
					
			}

		}

		if (isBlockPut) {
			moveBlock(block.lines, -45);
			if (block.getPosZ() <= 0) {
				block.creat(blockMap.SY, blockMap.SX, 0);
				roateAngle = 0;
				isBlockPut = false;
				isOK = false;
				readyStart = false;
			}
		}

		if (hg < 800)
			hg += 7;
		if (!gameStart && mapHeight < 5)
			mapHeight += 30;
		goalBlockColor += 0.001;
		goalBlockColor = (goalBlockColor > 1) ? 0 : goalBlockColor;
	}

	void mapOffsetLogic() { // 地图动态偏移判断
		switch (roateMode) {
		case 0:
			if (block.style == 0) {
				OffsetEndX = (block.x - 2) * BlockSize;
				OffsetEndY = (block.y) * BlockSize;
			} else {
				OffsetEndX = (block.x - 1) * BlockSize;
				OffsetEndY = (block.y) * BlockSize;
			}
			break;
		case 1:
			if (block.style == 1) {
				OffsetEndX = (block.x + 2) * BlockSize;
				OffsetEndY = (block.y) * BlockSize;
				OffSetStepX = OFFSET_STEP * 2;
				OffSetStepY = 0;
			} else {
				OffsetEndX = (block.x + 1) * BlockSize;
				OffsetEndY = (block.y) * BlockSize;
			}
			break;
		case 2:
			if (block.style == 0) {
				OffsetEndX = (block.x) * BlockSize;
				OffsetEndY = (block.y - 2) * BlockSize;
			} else {
				OffsetEndX = (block.x) * BlockSize;
				OffsetEndY = (block.y - 1) * BlockSize;
			}
			break;
		case 3:
			if (block.style == 2) {
				OffsetEndX = (block.x) * BlockSize;
				OffsetEndY = (block.y + 2) * BlockSize;
			} else {
				OffsetEndX = (block.x) * BlockSize;
				OffsetEndY = (block.y + 1) * BlockSize;
			}
			break;
		}
	}

	public void logic() { // 主逻辑判断

		Line3D.roateLines(goalBlock, Line3D.ROATE_MODE_X, BlockSize * blockMap.EY + BlockSize / 2,
				BlockSize * blockMap.EX + BlockSize / 2, BlockSize / 2 + 40, 1);
		Line3D.roateLines(goalBlock, Line3D.ROATE_MODE_Y, BlockSize * blockMap.EY + BlockSize / 2,
				BlockSize * blockMap.EX + BlockSize / 2, BlockSize / 2 + 40, 1);
		Line3D.roateLines(goalBlock, Line3D.ROATE_MODE_Z, BlockSize * blockMap.EY + BlockSize / 2,
				BlockSize * blockMap.EX + BlockSize / 2, BlockSize / 2 + 40, -1);

		gameAnimeLogic();

		if (isRoating) {
			switch (roateMode) {
			case 0:
				if (gameOver) {
					Line3D.roateLines(block.lines, Line3D.ROATE_MODE_Y, dropRoateX * BlockSize, dropRoateY * BlockSize,
							0, ROATE_STEP);
				} else {
					Line3D.roateLines(block.lines, Line3D.ROATE_MODE_Y, block.x * BlockSize, block.y * BlockSize, 0,
							ROATE_STEP);
				}
				roateAngle += ROATE_STEP;
				break;
			case 1:
				if (gameOver) {
					Line3D.roateLines(block.lines, Line3D.ROATE_MODE_Y, dropRoateX * BlockSize, dropRoateY * BlockSize,
							0, -ROATE_STEP);
				} else {
					Line3D.roateLines(block.lines, Line3D.ROATE_MODE_Y,
							(block.x + (block.style == 1 ? 2 : 1)) * BlockSize, block.y * BlockSize, 0, -ROATE_STEP);
				}
				roateAngle -= ROATE_STEP;
				break;
			case 2:
				if (gameOver) {
					Line3D.roateLines(block.lines, Line3D.ROATE_MODE_X, dropRoateX * BlockSize, dropRoateY * BlockSize,
							0, ROATE_STEP);
				} else {
					Line3D.roateLines(block.lines, Line3D.ROATE_MODE_X, block.x * BlockSize, block.y * BlockSize, 0,
							ROATE_STEP);
				}
				roateAngle += ROATE_STEP;
				break;
			case 3:
				if (gameOver) {
					Line3D.roateLines(block.lines, Line3D.ROATE_MODE_X, dropRoateX * BlockSize, dropRoateY * BlockSize,
							0, -ROATE_STEP);
				} else {
					Line3D.roateLines(block.lines, Line3D.ROATE_MODE_X, block.x * BlockSize,
							(block.y + (block.style == 2 ? 2 : 1)) * BlockSize, 0, -ROATE_STEP);
				}
				roateAngle += ROATE_STEP;
				break;
			}
		}

		if (isRoating) {
			if (Math.abs(roateAngle) >= 90) {
				if (isdropRoate) {
					isDrop = true;
					isRoating = false;
					isdropRoate = false;
				} else {
					switch (roateMode) {
					case 0:
						if (block.style == 0) {
							block.creat(block.x - 2, block.y, 1);
						} else if (block.style == 1) {
							block.creat(block.x - 1, block.y, 0);
						} else if (block.style == 2) {
							block.creat(block.x - 1, block.y, 2);
						}
						break;
					case 1:
						if (block.style == 0) {
							block.creat(block.x + 1, block.y, 1);
						} else if (block.style == 1) {
							block.creat(block.x + 2, block.y, 0);
						} else if (block.style == 2) {
							block.creat(block.x + 1, block.y, 2);
						}
						break;
					case 2:
						if (block.style == 0) {
							block.creat(block.x, block.y - 2, 2);
						} else if (block.style == 1) {
							block.creat(block.x, block.y - 1, 1);
						} else if (block.style == 2) {
							block.creat(block.x, block.y - 1, 0);
						}
						break;
					case 3:
						if (block.style == 0) {
							block.creat(block.x, block.y + 1, 2);
						} else if (block.style == 1) {
							block.creat(block.x, block.y + 1, 1);
						} else if (block.style == 2) {
							block.creat(block.x, block.y + 2, 0);
						}
						break;
					}
					roateAngle = 0;
					isRoating = false;
					dropSet();
					if (block.style == 0 && block.x == blockMap.EY && block.y == blockMap.EX) {
						isDrop = true;
						roateMode = -1;
						dropRoateMode = -1;
						gameWin = true;
					}

					if (isdropRoate) {
						roateMode = dropRoateMode;
						isRoating = true;
						audio.play(audio_res, 0.3f, 0.3f, 1, 0, 1f);
					} else {
						audio.play(audio_res, 1f, 1f, 1, 0, 1f);
					}
				}
			}
		}
		if (OffsetX < OffsetEndX) {
			OffsetX += OFFSET_STEP;
			if (OffsetX > OffsetEndX) {
				OffsetX = OffsetEndX;
			}
		}
		if (OffsetX > OffsetEndX) {
			OffsetX -= OFFSET_STEP;
			if (OffsetX < OffsetEndX) {
				OffsetX = OffsetEndX;
			}
		}
		if (OffsetY < OffsetEndY) {
			OffsetY += OFFSET_STEP;
			if (OffsetY > OffsetEndY) {
				OffsetY = OffsetEndY;
			}
		}
		if (OffsetY > OffsetEndY) {
			OffsetY -= OFFSET_STEP;
			if (OffsetY < OffsetEndY) {
				OffsetY = OffsetEndY;
			}
		}
	}

	public void selectLevel(int ID) { //选择关卡
		animeMode=false;
		isChangeLevel = true;
		nowLevelID = ID;
		if(nowLevelID>=blockMap.size){
			nowLevelID=blockMap.size-1;
		}
	}

}
