package train;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.imageio.ImageIO;
import model.Point;

public class Chongqing_SegCfg {
	
	public static ArrayList<BufferedImage> CharList;
	/**
	 * ∂¡»°ÕºœÒ£¨Ω¯––«–∏Ó
	 */
	public static void run(){
		File dir = new File("chongqing/");
		//Âè™ÂàóÂá∫jpg
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
		
		for (int i=0; i<files.length; i++) {
			try {
				File file = files[i];
				BufferedImage img = ImageIO.read(file);
				Preprocess preproce = new Preprocess();
				BufferedImage binaryimg = preproce.getBinaryImage(img);
				
				ArrayList<BufferedImage> charlist = new ArrayList<BufferedImage>();
				BufferedImage tmpImg0 = binaryimg.getSubimage(0, 0, 14, img.getHeight());
				charlist.add(tmpImg0);
				BufferedImage tmpImg1 = binaryimg.getSubimage(23, 0, 20, img.getHeight());
				charlist.add(tmpImg1);
				BufferedImage tmpImg2 = binaryimg.getSubimage(44, 0, 11, img.getHeight());
				charlist.add(tmpImg2);
				
				for(int j=0; j<charlist.size(); j++){
					BufferedImage subImg = charlist.get(j);
					int[] yproj = project(subImg,"Y");
					//LinkedHashMap<Integer,Integer> charloc = charlocation(yproj);
					ArrayList<Point> charloc2 = charlocation2(yproj);
					
					BufferedImage sub_Img = subImg.getSubimage(0, charloc2.get(0).x, subImg.getWidth(), charloc2.get(0).y);
					//BufferedImage sub_Img = charlist.get(j);
					String prex = file.getName().split("\\.")[0];
					String filename = "chongqing_cfs/" + prex + "-" + j + ".jpg";
					ImageIO.write(sub_Img, "JPG", new File(filename));
				}
				//NingXia_SegProj.CharList = charlist;
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static boolean isBlack(int rgb) {
		Color color = new Color(rgb);
		if (color.getRed() + color.getGreen() + color.getBlue() <= 300) {
			return true;
		}
		return false;
	}
	
	public static int[] project(BufferedImage sourceImage,String direction){
		
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
	
	public static LinkedHashMap<Integer,Integer> charlocation(int[] histData){
		
		LinkedHashMap<Integer,Integer> lhsMap = new LinkedHashMap<Integer,Integer>(); 
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
				  if(cwidth>5){
					  lhsMap.put(j, cwidth);
				  }
			  }
			  j = j + cwidth;
		  }
		return lhsMap;
	}

	public static ArrayList<Point> charlocation2(int[] histData){
		
		ArrayList<Point> lhsMap = new ArrayList<Point>();
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
				  if(cwidth>10){
					  Point pk = new Point(j,cwidth);
					  lhsMap.add(pk);
				  }
			  }
			  j = j + cwidth;
		  }
		return lhsMap;
	} 
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		run();
		System.out.println("over!");
	}
}

