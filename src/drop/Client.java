package drop;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import view.ViewDelegate;

import dns.NeighborDiscovery;
import dns.UserInfo;
import view.MainView;


public class Client {
	private FileInputStream fis;
	private DataInputStream dis;
	private DataOutputStream dos;
	private Socket socket;
	private Socket chatSocket;
	private byte[] buffer;
	
	private MainView mainView;

	private final int port = 6667;
	private final int chatPort = 6668;
	private boolean send = false;
	
	
	
	public Client() {
		mainView = ViewDelegate.getMainView();
	}
	
	public void setSend(boolean bool) {
		this.send = bool;
	}
	
	public void handleSend(File file, String ipAddr) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String response = null;
				Socket s = null;

			    if ((s = connect(ipAddr)) != null) {
				    openStreamSock(s);
			        sendfilename(file.getName());
			        response = recvResponse();

			        if (response.equals("ACK")) {
			        	sendFile(file.getPath());
			        }
		
			        closeStreamSock(s);
			    }
			}
		}).start();
	}
	

	
	public void handleChat(String ipAddr) throws IOException {

		if ((chatSocket = connectChatRoom(ipAddr)) != null) {
			PrintWriter out = new PrintWriter(chatSocket.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(chatSocket.getInputStream()));
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String input;
						System.out.println("input");
						while (true) {
							System.out.println("input ?");
							if(!((input = in.readLine()) == null))
							{
								mainView.updateMessageArea("[Host name protected]" + chatSocket.getLocalAddress() + " : "+ input);
								System.out.println("input in");
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
			
			//String line = reader.readLine();
			String line = mainView.getText();
			System.out.println("client" + line);

			while (!("end".equalsIgnoreCase(line))) {
				line = mainView.getText();

				if(send && !(line.equals("")))
				{
					System.out.println("client while");

					System.out.println("out");
					out.println(line);
					out.flush();
					line = mainView.getText();
					this.send = false;
				}
			}
			out.close();
			in.close();
		}
		chatSocket.close();
	}
		
	private Socket connect(String ipAddr) {
		try {
			socket = new Socket(ipAddr, port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return socket;
	}
	
	private Socket connectChatRoom(String ipAddr) {
		try {
			chatSocket = new Socket(ipAddr, chatPort);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return chatSocket;
	}
	
	

	private void sendfilename(String filename) {
		try {
            dos.writeUTF(filename);
		} catch (IOException e) {
			e.printStackTrace();
		}   
	}

	private void sendFile(String filename) {
		try {
			int length = 0;
			buffer = new byte[1024];
			fis = new FileInputStream(new File(filename));
			
			while ((length = fis.read(buffer)) > 0) {
				dos.write(buffer, 0, length);
				dos.flush();
			}	

			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String recvResponse() {
		try {
			return dis.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
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
}
