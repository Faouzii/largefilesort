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

	public static final long MAX_RAM_VALUE = 100;
	public static final String INPUT_FILE_DIR = "G:\\automation-heros\\";
	public static final String CHUNK_FILES_DIR = "G:\\automation-heros\\chunks\\";
	public static final String SORTED_CHUNK_FILES_DIR = "G:\\automation-heros\\sorted-chunks\\";
	public static final String INPUT_FILE_NAME = "input.txt";
	public static final String EOF = System.lineSeparator();
	
	/*
	 * TODO
	 * Write testable methods
	 * refactor methods
	 * unify file methods
	 */
	
	
	@Override
	public void sortFile() throws IOException {
		//Splite the large file (1GB) to multiple small files (chnunks) that will fit into the RAM
		spliteLargeFile(new File(INPUT_FILE_DIR + INPUT_FILE_NAME), MAX_RAM_VALUE);
		//Iterate over all chunk files and sort each one of them using RAM memory
		sortChunkFiles();
		//Merge chunks
		mergeSortedChunks(new FileOutputStream(new File(INPUT_FILE_DIR + "output.txt")));
	}
	

	
	public static void spliteLargeFile(File largeFile, long spliteSizeInMB) throws IOException{
		if (spliteSizeInMB <= 0) {
	        throw new IllegalArgumentException("spliteSizeInMB must be more than zero");
	    }

		int counter = 1;
	   // long sizeOfChunk = 1024 * 1024 * spliteSizeInMB;
		long sizeOfChunk =  10 *spliteSizeInMB;
	    try (BufferedReader br = new BufferedReader(new FileReader(largeFile))) {
	        //String name = largeFile.getName().substring(0,largeFile.getName().length() - 4);
	    	File chunkDirectory = new File(CHUNK_FILES_DIR);
	        if (!chunkDirectory.exists()) chunkDirectory.mkdirs();
	        
	        File sortedChunkDirectory = new File(SORTED_CHUNK_FILES_DIR);
	        if (!sortedChunkDirectory.exists()) sortedChunkDirectory.mkdirs();

	    	String name = "unsorted-chunck-";
	        String line = br.readLine();
	        while (line != null) {
	            File newFile = new File(CHUNK_FILES_DIR, name +  String.format("%03d", counter++)+".txt");
	            try (OutputStream out = new BufferedOutputStream(new FileOutputStream(newFile))) {
	                int fileSize = 0;
	                while (line != null) {
	                    byte[] bytes = (line + EOF).getBytes(Charset.defaultCharset());
	                    if (fileSize + bytes.length > sizeOfChunk)
	                        break;
	                    out.write(bytes);
	                    fileSize += bytes.length;
	                    line = br.readLine();
	                }
	            }
	        }
	    }
	}
	
	public void sortChunkFiles() throws FileNotFoundException, IOException {
		  File chunkDirectory = new File(CHUNK_FILES_DIR);
		  File[] chuckFiles = chunkDirectory.listFiles();
		  if (chuckFiles != null) {
		    for (File chunk : chuckFiles) {
		    	System.out.println("Reading chunk file : " + chunk.getName() +" into RAM memory..");
		    	File sortedChunkFile = new File(SORTED_CHUNK_FILES_DIR + chunk.getName().substring(2));
		    	
		    	//read from the chunk file and store data inside an arrayList
		    	List<Integer> sortedNumbersList = new ArrayList<>();
			    BufferedReader br = new BufferedReader(new FileReader(chunk));
				String line = br.readLine();
			    while ((line != null) && !line.isEmpty()) {
			    	try {
			    		sortedNumbersList.add(Integer.parseInt(line));
			        	line = br.readLine();
					} catch (Exception e) {
			            System.out.println("Error parsing number at line : " + line);

					}
			    }
			    br.close();
			     
		    	//sort the numbers
			    System.out.println(sortedNumbersList.toString());
			    System.out.println("Sorting read chunk in RAM memory, using ArrayList.sort()");
			    Collections.sort(sortedNumbersList);
			    System.out.println(sortedNumbersList.toString());

		    	//write the data back to a sorted chunk file
		    	System.out.println("Saving sorted chunk file to : " + sortedChunkFile.getParent()+ "\\" + sortedChunkFile.getName());
		    	createFileFromArrayList(new FileOutputStream(sortedChunkFile), sortedNumbersList);
		    }
		  } else {
			  throw new IOException("Error occured while reading Chunk files");
		  }
	}
	

	
	public void createFileFromArrayList(FileOutputStream stream, List<Integer> sortedNumbersList) throws FileNotFoundException {
	    PrintWriter pw = new PrintWriter(stream);
	    for (Integer number : sortedNumbersList)
	         pw.println(number);
	    pw.close();
	}

	
	public  void mergeSortedChunks(FileOutputStream output) throws IOException{
			
		PrintWriter pw = new PrintWriter(output);
		
		Map<IntegerWrapper, BufferedReader> map = new HashMap<IntegerWrapper, BufferedReader>();
		List<BufferedReader> readers = new ArrayList<BufferedReader>();
		File chunkDirectory = new File(SORTED_CHUNK_FILES_DIR);
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
			try{
				pw.close();
			}catch(Exception e){
				
			}
		}
	}
	
	

	

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
