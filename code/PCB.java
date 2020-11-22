/**  
* @Title: PCB.java
* @Package osexp1
* @Description: TODO
* @author Lu Ning  
* @date 2020-10-24 10:33:35
* @version V1.0  
*/
package code;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;


/**
 * @Description PCB类
 *
 */
public class PCB implements Comparable<PCB>{
	private int processID; //进程编号
	private int priority; //进程优先数
	private int inTimes; //进程创建时间
	private int endTimes; //进程结束时间
	private int runTimes; //进程运行时间
	private int turnTimes; //进程周转时间
	private int instructionNum; //进程中包含的指令数目
	private int psw;      //进程状态 0为未知 1为运行 2为就绪 3为阻塞
	private int pc;    	   //程序计数器信息，记录下一条指令地址
	private int ir;        //指令计数器信息，记录当前执行的指令类型
	
	private int timeSliceLeft;    //当前进程在cpu运行的剩余时间片，若非运行态则为0
	private int readyQueneNum;      //就绪队列位置编号
	private int readyQueneTimes;    //就绪队列进入时间
	private int blockedQueneNum1;		//阻塞队列1位置编号
	private int blockedQueneTimes1;	//阻塞队列1进入时间
	private int blockedQueneNum2;		//阻塞队列2位置编号
	private int blockedQueneTimes2;	//阻塞队列2进入时间
	private int blockedQueneNum3;		//阻塞队列3位置编号
	private int blockedQueneTimes3;	//阻塞队列3进入时间
	
	private static LinkedList<PCB> readyQuene = new LinkedList<PCB>();	//就绪队列  使用静态变量，所有PCB共享四个队列
	private static LinkedList<PCB> blockedQuene1 = new LinkedList<PCB>();	//阻塞队列1  java中Linkedlist实现了Queue接口，可以当队列使用
	private static LinkedList<PCB> blockedQuene2 = new LinkedList<PCB>();	//阻塞队列2
	private static LinkedList<PCB> blockedQuene3 = new LinkedList<PCB>(); 	//阻塞队列3
	private static ArrayList<PCB> allPCB = new ArrayList<>();    //PCB池
	private static int PCBNum = 0;                //PCB池中PCB数量
	
	/**
	* @Description: PCB的构造函数 
	* @param id         进程序号
	* @param priority   进程优先级
	* @param inTimes    进程进入作业时间
	* @param instrNum   进程内的指令数目
	*/
	public PCB(int id, int priority, int inTimes, int instrNum) {
		while(!isValidPCB(id))    //遇到重复的进程id，给id加1直至不与创建过的PCB的id重复。
			id++;
		createProcess(id, priority, inTimes, instrNum);
	} 
	
	/**
	* @Description: 检查一个进程id是否与其他存在过的进程重复，重复返回false
	* @param id
	* @return boolean    
	* @throws
	*/
	public boolean isValidPCB(int id) {
		for(PCB e : allPCB) {
			if(e.getID() == id)
				return false;
		}
		return true;
	}
	
	
	/**
	* @Description: 创建进程原语
	* @param id     进程id
	* @param inTimes   进程进入时间
	* @param priority  进程优先级
	* @param instrNum  进程的指令数量
	* @return PCB   
	* @throws
	*/
	protected synchronized void createProcess(int id, int priority, int inTimes, int instrNum) {
//		CPU.switchUserModeToKernelMode();           //CPU转化到内核态
		this.processID = id;
		this.priority = priority;
		this.inTimes = CPU.getTime();
		this.instructionNum = instrNum;
		this.runTimes = 0;
		this.turnTimes = 0;
		this.psw = 2;
		this.timeSliceLeft = 0;
		this.readyQueneNum = readyQuene.size();
		this.readyQueneTimes = CPU.getTime();
		this.blockedQueneNum1 = -1;
		this.blockedQueneTimes1 = 0;
		this.blockedQueneNum2 = -1;
		this.blockedQueneTimes2 = 0;
		this.blockedQueneNum3 = -1;
		this.blockedQueneTimes3 = 0;
		this.pc = 0;             //给定的指令文件从1开始，因此新建的PCB的pc也应该从1开始
		this.ir = 0;
		readyQuene.offer(this);    //创建进程后进入就绪队列
		allPCB.add(this);
		PCBNum++;
//		System.out.println("进程"+id+"已创建，进入时间为"+inTimes+"，优先级为"+priority+"，指令数量为"+instructionNum);
//		OperatingSystemGUI.textArea1.append("进程"+id+"已创建，进入时间为"+this.inTimes+"，优先级为"+priority+"，指令数量为"+instructionNum+"\n");
		OperatingSystemGUI.textArea2.append("进程"+id+"已创建，进入时间为"+this.inTimes+"，优先级为"+priority+"，指令数量为"+instructionNum+"\n");
//		CPU.switchKernelModeToUserMode();         //CPU转化到用户态
} 
	
	/**
	* @Description: 撤销进程原语  
	* @throws
	*/
	public synchronized void cancelProcess() {
		this.psw = 0;
		this.turnTimes = CPU.getTime() - this.inTimes + 1;
		allPCB.remove(this);
		readyQuene.remove(this); //被撤销的进程要么在CPU被撤销要么系统调用结束先进入就绪队列再被撤销
		Process.getAllProcess().remove(findProcessWithPCB(this));
		OperatingSystemGUI.textArea2.append("进程" + processID + "被撤销，执行了" + instructionNum + "条指令。" + "运行时间为：" + runTimes + "撤销时间为：" + CPU.getTime() + ",周转时间为:" + turnTimes + "\n");
		PCBNum--;
	}
	
	/**
	* @Description: 阻塞进程原语，为了保证线程安全，调用此原语会加锁
	* @param process    
	* @throws
	*/
	public synchronized void blockProcess() { 
		this.psw = 3;
		this.timeSliceLeft = 0;
		if(ir == 1) {            //根据ir的内容决定进入哪个阻塞队列
			joinBlockedQueue1(this);  //调用封装好的进入阻塞队列1的方法
		}
		else if(ir == 3){         //ir为3时为系统调用输出设备
			joinBlockedQueue2(this);
		} 
		else if(ir == 2) {       //ir为2时为PV通信
			joinBlockedQueue3(this);
		};
	}
	
	/**
	* @Description: 唤醒进程原语，为了保证线程安全，调用此原语会加锁
	* @param process    
	* @throws
	*/
	public synchronized void awakeProcess() {
		this.psw = 2;                //根据调度算法，被唤醒的原语一定是阻塞队列里的队头
		if(ir == 1) {
			this.blockedQueneNum1 = -1;
		}
		else if(ir == 3){
			this.blockedQueneNum2 = -1;
		}
		else if(ir == 2) {
			this.blockedQueneNum3 = -1;
		}
		joinReadyQueue(this);          //被唤醒的进程加入就绪队列
	}
	
	/**
	* @Description: 实现Comparable接口，方便使用Collections.sort函数对PCB列表进行排序，其中优先级数字小着应排在前面
	* @param PCB 另一个被比较的PCB
	* @return int this优先级数字大返回正数，小返回负数，相等返回0
	* @throws   
	 */
	public int compareTo(PCB p) {
		if(this.priority>p.priority)
			return 1;
		else if(this.priority<p.priority)
			return -1;
		else
			return 0;
	}
	
	
	/**
	* @Description: 静态优先级算法，将就绪队列中的PCB按照优先级排序   
	* @throws
	*/
	public synchronized static void staticPriority() {
		Collections.sort(readyQuene);
	}
	
	/**
	* @Description: 重设当前进程时间片为2   
	* @throws
	*/
	public void reSetTimeSlice() {
		this.timeSliceLeft = 2;
	}
	
	/**
	* @Description: 这个时钟内此进程占用了时间片，剩余时间片-1 
	* @throws
	*/
	public void useTimeSlice() {
		this.timeSliceLeft--;
	}
	
	/**
	* @Description: 时间片强制用完情况下调用此函数 
	* @throws
	*/
	public void setTimeSliceUseOut() {
		this.timeSliceLeft = 0;
	}
	
	/**
	* @Description: 返回时间片是否用完  
	* @throws
	*/
	public boolean ifTimeSliceLeft() {
		if(this.timeSliceLeft==0)
			return false;
		else {
			return true;
		}
	}
	
	/**
	* @Description: 改变PC的值
	* @param tempPC     
	* @throws
	*/
	public void setPC(int tempPC) {
		this.pc = tempPC;
	}
	
	/**
	* @Description: PC+1,并且检查进程是否执行完，是则撤销（此请求来自其他线程）
	* @throws
	*/
	public void interruptPlusPCAndCheckIfNeedToCancelTheProcess() {    
		if(pc < instructionNum - 1)       
			this.pc++;
		else {
			this.cancelProcess();
		}
	}
	
	/**
	* @Description: PC+1,并且检查进程是否执行完，是则撤销（此请求来自CPU） 区别在于是否影响cpu状态
	* @throws
	*/
	public void cpuPlusPCAndCheckIfNeedToCancelTheProcess() {    
		if(pc < instructionNum - 1)       
			this.pc++;
		else {
			this.cancelProcess();
			CPU.setCpuWorkState(false);  //一个进程结束被撤销，短时间内CPU可以视作不工作
		}
	}
	
	/**
	* @Description: 获取当前pc值
	* @return int    
	* @throws
	*/
	public int getPC() {
		return this.pc;
	}
	
	
	/**
	* @Description: 改变ir的值
	* @param tempIR     
	* @throws
	*/
	public void setIR(int tempIR) {
		this.ir = tempIR;
	}
	
	
	/**
	* @Description: 获取当前ir值
	* @return int    
	* @throws
	*/
	public int getIR() {
		return this.ir;
	}
	
	/**
	* @Description: 获取psw状态
	* @return int    
	* @throws
	*/
	public int getPSW() {
		return this.psw;
	}
	
	/**
	* @Description: 设定psw状态
	* @param tempPSW void    
	* @throws
	*/
	public void setPSW(int tempPSW) {
		this.psw = tempPSW;
	}
	
	/**
	* @Description: 静态方法，进程加入就绪队列
	* @param aPCB     
	* @throws
	*/
	public static void joinReadyQueue(PCB aPCB) {
		readyQuene.offer(aPCB);
		aPCB.setReadyQueueNum(readyQuene.indexOf(aPCB));
		aPCB.setReadyQueueInTime(CPU.getTime());
		PCB.staticPriority();  //按优先级大小对就绪队列进行重新排队
	}
	
	/**
	* @Description: 静态方法，进程加入阻塞队列1
	* @param aPCB     
	* @throws
	*/
	public static void joinBlockedQueue1(PCB aPCB) {
		blockedQuene1.offer(aPCB);
		aPCB.setBlockedQueue1Num(blockedQuene1.indexOf(aPCB));
		aPCB.setBlockedQueue1InTime(CPU.getTime());
	}
	
	/**
	* @Description: 静态方法，进程加入阻塞队列2
	* @param aPCB     
	* @throws
	*/
	public static void joinBlockedQueue2(PCB aPCB) {
		blockedQuene2.offer(aPCB);
		aPCB.setBlockedQueue2Num(blockedQuene2.indexOf(aPCB));
		aPCB.setBlockedQueue2InTime(CPU.getTime());		
	}
	
	/**
	* @Description: 静态方法，进程加入阻塞队列3
	* @param aPCB     
	* @throws
	*/
	public static void joinBlockedQueue3(PCB aPCB) {
		blockedQuene3.offer(aPCB);
		aPCB.setBlockedQueue3Num(blockedQuene3.indexOf(aPCB));
		aPCB.setBlockedQueue3InTime(CPU.getTime());
	}

	
	/**
	 * @return 
	* @Description: 就绪队列头部出队，其他进程更新序号   
	* @throws
	*/
	public static PCB pollReadyQueue() {
		PCB pollPcb = readyQuene.poll();
		for(PCB e : readyQuene) {
			e.setReadyQueueNum(readyQuene.indexOf(e));
		}
		return pollPcb;
	}
	
	/**
	* @Description: 阻塞队列1头部出队，其他进程更新序号   
	* @throws
	*/
	public static PCB pollBlockedQueue1() {
		PCB pollPcb = blockedQuene1.poll();
		for(PCB e : blockedQuene1) {
			e.setBlockedQueue1Num(blockedQuene1.indexOf(e));
		}
		return pollPcb;
	}
	
	/**
	* @Description: 阻塞队列2头部出队，其他进程更新序号   
	* @throws
	*/
	public static PCB pollBlockedQueue2() {
		PCB pollPcb = blockedQuene2.poll();
		for(PCB e : blockedQuene2) {
			e.setBlockedQueue2Num(blockedQuene2.indexOf(e));
		}
		return pollPcb;
	}
	
	/**
	* @Description: 阻塞队列3头部出队，其他进程更新序号   
	* @throws
	*/
	public static PCB pollBlockedQueue3() {
		PCB pollPcb = blockedQuene3.poll();
		for(PCB e : blockedQuene3) {
			e.setBlockedQueue3Num(blockedQuene3.indexOf(e));
		}
		return pollPcb;
	}

	
	/**
	* @Description: 设置进入就绪队列时间
	* @param time    
	* @throws
	*/
	public void setReadyQueueInTime(int time) {
		this.readyQueneTimes = time;
	}
	
	/**
	* @Description: 获取进入就绪队列时间
	* @return int   
	* @throws
	*/
	public int getReadyQueueInTime() {
		return this.readyQueneTimes;
	}
	
	/**
	* @Description: 设置在就绪队列的位置
	* @param num      
	* @throws
	*/
	public void setReadyQueueNum(int num) {
		this.readyQueneNum = num;
	}
	
	/**
	* @Description: 获取在就绪队列的位置
	* @return int    
	* @throws
	*/
	public int getReadyQueueNum() {
		return this.readyQueneNum;
	}
	/**
	* @Description: 设置进入阻塞队列1时间
	* @param time    
	* @throws
	*/
	public void setBlockedQueue1InTime(int time) {
		this.blockedQueneTimes1 = time;
	}
	
	/**
	* @Description: 获取进入阻塞队列1时间
	* @return int   
	* @throws
	*/
	public int getBlockedQueue1InTime() {
		return this.blockedQueneTimes1;
	}
	
	/**
	* @Description: 设置在阻塞队列1的位置
	* @param num      
	* @throws
	*/
	public void setBlockedQueue1Num(int num) {
		this.blockedQueneNum1 = num;
	}
	
	/**
	* @Description: 获取在阻塞队列1的位置
	* @return int    
	* @throws
	*/
	public int getBlockedQueue1Num() {
		return this.blockedQueneNum1;
	}
	
	/**
	* @Description: 设置进入阻塞队列2时间
	* @param time    
	* @throws
	*/
	public void setBlockedQueue2InTime(int time) {
		this.blockedQueneTimes2 = time;
	}
	
	/**
	* @Description: 获取进入阻塞队列2时间
	* @return int   
	* @throws
	*/
	public int getBlockedQueue2InTime() {
		return this.blockedQueneTimes2;
	}
	
	/**
	* @Description: 设置在阻塞队列2的位置
	* @param num      
	* @throws
	*/
	public void setBlockedQueue2Num(int num) {
		this.blockedQueneNum2 = num;
	}
	
	/**
	* @Description: 获取在阻塞队列2的位置
	* @return int    
	* @throws
	*/
	public int getBlockedQueue2Num() {
		return this.blockedQueneNum2;
	}
	
	/**
	* @Description: 设置进入阻塞队列3时间
	* @param time    
	* @throws
	*/
	public void setBlockedQueue3InTime(int time) {
		this.blockedQueneTimes3 = time;
	}
	
	/**
	* @Description: 获取进入阻塞队列3时间
	* @return int   
	* @throws
	*/
	public int getBlockedQueue3InTime() {
		return this.blockedQueneTimes3;
	}
	
	/**
	* @Description: 设置在阻塞队列3的位置
	* @param num      
	* @throws
	*/
	public void setBlockedQueue3Num(int num) {
		this.blockedQueneNum3 = num;
	}
	
	/**
	* @Description: 获取在阻塞队列3的位置
	* @return int    
	* @throws
	*/
	public int getBlockedQueue3Num() {
		return this.blockedQueneNum3;
	}
	
	/**
	* @Description: 返回就绪队列长度
	* @return int    
	* @throws
	*/
	public static int getReadyQueueLength() {
		return readyQuene.size();
	}
	
	/**
	* @Description: 返回阻塞队列1长度
	* @return int    
	* @throws
	*/
	public static int getBlockedQueue1Length() {
		return blockedQuene1.size();
	}
	
	/**
	* @Description: 返回阻塞队列21长度
	* @return int    
	* @throws
	*/
	

	public static int getBlockedQueue2Length() {
		return blockedQuene2.size();
	}
	
	/**
	* @Description: 返回阻塞队列3长度
	* @return int    
	* @throws
	*/
	public static int getBlockedQueue3Length() {
		return blockedQuene3.size();
	}
	
	/**
	* @Description: 展示就绪队列的进程号   
	* @throws
	*/
	public static void showReadyQueueIds() {
		OperatingSystemGUI.textList1.setText("");  //每秒清空GUI显示
		for(PCB e :readyQuene) {
			OperatingSystemGUI.textArea1.append(String.valueOf(e.processID) + " ");
			OperatingSystemGUI.textList1.append(String.valueOf(e.processID) + "进入时间" + e.getReadyQueueInTime() + "\n");
		}
		OperatingSystemGUI.textArea1.append("\n\n");
	}
	
	/**
	* @Description: 展示阻塞队列1里的的进程号     
	* @throws
	*/
	public static void showBlockedQueue1Ids() {
		if(KeyBoard.getUsingProcess() != null)
			OperatingSystemGUI.textList2.append(KeyBoard.getUsingProcess().getID() + "进入时间" + KeyBoard.getUsingProcess().getBlockedQueue1InTime() + "\n");
		for(PCB e :blockedQuene1) {
			OperatingSystemGUI.textArea1.append(String.valueOf(e.processID) + " ");
			OperatingSystemGUI.textList2.append(String.valueOf(e.processID) + "进入时间" + e.getBlockedQueue1InTime() + "\n");
		}
		OperatingSystemGUI.textArea1.append("\n");
	}
	
	/**
	* @Description: 展示阻塞队列2里的的进程号  
	* @throws
	*/
	public static void showBlockedQueue2Ids() {
		if(Display.getUsingProcess() != null)
			OperatingSystemGUI.textList3.append(Display.getUsingProcess().getID() + "进入时间" + Display.getUsingProcess().getBlockedQueue1InTime() + "\n");
		for(PCB e :blockedQuene2) {
			OperatingSystemGUI.textArea1.append(String.valueOf(e.processID) + " ");
			OperatingSystemGUI.textList3.append(String.valueOf(e.processID) + "进入时间" + e.getBlockedQueue2InTime() + "\n");
		}
		OperatingSystemGUI.textArea1.append("\n");
	}
	
	/**
	* @Description: 展示阻塞队列3里的的进程号    
	* @throws
	*/
	public static void showBlockedQueue3Ids() {
		if(PV.getUsingProcess() != null)
			OperatingSystemGUI.textList4.append(PV.getUsingProcess().getID() + "进入时间" + PV.getUsingProcess().getBlockedQueue1InTime() + "\n");
		for(PCB e :blockedQuene3) {
			OperatingSystemGUI.textArea1.append(String.valueOf(e.processID) + " ");
			OperatingSystemGUI.textList4.append(String.valueOf(e.processID) + "进入时间" + e.getBlockedQueue3InTime() + "\n");
		}
		OperatingSystemGUI.textArea1.append("\n");
	}
	/**
	* @Description: 为进程在CPU运行时间计数
	* @throws
	*/
	public void plusProcessRunTime() {
		this.runTimes++;
	}
	
	
	/**
	* @Description: 获取进程ID
	* @return int    
	* @throws
	*/
	public int getID() {
		return this.processID;
	}
	
	/**
	* @Description: 根据PCB确定进程（PCB与进程具有一一对应关系）
	* @param pcb        被查找的PCB
	* @return Process    找到的对应的进程或者空地址
	* @throws
	*/
	public static Process findProcessWithPCB(PCB pcb) {	
		if(pcb == null)
			return null;
		for(Process e : Process.getAllProcess()) {
			if(pcb.getID() == e.getID())
				return e;
		}
		return null;
	}


	/**
	 * @return the endTimes
	 */
	public int getEndTimes() {
		return endTimes;
	}


	/**
	 * @param endTimes the endTimes to set
	 */
	public void setEndTimes(int endTimes) {
		this.endTimes = endTimes;
	}


	/**
	 * @return the runTimes
	 */
	public int getRunTimes() {
		return runTimes;
	}


	/**
	 * @param runTimes the runTimes to set
	 */
	public void setRunTimes(int runTimes) {
		this.runTimes = runTimes;
	}


	/**
	 * @return the timeSliceLeft
	 */
	
	
	public int getTimeSliceLeft() {
		return timeSliceLeft;
	}


	/**
	 * @param timeSliceLeft the timeSliceLeft to set
	 */
	public void setTimeSliceLeft(int timeSliceLeft) {
		this.timeSliceLeft = timeSliceLeft;
	}

	/**
	 * @return the pCBNum
	 */
	public static int getPCBNum() {
		return PCBNum;
	}

	/**
	 * @param pCBNum the pCBNum to set
	 */
	public static void setPCBNum(int pCBNum) {
		PCBNum = pCBNum;
	}

}
