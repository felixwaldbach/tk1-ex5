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
	private static String MARKER = "m";
    private DatagramSocket udpSocket;
    private int port;
    private String partner, identifier;
    private String record = "";
    private Hangar hangar;
    private boolean enableRecord = false;
    private String uuid = "";
    
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
			if (payload[0].equals(MARKER)) {
				// if marker message
				logger.severe(identifier + " received marker message from " + partner );
				handleMarkerMessage(payload);
					
			} else {
				
				// if airplane                 
			    System.out.println("String Array with Airplanes received = " + payload);
			    hangar.addAirplanes(payload);
			    
			    // record
			    if (hangar.getH1().equals(partner)) {
			    	if(hangar.isEnableRecord()[0]) {
				    	record = hangar.getRecord() + "C<" + identifier + "," + partner + ">:" + payload + ";";
				    	hangar.setRecord(record);
				    }
		    	} else {
		    		if(hangar.isEnableRecord()[1]) {
				    	record = hangar.getRecord() + "C<" + identifier + "," + partner + ">:" + payload + ";";
				    	hangar.setRecord(record);
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
	
	public void handleMarkerMessage(String[] payload) {
		
		
		if (!hangar.isMarkerSent()[0] && !hangar.isMarkerSent()[1]) {
			
			//record current state of the hangar
			hangar.recordState();
			record = hangar.getRecord() + "C<" + identifier + "," + partner + ">: ;";
	    	hangar.setRecord(record);
	    	
	    	hangar.setMarkerReceived(new Boolean[] {true, true}, partner);
	    	
	    	if (hangar.getH1().equals(partner)) {
	    		hangar.setEnableRecord(new Boolean[] {false, hangar.isEnableRecord()[1]});
	    	} else {
	    		hangar.setEnableRecord(new Boolean[] {hangar.isEnableRecord()[0], false});
	    	}

		} else {

			hangar.setMarkerReceived(new Boolean[] {false, false}, partner);
			
			logger.info("Channel " + partner + " -> " + identifier+ " state recorded: " + hangar.getRecord());
			
			record = "";

			logger.info("C<" + partner + "," + identifier + ">" + " state recorded.");
		}
		
		
		
		/*
		logger.info("[A back message?] " + payload[3]);
		
		System.out.println(identifier + ": [A back message?] " + payload[3] +"; Is recorded? " + hangar.isRecorded());
		if(!Boolean.parseBoolean(payload[3])) {
			
			hangar.setSessionId(payload[1]);
			
			if(!hangar.isRecorded()) {
				//record current state of the hangar
				hangar.recordState();
				record = hangar.getRecord() + "C<" + identifier + "," + partner + ">: ;";
		    	hangar.setRecord(record);
		    	
		    	hangar.setEnableRecord(false);
		    	hangar.setMarkerReceived(new Boolean[] {true, true}, partner);
			} else {
				hangar.setEnableRecord(false);
				hangar.setMarkerReceived(new Boolean[] {false, false}, partner);
			}
	    	
		} else {
			hangar.setEnableRecord(false);
			hangar.setMarkerReceived(new Boolean[] {false, false}, partner);
			
			
			logger.info("Channel " + partner + " -> " + identifier+ " state recorded: " + hangar.getRecord());
			
			record = "";
			
			if(!hangar.getMarkerReceived()[0] && !hangar.getMarkerReceived()[1]) {
				logger.info(identifier + " end the algorithm");
			}
		}*/
		
	}
}