import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPServer implements Runnable {
	private static String MARKER = "CL_algorithm_start";
    private DatagramSocket udpSocket;
    private int port;
    private Hangar hangar;
    private String record = "";
 
    public UDPServer(int port, Hangar hangar) throws SocketException, IOException {
        this.port = port;
        this.hangar = hangar;
        this.udpSocket = new DatagramSocket(this.port);
    }
    
    private void listen() throws Exception {
        System.out.println("Running Server at " + InetAddress.getLocalHost() + ":" + port);
        boolean noMarker = true;
        
        while (true) {
            
            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            
            // blocks until a packet is received
            udpSocket.receive(packet);
            
            byte[] data = packet.getData();
            String received = new String(data, 0, packet.getLength());
            
            try {
            	// check if the message is airplanes or a marker message
            	if (received.equals(MARKER) && noMarker) {
            		// if marker message
            		System.out.println("Marker message received");
            		noMarker = false;
            		// start recording
            		//TODO
                	record = record + "Current Airplane Number: " + 
                			Integer.toString(hangar.getNumOfPlane());
                	MainWindow.historyListModel.addElement("Record: " + record);
            	} else if(received.equals(MARKER) && !noMarker) {
            		// when receiving the second marker message print the count of airplanes
            		
            		noMarker = true;
            		
            		
            	} else {
            		
            		// if airplane
            		ByteArrayInputStream in = new ByteArrayInputStream(data);
                    ObjectInputStream is = new ObjectInputStream(in);
                    
            		String[] airplanes = (String[]) is.readObject();
                	System.out.println("String Array with Airplanes received = " + airplanes);
                	hangar.addAirplanes(airplanes);
                	
                	is.close();
            	}

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