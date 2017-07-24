package train;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class Liaoning_SegProj {

	private BufferedImage sourceImage;
	
	/**
	 * 读取图像，进行切割
	 */
	private static void run(){
		File dir = new File("liaoning_gray/");
		//鍙垪鍑簀pg
		File[] files = dir.listFiles(new FilenameFilter() {
			
			public boolean isJpg(String file){   
			    if (file.toLowerCase().endsWith(".jpg")){   
			      return true;   
			    }else{   
			      return false;   
			    }   
			}
			
			@Override
			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				return isJpg(name);
			}
		});
		
		Liaoning_SegProj model = new Liaoning_SegProj();
		
		for (int i=0; i<files.length; i++) {
			try {
				File file = files[i];
				BufferedImage img = ImageIO.read(file);
				
				int[] xproj = model.project(img,"X");
				LinkedHashMap<Integer,Integer> xchar = charlocation(xproj);
				System.out.println(file.getName().split("\\.")[0]+ " "+ xchar);
				ArrayList<BufferedImage> charlist = new ArrayList<BufferedImage>();
				
				for (Integer key : xchar.keySet()) {
					if(key<10 && xchar.get(key)<10 && xchar.get(key)>4){//
						charlist.add(img.getSubimage(6, 9, 8, 13));
						charlist.add(img.getSubimage(22, 7, 16, 16));
						charlist.add(img.getSubimage(39, 10, 9, 13));
					}else if(key>10 && xchar.get(key)<10 && xchar.get(key)>4){
						charlist.add(img.getSubimage(10, 9, 9, 13));
						charlist.add(img.getSubimage(31, 9, 9, 13));
						charlist.add(img.getSubimage(51, 9, 9, 13));
						charlist.add(img.getSubimage(71, 9, 9, 13));
					}else{
							System.out.println("the img is Chinese characters");
							//break;
					}
						
					break;
				}
				for(int j=0; j<charlist.size(); j++){
					BufferedImage subImg = charlist.get(j);
					String prex = file.getName().split("\\.")[0];
					String filename = "liaoning_cfs/" + prex + "-" + j + ".jpg";
					ImageIO.write(subImg, "JPG", new File(filename));
				}
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private boolean isBlack(int rgb) {
		Color color = new Color(rgb);
		if (color.getRed() + color.getGreen() + color.getBlue() <= 300) {
			return true;
		}
		return false;
	}
	
	//根据投影统计各方向上的像素
	public int[] project(BufferedImage sourceImage,String direction){
		//this.imageList.clear();
		this.sourceImage = sourceImage;
		
		int width = sourceImage.getWidth();
		int height = sourceImage.getHeight();
		
		if(direction == "X"){
			int[] histData = new int[width];
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					if (!isBlack(sourceImage.getRGB(x, y))) {
						histData[x]++;
					}
				}
			}
			return histData;
		}
		else{
			int[] histData = new int[height];
			for (int x = 0; x < height; x++) {
				for (int y = 0; y < width; y++) {
					if (!isBlack(sourceImage.getRGB(y, x))) {
						histData[x]++;
					}
				}
			}
			return histData;
		}
	}
	
	//按顺序保存字符位置
	public static LinkedHashMap<Integer,Integer> charlocation(int[] histData){
		
		LinkedHashMap<Integer,Integer> lhsMap = new LinkedHashMap<Integer,Integer>(); 
		//int chchar = 1;
		for(int j=0;j<histData.length-1;){
			  int cwidth = 1;
			  if(histData[j] == 0){
				  j = j + 1;
				  continue;
			  }
			  else{
				  while( j+cwidth< histData.length-1 & histData[j+cwidth] != 0){
					  cwidth=cwidth+1;
				  }
				  lhsMap.put(j, cwidth);
			  }
			  j = j + cwidth;
		  }
		return lhsMap;
	}
	/**
	 * 二值化
	 * @param sourceImage
	 * @return 二值化之后的图像
	 */
	public BufferedImage getBinaryImage(BufferedImage sourceImage){
		double Wr = 30;//0.299
		double Wg = 59;//0.587
		double Wb = 11;//0.114
		
		int width = sourceImage.getWidth();
		int height = sourceImage.getHeight();
		int[][] gray = new int[width][height];
		
		//灰度化
		float[] hsv = new float[]{0,0,0};
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				Color color = new Color(sourceImage.getRGB(x, y));
				int rgb = (int) ((color.getRed()*Wr + color.getGreen()*Wg + color.getBlue()*Wb) / 100);
				gray[x][y] = rgb;
			}
		}
		/*
		//打印矩阵
		for(int i = 0; i < width;i++){
			for(int j = 0;j < height; j++){
				System.out.print(gray[i][j]+" ");
			}
			System.out.print("\n");
		}
		*/
		BufferedImage binaryBufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
		//二值化
		int threshold = getOstu(gray, width, height);//140
		//int threshold = 180;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (gray[x][y] > threshold) {
					int max = new Color(255, 255, 255).getRGB();
					gray[x][y] = max;
				}else{
					int min = new Color(0, 0, 0).getRGB();
					gray[x][y] = min;
				}
				
				binaryBufferedImage.setRGB(x, y, gray[x][y]);
			}
		}
		
		return binaryBufferedImage;
	}
	
	/**
	 * 获得二值化图像
	 * 最大类间方法差
	 * @param gray
	 * @param width
	 * @param height
	 */
	private int getOstu(int[][] gray, int width, int height){
		int grayLevel = 256;
		int[] pixelNum = new int[grayLevel];
		//计算所有色阶的直方图
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int color = gray[x][y];
				pixelNum[color] ++;
			}
		}
		
		double sum = 0;
		int total = 0;
		for (int i = 0; i < grayLevel; i++) {
			sum += i*pixelNum[i]; //x*f(x)质量距，也就是每一个灰度的值乘以其点数（归一化后为概率），sum为其总和
			total += pixelNum[i]; //n为图像总的点数，归一化就是累积概率
		}
		double sumB = 0;//前景色质矩总和
		int threshold = 0;
		double wF = 0;//前景色权重
		double wB = 0;//背景色权重
		
		double maxFreq = -1.0;//最大类间方差
		
		for (int i = 0; i < grayLevel; i++) {
			wB += pixelNum[i]; //wB为当前阈值背景图像的点数
			if (wB == 0) { //没有分出前后景
				continue;
			}
			
			wF = total - wB; //wB涓哄湪褰撳墠闃堝�煎墠鏅浘璞＄殑鐐规暟
			if (wF == 0) {//鍏ㄦ槸鍓嶆櫙鍥惧儚锛屽垯鍙互鐩存帴break
				break;
			}
			
			sumB += (double)(i*pixelNum[i]);
			double meanB = sumB / wB;
			double meanF = (sum - sumB) / wF;
			//freq类间方差
			double freq = (double)(wF)*(double)(wB)*(meanB - meanF)*(meanB - meanF);
			if (freq > maxFreq) {
				maxFreq = freq;
				threshold = i;
			}
		}
		
		return threshold;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		run();
	}
}
