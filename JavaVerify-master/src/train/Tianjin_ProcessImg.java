package train;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Tianjin_ProcessImg {

	public Tianjin_ProcessImg(){
		
	}
	
	private void run(){
		File dir = new File("tianjin_scale");//download3
		//鍙垪鍑簀pg
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
		
		for (File file : files) {
			try {
				
				BufferedImage img = ImageIO.read(file);
				//BufferedImage filterImg = colorfilter(img);
				//ImageIO.write(filterImg, "JPG", new File("tianjin_filterImg/" + file.getName()));//1_filterImg
				BufferedImage binaryImg = getBinaryImage(img);
				ImageIO.write(binaryImg, "JPG", new File("tianjin_scale/" + file.getName()));//1_gray3
				//BufferedImage dilateImg = dilate(binaryImg, 1);
				//BufferedImage eroseImg = erose(dilateImg, 1);
				//ImageIO.write(dilateImg, "JPG", new File("erosedImg/" + file.getName()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("binary img over");
	}
public BufferedImage colorfilter(BufferedImage sourceImage){
		
		int width = sourceImage.getWidth();
		int height = sourceImage.getHeight();
		int[] pixels = new int[width*height];

		try{
		      PixelGrabber pg = new PixelGrabber(sourceImage,0,0,width,height,pixels,0,width);
		      pg.grabPixels();
	      }catch(InterruptedException e3){
		    e3.printStackTrace();
	      }
		//BufferedImage filterImage = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
		BufferedImage filterImage = sourceImage;
		
		ColorModel cm = ColorModel.getRGBdefault();
		
		int[] tpRed = new int[9];
		int[] tpGreen = new int[9];
		int[] tpBlue = new int[9];
		    
		    for(int i=1;i<height-1;i++){
		    	for(int j=1;j<width-1;j++){
		    		
		    		tpRed[0] = cm.getRed(pixels[(i-1)*width+j-1]);
		    		tpRed[1] = cm.getRed(pixels[(i-1)*width+j]);
		    		tpRed[2] = cm.getRed(pixels[(i-1)*width+j+1]);
		    		tpRed[3] = cm.getRed(pixels[i*width+j-1]);
		    		tpRed[4] = cm.getRed(pixels[i*width+j]);
		    		tpRed[5] = cm.getRed(pixels[i*width+j+1]);
		    		tpRed[6] = cm.getRed(pixels[(i+1)*width+j-1]);
		    		tpRed[7] = cm.getRed(pixels[(i+1)*width+j]);
		    		tpRed[8] = cm.getRed(pixels[(i+1)*width+j+1]);
		    	    for(int rj=0; rj<8; rj++){
		    	    	for(int ri=0; ri<8-rj; ri++){
		    	    		if(tpRed[ri]>tpRed[ri+1]){
		    	    			int Red_Temp = tpRed[ri];
	  		    	    		tpRed[ri] = tpRed[ri+1];
	  		    	    		tpRed[ri+1] = Red_Temp;
		    	    		}
		    	    	}
		    	    }
		    	    int medianRed = tpRed[4];
		    	    
		    	    tpGreen[0] = cm.getGreen(pixels[(i-1)*width+j-1]);
		    	    tpGreen[1] = cm.getGreen(pixels[(i-1)*width+j]);
		    	    tpGreen[2] = cm.getGreen(pixels[(i-1)*width+j+1]);
		    	    tpGreen[3] = cm.getGreen(pixels[i*width+j-1]);
		    	    tpGreen[4] = cm.getGreen(pixels[i*width+j]);
		    	    tpGreen[5] = cm.getGreen(pixels[i*width+j+1]);
		    	    tpGreen[6] = cm.getGreen(pixels[(i+1)*width+j-1]);
		    	    tpGreen[7] = cm.getGreen(pixels[(i+1)*width+j]);
		    	    tpGreen[8] = cm.getGreen(pixels[(i+1)*width+j+1]);
		    	    for(int rj=0; rj<8; rj++){
	    	    	for(int ri=0; ri<8-rj; ri++){
	    	    		if(tpGreen[ri]>tpGreen[ri+1]){
	    	    			int Green_Temp = tpGreen[ri];
		    	    		tpGreen[ri] = tpGreen[ri+1];
		    	    		tpGreen[ri+1] = Green_Temp;
	    	    		}
	    	    	}
	    	    }
	    	    int medianGreen = tpGreen[4];
			    
	    	    tpBlue[0] = cm.getBlue(pixels[(i-1)*width+j-1]);
	    	    tpBlue[1] = cm.getBlue(pixels[(i-1)*width+j]);
	    	    tpBlue[2] = cm.getBlue(pixels[(i-1)*width+j+1]);
	    	    tpBlue[3] = cm.getBlue(pixels[i*width+j-1]);
	    	    tpBlue[4] = cm.getBlue(pixels[i*width+j]);
	    	    tpBlue[5] = cm.getBlue(pixels[i*width+j+1]);
	    	    tpBlue[6] = cm.getBlue(pixels[(i+1)*width+j-1]);
	    	    tpBlue[7] = cm.getBlue(pixels[(i+1)*width+j]);
	    	    tpBlue[8] = cm.getBlue(pixels[(i+1)*width+j+1]);
	    	    for(int rj=0; rj<8; rj++){
	    	    	for(int ri=0; ri<8-rj; ri++){
	    	    		if(tpBlue[ri]>tpBlue[ri+1]){
	    	    			int Blue_Temp = tpBlue[ri];
		    	    		tpBlue[ri] = tpBlue[ri+1];
		    	    		tpBlue[ri+1] = Blue_Temp;
	    	    		}
	    	    	}
	    	    }
	    	    int medianBlue = tpBlue[4];
	    	    
		    	int rgb = 255<<24|medianRed<<16|medianGreen<<8|medianBlue; 
		    	filterImage.setRGB(j, i, rgb);
		    	}	
		    }
		
		return filterImage;
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
		Tianjin_ProcessImg model = new Tianjin_ProcessImg();
		model.run();
	}

}
