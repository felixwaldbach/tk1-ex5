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
    
    public boolean recording = false;
 
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
            String[] payload = (String[]) is.readObject();
            // check if the message is airplanes or a marker message
			// if marker message
			// when receiving the second marker message print the count of airplanes
			if(payload[0].equals("m")) {
				if(recording) {
					recording = false;
					MainWindow.historyListModel.addElement("Snapshot Result at " + hangar.getIdentifier()+ ": " + hangar.getAirplanes().size());
				} else {
					// if receiving first marker start recording
					recording = true;
					// send marker messages to other channels
					if(payload[1].equals("H1")) {
						MainWindow.d1.startSnapshot();
					} else if(payload[1].equals("H2")) {
						MainWindow.d2.startSnapshot();
					} else if(payload[1].equals("H3")) {
						MainWindow.d3.startSnapshot();
					}
				}
			} else {
				// if airplanes
				System.out.println("String Array with Airplanes received = " + payload);
				hangar.addAirplanes(payload);	
			}
            threadSleep(350);
        }
    }
    
    public void setRecording(boolean recording) {
    	this.recording = recording;
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