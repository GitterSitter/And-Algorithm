import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.SystemColor;


public class GUI {

	private JFrame frame;
	private JTextField textField;
	private JLabel lblSearch;
	private JLabel lblResult;
	
	public static boolean[][] check;
	public static HashMap<String, Integer> docFreq;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setBackground(Color.DARK_GRAY);
		
		
		JTextArea textArea = new JTextArea();
		textArea.setForeground(Color.WHITE);
		textArea.setBackground(Color.DARK_GRAY);
		textArea.setEditable(false);
		textArea.setEnabled(false);
		
		lblSearch = new JLabel("Search");
		lblSearch.setForeground(Color.WHITE);
		
		lblResult = new JLabel("Result");
		lblResult.setForeground(Color.WHITE);
		textField = new JTextField();
		textField.setForeground(new Color(0, 0, 0));
		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				TreeMap<String, String[]> col = readFile();
				String x = search(col, textField.getText()).toString();
				x= x.replaceAll(",", "\n");
				textArea.setText(x);
				
			}
		});
		textField.setText("");
		textField.setColumns(10);
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(59)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(18)
							.addComponent(textArea, GroupLayout.PREFERRED_SIZE, 199, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblSearch)
							.addGap(38)
							.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(54, Short.MAX_VALUE))
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addContainerGap(155, Short.MAX_VALUE)
					.addComponent(lblResult)
					.addGap(145))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblSearch))
					.addGap(22)
					.addComponent(lblResult)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(textArea, GroupLayout.PREFERRED_SIZE, 161, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(28, Short.MAX_VALUE))
		);
		frame.getContentPane().setLayout(groupLayout);
		frame.setBounds(100, 100, 346, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	public static ArrayList<String> search(TreeMap<String, String[]> col, String searchParam) {
		searchParam = searchParam.replaceAll("[^\\p{L}\\p{Nd}]+", " ");
		searchParam = searchParam.toLowerCase();
		String[] search = searchParam.split(" ");
		docFreq = new HashMap<String, Integer>();
		check = new boolean[col.size()][search.length];

		int d = 0;
		for (Entry<String, String[]> index : col.entrySet()) {
			String key = index.getKey();
			String[] values = index.getValue();
			int freq = 0;
			for (String ind : values) {
				for (int i = 0; i < search.length; i++) {
					if (search[i].equals(ind)) {
						freq++;
						check[d][i] = true;

					}

				}

			}

			boolean notIn = false;
			for (int j = 0; j < check[d].length; j++) {
				if (check[d][j] == false)
					notIn = true;
			}
			if (notIn == false) {
				docFreq.put(key, freq);
			}
			d++;
		}

		List<Integer> sortList = new ArrayList<Integer>(docFreq.values());
		Collections.sort(sortList, new Comparator<Integer>() {
			@Override
			public int compare(Integer tall1, Integer tall2) {
				return -(tall1 - tall2);
			}
		});

		ArrayList<String> resultSet = new ArrayList<String>();
		while (!docFreq.isEmpty()) {
			Map.Entry<String, Integer> maxEntry = null;
			for (Entry<String, Integer> entry : docFreq.entrySet()) {
				if (maxEntry == null
						|| entry.getValue().compareTo(maxEntry.getValue()) > 0) {
					maxEntry = entry;
				}
			}

			resultSet.add(maxEntry.getKey());
			docFreq.remove(maxEntry.getKey());
		
		}
			return resultSet;
	}

	@SuppressWarnings("resource")
	public static TreeMap<String, String[]> readFile() {
		TreeMap<String, String[]> collection = new TreeMap<String, String[]>();
		try {
			File[] documents = getDocuments();
			for (File d : documents) {
				Scanner scan = new Scanner(d);
				String[] document = null;
				String text = "";
				String docName = "";
				docName = d.getName();
				while (scan.hasNextLine()) {
					text = text + " " + scan.nextLine();

				}
				text = text.replaceAll("[^\\p{L}\\p{Nd}]+", " ");
				text = text.toLowerCase();
				document = text.split(" ");
				collection.put(docName, document);
			}

		} catch (Exception e) {
		}

		return collection;
	}

	public static File[] getDocuments() {
		File fil = new File("Files");
		return fil.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				return filename.endsWith(".txt");
			}
		});
	}


}
