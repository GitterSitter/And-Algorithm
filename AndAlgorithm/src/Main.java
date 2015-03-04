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

/*
 For each query term t
 1. retrieve lexicon entry for t
 2. note ft and address of It (inverted list)
 2. Sort query terms by increasing ft
 3. Initialize candidate set C with It of the term with the smallest ft'
 4. For each remaining t
 1. Read It
 2. For each d ϵ C, if d ɇ It, C <- C – {d}
 3. If C = {}, return… there are no relevant docs
 5. Look up each d ϵϵ C and return to the user

 */
public class Main {

	public static boolean[][] check;
	public static HashMap<String, Integer> docFreq;

	public static void main(String[] args) {
		TreeMap<String, String[]> col = readFile();
		System.out.println(search(col, "B"));

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
