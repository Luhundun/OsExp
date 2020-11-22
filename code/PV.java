/**  
* @Title: PV.java
* @Package code
* @Description: TODO(��һ�仰�������ļ���ʲô)
* @author Lu Ning  
* @date 2020-10-28 10:03:14
* @version V1.0  
*/
package code;

/**
 * @Description PVͨ���࣬��һ���߳�ģ�⣬��Ҫ�ṹ��ע�ͼ�������
 *
 */
public class PV extends Thread {
	private static boolean ifPVWork = false; //��ʾ���������߳�״̬��falseΪ���У�trueΪæµ
    private static Process usingProcess = null;
	private static int lastUseTime = 0;

	public void run() {
		while(true) {
			OperatingSystemGUI.timerLock.lock();//������
			try {
				OperatingSystemGUI.timerCondition.await();
				doWhatPVDoEverySecond();
				
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			finally{
				OperatingSystemGUI.timerLock.unlock();//�ͷ���
			}
			
		}
	}
	
	/**
	* @Description: ���ݲ�ͬ��״̬��ִ��ÿ�δ��̱߳����Ѻ��ִ�еĳ���   
	* @throws
	*/
	private void doWhatPVDoEverySecond() {
		OperatingSystemGUI.textList4.setText("");  //ÿ�����GUI��ʾ
		if(ifPVWork && CPU.getTime()-lastUseTime==3) {
			usingProcess.awakeProcess();   //PV���ý��������Ѵ˽���
			usingProcess.interruptPlusPCAndCheckIfNeedToCancelTheProcess();   //�˽��̵�pcָ����һ��
			usingProcess = PCB.findProcessWithPCB(PCB.pollBlockedQueue3());
			
			if(usingProcess != null)
				lastUseTime = CPU.getTime()-1;
			else
				ifPVWork = false;
		}
		if(!ifPVWork) {
//			System.out.println("PV״̬���޽�������");
			OperatingSystemGUI.textArea1.append("PV״̬���޽�������" + "\n");
		}
		else{
//			System.out.println("PV״̬������" + usingProcess.getID() + "�����������л���"+ PCB.getBlockedQueue3Length() + "�����̵ȴ�");
			OperatingSystemGUI.textArea1.append("PV״̬������" + usingProcess.getID() + "�����������л���"+ PCB.getBlockedQueue3Length() + "�����̵ȴ�");
			PCB.showBlockedQueue3Ids();
		}
	}
	
	/**
	* @Description: ��̬����PV״̬
	* @return boolean    
	* @throws
	*/
	public static boolean getPVState() {
		return ifPVWork;
	}
	
	public static void setPVWork(Process p) {
		ifPVWork = true;
		usingProcess = p;
		p.setPSW(3);
		lastUseTime = CPU.getTime();
	}

	/**
	 * @return the usingProcess
	 */
	public static Process getUsingProcess() {
		return usingProcess;
	}

	/**
	 * @param usingProcess the usingProcess to set
	 */
	public static void setUsingProcess(Process usingProcess) {
		PV.usingProcess = usingProcess;
	}
}

