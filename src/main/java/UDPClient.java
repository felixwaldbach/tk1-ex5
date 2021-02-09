import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class UDPClient {

	private static String hostUrl = "127.0.0.1";
	private static String MARKER = "CL_algorithm_start";
	
    private DatagramSocket udpSocket;
    private InetAddress serverAddress;
    private int port;
    
    public UDPClient(int port) throws IOException {
        this.serverAddress = InetAddress.getByName(hostUrl);
        this.port = port;
        udpSocket = new DatagramSocket();
    }
    
    public void sendAirplanes(String[] airplanes) throws IOException {
    	ByteArrayOutputStream fos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(airplanes);
        oos.close();

        // end serialization
        byte[] sendByte = fos.toByteArray();

        DatagramPacket sendPacket = new DatagramPacket(
                sendByte, sendByte.length, serverAddress, port);
        
        this.udpSocket.send(sendPacket);               
    }
    
    public void sendMarkerMessage() throws IOException {
        
    	ByteArrayOutputStream fos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(MARKER);
        oos.close();
        
    	byte[] sendByte = MARKER.getBytes();
    	
    	DatagramPacket sendPacket = new DatagramPacket(
    			sendByte, sendByte.length, serverAddress, port);
    	
    	this.udpSocket.send(sendPacket);
    }
}  
