/**  
* @Title: Display.java
* @Package code
* @author Lu Ning  
* @date 2020-10-28 9:59:28
* @version V1.0  
*/
package code;

/**
 * @Description 显示器类，用一个线程模拟，主要结构的注释见键盘类
 *
 */
public class Display extends Thread {
	private static boolean ifDisplayWork = false; //表示键盘输入线程状态，false为空闲，true为忙碌
    private static Process usingProcess = null;
	private static int lastUseTime = 0;

	public void run() {
		while(true) {
			OperatingSystemGUI.timerLock.lock();//请求锁
			try {
				OperatingSystemGUI.timerCondition.await();
				doWhatDisplayDoEverySecond();
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
	private void doWhatDisplayDoEverySecond() {
		OperatingSystemGUI.textList3.setText("");  //每秒清空GUI显示
		if(ifDisplayWork && CPU.getTime()-lastUseTime==4) {
			usingProcess.awakeProcess();   //系统调用结束，唤醒此进程
			usingProcess.interruptPlusPCAndCheckIfNeedToCancelTheProcess();   //此进程的pc指向下一条
			usingProcess = PCB.findProcessWithPCB(PCB.pollBlockedQueue2());
			if(usingProcess != null)
				lastUseTime = CPU.getTime()-1;
			else
				ifDisplayWork = false;
		}
		if(!ifDisplayWork){
//			System.out.println("显示器状态： 无进程请求");
			OperatingSystemGUI.textArea1.append("显示器状态： 无进程请求" + "\n");
		}
		else {
//			System.out.println("显示器状态：进程" + usingProcess.getID() + "请求，阻塞队列还有" + PCB.getBlockedQueue2Length() + "个进程" );
			OperatingSystemGUI.textArea1.append("显示器状态：进程" + usingProcess.getID() + "请求，阻塞队列还有" + PCB.getBlockedQueue2Length() + "个进程：");
			PCB.showBlockedQueue2Ids();
		} 
	}
	
	
	
	/**
	* @Description: 静态返回显示器状态
	* @return boolean    
	* @throws
	*/
	public static boolean getDisplayState() {
		return ifDisplayWork;
	}
	
	public static void setDisplayWork(Process p) {
		ifDisplayWork = true;
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
		Display.usingProcess = usingProcess;
	}
}

