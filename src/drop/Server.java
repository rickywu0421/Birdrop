package drop;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import view.MainView;
import view.ViewDelegate;

public class Server {
	private DataInputStream dis;
	private DataOutputStream dos;
	private ServerSocket ss;
	private Socket socket;
	private FileOutputStream fos;
	private byte[] buffer;
	public String file;
	private static MainView mainView;

	private final int port = 6667;

	public Server() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				createServer(port);
			}
		}).start();
	}

	public void createServer(int port) {
		try {
			ss = new ServerSocket(port);
			while (true) {
				socket = ss.accept();
				handleReceive(socket);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void openStreamSock(Socket s) {
		try {
			dis = new DataInputStream(s.getInputStream());
			dos = new DataOutputStream(s.getOutputStream());
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	private void closeStreamSock(Socket s) {
		try {
			dis.close();
			dos.close();
			s.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	private void handleReceive(Socket s) {
		openStreamSock(s);
		String directory = System.getProperty("user.dir");
		String filename = directory + "/" + recvfilename();
		
		while ((mainView = ViewDelegate.getMainView()) == null);
		if (mainView.popOptionDialog("sender", filename)) {
			sendACK();
			receiveFile(filename);
		}
		else {
			sendNCK();
		}

		closeStreamSock(s);
	}
	
	private String recvfilename() {
		String filename = null;
		try {
			filename = dis.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return filename;
	}
	
	private void sendACK() {
		try {
			dos.writeUTF("ACK");
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	private void sendNCK() {
		try {
			dos.writeUTF("NCK");
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	private void receiveFile(String filename) {
		try {
			int length =0;
			buffer = new byte[1024];
			fos = new FileOutputStream(new File(filename));

			while ((length = dis.read(buffer)) > 0) {
				fos.write(buffer,0,length);
				fos.flush();
				
				if(length < 1024) {
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
