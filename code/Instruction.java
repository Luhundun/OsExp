/**  
* @Title: Instruction.java
* @Package code
* @author Lu Ning  
* @date 2020-10-26 21:25:58
* @version V1.0  
*/
package code;

/**
 * @Description TODO
 *
 */
public class Instruction {
	private int instructionID;      //指令序号
	private int instructionState;   //指令类型
	

	/**
	* @Description: Instruction类的构造函数  
	* @param id
	* @param state
	*/
	public Instruction(int id, int state) {
		this.instructionID = id;
		this.instructionState = state;
	}
	

	/**
	* @Description: 返回指令序号
	* @return int    
	* @throws
	*/
	public int getInstructionID() {
		return instructionID;
	}
	
	/**
	* @Description: 返回指令类型
	* @return int    
	* @throws
	*/
	public int getInstructionState() {
		return instructionState;
	}
	
	
	/**
	 *   格式化输出一条指令（调试用）
	 */
	public String toString() {
		return "指令序号:"+instructionID+" 指令类型"+instructionState;
	}
}
