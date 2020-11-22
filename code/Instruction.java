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
	private int instructionID;      //ָ�����
	private int instructionState;   //ָ������
	

	/**
	* @Description: Instruction��Ĺ��캯��  
	* @param id
	* @param state
	*/
	public Instruction(int id, int state) {
		this.instructionID = id;
		this.instructionState = state;
	}
	

	/**
	* @Description: ����ָ�����
	* @return int    
	* @throws
	*/
	public int getInstructionID() {
		return instructionID;
	}
	
	/**
	* @Description: ����ָ������
	* @return int    
	* @throws
	*/
	public int getInstructionState() {
		return instructionState;
	}
	
	
	/**
	 *   ��ʽ�����һ��ָ������ã�
	 */
	public String toString() {
		return "ָ�����:"+instructionID+" ָ������"+instructionState;
	}
}
