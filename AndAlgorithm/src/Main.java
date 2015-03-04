import java.io.File;
import java.io.FilenameFilter;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;

import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;

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
	
	public static void main(String[] args) {
		TreeMap<String, String[]> col = readFile();
		System.out.println(search(col, "Trond Andreas sexy"));
		printCheck();
		
	}

	
	
	public static String search(TreeMap<String, String[]> col,String searchParam) {
		searchParam = searchParam.replaceAll("[^\\p{L}\\p{Nd}]+", " ");
		searchParam = searchParam.toLowerCase();
		String[] search = searchParam.split(" ");
		TreeMap<Integer, String> docFreq = new TreeMap<Integer, String>();
		
		
		 check = new boolean[col.size()][search.length];
		 
		 System.out.println(col.size());

		// Documents
		int d =0 ;
	 for (Entry<String, String[]> index : col.entrySet()) {
			String key = index.getKey();
			String[] values = index.getValue();
			int freq = 0;
			System.out.println(key);
			
			for (String ind : values) {
				
				for (int i = 0; i<search.length;i++) {
					if (search[i].equals(ind)) {
						freq++;
					check[d][i] = true;
					System.out.println("doc "  + d + " " + i + " term");
					}
					
				}

			}
			
			docFreq.put(freq, key);
			d++;

		}

		for (Entry<Integer, String> entry : docFreq.entrySet()) {
			Integer key = entry.getKey();
			String val = entry.getValue();
			System.out.println(key + " " + val);
			
			
			for(int i =0;i<check.length;i++){
				System.out.println(i);
				for(int j= 0;j< check[i].length;j++){
					System.out.print(check[i][j] + " ");
			
				}
			}
			
		}

		if (docFreq.containsKey(0)) {
			return null;
		} else
			return docFreq.lastEntry().toString();
	}

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
	
	public static void printCheck(){
		for(int i = 0;i < check.length;i++){
			System.out.println("");
			System.out.print((i+1) +  " : ");
			for(int j = 0; j< check[i].length;j++){
				System.out.print(check[i][j] + " ");
			}
		}
	}

}
