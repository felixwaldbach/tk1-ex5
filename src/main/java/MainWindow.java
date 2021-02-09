import java.awt.BorderLayout;

import java.awt.GridLayout;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class MainWindow extends JFrame {

	public static DefaultListModel<String> historyListModel;
	private JList<String> historyList;

	private JLabel snapshot1Label;
	private JButton snapshot1Button;

	private JLabel snapshot2Label;
	private JButton snapshot2Button;

	private JLabel snapshot3Label;
	private JButton snapshot3Button;
	
	private Dispatcher d1, d2, d3;
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		MainWindow mainWindow = new MainWindow();
		mainWindow.setVisible(true);
	}

	public MainWindow() throws UnknownHostException, IOException {
		setSize(400, 300);
		setTitle("TK1-EX6");
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		// history list
		historyListModel = new DefaultListModel<String>();
		historyList = new JList<String>(historyListModel);
		historyList.setAutoscrolls(true);
		
		JScrollPane historyScroll = new JScrollPane(historyList);
		add(historyScroll, BorderLayout.CENTER);

		// slide panel
		JPanel sidePanel = new JPanel();
		sidePanel.setLayout(new GridLayout(3, 1));

		// snapshot 1
		snapshot1Label = new JLabel("Hangar 1 (#0)");
		snapshot1Button = new JButton("Snapshot");
		snapshot1Button.addActionListener(x -> snapshot(1));

		JPanel snapshot1 = new JPanel();
		snapshot1.setLayout(new GridLayout(2, 1));
		snapshot1.add(snapshot1Label);
		snapshot1.add(snapshot1Button);
		sidePanel.add(snapshot1);

		// snapshot 2
		snapshot2Label = new JLabel("Hangar 2 (#0)");
		snapshot2Button = new JButton("Snapshot");
		snapshot2Button.addActionListener(x -> snapshot(2));

		JPanel snapshot2 = new JPanel();
		snapshot2.setLayout(new GridLayout(2, 1));
		snapshot2.add(snapshot2Label);
		snapshot2.add(snapshot2Button);
		sidePanel.add(snapshot2);

		// snapshot 3
		snapshot3Label = new JLabel("Hangar 3 (#0)");
		snapshot3Button = new JButton("Snapshot");
		snapshot3Button.addActionListener(x -> snapshot(3));

		JPanel snapshot3 = new JPanel();
		snapshot3.setLayout(new GridLayout(2, 1));
		snapshot3.add(snapshot3Label);
		snapshot3.add(snapshot3Button);
		sidePanel.add(snapshot3);

		add(sidePanel, BorderLayout.EAST);
		
		// create 3 hangars and start the threads
		Hangar h1 = new Hangar("H1", "H2", "H3");
		new Thread(h1).start();
		Hangar h2 = new Hangar("H2", "H1", "H3");
		new Thread(h2).start();
		Hangar h3 = new Hangar("H3", "H1", "H2");
		new Thread(h3).start();
		
		// create UDP server for each channel
		// s12 -> hangar 1 receives from hangar 2
		UDPServer s12 = new UDPServer(3002, h1);
		new Thread(s12).start();
		UDPServer s13 = new UDPServer(3004, h1);
		new Thread(s13).start();
		UDPServer s21 = new UDPServer(3000, h2);
		new Thread(s21).start();
		UDPServer s23 = new UDPServer(3005, h2);
		new Thread(s23).start();
		UDPServer s31 = new UDPServer(3001, h3);
		new Thread(s31).start();
		UDPServer s32 = new UDPServer(3003, h3);
		new Thread(s32).start();
		
		// create UDP client for each channel
		// c12 -> hangar 1 sends to hangar 2
		// tuple (sij, cji) use the same channel	
		UDPClient c12 = new UDPClient(3000); //s21
		UDPClient c13 = new UDPClient(3001); //s31
		
		UDPClient c21 = new UDPClient(3002); //s12
		UDPClient c23 = new UDPClient(3003); //s32
		
		UDPClient c31 = new UDPClient(3004); //s13
		UDPClient c32 = new UDPClient(3005); //s23
		
		// create 3 dispatchers for each hangar with their sending channels
		d1 = new Dispatcher(h1, c12, c13);
		new Thread(d1).start();
		d2 = new Dispatcher(h2, c21, c23);
		new Thread(d2).start();
		d3 = new Dispatcher(h3, c31, c32);
		new Thread(d3).start();
	}

	private void snapshot(int snapshot) {
		// start snapshot at hangar snapshot
		if (snapshot == 1) {
			historyListModel.addElement("Snapshot: H1 initiator");
			d1.setMarkerMessage(true);
		} else if (snapshot == 2) {
			historyListModel.addElement("Snapshot: H2 initiator");
			d2.setMarkerMessage(true);
		} else if (snapshot == 3) {
			historyListModel.addElement("Snapshot: H3 initiator");
			d3.setMarkerMessage(true);
		}
	
	}

}
