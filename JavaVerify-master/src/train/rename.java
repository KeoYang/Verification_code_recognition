package train;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class rename {

	
	//只列出jpg
//	File[] files = dir.listFiles();
//	for(int i=0; i<files.length; i++){
//		File file = files[i];
//		String prex = file.getName().split("\\.")[0];
//		String filename = "ningxia_cfs/" + prex + "-" + i + ".jpg";
//	}
	
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
//		File dir = new File("ningxia_cfs/");
//		File[] files = dir.listFiles();
//		for(int i=0; i<files.length; i++){
//			File file = files[i];
//			String prex = file.getName().split("\\.")[0];
//			
//			File img = new File(file+"");
//			File[] imgs = img.listFiles();
//			for(int j = 0;j<imgs.length; j++){
//				File imgs_ = imgs[i];
//				BufferedImage _img = ImageIO.read(imgs_);
//				String filename = "ningxia_cfs/"+prex+"/" + prex + "-" + j + ".jpg";
//				System.out.println(filename);
//				ImageIO.write(_img, "JPG", new File(filename));
//			}
//			
//		}
		
		File dir = new File("jiangsu_cfs/Z/");
		File[] files = dir.listFiles();
		System.out.println(files.length);
		for(int i=0; i<files.length; i++){
			File file = files[i];
			BufferedImage _img = ImageIO.read(file);
			//String prex = file.getName().split("\\.")[0];
			
			String filename = "jiangsu_scale/"+ "Z" + "-" + i + ".jpg";
			System.out.println(filename);
			ImageIO.write(_img, "JPG", new File(filename));
			
			
		}
		
		
	}

}
