package pt.europeia.uemail;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;


public class Filter3 implements Runnable {

	UEmail email = new UEmail(null,null,null,null);
	static String s = "Connection to Filter 3 established.";
	ObjectOutputStream out;
	ObjectInputStream in;
	static Socket one;
	static ServerSocket sone;

	//Filter 1 socket to UEmailServer
	public static void Filter1Connection()  {

		try {

			one = new Socket("127.0.0.1",12345);
			ObjectOutputStream out = new ObjectOutputStream(one.getOutputStream());
			out.writeObject(s);

			while (true) {

				sone = new ServerSocket(12348);
				one = sone.accept();
				ObjectInputStream in = new ObjectInputStream(one.getInputStream());
				Object lis = in.readObject();
				UEmail email = (UEmail) lis;
				sone.close();

				if (email != null) {
					Filter3 f3 = new Filter3(email);
					new Thread(f3).start();
				}
			}





		} catch (UnknownHostException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		} catch (ClassNotFoundException e) {

			e.printStackTrace();

		}

	}

	public Filter3(UEmail email) {
		this.email = email;
	}

	public static void main(String [] args) {

		Filter1Connection();
	}

	@Override
	public void run() {

		if (email.getTo().equals("spam@universidadeeuropeia.pt")) {

			try {

				email.setSpam(true);
				email.setFilter(email.getFilter() + "Filter3 ");
				one = new Socket("127.0.0.1",12352);
				out = new ObjectOutputStream(one.getOutputStream());
				out.writeObject(email);
				out.close();
				one.close();

			} catch (IOException e) {

				e.printStackTrace();

			}


		} else {
			try {
				one = new Socket("127.0.0.1",12352);
				out = new ObjectOutputStream(one.getOutputStream());
				out.writeObject(email);
				out.close();
				one.close();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}

