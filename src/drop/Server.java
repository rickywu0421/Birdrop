package drop;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JOptionPane;

import view.MainView;
import view.ViewDelegate;

public class Server {
	private DataInputStream dis;
	private DataOutputStream dos;
	private ServerSocket ss;
	private Socket socket;
	private Socket chatSocket;
	private FileOutputStream fos;
	private byte[] buffer;
	public String file;
	private static MainView mainView;
	public static boolean send = false;

	private final int port = 6667;
	private final int sendPort = 6668;

	public Server() {
		createServer(port);
		createChatServer(sendPort);
	}

	public void createServer(int port) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
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
		}).start();

	}

	public void setSend(boolean bool) {
		this.send = bool;
	}
	
	public void createChatServer(int chatPort) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					ss = new ServerSocket(chatPort);

					while (true) {
						chatSocket = ss.accept();
						handleChat();
						// handleChat();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	public void handleChat() throws IOException {
		openStreamSock(chatSocket);
		PrintWriter out = new PrintWriter(chatSocket.getOutputStream());

		while ((mainView = ViewDelegate.getMainView()) == null)
			;
		new Thread(new Runnable() {

			@Override
			public void run() {
				boolean flag = true;
				BufferedReader reader;

				if (mainView.popChatboxConnectDialog("sender")) {
					try {
						mainView.popMessageboxPane();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("pop");
					try {
						reader = new BufferedReader(new InputStreamReader(chatSocket.getInputStream()));
						String socketName = chatSocket.getLocalAddress().toString();
						mainView.updateMessageArea("[Host name protected]" + socketName + "已加入聊天");

						print(socketName + "已加入聊天");

						while (flag) {
							String line = null;
							System.out.println("line?");
							line = reader.readLine();
							System.out.println("test");
							System.out.println(line);
							if (line == null) {
								flag = false;
								continue;
							}
							String msg = "[Host name protected]" + socketName + " : " + line;
							print(line);
							mainView.updateMessageArea(msg);

							System.out.println("send msg");
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();

		String line = mainView.getText();
		System.out.println(" server" + line);

		while (!"end".equalsIgnoreCase(line)) {
			line = mainView.getText();

			if (send && !(line.equals(""))) {
				mainView.updateMessageArea("[Host name protected]" + chatSocket.getLocalAddress() + " : "+ line);
				System.out.println("server while");
				out.println(line);
				out.flush();
				line = mainView.getText();
				this.send = false;
			}
		}
		out.close();
		chatSocket.close();

	}

	private void print(String msg) throws IOException {
		PrintWriter out = null;
		out = new PrintWriter(chatSocket.getOutputStream());
		out.println(msg);
		out.flush();
	}

	private void openStreamSock(Socket s) {
		try {
			dis = new DataInputStream(s.getInputStream());
			dos = new DataOutputStream(s.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void closeStreamSock(Socket s) {
		try {
			dis.close();
			dos.close();
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void handleReceive(Socket s) {
		openStreamSock(s);
		String directory = System.getProperty("user.dir");
		String filename = directory + "/" + recvfilename();

		while ((mainView = ViewDelegate.getMainView()) == null)
			;
		if (mainView.popRecvConfirmDialog("sender", filename)) {
			File file = mainView.popSenderFileChooser();
			sendACK();
			receiveFile(file.getPath());
		} else {
			sendNCK();
		}
		closeStreamSock(s);
		mainView.popFinish();
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendNCK() {
		try {
			dos.writeUTF("NCK");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void receiveFile(String filename) {
		try {
			int length = 0;
			buffer = new byte[1024];
			fos = new FileOutputStream(new File(filename));

			while ((length = dis.read(buffer)) > 0) {
				fos.write(buffer, 0, length);
				fos.flush();
				if (length < 1024) {
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
