/**  
* @Title: File.java
* @Package code
* @author Lu Ning  
* @date 2020-10-26 17:37:24
* @version V1.0  
*/
package code;

import java.io.*;
import java.util.ArrayList;

/**
 * @Description �ļ���д�࣬�������ļ��ж�ȡ�������ݺ�������Խ����������޹�
 *
 */
public class IOFile {
	
	
	
	
	/**
	* @Description: ��input�ļ��ж�ȡ��ʵ�ֵĽ���
	* @param fileName
	* @return ArrayList<int[]>   ����һ����ά���飬�����ArrayList�������Ҫд����̵�����
	* 					ÿһ�ж���һ�����̣�ÿһ�����̣���ά����ĳ�Ա���ֱ�洢
	* 					 ����id���������ȼ�����������ʱ��ͽ�����ָ��������
	* @throws
	*/
	public static ArrayList<int[]> readProcessFromFile(String fileName) {  
        File file = new File(fileName);  
        BufferedReader reader = null;  
        String[] inputProcessString;
        ArrayList<int[]> inputProcessInt = new ArrayList<int[]>();
        try {   
            reader = new BufferedReader(new FileReader(file));  
            String tempString = null;
            tempString = reader.readLine();                  //��һ����˵���У�û��ʵ������
            while ((tempString = reader.readLine()) != null) {  // һ�ζ���һ�У�ֱ������nullΪ�ļ����� 
            	inputProcessString = tempString.split("\\s+");  //���ո�ָ��ַ�������ÿһ�е�����ת����4�����ַ����γɵ�����
            	inputProcessInt.add(StringToInt(inputProcessString));  //��str����ת����int����
            }  
            reader.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            if (reader != null) {  
                try {  
                    reader.close();  
                } catch (IOException e1) {  
                }  
            }  
        } 
        return inputProcessInt; 
    }
	
	/**
	* @Description: ��ÿ�����̵��ļ��ж�ȡ��ʵ�ֵ�ָ��
	* @param fileName
	* @return ArrayList<int[]>   ����һ����ά���飬�����ArrayList�������Ҫд����̵�����
	* 					ÿһ�ж���һ�����̣�ÿһ�����̣���ά����ĳ�Ա���ֱ�洢
	* 					 ����id���������ȼ�����������ʱ��ͽ�����ָ��������
	* @throws
	*/
	public static ArrayList<int[]> readInstructionFromFile(String fileName) {  
        File file = new File(fileName);  
        BufferedReader reader = null;  
        String[] inputInstructionString;
        ArrayList<int[]> inputInstructionInt = new ArrayList<int[]>();
        try {   
            reader = new BufferedReader(new FileReader(file));  
            String tempString = null;
            tempString = reader.readLine();                  //��һ����˵���У�û��ʵ������
            while ((tempString = reader.readLine()) != null) {  // һ�ζ���һ�У�ֱ������nullΪ�ļ����� 
            	inputInstructionString = tempString.split("\\s+");  //���ո�ָ��ַ�������ÿһ�е�����ת����4�����ַ����γɵ�����
            	inputInstructionInt.add(StringToInt(inputInstructionString));  //��str����ת����int����
            }  
            reader.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            if (reader != null) {  
                try {  
                    reader.close();  
                } catch (IOException e1) {  
                }  
            }  
        } 
        return inputInstructionInt; 
    }
	
	
	/**
	* @Description: ��һ��һάString����ת����һάint���飬Ҫ��String����ȫΪ����
	* @param arrs
	* @return int[]    ����ת���ɹ���int����
	* @throws
	*/
	public static int[] StringToInt(String[] arrs){
	    int[] ints = new int[arrs.length];
	    for(int i=0;i<arrs.length;i++){
	        try {                 //�Է����ֽ����쳣����
	        	ints[i]= Integer.parseInt(arrs[i]);    //String���������ֶ�ת����int����
        	} catch (NumberFormatException e) {
        	    e.printStackTrace();
        	}
	    }
	    return ints;
	}
	
	/**
	* @Description: �����������浽·���ϣ����������򴴽�·��
	* @param content  �ı�����
	* @param fileName �ı�·��
	* @throws
	*/
	public static void saveAsFileWriter(String content, String fileName) {
		FileWriter fwriter = null;
		try {
			fwriter = new FileWriter(fileName);
			fwriter.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fwriter.flush();
				fwriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		
	}
	
//	public static void outPutToFileAndScreen(String ) {
//		
//	}
//	
	
	
	
}
