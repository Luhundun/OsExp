/**  
* @Title: KeyBorad.java
* @Package code
* @author Lu Ning  
* @date 2020-10-27 22:49:11
* @version V1.0  
*/
package code;

/**
 * @Descriptio 模拟键盘进程，用java的一个线程模拟
 *
 */
public class KeyBoard extends Thread {
	private static boolean ifKeyboardWork = false; //表示键盘输入线程状态，false为空闲，true为忙碌
    private static Process usingProcess = null;    //正在等待键盘结果的线程
	private static int lastUseTime = 0;            //用来计数等待的线程已经等了多久
	public void run() {
		while(true) {
			OperatingSystemGUI.timerLock.lock();//请求锁
			try {
				OperatingSystemGUI.timerCondition.await();        //等到时钟进程发出时钟中断，再开始执行下面操作
				doWhatKeyBoardDoEverySecond();                   //执行每秒此线程被唤醒后该执行的程序   
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
	private void doWhatKeyBoardDoEverySecond() {
		OperatingSystemGUI.textList2.setText("");  //每秒清空GUI显示
		if(ifKeyboardWork && CPU.getTime()-lastUseTime==5) {   //键盘中断需要4秒，如果阻塞队列为空，运行指令那一秒也会计算，因此此处差应为5秒
			usingProcess.awakeProcess();   //系统调用结束，唤醒此进程
			usingProcess.interruptPlusPCAndCheckIfNeedToCancelTheProcess();   //此进程的pc指向下一条
			usingProcess = PCB.findProcessWithPCB(PCB.pollBlockedQueue1());  //调入阻塞队列其他进程，如果有返回队头，如果没有返回空地址
			if(usingProcess != null)                 //如果阻塞队列不空队头得到键盘，空则空闲
				lastUseTime = CPU.getTime()-1;       //1秒作为阻塞队列后续进程获得键盘的补偿
			else
				ifKeyboardWork = false;
		}
		if(!ifKeyboardWork){                    //根据键盘的状态输出信息
			OperatingSystemGUI.textArea1.append("键盘状态：无进程请求" + "\n");
		}
		else {
			OperatingSystemGUI.textArea1.append("键盘状态：进程" + usingProcess.getID() + "请求，阻塞队列还有" + PCB.getBlockedQueue1Length() + "个进程:");
			PCB.showBlockedQueue1Ids();
		}
	}
	
	
	/**
	* @Description: 静态返回键盘状态
	* @return boolean    
	* @throws
	*/
	public static boolean getKeyBoardState() {
		return ifKeyboardWork;
	}
	/**
	* @Description: 设置键盘状态
	* @param state     
	* @throws
	*/
	public static void setKeyBoardState(boolean state) {
		ifKeyboardWork = state;
	}
	
	/**
	* @Description: 让一个进程得到键盘后执行的操作
	* @param p    要是用键盘的进程
	* @throws
	*/
	public static void setKeyBoardWorkForAProcess(Process p) {
		ifKeyboardWork = true;
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
		KeyBoard.usingProcess = usingProcess;
	}

	/**
	 * @return the lastUseTime
	 */
	public static int getLastUseTime() {
		return lastUseTime;
	}

	/**
	 * @param lastUseTime the lastUseTime to set
	 */
	public static void setLastUseTime(int lastUseTime) {
		KeyBoard.lastUseTime = lastUseTime;
	}
}
