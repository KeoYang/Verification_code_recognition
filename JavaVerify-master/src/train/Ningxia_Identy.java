package train;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class Ningxia_Identy{
	
	private static HashMap<Integer, String> labelMap = null;
	
	public Ningxia_Identy(){
		loadLabelMap();
	}
	
	private void loadLabelMap(){
		labelMap = new HashMap<Integer, String>();
		BufferedReader reader = null;
		try {
			 reader = new BufferedReader(new FileReader(new File("svm/ningxia_label.txt")));
			 String buff = null;
			 while((buff = reader.readLine()) != null){
				 String[] arr = buff.split(" ");
				 labelMap.put(Integer.parseInt(arr[1]), arr[0]);
			 }
			 
			 System.out.println("load image label finish!");
			 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	private static String getClassName(int label){
		return labelMap.get(label);
	}
	
	/**
	 * �����Ԥ�⣬����ʶ�������
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static String predict(File file) throws IOException{
		BufferedImage sourceImage = ImageIO.read(file);
		Preprocess preprocess = new Preprocess();           //ͼ��Ԥ����
		BufferedImage binaryImage = preprocess.getBinaryImage(sourceImage);
		
		//�ָ��ַ�
		ArrayList<BufferedImage> imageList = new ArrayList<BufferedImage>(); 
		BufferedImage tmpImg0 = binaryImage.getSubimage(20, 8, 14, 18);
		imageList.add(ImageUtil.scaleImage(tmpImg0));
		BufferedImage tmpImg1 = binaryImage.getSubimage(46, 13, 12, 11);
		imageList.add(ImageUtil.scaleImage(tmpImg1));
		BufferedImage tmpImg2 = binaryImage.getSubimage(71, 9, 13, 17);
		imageList.add(ImageUtil.scaleImage(tmpImg2));
		
//		for (int i = 0; i < imageList.size(); i++) {
//			ImageIO.write(imageList.get(i), "JPG", new File("tmp/" + i + ".jpg"));
//		}
		
		Predict.run(imageList);	//Ԥ��
		
		String result = "";
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(new File("svm/ningxia_result.txt")));
			String buff = "";
			
			while((buff = reader.readLine()) != null){
				int label = (int)Double.parseDouble(buff);
				String className = getClassName(label);
				result += className ;//+ " "
			}
			System.out.println(result);
			int finalnum;  //Ԥ����
			if(result.charAt(1) == '+')
				finalnum = Integer.parseInt(String.valueOf(result.charAt(0)))+Integer.parseInt(String.valueOf(result.charAt(2)));
			else{
				finalnum = Integer.parseInt(String.valueOf(result.charAt(0)))*Integer.parseInt(String.valueOf(result.charAt(2)));
			}
			System.out.println(finalnum);
				
//			try{
//			    Thread thread = Thread.currentThread();
//			    thread.sleep(2000);//��ͣ1.5���������ִ��
//			}catch (InterruptedException e) {
//			    // TODO Auto-generated catch block
//			    e.printStackTrace();
//			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if (reader != null) {
				reader.close();
			}
		}
		return result;
	}
	
	public static void main(String[] args) throws IOException{
		Ningxia_Identy index = new Ningxia_Identy();
//		for(int i=200;i<300;i++){
//			String imgname = "download3/" + i +".jpg";
//			index.predict(new File(imgname));
//		}
		index.predict(new File("download3/217.jpg"));
	}
}
