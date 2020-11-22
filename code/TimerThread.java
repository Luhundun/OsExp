/**  
* @Title: Timer.java
* @Package osexp1
* @author Lu Ning  
* @date 2020-10-24 9:52:03
* @version V1.0  
*/
package code;


/**
 * @Description ÿ�뷢��һ���ж��źŵļ�ʱ���߳�
 *
 */
public class TimerThread extends Thread {
	private static boolean ifTimerSuspend = false;   //����ʱ���Ƿ�����
	
	/**
	 *  �̼߳����ÿ�뷢��һ���ж��ź�
	 */
	public void run() {
		while(true) {
			
			if(ifTimerSuspend){    //�����GUI�İ�ť�ֶ���ͣ�ˣ�ʱ�Ӳ������źţ�ÿһ�����Ƿ��ڱ���ͣ
				try {
					Thread.sleep(1000);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}
			
			OperatingSystemGUI.timerLock.lock();//������
			try {
				OperatingSystemGUI.textField5.setText(String.valueOf(CPU.getTime()));
				OperatingSystemGUI.textArea1.append("CPUʱ�䣺" + CPU.getTime() + "\n");
				OperatingSystemGUI.timerCondition.signalAll();//�����������м����߳�
				
			}
			finally{
				OperatingSystemGUI.timerLock.unlock();//�ͷ���
			}
			
			try {
				Thread.sleep(1000);//��������1�룬ģ��ʱ�ӹ�ȥ��1��
				OperatingSystemGUI.textArea1.setCaretPosition(OperatingSystemGUI.textArea1.getText().length());   //Ϊ����GUI���ı����������
				CPU.passTime();// ����CPU��ȥ��1�� 
	
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