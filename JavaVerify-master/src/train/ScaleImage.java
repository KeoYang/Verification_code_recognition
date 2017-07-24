package train;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.naming.ldap.SortControl;

import com.jhlabs.image.ScaleFilter;

/**
 * 灏嗗師鍥剧墖缂╂斁鎴愮粺涓�澶у皬鐨勫浘鐗�
 * @author wangjiewen
 *
 */
public class ScaleImage {
	
	public static void run(){
		try {
			File dir = new File("jiangsu_scale");//3_drop
			SegWaterDrop model = new SegWaterDrop();
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
			
			File targetDir = new File("jiangsu_scale");//4_scale
			if (!targetDir.exists()) {
				targetDir.mkdir();
			}
			
			for (File file : files) {
				BufferedImage sourceImage = ImageIO.read(file);
				BufferedImage targetImage = ImageUtil.scaleImage(sourceImage);
				ImageIO.write(targetImage, "JPG", new File("jiangsu_scale/" + file.getName()));//4_scale
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	public static void main(String[] args){
		run();
		System.out.println("Scale over!");
	}

}
