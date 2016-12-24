package cn.vamtrices.tools;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import android.content.Context;

/*
 * 地图类，提供存储读取本地地图信息、切换地图等方法
 * 
 */

public class BlockMap {
	
	private class mapType {
		public int[][] map;
		public int W, H;
		public int SX, SY;
		public int EX, EY;
	}

	private ArrayList<mapType> mapList;

	public int[][] map;
	public int W, H;
	public int SX, SY;
	public int EX, EY;
	public int size;

	public BlockMap(Context context) {  //初始化，载入地图
		mapList = new ArrayList<mapType>();
		InputStream mapFile = null;
		try {
			mapFile = context.getAssets().open("map.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
		Scanner scanner = new Scanner(mapFile, "UTF-8");
		while (scanner.hasNext()) {
			if (scanner.next().charAt(0) == '@') {
				mapType newMap = new mapType();
				newMap.W = scanner.nextInt();
				newMap.H = scanner.nextInt();
				newMap.SX = scanner.nextInt()-1;
				newMap.SY = scanner.nextInt()-1;
				newMap.EX = scanner.nextInt()-1;
				newMap.EY = scanner.nextInt()-1;
				newMap.map = new int[newMap.H][newMap.W];
				for (int i = 0; i < newMap.H; i++) {
					for (int j = 0; j < newMap.W; j++) {
						newMap.map[i][j] = scanner.nextInt();
					}
				}
				System.out.println(newMap.W+" "+newMap.H+" "+newMap.SX+" "+newMap.SY+" "+newMap.EX+" "+newMap.EY);
				mapList.add(newMap);
			}
		}
		scanner.close();
		try {
			mapFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		selectMap(0);
		size=mapList.size();
	}

	public Boolean selectMap(int ID) { //切换地图
		if (ID < mapList.size()) {
			mapType smap = mapList.get(ID);
			this.W = smap.W;
			this.H = smap.H;
			this.SX = smap.SX;
			this.SY = smap.SY;
			this.EX = smap.EX;
			this.EY = smap.EY;
			this.map = smap.map;
			return true;
		}
		return false;
	}
}
