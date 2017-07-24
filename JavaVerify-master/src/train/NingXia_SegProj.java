package train;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class NingXia_SegProj {
	
	public static ArrayList<BufferedImage> CharList;
	/**
	 * 读取图像，进行切割
	 */
	public static void run(){
		File dir = new File("ningxia_gray/");
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
		
		//NingXia_SegProj model = new NingXia_SegProj();
		
		for (int i=0; i<files.length; i++) {
			try {
				File file = files[i];
				BufferedImage img = ImageIO.read(file);
				
				
				ArrayList<BufferedImage> charlist = new ArrayList<BufferedImage>();
				BufferedImage tmpImg0 = img.getSubimage(20, 8, 14, 18);
				charlist.add(tmpImg0);
				BufferedImage tmpImg1 = img.getSubimage(46, 13, 12, 11);
				charlist.add(tmpImg1);
				BufferedImage tmpImg2 = img.getSubimage(71, 9, 13, 17);
				charlist.add(tmpImg2);
						
				for(int j=0; j<charlist.size(); j++){
					BufferedImage subImg = charlist.get(j);
					String prex = file.getName().split("\\.")[0];
					String filename = "img1/" + prex + "-" + j + ".jpg";
					ImageIO.write(subImg, "JPG", new File(filename));
				}
				NingXia_SegProj.CharList = charlist;
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		run();
		System.out.println(CharList.size());
	}
}
