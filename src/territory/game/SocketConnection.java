package territory.game;

import java.net.*;
import java.io.*;
import java.sql.SQLOutput;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class SocketConnection {
	
	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;	

	private List<Object> messageQueue = new ArrayList<>();
	
	protected abstract Socket getSocket() throws IOException;
	
	//return whether the connection was made;
	public boolean connect(){
		try {
			socket = this.getSocket();
			
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
				
			new Thread(this::listen).start();

			sendMessageQueue();
        }
		catch(ConnectException exc){
			exc.printStackTrace();
			return false;
		}
		catch (UnknownHostException exc) {
            exc.printStackTrace();
			return false;
        }
		catch (IOException exc) {
			exc.printStackTrace();
			return false;
        }

		return true;
	}
	
	private void listen(){
		Object message;
		
		try{
			while (true) {
				try{
					synchronized (in){
						message = in.readObject();
					}
				}
				catch(EOFException exc){
					exc.printStackTrace();
					return;
				}

				if(message == null){
					break;
				}

				this.receiveMessage(message);
			}
		}
		catch(ClassNotFoundException exc){
			exc.printStackTrace();
		}
		catch(IOException exc){
			exc.printStackTrace();
		}
		finally{
			this.close();
		}
	}

	/**
	 * Enqueue, and is possible send the given message
	 * @param message the Serializable message to send
	 */
	public void sendMessage(Object message){

		//add this message to the queue
		messageQueue.add(message);

		//if we have no output stream, we can't send right now
		if(out == null){
			return;
		}

		//otherwise send all messages in the queue
		sendMessageQueue();
	}

	/**
	 * Send all messages in the message queue
	 */
	private void sendMessageQueue(){

		List<Object> messagesToSend = null;

		synchronized (messageQueue){
			messagesToSend = new ArrayList<>(messageQueue);
			messageQueue.clear();
		}

		for(Object message : messagesToSend){
			try{
				synchronized (out) {
					out.writeObject(message);
					out.flush();
				}
			}
			catch(IOException exc){
				System.err.println("Error sending message in queue: " + message);
				exc.printStackTrace();
			}
		}
	}
	
	public void close(){
		try {
			socket.close();
			in.close();
			out.close();
		}
		catch(IOException exc){
			exc.printStackTrace();
		}
	}
	
	protected abstract void receiveMessage(Object message);

	/**
	 * Create a new connection acting as a server. The connect() method of the returned
	 * ServerConnection must be called before any client connection attempts to connect
	 *
	 * @param port the port of the connection
	 * @param callback the function that will be called when this SocketConnection receives an object
	 * @return a new SocketConnection
	 */
	public static SocketConnection createServer(int port, Consumer<Object> callback){
		return new SocketConnection(){
			protected Socket getSocket() throws IOException {
				ServerSocket serverSocket = new ServerSocket(port);
				
				return serverSocket.accept();
			}
			
			protected void receiveMessage(Object message){
				callback.accept(message);
			}
		};
	}

	/**
	 * Create a new connection acting as a client. The connect() method of the returned
	 * ServerConnection must be called after the server's
	 *
	 * @param host the host name (ip address) of the host
	 * @param port the port of the connection
	 * @param callback the function that will be called when this SocketConnection receives an object
	 * @return a new SocketConnection
	 */
	public static SocketConnection createClient(String host, int port, Consumer<Object> callback){
		return new SocketConnection(){
			public Socket getSocket() throws IOException {
				return new Socket(host, port);
			}
			
			protected void receiveMessage(Object message){
				callback.accept(message);
			}
		};
	}

	private static String timeStamp(){
		return String.format("%d", System.currentTimeMillis()%10000000);
	}
}