import java.util.LinkedList;

public class FIFOList {
	

	private LinkedList<String[]> data;
	
	public FIFOList() {
		this.data = new LinkedList<String[]>();
	}
	
	 
	public void enqueue(String[] item) {
		data.addLast(item);
		// System.out.println("Added: " + item);
	}
	 
	public String[] dequeue() {
	    // System.out.println("Removed: " + data.getFirst());
	    return data.removeFirst();
	     
	}
	 
	public String[] peek() {
	    return data.getFirst();
	}
	 
	public int size() {
	    return data.size();
	}
	 
	public boolean isEmpty() {
	    return data.isEmpty();
	}
	
}
