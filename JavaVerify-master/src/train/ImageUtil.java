package train;

import java.awt.Color;
import java.awt.Label;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import com.jhlabs.image.ScaleFilter;

public class ImageUtil {
	
	
	/**
	 * �ж��Ƿ�Ϊ��ɫ����
	 * @param rgb
	 * @return
	 */
	public static boolean isBlack(int rgb) {
		Color color = new Color(rgb);
		if (color.getRed() + color.getGreen() + color.getBlue() <= 300) {
			return true;
		}
		return false;
	}
	
	/**
	 * ����ͼ��Ĭ��16x16
	 * @param img
	 * @return
	 */
	public static BufferedImage scaleImage(BufferedImage img) {
		return scaleImage(img, 16, 16);
	}
	
	/**
	 * ����ͼ��
	 * @param img
	 * @param width
	 * @param height
	 * @return
	 */
	public static BufferedImage scaleImage(BufferedImage img, int width, int height){
		ScaleFilter sf = new ScaleFilter(width,height);
		BufferedImage imgdest = new BufferedImage(width, height, img.getType());
		return sf.filter(img, imgdest);
	}
	
	
	/**
	 * ���ѵ����ͼƬ�ķ��࣬ ��a-12.jpg������a
	 * @param filename
	 * @return
	 */
	public static String getImgClass(String filename){
		String[] arr = filename.split("-");
		if (arr != null) {
			return arr[0];
		}else{
			return "";
		}
	}
	
	
	

}
