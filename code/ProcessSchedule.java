/**  
* @Title: ProcessSchedule.java
* @Package code
* @author Lu Ning  
* @date 2020-10-26 15:48:55
* @version V1.0  
*/
package code;

import java.util.ArrayList;

/**
 * @Description 进程调度类
 *
 */
public class ProcessSchedule extends Thread{

	/**
	 *  进程调度类被唤醒后执行的程序
	 */
	public void run() {
//		allPCB = new LinkedList<PCB>();
//		readyQuene = new LinkedList<PCB>();
//		blockedQuene1 = new LinkedList<PCB>();
//		blockedQuene2 = new LinkedList<PCB>();
//		blockedQuene3 = new LinkedList<PCB>();
		String fileName = "input/19218104-jobs-input.txt";
		ArrayList<int[]> readFromProcessFile = IOFile.readProcessFromFile(fileName);

		while(true) {                  	
			OperatingSystemGUI.timerLock.lock();//请求锁
            try{
                OperatingSystemGUI.timerCondition.await();  //唤醒所有等待线程
            }			
            catch (InterruptedException e) {
				e.printStackTrace();
			}
            finally{
            	OperatingSystemGUI.timerLock.unlock();//释放锁
            }
			if(CPU.getTime()%5 == 0)            //每5秒检查是否有新作业
				analyseWhichToCreate(readFromProcessFile);
			roundRobinScheduling();       //执行时间片轮转算法的一系列操作						
		}
		
	}
	
	/**
	* @Description: 时间片轮转算法   
	* @throws
	*/
	public void roundRobinScheduling() {
		PCB.staticPriority();  //按优先级大小对就绪队列进行重新排队
		if(CPU.getCpuWorkState() == true) {
			if(CPU.workingProcess.ifTimeSliceLeft()) {   //如果正在运行的进程时间片还有剩余，那么一个时钟中断周期内此进程继续运行
				CPU.doInstruction();             //根据不同的指令执行对应的操作
			}
			else {                                  //时间片到，将此进程移到就绪队列排队，进行进程上下文切换，再从就绪队列取出优先级最高的进程执行
				CPU.workingProcess.setPSW(2);       
				CPU.workingProcess.setReadyQueueInTime(CPU.getTime());
				PCB.joinReadyQueue(CPU.workingProcess);                                 //当前进程进入就绪队列
				PCB.staticPriority();  //按优先级大小对就绪队列进行重新排队
				processContextSwitch(PCB.findProcessWithPCB(PCB.pollReadyQueue()));   //进行进程上下文切换
				CPU.workingProcess.reSetTimeSlice(); 
				CPU.doInstruction();            //根据不同的指令执行对应的操作
			}
		}
		else {
			PCB readyPcb = PCB.pollReadyQueue();      //如果CPU此刻不工作，就从就绪队列首位取元素,就绪队列为空会返回一个空地址
			if(readyPcb == null) {                     //如果就绪队列空，打印CPU空闲状态
				OperatingSystemGUI.textArea1.append("CPU空闲\n\n");
				OperatingSystemGUI.textField1.setText("空闲");
				OperatingSystemGUI.textField2.setText("无");
				OperatingSystemGUI.textField3.setText("无");
				OperatingSystemGUI.textField4.setText("无");				
			}
			else {                                 //就绪队列不空，进行进程上下文切换，再从就绪队列取出优先级最高的进程执行			
				processContextSwitch(PCB.findProcessWithPCB(readyPcb));
				CPU.setCpuWorkState(true);       //检测到了还有指令没做完，CPU状态设为work
				CPU.workingProcess.reSetTimeSlice();
				CPU.doInstruction();            //根据不同的指令执行对应的操作
			}
		}
	}
	
	/**
	* @Description: 进程上下文切换,将CPU的现场改成新进程的现场,   ，并修改新锦成的状态
	* @throws
	*/
	public void processContextSwitch(Process newRunProcess) {
		CPU.workingProcess = PCB.findProcessWithPCB(newRunProcess);
		CPU.switchUserModeToKernelMode();       //进程上下文切换是要在CPU核心态下实现的
		newRunProcess.setPSW(1);
		CPU.workingProcess = newRunProcess;
		CPU.switchKernelModeToUserMode();
		CPU.setPC(newRunProcess.getPC());
		CPU.setIR(newRunProcess.getIR());
		CPU.setPSW(newRunProcess.getPSW());
	}
	
	
	/**
	* @Description: 检查当前时刻是否有新进程产生
	* @param readFromProcessFile   从文件中读出的有关进程的数据  
	* @throws
	*/
	public void analyseWhichToCreate(ArrayList<int[]> readFromProcessFile) {
		int time = CPU.getTime();
		for(int[] e:readFromProcessFile) {
			if(e[2] > time-5 && e[2] <= time)
				{
					String id = Integer.toString(e[0]);
					id = "input/" + id + ".txt";    //根据进程id，去读这个进程对应的指令的文件
					ArrayList<int[]> readFromInstructionFile = IOFile.readInstructionFromFile(id);
					createTask(e,readFromInstructionFile);
				}
		}
	}
	
	/**
	* @Description: 创建一个新进程
	* @param infomationPCB   要创建的进程的信息 
	* @throws
	*/
	public void createTask(int[] infomationPCB, ArrayList<int[]> readFromInstructionFile) {
		ArrayList<Instruction> instructionSegment = new ArrayList<>(); 
		for(int[] e : readFromInstructionFile) {     //根据文件读出的内容生成程序段
			Instruction instruction = new Instruction(e[0], e[1]);
			instructionSegment.add(instruction);
		}
		new Process(infomationPCB[0], infomationPCB[1], infomationPCB[2], infomationPCB[3], instructionSegment);  //创建新进程
	}
	

//	
}
