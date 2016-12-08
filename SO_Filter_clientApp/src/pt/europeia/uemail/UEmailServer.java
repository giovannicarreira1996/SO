package pt.europeia.uemail;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class UEmailServer implements Serializable, Runnable {

	public static UEmail email = new UEmail(null,null,null,null);

	static int emailNumber = 1;

	private static int sActivated = 0;

	public static ObservableList<String> serverEmails = FXCollections.observableArrayList();

	public static ObservableList<String> clientEmails = FXCollections.observableArrayList();

	public static ObservableList<String> events = FXCollections.observableArrayList();

	public static ArrayList<String> UEmails = new ArrayList<String>();

	//Filters Server Socket
	static ServerSocket servF;
	//Client Server Socket
	static ServerSocket servC;

	private Socket listenerF;



	//Connection to the Filters
	public static void startServer() throws IOException, ClassNotFoundException {

		servF = new ServerSocket(12345);

		//connects to all 4 Filters one at a time
		for (int times = 1 ; times < 5; times++) {

			new Thread(new UEmailServer()).start();


			ProcessBuilder pb = new ProcessBuilder("java", "-cp", "bin", "pt.europeia.uemail.Filter"+times);
			pb.start();


		}

		servC = new ServerSocket(23456);

		new Thread(new UEmailServer()).start();

	}


	@Override
	public void run() {



		if( getsActivated() < 4 ) {

			setsActivated(getsActivated()+1);

			try {

				Socket listenerF = servF.accept();
				ObjectInputStream inC = new ObjectInputStream(listenerF.getInputStream());
				Object lis = inC.readObject();
				events.add(lis.toString());


			} catch (ClassNotFoundException | IOException e1) {

				e1.printStackTrace();

			}


		} else {

			while (true) {

				try {

					//receber email do cliente
					Socket listenerC = servC.accept();
					ObjectInputStream inC = new ObjectInputStream(listenerC.getInputStream());
					Object lis = inC.readObject();

					new Thread(new UEmailServer()).start();

					//escrever para os filtros
					listenerF = new Socket("127.0.0.1",12346);
					ObjectOutputStream outF1 = new ObjectOutputStream(listenerF.getOutputStream());
					outF1.writeObject(lis);
					listenerF.close();
					outF1.close();

					//receber do filtro 1
					ServerSocket rec1 = new ServerSocket(12350);
					Socket listenerF1 = rec1.accept();
					ObjectInputStream inF1 = new ObjectInputStream(listenerF1.getInputStream());
					Object lis2 = inF1.readObject();
					UEmail emailf = (UEmail) lis2;
					listenerF1.close();
					rec1.close();
					
					//escrever para o filtro 2
					listenerF = new Socket("127.0.0.1",12347);
					ObjectOutputStream outF2 = new ObjectOutputStream(listenerF.getOutputStream());
					outF2.writeObject(emailf);
					listenerF.close();
					outF2.close();
					
					//receber do filtro 2
					ServerSocket rec2 = new ServerSocket(12351);
					Socket listenerF2 = rec2.accept();
					ObjectInputStream inF2 = new ObjectInputStream(listenerF2.getInputStream());
					lis2 = inF2.readObject();
					emailf = (UEmail) lis2;
					rec2.close();
					
					//escrever para o filtro 3
					listenerF = new Socket("127.0.0.1",12348);
					ObjectOutputStream outF3 = new ObjectOutputStream(listenerF.getOutputStream());
					outF3.writeObject(emailf);
					listenerF.close();
					outF3.close();
					
					//receber do filtro 3
					ServerSocket rec3 = new ServerSocket(12352);
					Socket listenerF3 = rec3.accept();
					ObjectInputStream inF3 = new ObjectInputStream(listenerF3.getInputStream());
					lis2 = inF3.readObject();
					emailf = (UEmail) lis2;
					rec3.close();
					
					//escrever para o filtro 4
					listenerF = new Socket("127.0.0.1",12349);
					ObjectOutputStream outF4 = new ObjectOutputStream(listenerF.getOutputStream());
					outF4.writeObject(emailf);
					listenerF.close();
					outF4.close();
					
					//receber do filtro 4
					ServerSocket rec4 = new ServerSocket(12353);
					Socket listenerF4 = rec4.accept();
					ObjectInputStream inF4 = new ObjectInputStream(listenerF4.getInputStream());
					lis2 = inF4.readObject();
					emailf = (UEmail) lis2;
					rec4.close();
					
					UEmails.add(emailf.toString());

					if (emailf.isSpam()) {

						events.add(emailf.getFilter() + " says email " + emailNumber + " is a spam.");
						
						serverEmails.add(emailf.toString());

					} else {
						
						serverEmails.add(email.toString());

						clientEmails.add(("From: client@universidadeeuropeia.pt"  + "\nTo: " + emailf.getTo()+ "\nSubject: " + emailf.getSubject()));

					}

					emailNumber++;

				} catch (IOException | ClassNotFoundException e) {

					e.printStackTrace();

				}

			}

		}


	} 
	public static void main (String[]args) throws ClassNotFoundException, IOException {

		startServer();
	}

	public static int getsActivated() {
		return sActivated;
	}


	public static void setsActivated(int sActivated) {
		UEmailServer.sActivated = sActivated;
	}
}
