import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

/*
 * A Hangar is responsible for managing airplanes, and adding random airplanes in queues.
 */
public class Hangar implements Runnable {
	
	Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	// the identifier of hanger: H1, H2, H3, ...
	private String identifier;
	private String record;
	private String sender;
	//private boolean markerReceived = false;
	private Boolean[] markerReceived = new Boolean[] {false, false};
	private Boolean[] markerSent = new Boolean[] {false, false};
	private Boolean[] enableRecord = new Boolean[] {false, false};
	private boolean initiator = false;
	private boolean enableInitiate = true;
	private boolean recorded = false;
	private String sessionId;
	
	
	// the identifiers of adjacent hangers
	String h1, h2;
	private List<String> airplanes;
	
	// storages for channel 1 and 2
	private FIFOList c1;
	private FIFOList c2;
	
	
	public Hangar(String identifier, String h1, String h2) {
		System.out.println("Hangar " + identifier + " entering the system. Adding initial airplanes...");
		airplanes = new ArrayList<>();
		
		this.identifier = identifier;
		this.h1 = h1;
		this.h2 = h2;
		
		c1 = new FIFOList();
		c2 = new FIFOList();
		
		// Add initial 10 airplanes
		for(int i=0; i<10; i++) {
			airplanes.add(identifier + "_AIR_" + i);
		}
		System.out.println("Hangar " +identifier+" initialized...");
	}
	
	@Override
	public void run() {
		while(true) {
			threadSleep(getRandomNumber(4000, 1000));
			sendAirplanesRandomly();	
		}
	}

	public void sendAirplanesRandomly() {
		int randomChannel = getRandomNumber(1, 0);
		
		int randomAmount = getRandomNumber(Math.min(airplanes.size(), 5), 1);
		
		String[] randomAirplanes = new String[randomAmount];
		
		// remove planes from airplanes, add them to randomAirplanes
		for(int i=0; i<randomAmount; i++) {
			if(airplanes.size() == 0) {
				break; 
			}
			int randomIndex = getRandomNumber(airplanes.size() - 1, 0);
			randomAirplanes[i] = airplanes.get(randomIndex);
			airplanes.remove(randomIndex);
		}
		
		
		if(randomChannel == 0) {
			MainWindow.historyListModel.addElement("Transfer: " + identifier + " -> " + h1 + " (" +randomAmount+ ")");
			System.out.println("Hangar " + identifier + " puts "+ randomAmount + " airplanes to Hangar " +h1+ " in queue...");
			// put randomAirplanes in queue of channel 1
			c1.enqueue(randomAirplanes);
		} else {
			MainWindow.historyListModel.addElement("Transfer: " + identifier + " -> " + h2 + " (" +randomAmount+ ")");
			System.out.println("Hangar " + identifier + " puts "+ randomAmount + " airplanes to Hangar " +h2+ " in queue...");
			// put randomAirplanes in queue of channel 2
			c2.enqueue(randomAirplanes);
		}
	}
	
	private void threadSleep(long millis) {
		try {
			System.out.println("Hangar " + identifier + " goes to sleep for "+ millis + "ms...");
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private int getRandomNumber(int uBound, int lBound) {
		return ((int)(Math.random() * ((uBound - lBound) + 1)) + lBound);
	}
	
	public FIFOList getC1() {
		return this.c1;
	}
	

	public FIFOList getC2() {
		return this.c2;
	}
	
	public String getIdentifier() {
		return this.identifier;
	}

	public String getH1() {
		return this.h1;
	}

	public String getH2() {
		return this.h2;
	}
	
	public void setRecord(String record) {
		this.record = record;
	}
	
	public String getRecord() {
		return this.record;
	}
	
	public String getSender() {
		return this.sender;
	}
	
	public void setMarkerReceived(Boolean[] markerReceived, String sender) {
		this.markerReceived = markerReceived;
		this.sender = sender;
	}
	
	public Boolean[] getMarkerReceived() {
		return this.markerReceived;
	}
	
	public void setMarkerSent(Boolean[] markerSent) {
		this.markerSent = markerSent;
	}
	
	public Boolean[] isMarkerSent() {
		return this.markerSent;
	}
	
	public Boolean[] isEnableRecord() {
		return this.enableRecord;
	}
	
	public void setEnableRecord (Boolean[] en) {
		this.enableRecord = en;
	}
	
	public void startSnapshop() {
		
		logger.info(identifier+ " initializes snapshot...");
    	setEnableInitiator(false);
    	setSessionId(UUID.randomUUID().toString());
    	
		recordState();
		setMarkerReceived(new Boolean[] {true, true}, identifier); 
		
		this.initiator = true;
		this.enableRecord = new Boolean[] {true, true};
		this.enableInitiate = true;
	}
	
	
	
	public void setEnableInitiator (boolean ei) {
		this.enableInitiate = ei;
	}
	
	public boolean getEnableInitiator () {
		return this.enableInitiate;
	}
	
	public boolean isInitiator() {
		return this.initiator;
	}
	
	public void setRecorded (boolean r) {
		this.recorded = r;
	}
	
	public boolean isRecorded() {
		return this.recorded;
	}
	
	public void setSessionId(String id) {
		this.sessionId = sessionId;
	}
	
	public String getSessionId() {
		return this.record;
	}
	
	// add new airplanes to the airline array of hanger
	public void addAirplanes(String [] airplanes) {
		System.out.println("Hangar " + identifier + " received airplanes :");
		for(String a: airplanes) {
			System.out.print(a + "; ");
			this.airplanes.add(a);
		}
		System.out.print("\n");
	}
	
	public void recordState() {
		
		record = identifier + ":" + Integer.toString(this.airplanes.size()) + ";";

		setRecorded(true);
		
		logger.info(identifier+ " state recorded: " + record);
	}
	
}
