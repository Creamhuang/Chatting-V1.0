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
	JTextArea showArea;//文本区
	JTextField msgText;//文本框
	JFrame mainJframe;//框架
	JButton sentBtn;//发送按钮
	JScrollPane JSPane;//滚动面板
	JPanel pane;//面板
	Container con;//内容面板
	
	Thread thread = null;
	ServerSocket serverSocket;
	Socket connectToClent;
	DataInputStream inFromClient;
	DataOutputStream outToClient;
	
	public chatServer(){
		mainJframe = new JFrame("chatting——服务器端");
		/**
		 * JFrame虽然继承了Frame的所有非私有方法，但有些方法用起来还是会出错，如add、setLayout，
		 * 这是因为要添加的组件不能直接加在JFrame，所以这些方法只能通过内容面板来使用。
		 */
		con = mainJframe.getContentPane();
		showArea = new JTextArea();
		showArea.setEditable(false);//设置为禁止编辑
		showArea.setLineWrap(true);//设置为自动拆行
		//文本区本身是没有滚动条的，所以应将它放到一个滚动面板中
		JSPane = new JScrollPane(showArea);
		
		msgText = new JTextField();
		msgText.setColumns(30);//设置文本框的宽度为30个字符
		msgText.addActionListener(this);
		
		sentBtn = new JButton("发送");
		sentBtn.addActionListener(this);
		
		pane = new JPanel();
		pane.setLayout(new FlowLayout());
		pane.add(msgText);
		pane.add(sentBtn);
		
		con.add(JSPane, BorderLayout.CENTER);//滚动面板加入内容面板
		con.add(pane, BorderLayout.SOUTH);//面板加入内容面板
		
		mainJframe.setLocation(200, 200);
		mainJframe.setSize(500, 350);
		mainJframe.setVisible(true);
		mainJframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//使用 System exit 方法退出应用程序。仅在应用程序中使用。 
		
		try{
			//创建套接字服务
			serverSocket = new ServerSocket(8890);
			showArea.append("正在等待对话请求...\n");
			//监听客户端的连接请求
			connectToClent = serverSocket.accept();
			inFromClient = new DataInputStream(connectToClent.getInputStream());
			outToClient = new DataOutputStream(connectToClent.getOutputStream());
			
			thread = new Thread(this);
			//thread.setPriority(Thread.MIN_PRIORITY);
			thread.start();
		}catch(IOException e){
			showArea.append("出错，不能创建服务器!");
			msgText.setEditable(false);
			sentBtn.setEnabled(false);
		}
	}
	

	public void actionPerformed(ActionEvent e){//实现ActionListener这一接口中的方法
		String s = msgText.getText();
		
		if(s.length() > 0){
			try{
				outToClient.writeUTF(s);
				outToClient.flush();
				showArea.append("我说（服务器）:"+msgText.getText()+"\n");
				msgText.setText(null);
			}catch(IOException e2){
				showArea.append("你的消息:"+msgText.getText()+"未能发送出去!\n");
			}
		}
	}
	
	public void run(){
		try{
			while(true){
				showArea.append("对方说:"+inFromClient.readUTF()+"\n");
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
 * 客户端和服务端界面完全一样，功能类似，只是服务端比客户端多了一个服务用的套接字
 * 客户端和服务器端都可由用户输入文字 并向对方发送，
 * 在用户输入文字时，将接收数据部分放在线程中，他始终运行在后台，一旦对方发来了数据，会立即显示在界面上。
 * 主界面负责输入文字和发送数据，从而不会导致接收数据和发送数据相冲突
 * 
 */
鏈嶅姟鍣ㄧ绋嬪簭
