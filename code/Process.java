/**  
* @Title: Process.java
* @Package code
* @author Lu Ning  
* @date 2020-10-27 16:33:22
* @version V1.0  
*/
package code;

import java.util.ArrayList;
import java.util.Stack;

/**
 * @Description �����࣬�̳���PCB�࣬�ڴ˻����������˳���κ����ݶΡ�
 * 				ʹ�ü̳ж����ǽ�PCB����Ϊ�����Կ������Ӵ��븴����
 * 				Process��ʵ��ʹ�ÿ���ԭ��ʱ������ø���PCB�ķ���
 *				
 */
public class Process extends PCB {
    private static ArrayList<Process> allProcess = new ArrayList<>();  //��̬��������������һ���㹻����ڴ�ռ䣬�洢����PCB������κ����ݶ� 
	private ArrayList<Instruction> instructionSegments;   //����� java����ַ���ݣ�Process
	private Stack<Integer> userStack;    //�û�ջ
	private Stack<Integer> coreStack;	//����ջ
	
	
	/**
	* @Description: ������Ĺ��캯��  
	*/
	public Process(int id, int priority, int inTimes, int instrNum, ArrayList<Instruction> segment) {
		super(id, priority, inTimes, instrNum);   //��������PCB
		this.instructionSegments = segment;       //�������� �û�ջ�ͺ���ջ
		userStack = new Stack<>();
		coreStack = new Stack<>();
		getAllProcess().add(this);       
//		showInstructionSegment();
	}

	
	
	/**
	* @Description: ��IR���ó�PCָ�����εĵ�ָ�������Ϣ��
	* @throws
	*/
	public void setIRNewInstructionState() {
			this.setIR(instructionSegments.get(getPC()).getInstructionState());
	}
	
	/**
	* @Description: ��ȡ��ǰ���̵�pcָ��ĳ���εľ���ָ��
	* @return  int
	* @throws
	*/
	public int getCurrentInstructionID() {
		return instructionSegments.get(getPC()).getInstructionID();
	}
	
	/**
	* @Description: ��ӡҪִ�еĳ����ۺϣ������ã�    
	* @throws
	*/
	public void showInstructionSegment() {
		for(Instruction e:instructionSegments) {
			System.out.println(e);
		}	
	}
	
	/**
	* @Description: ���ݽ������ջ
	* @param register     
	* @throws
	*/
	public void inCoreStack(int register) {
		coreStack.push(register);
	}
	
	/**
	* @Description: ����ջ��ջ
	* @return int    
	* @throws
	*/
	public int outCoreStack() {
		return coreStack.pop();
	}



	/**
	 * @return the allProcess
	 */
	public static ArrayList<Process> getAllProcess() {
		return allProcess;
	}



	/**
	 * @return the userStack
	 */
	public Stack<Integer> getUserStack() {
		return userStack;
	}



	/**
	 * @param userStack the userStack to set
	 */
	public void setUserStack(Stack<Integer> userStack) {
		this.userStack = userStack;
	}
	
}
