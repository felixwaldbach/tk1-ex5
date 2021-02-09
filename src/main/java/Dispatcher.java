import java.io.IOException;

public class Dispatcher implements Runnable {

	private Hangar hangar;
	private String identifier;
	private UDPClient c1;
	private UDPClient c2;
	private UDPServer s1;
	private UDPServer s2;
	private String identC1;
	private String identC2;
	
	
	public Dispatcher(Hangar hangar, UDPClient c1, UDPClient c2, UDPServer s1, UDPServer s2, String identC1, String identC2) {
		System.out.println("Dispatcher for hangar " +hangar.getIdentifier()+ "started...");
		this.hangar = hangar;
		this.identifier = hangar.getIdentifier();
		this.c1 = c1;
		this.c2 = c2;
		this.s1 = s1;
		this.s2 = s2;
		this.identC1 = identC1;
		this.identC2 = identC2;
	}
	
	@Override
	public void run() {
		while(true) {
			int randomChannel = getRandomNumber(1, 0);
			if(randomChannel == 0 && hangar.getC1().size() > 0) {
				// send first message to other hangar
				try {
					System.out.println("D" +identifier+ " sends airplanes to " + hangar.getH1() + "...");
					c1.sendAirplanes(hangar.getC1().peek());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if(randomChannel == 1 && hangar.getC2().size() > 0) {
				try {
					System.out.println("D" +identifier+ " sends airplanes to " + hangar.getH2() + "...");
					c2.sendAirplanes(hangar.getC2().peek());
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
	
	public void sendMarkerMessages() throws IOException {
		c1.sendMarkerMessage(identC1);
		c2.sendMarkerMessage(identC2);
	}
	
	public void startSnapshot() throws IOException {
		this.sendMarkerMessages();
		s1.setRecording(true);
		s2.setRecording(true);
	}

}
