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
 * @Description 文件读写类，用于用文件中读取测试数据和输出测试结果，与进程无关
 *
 */
public class IOFile {
	
	
	
	
	/**
	* @Description: 从input文件中读取待实现的进程
	* @param fileName
	* @return ArrayList<int[]>   返回一个二维数组，外层用ArrayList灵活设置要写入进程的数量
	* 					每一行都是一个进程，每一个进程（二维数组的成员）分别存储
	* 					 进程id，进程优先级，进程运行时间和进程内指令数量。
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
            tempString = reader.readLine();                  //第一行是说明行，没有实际意义
            while ((tempString = reader.readLine()) != null) {  // 一次读入一行，直到读入null为文件结束 
            	inputProcessString = tempString.split("\\s+");  //按空格分割字符串，将每一行的输入转化成4个子字符串形成的数组
            	inputProcessInt.add(StringToInt(inputProcessString));  //将str数组转化成int数组
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
	* @Description: 从每个进程的文件中读取待实现的指令
	* @param fileName
	* @return ArrayList<int[]>   返回一个二维数组，外层用ArrayList灵活设置要写入进程的数量
	* 					每一行都是一个进程，每一个进程（二维数组的成员）分别存储
	* 					 进程id，进程优先级，进程运行时间和进程内指令数量。
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
            tempString = reader.readLine();                  //第一行是说明行，没有实际意义
            while ((tempString = reader.readLine()) != null) {  // 一次读入一行，直到读入null为文件结束 
            	inputInstructionString = tempString.split("\\s+");  //按空格分割字符串，将每一行的输入转化成4个子字符串形成的数组
            	inputInstructionInt.add(StringToInt(inputInstructionString));  //将str数组转化成int数组
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
	* @Description: 将一个一维String数组转化成一维int数组，要求String数组全为数字
	* @param arrs
	* @return int[]    返回转化成功的int数组
	* @throws
	*/
	public static int[] StringToInt(String[] arrs){
	    int[] ints = new int[arrs.length];
	    for(int i=0;i<arrs.length;i++){
	        try {                 //对非数字进行异常处理
	        	ints[i]= Integer.parseInt(arrs[i]);    //String数组内数字都转化成int类型
        	} catch (NumberFormatException e) {
        	    e.printStackTrace();
        	}
	    }
	    return ints;
	}
	
	/**
	* @Description: 将输出结果保存到路径上，若不存在则创建路径
	* @param content  文本内容
	* @param fileName 文本路径
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
