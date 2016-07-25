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

public class chatClient implements ActionListener, Runnable {
	JTextArea showArea;
	JTextField msgText;
	JFrame mainJframe;
	JButton sentBtn;
	JScrollPane JSPane;
	JPanel pane;
	Container con;
	
	Thread thread = null;
	Socket connectToServer;
	DataInputStream inFromServer;
	DataOutputStream outToServer;
	
	public chatClient(){
		mainJframe = new JFrame("chatting——客户端");
		con = mainJframe.getContentPane();
		showArea = new JTextArea();
		showArea.setEditable(false);
		showArea.setLineWrap(true);
		
		JSPane = new JScrollPane(showArea);
		
		msgText = new JTextField();
		msgText.setColumns(30);
		msgText.addActionListener(this);
		
		sentBtn = new JButton("发送");
		sentBtn.addActionListener(this);
		
		pane = new JPanel();
		pane.setLayout(new FlowLayout());
		pane.add(msgText);
		pane.add(sentBtn);
		
		con.add(JSPane, BorderLayout.CENTER);
		con.add(pane, BorderLayout.SOUTH);

		mainJframe.setLocation(750,200);
		mainJframe.setSize(500, 350);
		mainJframe.setVisible(true);
		mainJframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		try{
			connectToServer = new Socket("localhost", 8890);
			inFromServer = new DataInputStream(connectToServer.getInputStream());
			outToServer = new DataOutputStream(connectToServer.getOutputStream());
			
			showArea.append("连接成功，可以开始交流了。\n");
			
			thread = new Thread(this);
			//thread.setPriority(Thread.MIN_PRIORITY);
			thread.start();
		}catch(IOException e){
			showArea.append("出错了，没能连接到服务器!");
			msgText.setEditable(false);
			sentBtn.setEnabled(false);
		}		
	}
	
	public void actionPerformed(ActionEvent e){
		String s = msgText.getText();
		
		if(s.length() > 0){
			try{
				outToServer.writeUTF(s);
				outToServer.flush();
				showArea.append("我（客户端）:"+msgText.getText()+"\n");
				msgText.setText(null);
			}catch(Exception e2){
				showArea.append("你的消息:"+msgText.getText()+"未能发送出去!\n");
			}
		}
	}
	
	public void run(){
		try{
			while(true){
				showArea.append("对方说:"+inFromServer.readUTF()+"\n");
				Thread.sleep(1000);
			}
		}catch(IOException e3){
		}catch(InterruptedException e){
		}
	}
	
	public static void main(String[] args){
		new chatClient();
	}
}
