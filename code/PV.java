/**  
* @Title: PV.java
* @Package code
* @Description: TODO(用一句话描述该文件做什么)
* @author Lu Ning  
* @date 2020-10-28 10:03:14
* @version V1.0  
*/
package code;

/**
 * @Description PV通信类，用一个线程模拟，主要结构的注释见键盘类
 *
 */
public class PV extends Thread {
	private static boolean ifPVWork = false; //表示键盘输入线程状态，false为空闲，true为忙碌
    private static Process usingProcess = null;
	private static int lastUseTime = 0;

	public void run() {
		while(true) {
			OperatingSystemGUI.timerLock.lock();//请求锁
			try {
				OperatingSystemGUI.timerCondition.await();
				doWhatPVDoEverySecond();
				
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			finally{
				OperatingSystemGUI.timerLock.unlock();//释放锁
			}
			
		}
	}
	
	/**
	* @Description: 根据不同的状态，执行每次此线程被唤醒后该执行的程序   
	* @throws
	*/
	private void doWhatPVDoEverySecond() {
		OperatingSystemGUI.textList4.setText("");  //每秒清空GUI显示
		if(ifPVWork && CPU.getTime()-lastUseTime==3) {
			usingProcess.awakeProcess();   //PV调用结束，唤醒此进程
			usingProcess.interruptPlusPCAndCheckIfNeedToCancelTheProcess();   //此进程的pc指向下一条
			usingProcess = PCB.findProcessWithPCB(PCB.pollBlockedQueue3());
			
			if(usingProcess != null)
				lastUseTime = CPU.getTime()-1;
			else
				ifPVWork = false;
		}
		if(!ifPVWork) {
//			System.out.println("PV状态：无进程请求");
			OperatingSystemGUI.textArea1.append("PV状态：无进程请求" + "\n");
		}
		else{
//			System.out.println("PV状态：进程" + usingProcess.getID() + "请求，阻塞队列还有"+ PCB.getBlockedQueue3Length() + "个进程等待");
			OperatingSystemGUI.textArea1.append("PV状态：进程" + usingProcess.getID() + "请求，阻塞队列还有"+ PCB.getBlockedQueue3Length() + "个进程等待");
			PCB.showBlockedQueue3Ids();
		}
	}
	
	/**
	* @Description: 静态返回PV状态
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

