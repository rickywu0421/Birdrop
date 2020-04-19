package drop;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import dns.NeighborDiscovery;
import dns.UserInfo;

public class Client {
	private FileInputStream fis;
	private DataInputStream dis;
	private DataOutputStream dos;
	private Socket socket;
	private byte[] buffer;

	private final int port = 6667;
	
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
	
	private Socket connect(String ipAddr) {
		try {
			socket = new Socket(ipAddr, port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return socket;
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
