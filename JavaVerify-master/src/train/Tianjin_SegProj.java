package train;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Tianjin_SegProj {
	
	public static ArrayList<BufferedImage> CharList;
	/**
	 * ¶ÁÈ¡Í¼Ïñ£¬½øĞĞÇĞ¸î
	 */
	public static void run(){
		File dir = new File("tianjin/");
		//åªåˆ—å‡ºjpg
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
				BufferedImage tmpImg0 = img.getSubimage(11, 4, 12, 17);
				charlist.add(tmpImg0);
				BufferedImage tmpImg1 = img.getSubimage(39, 3, 22, 19);
				charlist.add(tmpImg1);
				BufferedImage tmpImg2 = img.getSubimage(71, 5, 12, 16);
				charlist.add(tmpImg2);
						
				for(int j=0; j<charlist.size(); j++){
					BufferedImage subImg = charlist.get(j);
					String prex = file.getName().split("\\.")[0];
					String filename = "tianjin_cfs/" + prex + "-" + j + ".jpg";
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
		//System.out.println(CharList.size());
	}
}

