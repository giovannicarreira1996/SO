package pt.europeia.uemail;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;


public class Filter4 implements Runnable {

	UEmail email = new UEmail(null,null,null,null);
	static String s = "Connection to Filter 4 established.";
	ObjectOutputStream out;
	ObjectInputStream in;
	static Socket one;
	static ServerSocket sone;
	Object lis;

	//Filter 1 socket to UEmailServer
	public static void Filter1Connection()  {

		try {

			one = new Socket("127.0.0.1",12345);
			ObjectOutputStream out = new ObjectOutputStream(one.getOutputStream());
			out.writeObject(s);
			out.close();



			while (true) {

				sone = new ServerSocket(12349);
				one = sone.accept();
				ObjectInputStream in = new ObjectInputStream(one.getInputStream());
				Object lis = in.readObject();
				UEmail email = (UEmail) lis;
				sone.close();

				if (email != null) {
					Filter4 f4 = new Filter4(email);
					new Thread(f4).start();
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

	public Filter4(UEmail email) {
		this.email = email;
	}

	public static void main(String [] args) {

		Filter1Connection();
	}

	@Override
	public void run() {

		if (email.getBody().equals("spam")) {

			try {

				email.setSpam(true);
				email.setFilter(email.getFilter() +"Filter4 ");
				one = new Socket("127.0.0.1",12353);
				out = new ObjectOutputStream(one.getOutputStream());
				out.writeObject(email);
				out.close();

			} catch (IOException e) {

				e.printStackTrace();

			}


		} else {
			try {
				one = new Socket("127.0.0.1",12353);
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

