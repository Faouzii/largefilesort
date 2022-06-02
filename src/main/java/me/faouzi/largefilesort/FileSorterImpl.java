package me.faouzi.largefilesort;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * Main large file sorter class implementation
 * @author Faouzi
 */
class FileSorterImpl implements FileSorter{

	private long maxRamValueInMb;

	
	public static final String EOF = System.lineSeparator();
	public static final String CHUNK_DIR = "\\chunks";
	public static final String DEFAULT_OUTPUT_FILE = "output.txt";
	public static final String CHUNK_PREFIX = "chunk-";
	public static final String TXT_EXT = ".txt";
	public static final String SLASH = "\\";
	
	

	
	@Override
	public void sortFile(File largeFile) throws IOException {
		maxRamValueInMb = 100;
		String chunckFileDirPath = largeFile.getParent() + CHUNK_DIR;
		
		spliteAndSortChunk(largeFile, maxRamValueInMb);
		mergeSortedChunks(new FileOutputStream(new File(largeFile.getParent() + SLASH + DEFAULT_OUTPUT_FILE)), chunckFileDirPath);
	}
	
	@Override
	public void sortFile(File largeFile, long maxRamMemoryInMb) throws IOException {
		String chunckFileDirPath = largeFile.getParent() + CHUNK_DIR;

		spliteAndSortChunk(largeFile, maxRamMemoryInMb);		
		mergeSortedChunks(new FileOutputStream(new File(largeFile.getParent() + SLASH + DEFAULT_OUTPUT_FILE)), chunckFileDirPath);
	}
	
	@Override
	public void sortFile(File largeFile, File outputFile) throws IOException {
		maxRamValueInMb = 100;
		String chunckFileDirPath = largeFile.getParent() + CHUNK_DIR;

		spliteAndSortChunk(largeFile, maxRamValueInMb);		
		mergeSortedChunks(new FileOutputStream(outputFile), chunckFileDirPath);
	}
	
	@Override
	public void sortFile(File largeFile, long maxRamMemoryInMb, File outputFile) throws IOException {
		String chunckFileDirPath = largeFile.getParent() + CHUNK_DIR;

		spliteAndSortChunk(largeFile, maxRamMemoryInMb);		
		mergeSortedChunks(new FileOutputStream(outputFile), chunckFileDirPath);
	}

	/**

	 * Splite the input large file into multiple chunk files sorted using RAM memory 

	 * @param largeFile

	 * @param osspliteSizeInMB

	 * @throws IOException

	 */
	
	public  void spliteAndSortChunk(File largeFile, long spliteSizeInMB) throws IOException{
		String chunckFileDirPath = largeFile.getParent() + CHUNK_DIR;
		
		if (spliteSizeInMB <= 0) {
	        throw new IllegalArgumentException("spliteSizeInMB must be more than zero");
	    }

		int counter = 1;
	    long sizeOfChunk = 1024 * 1024 * spliteSizeInMB;
		BufferedReader br = null;
	    try  {
	    	br = new BufferedReader(new FileReader(largeFile));
	    	
	        File sortedChunkDirectory = new File(chunckFileDirPath);
	        if (!sortedChunkDirectory.exists()) sortedChunkDirectory.mkdirs();

	    	String name = CHUNK_PREFIX;
	        String line = null;
	        
	        List<Integer> fileLines = new ArrayList<>();
	        int fileSize = 0;
	        //While spliting each chunk, we will load the chunk data in a list and sort it just before printing it into a file..
	        while ((line = br.readLine()) != null) {
	        	fileLines.add(Integer.parseInt(line));
	        	byte[] bytes = (line + EOF).getBytes(Charset.defaultCharset());
	        	fileSize += bytes.length;
	        	if (fileSize >= sizeOfChunk) {
	        		fileSize = 0;
	        		Collections.sort(fileLines);
	        		File chunk = new File(chunckFileDirPath, name +  String.format("%03d", counter++) + TXT_EXT);
	                createFileFromArrayList(new FileOutputStream(chunk),fileLines );
	        		fileLines.clear();
	        	}
	        }
	        
	        //printing the remaining chunk
	        Collections.sort(fileLines);
    		File newFile = new File(chunckFileDirPath, name +  String.format("%03d", counter++) + TXT_EXT);
            createFileFromArrayList(new FileOutputStream(newFile),fileLines );
    		fileLines.clear();
    	           
            }catch(IOException e) {
            	 e.printStackTrace();
            	 throw e;
            }finally {
				if(br != null) {
					br.close();
				}
			}
	}
	

	

	/**

	 * Merge all sorted chunk files into one large output file  

	 * @param output

	 * @param chunckFileDirPath

	 * @throws IOException

	 */
	
	public  void mergeSortedChunks(FileOutputStream output, String chunckFileDirPath) throws IOException{
	
		PrintWriter pw = new PrintWriter(output);
		
		Map<IntegerWrapper, BufferedReader> map = new HashMap<IntegerWrapper, BufferedReader>();
		List<BufferedReader> readers = new ArrayList<BufferedReader>();
		File chunkDirectory = new File(chunckFileDirPath);
		File[] sortedChuckFiles = chunkDirectory.listFiles();
		try{
 
			for ( int i = 0; i < sortedChuckFiles.length; i++ ){
				BufferedReader reader = new BufferedReader(new FileReader(sortedChuckFiles[i]));
				readers.add(reader);
				String line = reader.readLine();
				if ( line != null ){
					map.put(new IntegerWrapper(line), readers.get(i));
				}
			}
			
			List<IntegerWrapper> sorted = new LinkedList<IntegerWrapper>(map.keySet());
			while ( map.size() > 0 ){
				Collections.sort(sorted);
				System.out.println(sorted.toString());
				IntegerWrapper line = sorted.remove(0);
								
				pw.println(line);
				BufferedReader reader = map.remove(line);
				String nextLine = reader.readLine();
				if ( nextLine != null && !nextLine.isEmpty()){
					IntegerWrapper sw = new IntegerWrapper(nextLine);
					map.put(sw,  reader);
					sorted.add(sw);
				}
			}

		}catch(IOException io){
			throw io;
		}finally{
			for ( int i = 0; i < readers.size(); i++ ){
				try{readers.get(i).close();}catch(Exception e){}
			}
			for ( int i = 0; i < sortedChuckFiles.length; i++ ){
				sortedChuckFiles[i].delete();
			}
			chunkDirectory.delete();
			try{
				pw.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	

	/**

	 * Print a file from an ArrayList of Integers
	 * 
	 * @param stream
	 * 
	 * @param sortedNumbersList
	 * 
	 * @throws IOException
	 * 
	 */
	public void createFileFromArrayList(FileOutputStream stream, List<Integer> sortedNumbersList) throws IOException{
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(stream);
			for (Integer number : sortedNumbersList)
		         pw.println(number);
		}finally {
		   if(pw != null)
			   pw.close();
		}	   
	}
	
	


	/**

	 * Class which is a wrapper class for an Integer. This is necessary for integers duplicates, which may cause equals/hashCode

	 * conflicts within the HashMap used in the file merge.
	 *

	 */

	private class IntegerWrapper implements Comparable<IntegerWrapper>{
		private final Integer number;
		
		public IntegerWrapper(String line){
			this.number = Integer.parseInt(line);
		}

		@Override
		public int compareTo(IntegerWrapper o) {
			return number.compareTo(o.number);
		}

		public String toString() {
			return number.toString();
		}
		
	}
	

}
