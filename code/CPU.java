/**  
* @Title: CPU.java
* @Package osexp1
* @author Lu Ning  
* @date 2020-10-23 15:10:59
* @version V1.0  
*/
package code;

/**
 * @Description ģ��CPU�࣬����Ĭ�ϵ��ǵ��˴������������з��������Զ���Ϊ��̬��
 *
 */
public class CPU {
	private static int pc;    	   //�����������Ϣ
	private static int ir;        //ָ����Ϣ
	private static int psw;      //����״̬�ּĴ���
	private static int cpuTime = 0;  //���������е���ʱ��
	private static boolean ifCpuWork = false;  // cpu�Ƿ���
	private static boolean ifCpuCloseInterrupt = false;  //���жϱ�־λ��Ϊ�˼�ģ�⣬���Ե���falseʱcpu�����û�̬��trueʱ���ں���̬
	static Process workingProcess = null;          //����CPU�����Ľ���,��Ϊpublic�������඼֪���ĸ��ڹ�����
	
	/**
	* @Description: ���ݽ��̵�ǰ��ָ�����Ӧ�Ĳ���   
	* @throws
	*/
	public static void doInstruction() {
		workingProcess.plusProcessRunTime();
		workingProcess.useTimeSlice();
		workingProcess.setIRNewInstructionState();  //ȷ��ÿ��ִ��ָ��ʱ����ǰָ������µ�
		ir = workingProcess.getIR();
		OperatingSystemGUI.textField2.setText(String.valueOf(workingProcess.getID()));
		OperatingSystemGUI.textField3.setText(String.valueOf(workingProcess.getPC()));
		OperatingSystemGUI.textField4.setText(String.valueOf(workingProcess.getIR()));	
		OperatingSystemGUI.textArea1.append("CPU״̬���û�̬������ִ�н���" + workingProcess.getID() + "��" + workingProcess.getCurrentInstructionID() + "��ָ�����Ϊ" + ir +"\n");
		if(ir == 0) {              //����ִ��ָ��
			CPU.setCpuWorkState(true);
			OperatingSystemGUI.textField1.setText("�û�̬");
			workingProcess.cpuPlusPCAndCheckIfNeedToCancelTheProcess();                 	  //����PCBָ����һ��ָ�
		}
		else if(ir == 1 ) {                                                             	 //ϵͳ���ü���
			switchUserModeToKernelMode();     												//CPU�û�̬ת������̬
			if(KeyBoard.getKeyBoardState())
				workingProcess.blockProcess(); 											    //������ԭ������Ӧ�����������Ŷ�
			else
				KeyBoard.setKeyBoardWorkForAProcess(workingProcess);
			switchKernelModeToUserMode();   												 //����̬ת��Ϊ�û�̬
			CPU.ifCpuWork = false;             												 //����false����Ϊ��ǿ������ʣ��ʱ��Ƭ�����ҷ����ж�ָ���Ƿ�ȫ��ִ����
		}else if(ir == 3) {       															 //ϵͳ������ʾ��
			switchUserModeToKernelMode();    												//CPU�û�̬ת������̬
			if(Display.getDisplayState())
				workingProcess.blockProcess();  											 //������ԭ������Ӧ�����������Ŷ�
			else 
				Display.setDisplayWork(workingProcess);
			switchKernelModeToUserMode();   												//����̬ת��Ϊ�û�̬
			CPU.ifCpuWork = false;
		}
		else if(ir == 2) {        														    //ϵͳ����PVͨ���߳�
			if(PV.getPVState())
				workingProcess.blockProcess();
			else 
				PV.setPVWork(workingProcess);
			CPU.ifCpuWork = false;
		}
		OperatingSystemGUI.textArea1.append("����������" + PCB.getReadyQueueLength() + "������:");
		PCB.showReadyQueueIds();
	}
	
	/**
	* @Description: CPU�û�̬ת�ں�̬  
	* @throws
	*/
	public static void switchUserModeToKernelMode() {   
		CPU.ifCpuCloseInterrupt = true;  //���ж�
		workingProcess.inCoreStack(pc);    //ģ���ֳ�����
		workingProcess.inCoreStack(ir);
		workingProcess.inCoreStack(psw);
		OperatingSystemGUI.textField1.setText("�ں�̬");
	}
	
	/**
	* @Description: CPU�ں�̬ת�û�̬   
	* @throws
	*/
	public static void switchKernelModeToUserMode() {     //CPU�ں�̬ת�û�̬
		psw = workingProcess.outCoreStack();
		ir = workingProcess.outCoreStack();     //ģ�ⷵ���ֳ�
		pc = workingProcess.outCoreStack();
		CPU.ifCpuCloseInterrupt = false;      //ģ�⿪�ж�
		OperatingSystemGUI.textField1.setText("�û�̬");
	}

	
//	public static void doInstruction() {
//		workingProcess.plusProcessRunTime();
//		workingProcess.useTimeSlice();
//		workingProcess.setIRNewInstructionState();  //ȷ��ÿ��ִ��ָ��ʱ����ǰָ������µ�
//		ir = workingProcess.getIR();
//		
//		if(!CPU.ifCpuCloseIntrrupt)           //cpu���ں���̬���޷����н�����
//			System.out.println("CPU״̬���û�̬������ִ�н���" + workingProcess.getID() + "��" + workingProcess.getCurrentInstructionID() + "��ָ�����Ϊ" + ir);
//		else {                             
//			System.out.println("CPU״̬������̬��" + workingProcess.getID() + "��" + workingProcess.getCurrentInstructionID() + "��ָ���������жϴ������");
//			if(ir == 0) {
//	//			System.out.println("�������л���" + PCB.getReadyQueueLength() + "������");
//				CPU.setCpuWorkState(true);
//				workingProcess.plusPCAndCheckIfNeedToCancelTheProcess();  //����PCBָ����һ��ָ�
//			}
//			else if(ir == 1 ) {
//				//�����ģʽ�л�
//				if(KeyBoard.getKeyBoardState())
//					workingProcess.blockProcess();  //������ԭ������Ӧ�����������Ŷ�
//				else
//					KeyBoard.setKeyBoardWork(workingProcess);
//				CPU.ifCpuWork = false;              //����false����Ϊ��ǿ������ʣ��ʱ��Ƭ�����ҷ����ж�ָ���Ƿ�ȫ��ִ����
//			}else if(ir == 3) {
//				//�����ģʽ�л�
//				if(Display.getDisplayState())
//					workingProcess.blockProcess();  //������ԭ������Ӧ�����������Ŷ�
//				else 
//					Display.setDisplayWork(workingProcess);
//				CPU.ifCpuWork = false;
//			}
//			else if(ir == 2) {
//				if(PV.getPVState())
//					workingProcess.blockProcess();
//				else 
//					PV.setPVWork(workingProcess);
//				CPU.ifCpuCloseIntrrupt = true;
//			}
//		}
//		System.out.println("����������" + PCB.getReadyQueueLength() + "�����̡�\n\n");
//	}
	
	/**
	* @Description: ���ú���ʱ�ӹ�ȥһ��  
	* @throws
	*/
	public static void passTime() {
		cpuTime++;
	}
	
	/**
	* @Description: ��ȡ��ǰʱ��
	* @return int    
	* @throws
	*/
	public static int getTime() {
		return CPU.cpuTime;
	}
	
	/**
	* @Description: ��ȡCPU����״̬
	* @return boolean    
	* @throws
	*/
	public static boolean getCpuWorkState() {
		return ifCpuWork;
	}
	
	/**
	* @Description: ����CPU����״̬
	* @param state   �����õ�״̬    
	* @throws
	*/
	public static void setCpuWorkState(boolean state) {
		ifCpuWork = state;
	}
	
	/**
	* @Description:  ����CPU��PC�Ĵ���������Ҫִ����һ����ַ��ָ��
	* @param tempPC �����õ�pc    
	* @throws
	*/
	public static void setPC(int tempPC) {
		pc = tempPC;
	}
	
	/**
	* @Description: ����CPU��IR�Ĵ���������Ҫִ��ʲôָ��
	* @param tempIR     
	* @throws
	*/
	public static void setIR(int tempIR) {
		ir = tempIR;
	}
	
	/**
	* @Description: ����CPU��PSW�Ĵ���
	* @param tempPSW void    
	* @throws
	*/
	public static void setPSW(int tempPSW) {
		psw = tempPSW;
	}

	/**
	 * @return the ifCpuCloseIntrrupt
	 */
	public static boolean getIfCpuCloseIntrrupt() {
		return ifCpuCloseInterrupt;
	}

	/**
	 * @param ifCpuCloseIntrrupt the ifCpuCloseIntrrupt to se
	 */
	public static void setIfCpuCloseIntrrupt(boolean ifCpuCloseIntrrupt) {
		CPU.ifCpuCloseInterrupt = ifCpuCloseIntrrupt;
	}
}
