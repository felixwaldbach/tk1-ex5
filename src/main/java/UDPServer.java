import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPServer implements Runnable {
    private DatagramSocket udpSocket;
    private int port;
    private Hangar hangar;
 
    public UDPServer(int port, Hangar hangar) throws SocketException, IOException {
        this.port = port;
        this.hangar = hangar;
        this.udpSocket = new DatagramSocket(this.port);
    }
    
    private void listen() throws Exception {
        System.out.println("Running Server at " + InetAddress.getLocalHost() + ":" + port);
        
        while (true) {
            
            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            
            // blocks until a packet is received
            udpSocket.receive(packet);
            
            byte[] data = packet.getData();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            ObjectInputStream is = new ObjectInputStream(in);
            try {
            	// check if the message is airplanes or a marker message
            	
            	// if airplanes
            	String[] airplanes = (String[]) is.readObject();
            	System.out.println("String Array with Airplanes received = " + airplanes);
            	hangar.addAirplanes(airplanes);
            	
            	// if marker message
            	// start recording
            	// when receiving the second marker message print the count of airplanes
            } catch (ClassNotFoundException e) {
            	e.printStackTrace();
            }
            threadSleep(350);
        }
    }

	@Override
	public void run() {
		try {
			listen();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void threadSleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}