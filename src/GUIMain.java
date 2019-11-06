import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.Border;

public class GUIMain {

	public static void constructGUI(RoadMap roadmap) {
		int w = 800;
		int h = 800;
		JFrame frame = new JFrame("Trip Planner");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		MapVisualizer map = new MapVisualizer(roadmap);

		JLabel titleText = new JLabel("Lone Star Traversal");
		titleText.setLocation(w / 2 - 100, -h / 2 + 50);
		titleText.setSize(w, h);
		titleText.setFont(new Font("Freestyle Script", 1, 30));
		GridLayout SRlayout = new GridLayout(5, 0);
		GridLayout TPlayout = new GridLayout(4, 2);

		JPanel contentPane = new JPanel();
		contentPane.setOpaque(true);
		contentPane.setBackground(Color.WHITE);
		contentPane.setLayout(null);

		JTabbedPane tab = new JTabbedPane();
		tab.setSize(w, 500);
		tab.setVisible(true);
		tab.setLocation(0, 100);

		Border border = BorderFactory.createLineBorder(Color.black, 5);

		JLabel startlabel = new JLabel("Starting City: ");
		startlabel.setBackground(Color.cyan);
		JTextField start = new JTextField("Enter in your starting city!");
		start.setBackground(Color.blue);
		start.setBorder(border);
		JLabel endlabel = new JLabel("Ending City: ");
		endlabel.setBackground(new Color(153, 255, 102));
		JTextField end = new JTextField("Enter in your ending city!");
		end.setBackground(Color.green);
		JTextField searcher = new JTextField("Enter in a character and see which city you want!");

		JButton SR = new JButton("Go!");
		// SR.addActionListener(new ActionListener());
		//
		// public void actionPerformed(ActionEvent e)
		// {
		//
		// }

		TextFieldListener sL = new TextFieldListener(start);
		TextFieldListener eL = new TextFieldListener(end);
		TextFieldListener searchL = new TextFieldListener(searcher);

		start.addActionListener(sL);
		end.addActionListener(eL);
		searcher.addActionListener(searchL);

		JPanel SRPanel = new JPanel();
		SRPanel.setOpaque(true);
		SRPanel.setBackground(Color.white);
		SRPanel.setSize(200, 200);
		SRPanel.setLayout(SRlayout);
		SRPanel.add(startlabel);
		SRPanel.add(start);
		SRPanel.add(endlabel);
		SRPanel.add(end);
		SRPanel.add(SR);

		JLabel TPSCLabel = new JLabel("Starting City: ");
		JTextField tpstart = new JTextField("Enter in your starting city!");
		JLabel timeLabel = new JLabel("Time: ");
		JTextField time = new JTextField("0");
		JLabel distanceLabel = new JLabel("Distance: ");
		JTextField distance = new JTextField("0");
		JButton timeButton = new JButton("Calculate Based on Time");
		JButton distanceButton = new JButton("Calculate Based on Distance");

		JPanel TPPanel = new JPanel();
		TPPanel.setOpaque(true);
		TPPanel.setBackground(Color.white);
		TPPanel.setSize(200, 200);
		TPPanel.setLayout(TPlayout);
		TPPanel.add(TPSCLabel);
		TPPanel.add(tpstart);
		TPPanel.add(timeLabel);
		TPPanel.add(distanceLabel);
		TPPanel.add(time);
		TPPanel.add(distance);
		TPPanel.add(timeButton);
		TPPanel.add(distanceButton);

		tab.addTab("Trip Planner", TPPanel);
		tab.addTab("Shortest Route", SRPanel);
		tab.addTab("City Search", searcher);

		contentPane.add(titleText);
		contentPane.add(tab);
		contentPane.add(map);

		frame.setContentPane(contentPane);
		frame.setSize(w, h);
		frame.setLocationByPlatform(true);
		frame.setVisible(true);

	}
}
