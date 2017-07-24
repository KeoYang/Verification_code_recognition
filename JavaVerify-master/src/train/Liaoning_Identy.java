package train;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.imageio.ImageIO;

public class Liaoning_Identy{
	
	private static HashMap<Integer, String> labelMap = null;
	
	public Liaoning_Identy(){
		loadLabelMap();
	}
	
	private void loadLabelMap(){
		labelMap = new HashMap<Integer, String>();
		BufferedReader reader = null;
		try {
			 reader = new BufferedReader(new FileReader(new File("svm/liaoning_label.txt")));
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
	 * 具体的预测，返回识别的文字
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static String predict(File file) throws IOException{
		BufferedImage sourceImage = ImageIO.read(file);
		
		Liaoning_SegProj seproj = new Liaoning_SegProj();
		BufferedImage binaryImage = seproj.getBinaryImage(sourceImage);
		int[] xproj = seproj.project(binaryImage, "X");
		LinkedHashMap<Integer,Integer> xchar = Liaoning_SegProj.charlocation(xproj);
		String flag = "";
		//分割字符
		ArrayList<BufferedImage> imageList = new ArrayList<BufferedImage>(); 
		for (Integer key : xchar.keySet()) {
			if(key<10 && xchar.get(key)<10 && xchar.get(key)>4){//
				flag = "Algorithm";
				imageList.add(ImageUtil.scaleImage(binaryImage.getSubimage(6, 9, 8, 13)));
				imageList.add(ImageUtil.scaleImage(binaryImage.getSubimage(22, 7, 16, 16)));
				imageList.add(ImageUtil.scaleImage(binaryImage.getSubimage(39, 10, 9, 13)));
			}else if(key>10 && xchar.get(key)<10 && xchar.get(key)>4){
				flag = "Numbers";
				imageList.add(ImageUtil.scaleImage(binaryImage.getSubimage(10, 9, 9, 13)));
				imageList.add(ImageUtil.scaleImage(binaryImage.getSubimage(31, 9, 9, 13)));
				imageList.add(ImageUtil.scaleImage(binaryImage.getSubimage(51, 9, 9, 13)));
				imageList.add(ImageUtil.scaleImage(binaryImage.getSubimage(71, 9, 9, 13)));
			}else{
					flag = "Chinese_idiom";
					//System.out.println("the img is Chinese characters");
			}
			break;
		}
		
//		for (int i = 0; i < imageList.size(); i++) {
//			ImageIO.write(imageList.get(i), "JPG", new File("tmp/" + i + ".jpg"));
//		}
		
		String result = "";
		String finalresult = "";
		BufferedReader reader = null;
		
		if(flag == "Chinese_idiom"){
			LinkedHashMap<String,Double> Chinese_final = new LinkedHashMap<String,Double>();
			File dir = new File("liaoning_Chinese_idiom");
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
			
			for (File f : files) {
				BufferedImage img = ImageIO.read(f);
				BufferedImage binary_Image = seproj.getBinaryImage(img);
				double sc = distance(binary_Image,binaryImage);
				Chinese_final.put(f.toString().substring(f.toString().length()-8,f.toString().length()-4), sc);
			}
			double minv = 0.001;
			
			 for (String key : Chinese_final.keySet()) {
					if(Chinese_final.get(key)<minv){
						minv = Chinese_final.get(key);
						finalresult = key;
					} 
				 }
			
		}else{
		try {
			Predict.run(imageList);	//预测
			
			reader = new BufferedReader(new FileReader(new File("svm/liaoning_result.txt")));
			String buff = "";
			
			while((buff = reader.readLine()) != null){
				int label = (int)Double.parseDouble(buff);
				String className = getClassName(label);
				result += className ;//+ " "
			}
			System.out.println(result);
			
			if(flag == "Algorithm"){
				int finalnum;  //预测结果
				if(result.charAt(1) == '+')
					finalnum = Integer.parseInt(String.valueOf(result.charAt(0)))+Integer.parseInt(String.valueOf(result.charAt(2)));
				else if(result.charAt(1) == 'x'){
					finalnum = Integer.parseInt(String.valueOf(result.charAt(0)))*Integer.parseInt(String.valueOf(result.charAt(2)));
				}else{
					finalnum = Integer.parseInt(String.valueOf(result.charAt(0)))-Integer.parseInt(String.valueOf(result.charAt(2)));
				}
				finalresult = Integer.toString(finalnum);
				//System.out.println(finalresult);
			}else if(flag == "Numbers"){
				//System.out.println(result);
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if (reader != null) {
				reader.close();
			}
		}
		}

//		try{
//		    Thread thread = Thread.currentThread();
//		    thread.sleep(10);//暂停1.5秒后程序继续执行
//		}catch (InterruptedException e) {
//		    // TODO Auto-generated catch block
//		    e.printStackTrace();
//		}
		
		System.out.println(finalresult);
		return finalresult;
	}
	
	public static double distance(BufferedImage img1,BufferedImage img2){
		double score=0;
		int weight = img1.getWidth();
		int height = img1.getHeight();
		
		for(int i = 0;i<weight;i++){
			for(int j = 0;j<height;j++){
				score = score+Math.abs(img1.getRGB(i, j)-img2.getRGB(i, j));
			}
		}
		return score;
	}
	
	public static void main(String[] args) throws IOException{
		Liaoning_Identy index = new Liaoning_Identy();
//		for(int i=701;i<848;i++){
//			String imgname = "download3/" + i +".jpg";
//			System.out.println(imgname);
//			index.predict(new File(imgname));
//		}
		index.predict( new File("download3/522.jpg"));
	}
}
