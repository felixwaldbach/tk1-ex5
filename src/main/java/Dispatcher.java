import java.io.IOException;
import java.util.logging.Logger;

/*
 *  A dispatcher monitors channel queues for each hangar, and sends the item via UDP channel
 */
public class Dispatcher implements Runnable {

	Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	private Hangar hangar;
	private String identifier;
	private UDPClient c1, c2;
	private String h1, h2;
	private String sessionId;
	
	public Dispatcher(Hangar hangar, UDPClient c1, UDPClient c2) {
		System.out.println("Dispatcher for hangar " +hangar.getIdentifier()+ " started...");
		this.hangar = hangar;
		this.identifier = hangar.getIdentifier();
		this.c1 = c1;
		this.c2 = c2;
		this.h1 = hangar.getH1();
		this.h2 = hangar.getH2(); 
	}
	
	@Override
	public void run() {
		
		// The thread sends one item in channel queue each time
		while(true) {
			int randomChannel = getRandomNumber(1, 0);
			
			// if marker message received 
			if(hangar.getMarkerReceived()[0] || hangar.getMarkerReceived()[1]) {
				if(hangar.getMarkerReceived()[0]) {
					
					hangar.setMarkerReceived(new Boolean[] {false, hangar.getMarkerReceived()[1]}, hangar.getSender());
					
					if(!hangar.isMarkerSent()[0]) {
						System.out.println(identifier + " receives marker from sender " +hangar.getSender()+ "; h1:" + h1 + "; h2:" + h2);
						
						// send marker message to other h1
						sendMarkerMessage(c1, h1);
					}
				}
				
				if (hangar.getMarkerReceived()[1]) {
					
					hangar.setMarkerReceived(new Boolean[] {hangar.getMarkerReceived()[0], false}, hangar.getSender());
					// send marker message to other hangar h2
					sendMarkerMessage(c2, h2);
					
				}
			} else if (hangar.getSendState()){
				
				System.out.println("Initiator: " +hangar.getInitiator() + "; Identifier: " + identifier);
				// send record to initiator
				try {
					if (hangar.getInitiator().contentEquals(h1)) {
						c1.sendState(hangar.getRecord());
						System.out.println("D" +identifier+ " sends state to " + h1 + "...");
						
					} else if (hangar.getInitiator().contentEquals(h2)){
						c2.sendState(hangar.getRecord());
						System.out.println("D" +identifier+ " sends state to " + h2 + "...");
					}
					hangar.setSendState(false);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if(randomChannel == 0 && hangar.getC1().size() > 0) {
				// send first message to other hangar
				try {
					System.out.println("D" +identifier+ " sends airplanes to " + h1 + "...");
					
					String[] airplanes = hangar.getC1().dequeue();
					MainWindow.historyListModel.addElement("Transfer: " + identifier + " -> " + h1 + " (" +airplanes.length+ ")");
					c1.sendAirplanes(airplanes);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if(randomChannel == 1 && hangar.getC2().size() > 0) {
				try {
					System.out.println("D" +identifier+ " sends airplanes to " + h2 + "...");
					
					String[] airplanes = hangar.getC2().dequeue();
					MainWindow.historyListModel.addElement("Transfer: " + identifier + " -> " + h2 + " (" +airplanes.length+ ")");
					c2.sendAirplanes(airplanes);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			threadSleep(getRandomNumber(3000, 1000));

		}		
	}
	
	private void threadSleep(long millis) {
		try {
			System.out.println("D" +identifier+" goes to sleep for " + millis + "ms...");
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private int getRandomNumber(int uBound, int lBound) {
		return ((int)(Math.random() * ((uBound - lBound) + 1)) + lBound);
	}
	
	private void sendMarkerMessage(UDPClient c, String h) {

		logger.info("D" +identifier+ " sends marker message to " + h + "...");
		MainWindow.historyListModel.addElement("Marker: " + identifier + " -> " + h);
		try {
			c.sendMarkerMessage(hangar.getInitiator());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		hangar.setMarkerSent(new Boolean[] {true, hangar.isMarkerSent()[1]});
		
	}

}
