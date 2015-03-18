import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
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

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

//kommentar for oppdatering

public class GUI {

	private JFrame frame;
	private JTextField textField;
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

		final JTextArea textArea = new JTextArea();
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 18));
		textArea.setBounds(70, 81, 221, 213);
		textArea.setForeground(Color.WHITE);
		textArea.setBackground(Color.DARK_GRAY);
		textArea.setEditable(false);
		textArea.setEnabled(false);

		lblResult = new JLabel("Results: ");
		lblResult.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblResult.setBounds(125, 42, 119, 20);
		lblResult.setForeground(Color.WHITE);
		textField = new JTextField();
		textField.setBounds(115, 11, 119, 20);
		textField.setForeground(new Color(0, 0, 0));
		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				TreeMap<String, String[]> col = readFile();
				String x = search(col, textField.getText()).toString();
				x = x.replaceAll(",", "\n");
				textArea.setText(x);

			}
		});
		textField.setText("");
		textField.setColumns(10);
		frame.getContentPane().setLayout(null);
		frame.getContentPane().add(textArea);
		frame.getContentPane().add(textField);
		frame.getContentPane().add(lblResult);
		frame.setBounds(100, 100, 431, 458);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static ArrayList<String> search(TreeMap<String, String[]> col,
			String searchParam) {
		searchParam = searchParam.replaceAll("[^\\p{L}\\p{Nd}]+", " ");
		searchParam = searchParam.toLowerCase();
		String[] search = searchParam.split(" ");
		docFreq = new HashMap<String, Integer>();
		check = new boolean[col.size()][search.length];
		double totalDoc = col.size();
		int d = 0;
		HashMap<String, Map<String, Integer>> doc = new HashMap<String, Map<String, Integer>>();
		for (Entry<String, String[]> index : col.entrySet()) {
			String document = index.getKey();
			String[] values = index.getValue();
			HashMap<String, Integer> tf = new HashMap<String, Integer>();
			
			int freq = 0;
		
			for (int i = 0; i < search.length; i++) {
				termDocCounter(search[i],values);
				for (String word : values) {
					if (search[i].equals(word)) {
						freq++;
						check[d][i] = true;
					
					}
				}
				
				boolean notIn = false;
				for(int j = 0; j < check[d].length;j++){
					if (check[d][j] == false) {
						notIn = true;
					}
				}
				if (notIn == false) {
					docFreq.put(document, freq);
					tf.put(search[i], freq);

				}
				freq = 0;
			}
			doc.put(document, tf);
			d++;
			
		}

		double ni = 0;
		for (Entry<String, Map<String, Integer>> entry : doc.entrySet()) {
			
			int tall=0;
			int[] tallet = new int[search.length];
			for (Entry<String, Integer> ent : entry.getValue().entrySet()) {
				boolean exists = false;
				if(ent.getKey()==search[tall]){
					tall++;
				}
			}
			
			for (Entry<String, Integer> ent : entry.getValue().entrySet()) {

				double idf = Math.log(totalDoc / ni) / Math.log(2);
				double tf = 1 + Math.log((double) ent.getValue()) / Math.log(2);
				double result = tf * idf;

				//System.out.println(tf + "   " + idf +" "+ ni  +" total doc " + totalDoc);
			//	System.out.println( ent.getKey() + " " + entry.getKey()+ " antall " + ent.getValue());
				
			
			}

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
	
	public static int termDocCounter(String term, String[] doc){
		for(int i = 0; i < doc.length;i++){
			if(doc[i].equals(term)){
				return 1;
			}
		}
		return 0;		
	}

}
