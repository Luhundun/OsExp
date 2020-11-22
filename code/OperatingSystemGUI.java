/**  
* @Title: OperatingSystem.java
* @Package code
* @author Lu Ning  
* @date 2020-10-27 12:31:50
* @version V1.0  
*/
package code;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

 
/**
 * @Description ͼ�ν����࣬Ҳ��main���������
 *
 */
public class OperatingSystemGUI {
	public static JFrame frame;
	public static JTextArea textArea1;
	public static JTextArea textArea2;
	public static JTextArea textList1;
	public static JTextArea textList2;
	public static JTextArea textList3;
	public static JTextArea textList4;
	public static ReentrantLock timerLock=new ReentrantLock();           //����������Ҫ���ڿ���ʱ�ӽ������������̵�ͨ��
    public static Condition timerCondition =timerLock.newCondition();
    public static int newProcessNum = 0;;                             //�ֶ������Ľ��̵���Ŀ
    public static JTextField textField1;
    public static JTextField textField2;
    public static JTextField textField3;
    public static JTextField textField4;
    public static JTextField textField5;
 
	public  OperatingSystemGUI() {
		// ���ڿ��
		frame = new JFrame();
		frame.setTitle("GUI");
		frame.setBounds(300, 150, 1440, 800);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
		// ���1
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.NORTH);
		
		
		//����һ����ť�����ֺ͵����ʵ�ֵĹ���
		JButton button1 = new JButton("��ʼ");   //����ʵ������Ҫ���̣߳�java�����ϵ��̣߳�ģ���os�����ϵĽ��̣�
		button1.addActionListener(event -> { 
			new TimerThread().start();   //����ʱ�ӽ���
			new KeyBoard().start();      //�������̽���
			new Display().start();      //������ʾ������
			new PV().start();         //����PVͨ�Ž���
			new ProcessSchedule().start();  //�������̵��Ƚ���
		});
		panel.add(button1);
		
		
		JButton button2 = new JButton("�����½���");
		button2.addActionListener(event -> {
			textArea2.setCaretPosition(textArea2.getText().length());
			Random rand = new Random();			
			int id = 6 + newProcessNum++;  //�ֶ������Ľ���id��6��ʼ����
			int priority = rand.nextInt(5) + 1;  //����������ȼ�����1��5֮��
			int inTimes = CPU.getTime();
			int instructionNum = rand.nextInt(20) + 1;  //�������ָ����Ŀ��1��20֮��
			ArrayList<Instruction> instructionSegment = new ArrayList<>(); 
			for(int i = 0; i < instructionNum ; i++) {
				Instruction instruction = new Instruction(i+1, rand.nextInt(4));     //ÿ�������ɵ�ָ����0��3֮��
				instructionSegment.add(instruction);
			}
			new Process(id, priority, inTimes, instructionNum, instructionSegment);
		}
	);
		panel.add(button2);
		
		JButton button3 = new JButton("��ͣ");         //������ͣ�ͼ�����ťΪ�˿���ʱ�ӵ����У�ҲΪ�˷������
		button3.addActionListener(event -> {
			TimerThread.setIfTimerSuspend(true);
		});
		panel.add(button3);
		
		JButton button4 = new JButton("����");
		button4.addActionListener(event -> {
			TimerThread.setIfTimerSuspend(false);
		});
		panel.add(button4);
 
		
		JButton button5 = new JButton("�رղ�������");            //��������������txt�ļ���ʽ����
		button5.addActionListener(event -> {   
			IOFile.saveAsFileWriter(textArea1.getText(), "output/ProcessResultPart1.txt");
			IOFile.saveAsFileWriter(textArea2.getText(), "output/ProcessResultPart2.txt");
			frame.dispatchEvent(new WindowEvent(frame,WindowEvent.WINDOW_CLOSING));
		});
		panel.add(button5);
	
		
		
		// �ɹ������
		JPanel panel2 = new JPanel();
		frame.getContentPane().add(panel2, BorderLayout.CENTER);
		panel2.setLayout(new GridLayout(1,2,5,5));
		panel2.setVisible(true);
		
		JScrollPane scrollPane1 = new JScrollPane();
		panel2.add(scrollPane1);
		textArea1 = new JTextArea();
		textArea1.setFont(new Font("����", 0 , 15));
		scrollPane1.setViewportView(textArea1);
		frame.setVisible(true);
		
		
		JPanel panel3 = new JPanel();
		panel2.add(panel3);
		panel3.setLayout(new GridLayout(2,1,5,5));
		panel3.setVisible(true);
		
		JScrollPane scrollPane2 = new JScrollPane();
		panel3.add(scrollPane2);
		textArea2 = new JTextArea();
		textArea2.setFont(new Font("����", 0 , 15));
		scrollPane2.setViewportView(textArea2);
		
		JPanel panel4 = new JPanel();
		panel3.add(panel4);
		panel4.setLayout(null);
		panel4.setVisible(true);
		
		JLabel lblNewLabel = new JLabel("��������");
		lblNewLabel.setBounds(14, 62, 60, 18);
		panel4.add(lblNewLabel);
		
		textList1 = new JTextArea();
		textList1.setBounds(14, 93, 81, 249);
		panel4.add(textList1);
		
		JLabel lblNewLabel1 = new JLabel("��������1");
		lblNewLabel1.setBounds(122, 62, 60, 18);
		panel4.add(lblNewLabel1);
		
		textList2 = new JTextArea();
		textList2.setBounds(122, 93, 81, 249);
		panel4.add(textList2);
		
		JLabel lblNewLabel2 = new JLabel("��������2");
		lblNewLabel2.setBounds(229, 62, 60, 18);
		panel4.add(lblNewLabel2);
		
		textList3 = new JTextArea();
		textList3.setBounds(229, 93, 81, 249);
		panel4.add(textList3);
		
		JLabel lblNewLabel3 = new JLabel("��������3");
		lblNewLabel3.setBounds(342, 62, 60, 18);
		panel4.add(lblNewLabel3);
		
		textList4 = new JTextArea();
		textList4.setBounds(342, 93, 81, 249);
		panel4.add(textList4);
		
		JLabel lblNewLabel8 = new JLabel("CPUʱ��");
		lblNewLabel8.setBounds(474, 62, 72, 18);     //
		panel4.add(lblNewLabel8);
		
		textField5 = new JTextField();
		textField5.setColumns(10);
		textField5.setBounds(575, 59, 86, 24);
		panel4.add(textField5);
		
		JLabel lblNewLabel4 = new JLabel("CPU״̬");
		lblNewLabel4.setBounds(474, 120, 72, 18);
		panel4.add(lblNewLabel4);
		
		textField1 = new JTextField();
		textField1.setBounds(575, 117, 86, 24);
		panel4.add(textField1);
		textField1.setColumns(10);
		
		JLabel lblNewLabel5 = new JLabel("CPU����");
		lblNewLabel5.setBounds(474, 175, 72, 18);
		panel4.add(lblNewLabel5);
		
		textField2 = new JTextField();
		textField2.setColumns(10);
		textField2.setBounds(575, 172, 86, 24);
		panel4.add(textField2);
		
		JLabel lblNewLabel6 = new JLabel("ִ��ָ���");
		lblNewLabel6.setBounds(474, 231, 72, 18 );
		panel4.add(lblNewLabel6);
		
		textField3 = new JTextField();
		textField3.setColumns(10);
		textField3.setBounds(575, 228, 86, 24);
		panel4.add(textField3);
		
		textField4 = new JTextField();
		textField4.setColumns(10);
		textField4.setBounds(575, 278, 86, 24);
		panel4.add(textField4);
		
		JLabel lblNewLabel7 = new JLabel("ָ������");
		lblNewLabel7.setBounds(474, 281, 72, 18);
		panel4.add(lblNewLabel7);
		
		
	}
	
    /**
    * @Description: main������ϵͳ�������
    * @param args 
    * @throws
    */
    public static void main(String[] args) {
    	EventQueue.invokeLater(() ->{            
    		new OperatingSystemGUI();
    	});

    }
}