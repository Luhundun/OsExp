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
 * @Description PCB��
 *
 */
public class PCB implements Comparable<PCB>{
	private int processID; //���̱��
	private int priority; //����������
	private int inTimes; //���̴���ʱ��
	private int endTimes; //���̽���ʱ��
	private int runTimes; //��������ʱ��
	private int turnTimes; //������תʱ��
	private int instructionNum; //�����а�����ָ����Ŀ
	private int psw;      //����״̬ 0Ϊδ֪ 1Ϊ���� 2Ϊ���� 3Ϊ����
	private int pc;    	   //�����������Ϣ����¼��һ��ָ���ַ
	private int ir;        //ָ���������Ϣ����¼��ǰִ�е�ָ������
	
	private int timeSliceLeft;    //��ǰ������cpu���е�ʣ��ʱ��Ƭ����������̬��Ϊ0
	private int readyQueneNum;      //��������λ�ñ��
	private int readyQueneTimes;    //�������н���ʱ��
	private int blockedQueneNum1;		//��������1λ�ñ��
	private int blockedQueneTimes1;	//��������1����ʱ��
	private int blockedQueneNum2;		//��������2λ�ñ��
	private int blockedQueneTimes2;	//��������2����ʱ��
	private int blockedQueneNum3;		//��������3λ�ñ��
	private int blockedQueneTimes3;	//��������3����ʱ��
	
	private static LinkedList<PCB> readyQuene = new LinkedList<PCB>();	//��������  ʹ�þ�̬����������PCB�����ĸ�����
	private static LinkedList<PCB> blockedQuene1 = new LinkedList<PCB>();	//��������1  java��Linkedlistʵ����Queue�ӿڣ����Ե�����ʹ��
	private static LinkedList<PCB> blockedQuene2 = new LinkedList<PCB>();	//��������2
	private static LinkedList<PCB> blockedQuene3 = new LinkedList<PCB>(); 	//��������3
	private static ArrayList<PCB> allPCB = new ArrayList<>();    //PCB��
	private static int PCBNum = 0;                //PCB����PCB����
	
	/**
	* @Description: PCB�Ĺ��캯�� 
	* @param id         �������
	* @param priority   �������ȼ�
	* @param inTimes    ���̽�����ҵʱ��
	* @param instrNum   �����ڵ�ָ����Ŀ
	*/
	public PCB(int id, int priority, int inTimes, int instrNum) {
		while(!isValidPCB(id))    //�����ظ��Ľ���id����id��1ֱ�����봴������PCB��id�ظ���
			id++;
		createProcess(id, priority, inTimes, instrNum);
	} 
	
	/**
	* @Description: ���һ������id�Ƿ����������ڹ��Ľ����ظ����ظ�����false
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
	* @Description: ��������ԭ��
	* @param id     ����id
	* @param inTimes   ���̽���ʱ��
	* @param priority  �������ȼ�
	* @param instrNum  ���̵�ָ������
	* @return PCB   
	* @throws
	*/
	protected synchronized void createProcess(int id, int priority, int inTimes, int instrNum) {
//		CPU.switchUserModeToKernelMode();           //CPUת�����ں�̬
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
		this.pc = 0;             //������ָ���ļ���1��ʼ������½���PCB��pcҲӦ�ô�1��ʼ
		this.ir = 0;
		readyQuene.offer(this);    //�������̺�����������
		allPCB.add(this);
		PCBNum++;
//		System.out.println("����"+id+"�Ѵ���������ʱ��Ϊ"+inTimes+"�����ȼ�Ϊ"+priority+"��ָ������Ϊ"+instructionNum);
//		OperatingSystemGUI.textArea1.append("����"+id+"�Ѵ���������ʱ��Ϊ"+this.inTimes+"�����ȼ�Ϊ"+priority+"��ָ������Ϊ"+instructionNum+"\n");
		OperatingSystemGUI.textArea2.append("����"+id+"�Ѵ���������ʱ��Ϊ"+this.inTimes+"�����ȼ�Ϊ"+priority+"��ָ������Ϊ"+instructionNum+"\n");
//		CPU.switchKernelModeToUserMode();         //CPUת�����û�̬
} 
	
	/**
	* @Description: ��������ԭ��  
	* @throws
	*/
	public synchronized void cancelProcess() {
		this.psw = 0;
		this.turnTimes = CPU.getTime() - this.inTimes + 1;
		allPCB.remove(this);
		readyQuene.remove(this); //�������Ľ���Ҫô��CPU������Ҫôϵͳ���ý����Ƚ�����������ٱ�����
		Process.getAllProcess().remove(findProcessWithPCB(this));
		OperatingSystemGUI.textArea2.append("����" + processID + "��������ִ����" + instructionNum + "��ָ�" + "����ʱ��Ϊ��" + runTimes + "����ʱ��Ϊ��" + CPU.getTime() + ",��תʱ��Ϊ:" + turnTimes + "\n");
		PCBNum--;
	}
	
	/**
	* @Description: ��������ԭ�Ϊ�˱�֤�̰߳�ȫ�����ô�ԭ������
	* @param process    
	* @throws
	*/
	public synchronized void blockProcess() { 
		this.psw = 3;
		this.timeSliceLeft = 0;
		if(ir == 1) {            //����ir�����ݾ��������ĸ���������
			joinBlockedQueue1(this);  //���÷�װ�õĽ�����������1�ķ���
		}
		else if(ir == 3){         //irΪ3ʱΪϵͳ��������豸
			joinBlockedQueue2(this);
		} 
		else if(ir == 2) {       //irΪ2ʱΪPVͨ��
			joinBlockedQueue3(this);
		};
	}
	
	/**
	* @Description: ���ѽ���ԭ�Ϊ�˱�֤�̰߳�ȫ�����ô�ԭ������
	* @param process    
	* @throws
	*/
	public synchronized void awakeProcess() {
		this.psw = 2;                //���ݵ����㷨�������ѵ�ԭ��һ��������������Ķ�ͷ
		if(ir == 1) {
			this.blockedQueneNum1 = -1;
		}
		else if(ir == 3){
			this.blockedQueneNum2 = -1;
		}
		else if(ir == 2) {
			this.blockedQueneNum3 = -1;
		}
		joinReadyQueue(this);          //�����ѵĽ��̼����������
	}
	
	/**
	* @Description: ʵ��Comparable�ӿڣ�����ʹ��Collections.sort������PCB�б���������������ȼ�����С��Ӧ����ǰ��
	* @param PCB ��һ�����Ƚϵ�PCB
	* @return int this���ȼ����ִ󷵻�������С���ظ�������ȷ���0
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
	* @Description: ��̬���ȼ��㷨�������������е�PCB�������ȼ�����   
	* @throws
	*/
	public synchronized static void staticPriority() {
		Collections.sort(readyQuene);
	}
	
	/**
	* @Description: ���赱ǰ����ʱ��ƬΪ2   
	* @throws
	*/
	public void reSetTimeSlice() {
		this.timeSliceLeft = 2;
	}
	
	/**
	* @Description: ���ʱ���ڴ˽���ռ����ʱ��Ƭ��ʣ��ʱ��Ƭ-1 
	* @throws
	*/
	public void useTimeSlice() {
		this.timeSliceLeft--;
	}
	
	/**
	* @Description: ʱ��Ƭǿ����������µ��ô˺��� 
	* @throws
	*/
	public void setTimeSliceUseOut() {
		this.timeSliceLeft = 0;
	}
	
	/**
	* @Description: ����ʱ��Ƭ�Ƿ�����  
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
	* @Description: �ı�PC��ֵ
	* @param tempPC     
	* @throws
	*/
	public void setPC(int tempPC) {
		this.pc = tempPC;
	}
	
	/**
	* @Description: PC+1,���Ҽ������Ƿ�ִ���꣬�����������������������̣߳�
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
	* @Description: PC+1,���Ҽ������Ƿ�ִ���꣬������������������CPU�� ���������Ƿ�Ӱ��cpu״̬
	* @throws
	*/
	public void cpuPlusPCAndCheckIfNeedToCancelTheProcess() {    
		if(pc < instructionNum - 1)       
			this.pc++;
		else {
			this.cancelProcess();
			CPU.setCpuWorkState(false);  //һ�����̽�������������ʱ����CPU��������������
		}
	}
	
	/**
	* @Description: ��ȡ��ǰpcֵ
	* @return int    
	* @throws
	*/
	public int getPC() {
		return this.pc;
	}
	
	
	/**
	* @Description: �ı�ir��ֵ
	* @param tempIR     
	* @throws
	*/
	public void setIR(int tempIR) {
		this.ir = tempIR;
	}
	
	
	/**
	* @Description: ��ȡ��ǰirֵ
	* @return int    
	* @throws
	*/
	public int getIR() {
		return this.ir;
	}
	
	/**
	* @Description: ��ȡpsw״̬
	* @return int    
	* @throws
	*/
	public int getPSW() {
		return this.psw;
	}
	
	/**
	* @Description: �趨psw״̬
	* @param tempPSW void    
	* @throws
	*/
	public void setPSW(int tempPSW) {
		this.psw = tempPSW;
	}
	
	/**
	* @Description: ��̬���������̼����������
	* @param aPCB     
	* @throws
	*/
	public static void joinReadyQueue(PCB aPCB) {
		readyQuene.offer(aPCB);
		aPCB.setReadyQueueNum(readyQuene.indexOf(aPCB));
		aPCB.setReadyQueueInTime(CPU.getTime());
		PCB.staticPriority();  //�����ȼ���С�Ծ������н��������Ŷ�
	}
	
	/**
	* @Description: ��̬���������̼�����������1
	* @param aPCB     
	* @throws
	*/
	public static void joinBlockedQueue1(PCB aPCB) {
		blockedQuene1.offer(aPCB);
		aPCB.setBlockedQueue1Num(blockedQuene1.indexOf(aPCB));
		aPCB.setBlockedQueue1InTime(CPU.getTime());
	}
	
	/**
	* @Description: ��̬���������̼�����������2
	* @param aPCB     
	* @throws
	*/
	public static void joinBlockedQueue2(PCB aPCB) {
		blockedQuene2.offer(aPCB);
		aPCB.setBlockedQueue2Num(blockedQuene2.indexOf(aPCB));
		aPCB.setBlockedQueue2InTime(CPU.getTime());		
	}
	
	/**
	* @Description: ��̬���������̼�����������3
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
	* @Description: ��������ͷ�����ӣ��������̸������   
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
	* @Description: ��������1ͷ�����ӣ��������̸������   
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
	* @Description: ��������2ͷ�����ӣ��������̸������   
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
	* @Description: ��������3ͷ�����ӣ��������̸������   
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
	* @Description: ���ý����������ʱ��
	* @param time    
	* @throws
	*/
	public void setReadyQueueInTime(int time) {
		this.readyQueneTimes = time;
	}
	
	/**
	* @Description: ��ȡ�����������ʱ��
	* @return int   
	* @throws
	*/
	public int getReadyQueueInTime() {
		return this.readyQueneTimes;
	}
	
	/**
	* @Description: �����ھ������е�λ��
	* @param num      
	* @throws
	*/
	public void setReadyQueueNum(int num) {
		this.readyQueneNum = num;
	}
	
	/**
	* @Description: ��ȡ�ھ������е�λ��
	* @return int    
	* @throws
	*/
	public int getReadyQueueNum() {
		return this.readyQueneNum;
	}
	/**
	* @Description: ���ý�����������1ʱ��
	* @param time    
	* @throws
	*/
	public void setBlockedQueue1InTime(int time) {
		this.blockedQueneTimes1 = time;
	}
	
	/**
	* @Description: ��ȡ������������1ʱ��
	* @return int   
	* @throws
	*/
	public int getBlockedQueue1InTime() {
		return this.blockedQueneTimes1;
	}
	
	/**
	* @Description: ��������������1��λ��
	* @param num      
	* @throws
	*/
	public void setBlockedQueue1Num(int num) {
		this.blockedQueneNum1 = num;
	}
	
	/**
	* @Description: ��ȡ����������1��λ��
	* @return int    
	* @throws
	*/
	public int getBlockedQueue1Num() {
		return this.blockedQueneNum1;
	}
	
	/**
	* @Description: ���ý�����������2ʱ��
	* @param time    
	* @throws
	*/
	public void setBlockedQueue2InTime(int time) {
		this.blockedQueneTimes2 = time;
	}
	
	/**
	* @Description: ��ȡ������������2ʱ��
	* @return int   
	* @throws
	*/
	public int getBlockedQueue2InTime() {
		return this.blockedQueneTimes2;
	}
	
	/**
	* @Description: ��������������2��λ��
	* @param num      
	* @throws
	*/
	public void setBlockedQueue2Num(int num) {
		this.blockedQueneNum2 = num;
	}
	
	/**
	* @Description: ��ȡ����������2��λ��
	* @return int    
	* @throws
	*/
	public int getBlockedQueue2Num() {
		return this.blockedQueneNum2;
	}
	
	/**
	* @Description: ���ý�����������3ʱ��
	* @param time    
	* @throws
	*/
	public void setBlockedQueue3InTime(int time) {
		this.blockedQueneTimes3 = time;
	}
	
	/**
	* @Description: ��ȡ������������3ʱ��
	* @return int   
	* @throws
	*/
	public int getBlockedQueue3InTime() {
		return this.blockedQueneTimes3;
	}
	
	/**
	* @Description: ��������������3��λ��
	* @param num      
	* @throws
	*/
	public void setBlockedQueue3Num(int num) {
		this.blockedQueneNum3 = num;
	}
	
	/**
	* @Description: ��ȡ����������3��λ��
	* @return int    
	* @throws
	*/
	public int getBlockedQueue3Num() {
		return this.blockedQueneNum3;
	}
	
	/**
	* @Description: ���ؾ������г���
	* @return int    
	* @throws
	*/
	public static int getReadyQueueLength() {
		return readyQuene.size();
	}
	
	/**
	* @Description: ������������1����
	* @return int    
	* @throws
	*/
	public static int getBlockedQueue1Length() {
		return blockedQuene1.size();
	}
	
	/**
	* @Description: ������������21����
	* @return int    
	* @throws
	*/
	

	public static int getBlockedQueue2Length() {
		return blockedQuene2.size();
	}
	
	/**
	* @Description: ������������3����
	* @return int    
	* @throws
	*/
	public static int getBlockedQueue3Length() {
		return blockedQuene3.size();
	}
	
	/**
	* @Description: չʾ�������еĽ��̺�   
	* @throws
	*/
	public static void showReadyQueueIds() {
		OperatingSystemGUI.textList1.setText("");  //ÿ�����GUI��ʾ
		for(PCB e :readyQuene) {
			OperatingSystemGUI.textArea1.append(String.valueOf(e.processID) + " ");
			OperatingSystemGUI.textList1.append(String.valueOf(e.processID) + "����ʱ��" + e.getReadyQueueInTime() + "\n");
		}
		OperatingSystemGUI.textArea1.append("\n\n");
	}
	
	/**
	* @Description: չʾ��������1��ĵĽ��̺�     
	* @throws
	*/
	public static void showBlockedQueue1Ids() {
		if(KeyBoard.getUsingProcess() != null)
			OperatingSystemGUI.textList2.append(KeyBoard.getUsingProcess().getID() + "����ʱ��" + KeyBoard.getUsingProcess().getBlockedQueue1InTime() + "\n");
		for(PCB e :blockedQuene1) {
			OperatingSystemGUI.textArea1.append(String.valueOf(e.processID) + " ");
			OperatingSystemGUI.textList2.append(String.valueOf(e.processID) + "����ʱ��" + e.getBlockedQueue1InTime() + "\n");
		}
		OperatingSystemGUI.textArea1.append("\n");
	}
	
	/**
	* @Description: չʾ��������2��ĵĽ��̺�  
	* @throws
	*/
	public static void showBlockedQueue2Ids() {
		if(Display.getUsingProcess() != null)
			OperatingSystemGUI.textList3.append(Display.getUsingProcess().getID() + "����ʱ��" + Display.getUsingProcess().getBlockedQueue1InTime() + "\n");
		for(PCB e :blockedQuene2) {
			OperatingSystemGUI.textArea1.append(String.valueOf(e.processID) + " ");
			OperatingSystemGUI.textList3.append(String.valueOf(e.processID) + "����ʱ��" + e.getBlockedQueue2InTime() + "\n");
		}
		OperatingSystemGUI.textArea1.append("\n");
	}
	
	/**
	* @Description: չʾ��������3��ĵĽ��̺�    
	* @throws
	*/
	public static void showBlockedQueue3Ids() {
		if(PV.getUsingProcess() != null)
			OperatingSystemGUI.textList4.append(PV.getUsingProcess().getID() + "����ʱ��" + PV.getUsingProcess().getBlockedQueue1InTime() + "\n");
		for(PCB e :blockedQuene3) {
			OperatingSystemGUI.textArea1.append(String.valueOf(e.processID) + " ");
			OperatingSystemGUI.textList4.append(String.valueOf(e.processID) + "����ʱ��" + e.getBlockedQueue3InTime() + "\n");
		}
		OperatingSystemGUI.textArea1.append("\n");
	}
	/**
	* @Description: Ϊ������CPU����ʱ�����
	* @throws
	*/
	public void plusProcessRunTime() {
		this.runTimes++;
	}
	
	
	/**
	* @Description: ��ȡ����ID
	* @return int    
	* @throws
	*/
	public int getID() {
		return this.processID;
	}
	
	/**
	* @Description: ����PCBȷ�����̣�PCB����̾���һһ��Ӧ��ϵ��
	* @param pcb        �����ҵ�PCB
	* @return Process    �ҵ��Ķ�Ӧ�Ľ��̻��߿յ�ַ
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
