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

public class Jiangsu_Idently{
	
	private static HashMap<Integer, String> labelMap = null;
	
	public Jiangsu_Idently(){
		loadLabelMap();
	}
	
	private void loadLabelMap(){
		labelMap = new HashMap<Integer, String>();
		BufferedReader reader = null;
		try {
			 reader = new BufferedReader(new FileReader(new File("svm/jiangsu_label.txt")));
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
		Jiangsu_SeProj preprocess = new Jiangsu_SeProj();           //图像预处理
		BufferedImage binaryImage = preprocess.getBinaryImage(sourceImage);
		
		BufferedImage SubImg = binaryImage.getSubimage(0, 19, sourceImage.getWidth(), sourceImage.getHeight()-35);
		
		int[] yproj = preprocess.project(SubImg,"X");
		ArrayList<Point> charloc2 = preprocess.charlocation2(yproj);

		ArrayList<BufferedImage> imageList = new ArrayList<BufferedImage>(); 
		ArrayList<Point> charloc3 = new ArrayList<Point>();
		
		for(int i = 0;i<charloc2.size();i++){
			Point tmp = charloc2.get(i);
			if(tmp.y<32 ||tmp.y == 32){
				charloc3.add(tmp);
			}else{
				ArrayList<Point> lk = preprocess.reseg(tmp);
				for(int j =0;j<lk.size();j++){
					Point tep = lk.get(j);
					charloc3.add(tep);
				}
			}
		}
		for(int j=0; j<charloc3.size(); j++){
			Point tmp = charloc3.get(j);
			BufferedImage subImg = SubImg.getSubimage(tmp.x, 0, tmp.y, SubImg.getHeight());
			imageList.add(ImageUtil.scaleImage(subImg));
		}
		
		for (int i = 0; i < imageList.size(); i++) {
			ImageIO.write(imageList.get(i), "JPG", new File("tmp/" + i + ".jpg"));
		}
		
		Predict.run(imageList);	//预测
		
		String result = "";
//		String finalresult="";
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(new File("svm/jiangsu_result.txt")));
			String buff = "";
			
			while((buff = reader.readLine()) != null){
				int label = (int)Double.parseDouble(buff);
				String className = getClassName(label);
				result += className ;//+ " "
			}
			System.out.println(result);
//			int finalnum;  //预测结果
//			if(result.charAt(1) == '+')
//				finalnum = Integer.parseInt(String.valueOf(result.charAt(0)))+Integer.parseInt(String.valueOf(result.charAt(2)));
//			else{
//				finalnum = Integer.parseInt(String.valueOf(result.charAt(0)))*Integer.parseInt(String.valueOf(result.charAt(2)));
//			}
//			finalresult = Integer.toString(finalnum);
			//System.out.println(finalnum);
				
			try{
			    Thread thread = Thread.currentThread();
			    thread.sleep(7000);//暂停1.5秒后程序继续执行
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
//		System.out.println(result);
		return result;
	}
	
	public static void main(String[] args) throws IOException{
		Jiangsu_Idently index = new Jiangsu_Idently();
		for(int i=1017;i<1100;i++){
			String imgname = "download3/" + i +".jpg";
			System.out.println(imgname);
			index.predict(new File(imgname));
		}
//		index.predict(new File("download3/1036.jpg"));
	}
}
