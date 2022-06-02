package me.faouzi.largefilesort;

import java.io.File;
import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {
		
		
		
		File input = new File("E:\\automation-heros\\input.txt");
		File output = new File("E:\\automation-heros\\output.txt");
			
			
		
		FileSorterImpl test = new FileSorterImpl();
		test.sortFile(input, 100, output);
	}

}
