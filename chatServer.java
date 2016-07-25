package chat;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class chatServer implements ActionListener, Runnable {
	JTextArea showArea;//�ı���
	JTextField msgText;//�ı���
	JFrame mainJframe;//���
	JButton sentBtn;//���Ͱ�ť
	JScrollPane JSPane;//�������
	JPanel pane;//���
	Container con;//�������
	
	Thread thread = null;
	ServerSocket serverSocket;
	Socket connectToClent;
	DataInputStream inFromClient;
	DataOutputStream outToClient;
	
	public chatServer(){
		mainJframe = new JFrame("chatting������������");
		/**
		 * JFrame��Ȼ�̳���Frame�����з�˽�з���������Щ�������������ǻ������add��setLayout��
		 * ������ΪҪ��ӵ��������ֱ�Ӽ���JFrame��������Щ����ֻ��ͨ�����������ʹ�á�
		 */
		con = mainJframe.getContentPane();
		showArea = new JTextArea();
		showArea.setEditable(false);//����Ϊ��ֹ�༭
		showArea.setLineWrap(true);//����Ϊ�Զ�����
		//�ı���������û�й������ģ�����Ӧ�����ŵ�һ�����������
		JSPane = new JScrollPane(showArea);
		
		msgText = new JTextField();
		msgText.setColumns(30);//�����ı���Ŀ��Ϊ30���ַ�
		msgText.addActionListener(this);
		
		sentBtn = new JButton("����");
		sentBtn.addActionListener(this);
		
		pane = new JPanel();
		pane.setLayout(new FlowLayout());
		pane.add(msgText);
		pane.add(sentBtn);
		
		con.add(JSPane, BorderLayout.CENTER);//�����������������
		con.add(pane, BorderLayout.SOUTH);//�������������
		
		mainJframe.setLocation(200, 200);
		mainJframe.setSize(500, 350);
		mainJframe.setVisible(true);
		mainJframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//ʹ�� System exit �����˳�Ӧ�ó��򡣽���Ӧ�ó�����ʹ�á� 
		
		try{
			//�����׽��ַ���
			serverSocket = new ServerSocket(8890);
			showArea.append("���ڵȴ��Ի�����...\n");
			//�����ͻ��˵���������
			connectToClent = serverSocket.accept();
			inFromClient = new DataInputStream(connectToClent.getInputStream());
			outToClient = new DataOutputStream(connectToClent.getOutputStream());
			
			thread = new Thread(this);
			//thread.setPriority(Thread.MIN_PRIORITY);
			thread.start();
		}catch(IOException e){
			showArea.append("�������ܴ���������!");
			msgText.setEditable(false);
			sentBtn.setEnabled(false);
		}
	}
	

	public void actionPerformed(ActionEvent e){//ʵ��ActionListener��һ�ӿ��еķ���
		String s = msgText.getText();
		
		if(s.length() > 0){
			try{
				outToClient.writeUTF(s);
				outToClient.flush();
				showArea.append("��˵����������:"+msgText.getText()+"\n");
				msgText.setText(null);
			}catch(IOException e2){
				showArea.append("�����Ϣ:"+msgText.getText()+"δ�ܷ��ͳ�ȥ!\n");
			}
		}
	}
	
	public void run(){
		try{
			while(true){
				showArea.append("�Է�˵:"+inFromClient.readUTF()+"\n");
				Thread.sleep(1000);
			}
		}catch(IOException e3){	
		}catch(InterruptedException e){
		}
	}
	
	public static void main(String[] args){
		new chatServer();
	}
}
/*
 * �ͻ��˺ͷ���˽�����ȫһ�����������ƣ�ֻ�Ƿ���˱ȿͻ��˶���һ�������õ��׽���
 * �ͻ��˺ͷ������˶������û��������� ����Է����ͣ�
 * ���û���������ʱ�����������ݲ��ַ����߳��У���ʼ�������ں�̨��һ���Է����������ݣ���������ʾ�ڽ����ϡ�
 * �����渺���������ֺͷ������ݣ��Ӷ����ᵼ�½������ݺͷ����������ͻ
 * 
 */
服务器端程序
