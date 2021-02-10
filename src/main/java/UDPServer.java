import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.UUID;
import java.util.logging.Logger;

public class UDPServer implements Runnable {
    private DatagramSocket udpSocket;
    private int port;
    private String partner, identifier;
    private Hangar hangar;
    
    Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    
 
    public UDPServer(int port, Hangar hangar, String partner) throws SocketException, IOException {
        this.port = port;
        this.hangar = hangar;
        this.identifier = hangar.getIdentifier();
        this.partner = partner;
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
			if (payload[0].contentEquals("m")) {
				// if marker message
				logger.severe(identifier + " received marker message from " + partner);
				hangar.setInitiator(payload[1]);
				handleMarkerMessage();
					
			} else if (payload[0].contentEquals("s")){

				hangar.setRecord(hangar.getRecord()+ payload[1]);
				MainWindow.historyListModel.addElement("Global State: " + hangar.getRecord());
			} else {
				
				// if airplane                 
			    System.out.println("String Array with Airplanes received = " + payload);
			    hangar.addAirplanes(payload);
			    
			    // record the message
			    if (hangar.getH1().equals(partner)) {
			    	if(hangar.isEnableRecord()[0]) {
				    	hangar.setRecord(hangar.getRecord() + "C<" + identifier + "," + partner + ">:<" + Integer.toString(payload.length) + ">;");
				    }
		    	} else {
		    		if(hangar.isEnableRecord()[1]) {
				    	hangar.setRecord(hangar.getRecord() + "C<" + identifier + "," + partner + ">:<" + Integer.toString(payload.length) + ">;");
				    }
		    	}

			    is.close();

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
	
	public void handleMarkerMessage() {
	
		if (!hangar.isMarkerSent()[0] && !hangar.isMarkerSent()[1]) {
			
			//record current state of the hangar
			hangar.recordState();
	    	hangar.setRecord(hangar.getRecord() + "C<" + identifier + "," + partner + ">:<>;");
	    	
	    	hangar.setMarkerReceived(new Boolean[] {true, true}, partner);
	    	
	    	if (hangar.getH1().equals(partner)) {
	    		hangar.setEnableRecord(new Boolean[] {false, hangar.isEnableRecord()[1]});
	    	} else {
	    		hangar.setEnableRecord(new Boolean[] {hangar.isEnableRecord()[0], false});
	    	}

		} else {

			hangar.setMarkerReceived(new Boolean[] {false, false}, partner);
			
			if (hangar.getH1().equals(partner)) {
	    		hangar.setEnableRecord(new Boolean[] {false, hangar.isEnableRecord()[1]});
	    	} else {
	    		hangar.setEnableRecord(new Boolean[] {hangar.isEnableRecord()[0], false});
	    	}
			logger.info(identifier + " first channel: "+ !hangar.isEnableRecord()[0] + "; second channel:" + !hangar.isEnableRecord()[1]);
			logger.info("C<" + partner + "," + identifier + ">" + " state recorded.");
			
			if (!hangar.isEnableRecord()[0] && !hangar.isEnableRecord()[1]) {
				if (hangar.getInitiator().equals(identifier)) {
					MainWindow.historyListModel.addElement("Global State: " + hangar.getRecord());
				} else {
					hangar.setSendState(true);
				}
			}
		}
		
	}
}