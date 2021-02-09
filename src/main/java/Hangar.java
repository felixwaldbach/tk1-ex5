import java.util.ArrayList;
import java.util.List;

/*
 * A Hangar is responsible for managing airplanes, and adding random airplanes in queues.
 */
public class Hangar implements Runnable {
	
	// the identifier of hanger: H1, H2, H3, ...
	private String identifier;
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
	
	public int getNumOfPlane() {
		return this.airplanes.size();
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
	
}
