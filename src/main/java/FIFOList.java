import java.util.LinkedList;

/**
 * Simple Queue (FIFO) based on LinkedList.
 */

public class FIFOList {
	

	private LinkedList<String[]> data;
	
	public FIFOList() {
		this.data = new LinkedList<String[]>();
	}
	
	/*
	 * Puts string array in queue.
	 */
	public void enqueue(String[] item) {
		data.addLast(item);
		// System.out.println("Added: " + item);
	}
	
	/*
	 * Returns a string array from queue.
	 * 
	 * @return element from queue or <code>null</code> if queue is empty
	 */
	public String[] dequeue() {
	    // System.out.println("Removed: " + data.getFirst());
		if (data.isEmpty()) {
			return null;
		}
	    return data.removeFirst();
	     
	}
	 
	/*
	 * Peeks an element in the queue. Returned elements is not removed from the queue.
	 */
	public String[] peek() {
	    return data.getFirst();
	}
	
	/*
	 * Returns queue size.
	 */
	
	public int size() {
	    return data.size();
	}
	
	/*
	 * Returns true if queue is empty, otherwise false.
	 */
	public boolean isEmpty() {
	    return data.isEmpty();
	}
	
}
