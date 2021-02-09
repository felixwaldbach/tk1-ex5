import java.io.IOException;

/*
 *  A dispatcher monitors channel queues for each hangar, and sends the item via UDP channel
 */
public class Dispatcher implements Runnable {

	private Hangar hangar;
	private String identifier;
	private UDPClient c1, c2;
	private String h1, h2;
	private boolean enableMarker;
	
	public Dispatcher(Hangar hangar, UDPClient c1, UDPClient c2) {
		System.out.println("Dispatcher for hangar " +hangar.getIdentifier()+ "started...");
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
			if(enableMarker) {
				enableMarker = false;
				// send marker message to other hangars
				try {
					System.out.println("D" +identifier+ " sends marker message to " + hangar.getH1() + "...");
					MainWindow.historyListModel.addElement("Marker: " + identifier + " -> " + h1);
					c1.sendMarkerMessage();
					System.out.println("D" +identifier+ " sends marker message to " + hangar.getH2() + "...");
					MainWindow.historyListModel.addElement("Marker: " + identifier + " -> " + h2);
					c2.sendMarkerMessage();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if(randomChannel == 0 && hangar.getC1().size() > 0) {
				// send first message to other hangar
				try {
					System.out.println("D" +identifier+ " sends airplanes to " + hangar.getH1() + "...");
					
					String[] airplanes = hangar.getC1().dequeue();
					MainWindow.historyListModel.addElement("Transfer: " + identifier + " -> " + h1 + " (" +airplanes.length+ ")");
					c1.sendAirplanes(airplanes);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if(randomChannel == 1 && hangar.getC2().size() > 0) {
				try {
					System.out.println("D" +identifier+ " sends airplanes to " + hangar.getH2() + "...");
					
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
	
	public void setMarkerMessage(boolean enableMarker) {
		this.enableMarker = enableMarker;
	}
	// start Snapshot
	// send marker message over c1 and c2

}
