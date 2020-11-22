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
 * @Description 进程类，继承了PCB类，在此基础上增加了程序段和数据段。
 * 				使用继承而不是将PCB类作为其属性可以增加代码复用性
 * 				Process的实例使用控制原语时，会调用父类PCB的方法
 *				
 */
public class Process extends PCB {
    private static ArrayList<Process> allProcess = new ArrayList<>();  //静态变量，可以视作一个足够大的内存空间，存储所有PCB、程序段和数据段 
	private ArrayList<Instruction> instructionSegments;   //程序段 java按地址传递，Process
	private Stack<Integer> userStack;    //用户栈
	private Stack<Integer> coreStack;	//核心栈
	
	
	/**
	* @Description: 进程类的构造函数  
	*/
	public Process(int id, int priority, int inTimes, int instrNum, ArrayList<Instruction> segment) {
		super(id, priority, inTimes, instrNum);   //构造子类PCB
		this.instructionSegments = segment;       //构造程序段 用户栈和核心栈
		userStack = new Stack<>();
		coreStack = new Stack<>();
		getAllProcess().add(this);       
//		showInstructionSegment();
	}

	
	
	/**
	* @Description: 将IR设置成PC指向程序段的的指令类别信息，
	* @throws
	*/
	public void setIRNewInstructionState() {
			this.setIR(instructionSegments.get(getPC()).getInstructionState());
	}
	
	/**
	* @Description: 获取当前进程的pc指向的程序段的具体指令
	* @return  int
	* @throws
	*/
	public int getCurrentInstructionID() {
		return instructionSegments.get(getPC()).getInstructionID();
	}
	
	/**
	* @Description: 打印要执行的程序综合（调试用）    
	* @throws
	*/
	public void showInstructionSegment() {
		for(Instruction e:instructionSegments) {
			System.out.println(e);
		}	
	}
	
	/**
	* @Description: 数据进入核心栈
	* @param register     
	* @throws
	*/
	public void inCoreStack(int register) {
		coreStack.push(register);
	}
	
	/**
	* @Description: 核心栈出栈
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
