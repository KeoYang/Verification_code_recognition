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

import model.Point;

public class Chongqing_Identy{
	
	private static HashMap<Integer, String> labelMap = null;
	
	public Chongqing_Identy(){
		loadLabelMap();
	}
	
	private void loadLabelMap(){
		labelMap = new HashMap<Integer, String>();
		BufferedReader reader = null;
		try {
			 reader = new BufferedReader(new FileReader(new File("svm/chongqing_label.txt")));
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
		Preprocess preprocess = new Preprocess();           //图像预处理
		BufferedImage binaryImage = preprocess.getBinaryImage(sourceImage);
		Chongqing_SegCfg segcfg = new Chongqing_SegCfg();
		//分割字符
		ArrayList<BufferedImage> imageList = new ArrayList<BufferedImage>(); 
		ArrayList<BufferedImage> charlist = new ArrayList<BufferedImage>();
		
		BufferedImage tmpImg0 = binaryImage.getSubimage(0, 0, 14, binaryImage.getHeight());
		charlist.add(tmpImg0);
		BufferedImage tmpImg1 = binaryImage.getSubimage(23, 0, 20, binaryImage.getHeight());
		charlist.add(tmpImg1);
		BufferedImage tmpImg2 = binaryImage.getSubimage(44, 0, 11, binaryImage.getHeight());
		charlist.add(tmpImg2);
		
		for(int j=0; j<charlist.size(); j++){
			BufferedImage subImg = charlist.get(j);
			int[] yproj = segcfg.project(subImg,"Y");
			ArrayList<Point> charloc2 = segcfg.charlocation2(yproj);
			BufferedImage sub_Img = subImg.getSubimage(0, charloc2.get(0).x, subImg.getWidth(), charloc2.get(0).y);
			imageList.add(ImageUtil.scaleImage(sub_Img));
		}
		
		for (int i = 0; i < imageList.size(); i++) {
			ImageIO.write(imageList.get(i), "JPG", new File("tmp/" + i + ".jpg"));
		}
		
		Predict.run(imageList);	//预测
		
		String result = "";
		String finalresult="";
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(new File("svm/chongqing_result.txt")));
			String buff = "";
			
			while((buff = reader.readLine()) != null){
				int label = (int)Double.parseDouble(buff);
				String className = getClassName(label);
				result += className ;//+ " "
			}
			System.out.println(result);
			int finalnum;  //预测结果
			if(result.charAt(1) == '+')
				finalnum = Integer.parseInt(String.valueOf(result.charAt(0)))+Integer.parseInt(String.valueOf(result.charAt(2)));
			else{
				finalnum = Integer.parseInt(String.valueOf(result.charAt(0)))-Integer.parseInt(String.valueOf(result.charAt(2)));
			}
			finalresult = Integer.toString(finalnum);
			System.out.println(finalnum);
				
			try{
			    Thread thread = Thread.currentThread();
			    thread.sleep(2000);//暂停1.5秒后程序继续执行
			}catch (InterruptedException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if (reader != null) {
				reader.close();
			}
		}
		
		return finalresult;
	}
	
	public static void main(String[] args) throws IOException{
		Chongqing_Identy index = new Chongqing_Identy();
		for(int i=340;i<358;i++){
			String imgname = "download3/" + i +".jpg";
			System.out.println(imgname);
			index.predict(new File(imgname));
		}
//		index.predict(new File("download3/399.jpg"));
	}
}
