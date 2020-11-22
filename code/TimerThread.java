/**  
* @Title: Timer.java
* @Package osexp1
* @author Lu Ning  
* @date 2020-10-24 9:52:03
* @version V1.0  
*/
package code;


/**
 * @Description 每秒发送一次中断信号的计时器线程
 *
 */
public class TimerThread extends Thread {
	private static boolean ifTimerSuspend = false;   //控制时钟是否运行
	
	/**
	 *  线程激活后，每秒发送一次中断信号
	 */
	public void run() {
		while(true) {
			
			if(ifTimerSuspend){    //如果被GUI的按钮手动暂停了，时钟不发送信号，每一秒检查是否还在被暂停
				try {
					Thread.sleep(1000);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}
			
			OperatingSystemGUI.timerLock.lock();//请求锁
			try {
				OperatingSystemGUI.textField5.setText(String.valueOf(CPU.getTime()));
				OperatingSystemGUI.textArea1.append("CPU时间：" + CPU.getTime() + "\n");
				OperatingSystemGUI.timerCondition.signalAll();//唤醒其他所有加锁线程
				
			}
			finally{
				OperatingSystemGUI.timerLock.unlock();//释放锁
			}
			
			try {
				Thread.sleep(1000);//进程休眠1秒，模拟时钟过去了1秒
				OperatingSystemGUI.textArea1.setCaretPosition(OperatingSystemGUI.textArea1.getText().length());   //为了让GUI的文本框滚动起来
				CPU.passTime();// 设置CPU过去了1秒 
	
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
        }               
	}


	/**
	 * @return the ifTimerSuspend
	 */
	public static boolean isIfTimerSuspend() {
		return ifTimerSuspend;
	}


	/**
	 * @param ifTimerSuspend the ifTimerSuspend to set
	 */
	public static void setIfTimerSuspend(boolean ifTimerSuspend) {
		TimerThread.ifTimerSuspend = ifTimerSuspend;
	}

}