package train;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import java.util.LinkedHashMap;  
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Iterator;
import model.Point;

public class SegProj {

	private int minW = 6;//最小字符宽度
	private int minH = 4;//最小字符高度
	
	private ArrayList<BufferedImage> projList;
	private ArrayList<BufferedImage> imageList;
	private BufferedImage sourceImage;
	
	//public SegProj(){
	//	this.init();
		
	//}
	
	/**
	 * 读取图像，进行切割
	 */
	private static void run(){
		File dir = new File("1_gray3/");
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
		
		SegProj model = new SegProj();
		
		for (int i=0; i<files.length; i++) {
			try {
				File file = files[i];
				BufferedImage img = ImageIO.read(file);
				
				int[] xproj = model.project(img,"X");
				LinkedHashMap<Integer,Integer> xchar = charlocation(xproj);
				System.out.println(file.getName().split("\\.")[0]+ " "+ xchar);
				ArrayList<BufferedImage> xcharimg = segmente_character(xchar, img);
				
				ArrayList<BufferedImage> charlist = new ArrayList<BufferedImage>();
//				BufferedImage tmpImg;
				
				for(int j = 0; j<xcharimg.size(); j++){
					if(xcharimg.get(j).getWidth()>27){   //大于27的子图像再次切割
						int l =  (int) Math.ceil(xcharimg.get(j).getWidth()/2);
						int h = xcharimg.get(j).getHeight();
						BufferedImage tmpImgleft = xcharimg.get(j).getSubimage(0, 0, l+1, h);
						//ImageIO.write(tmpImgleft, "JPG", new File("1_gray3/" + "1001.jpg"));
						//System.out.println(xcharimg.get(j).getWidth() + " " + l);
						BufferedImage tmpImgright = xcharimg.get(j).getSubimage(l-1, 0, xcharimg.get(j).getWidth()-l+1, h);
						//ImageIO.write(tmpImgright, "JPG", new File("1_gray3/" + "1002.jpg"));
						
						model.loactionxy(tmpImgleft,charlist);
						model.loactionxy(tmpImgright,charlist);
					}else{
						model.loactionxy(xcharimg.get(j),charlist);
					}
					
//					BufferedImage yy = xcharimg.get(j).getSubimage(0, tmpkey, xcharimg.get(j).getWidth(), tmpvalue);
//					ImageIO.write(yy, "JPG", new File("1_gray3/" + "1001.jpg"));
				}
				
				for(int j=0; j<charlist.size(); j++){
					BufferedImage subImg = charlist.get(j);
					String prex = file.getName().split("\\.")[0];
					String filename = "4_cfs/" + prex + "-" + j + ".jpg";
					ImageIO.write(subImg, "JPG", new File(filename));
				}
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	// 确定字符
	public void loactionxy(BufferedImage xcharimg,ArrayList<BufferedImage> charlist){
		
		int[] yproj = project(xcharimg,"Y");
		LinkedHashMap<Integer,Integer> ychar = charlocation(yproj);
		if(ychar.isEmpty()){
			return;
		}
		int tmpvalue= 0,tmpkey = 0;
		if(ychar.size()==1){
			for(Map.Entry<Integer, Integer> entry : ychar.entrySet()){  
					tmpvalue += entry.getValue();
					tmpkey = entry.getKey();
			}
		}
		else if(ychar.size()>1){
			for(Map.Entry<Integer, Integer> entry : ychar.entrySet()){  
				tmpvalue += entry.getValue();
			}
			for(Map.Entry<Integer, Integer> entry : ychar.entrySet()){  
					tmpkey = entry.getKey();
					break;
			}
		}
		BufferedImage tmpImg = xcharimg.getSubimage(0, tmpkey, xcharimg.getWidth(), tmpvalue);
		charlist.add(tmpImg);
		
		
	}
	
	public static ArrayList<BufferedImage> segmente_character(Map<Integer,Integer> V_loc,BufferedImage img) throws IOException{
		 
		 ArrayList<BufferedImage> CharMat = new ArrayList<BufferedImage>();
		  
		 for (Integer key : V_loc.keySet()) {
			BufferedImage tmp = img.getSubimage(key, 0, V_loc.get(key), img.getHeight());
			//ImageIO.write(tmp, "JPG", new File("1_gray3/" + "1001.jpg"));
			CharMat.add(tmp);
		 }
		 return CharMat;
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
					if (isBlack(sourceImage.getRGB(x, y))) {
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
					if (isBlack(sourceImage.getRGB(y, x))) {
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
				  int tmpwidth=0;
				  if(cwidth >= 5&&cwidth <= 11){
					  int flag = j+cwidth;  
					  while(histData[flag]==0&&flag<histData.length-1){
						  flag++;
					  }
					  while(flag < histData.length-1 & histData[flag] != 0){
						  tmpwidth=tmpwidth+1;
						  flag++;
					  }
				  }
				  if(tmpwidth!=0 && tmpwidth<11){
					  lhsMap.put(j, cwidth+tmpwidth+1);
					  j = j + cwidth+tmpwidth+1;
					  //continue;
			  	  }else if(cwidth>5){
			  		lhsMap.put(j, cwidth);
			  		j = j + cwidth;
			  		//continue;
			  	  }
			  }
			  j = j + 1;
		  }
		return lhsMap;
	}
	
	public LinkedHashMap<Integer,Integer> charrelocation(int[] histData) {
		
		LinkedHashMap<Integer,Integer> lhsMap = new LinkedHashMap<Integer,Integer>();
		
		for(int j=0;j<histData.length-1;){
			
			
			
		}
		
		
		return lhsMap;
	}
	
	
	public BufferedImage getBinaryImage(BufferedImage sourceImage){
		double Wr = 30;//0.299
		double Wg = 59;//0.587
		double Wb = 11;//0.114
		
		int width = sourceImage.getWidth();
		int height = sourceImage.getHeight();
		int[][] gray = new int[width][height];
		
		//灰度化
		float[] hsv = new float[3];
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
		//int threshold = 185;
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
