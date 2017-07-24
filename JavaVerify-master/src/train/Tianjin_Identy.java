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

public class Tianjin_Identy{
	
	private static HashMap<Integer, String> labelMap = null;
	
	public Tianjin_Identy(){
		loadLabelMap();
	}
	
	private void loadLabelMap(){
		labelMap = new HashMap<Integer, String>();
		BufferedReader reader = null;
		try {
			 reader = new BufferedReader(new FileReader(new File("svm/tianjin_label.txt")));
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
		Tianjin_ProcessImg preprocess = new Tianjin_ProcessImg();           //ͼ��Ԥ����
		BufferedImage binaryImage = preprocess.getBinaryImage(sourceImage);
		
		//�ָ��ַ�
		ArrayList<BufferedImage> imageList = new ArrayList<BufferedImage>(); 
		BufferedImage tmpImg0 = binaryImage.getSubimage(11, 4, 12, 17);
		imageList.add(preprocess.getBinaryImage(ImageUtil.scaleImage(tmpImg0)));
		BufferedImage tmpImg1 = binaryImage.getSubimage(39, 3, 22, 19);
		imageList.add(preprocess.getBinaryImage(ImageUtil.scaleImage(tmpImg1)));
		BufferedImage tmpImg2 = binaryImage.getSubimage(71, 5, 12, 16);
		imageList.add(preprocess.getBinaryImage(ImageUtil.scaleImage(tmpImg2)));
		
//		for (int i = 0; i < imageList.size(); i++) {
//			ImageIO.write(imageList.get(i), "JPG", new File("tmp/" + i + ".jpg"));
//		}
		
		Predict.run(imageList);	//Ԥ��
		
		String result = "";
		String finalresult="";
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(new File("svm/tianjin_result.txt")));
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
			finalresult = Integer.toString(finalnum);
			//System.out.println(finalnum);
				
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
		System.out.println(finalresult);
		return finalresult;
	}
	
	public static void main(String[] args) throws IOException{
		Tianjin_Identy index = new Tianjin_Identy();
//		for(int i=420;i<521;i++){
//			String imgname = "download3/" + i +".jpg";
//			System.out.println(imgname);
//			index.predict(new File(imgname));
//		}
		index.predict(new File("download3/520.jpg"));
	}
}

