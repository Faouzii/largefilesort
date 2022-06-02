package me.faouzi.largefilesort;

import java.io.File;
import java.io.IOException;

/**
 * @author Faouzi
 */
public interface FileSorter {
	
	public void sortFile(File largeFile) throws IOException;
	public void sortFile(File largeFile, long maxRamMemoryInMb) throws IOException;
	public void sortFile(File largeFile, File outputFile) throws IOException;
	public void sortFile(File largeFile ,long maxRamMemoryInMb , File outputFile) throws IOException;


}
