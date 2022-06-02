package me.faouzi.largefilesort;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.io.FileWriter;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/*
 * @author Faouzi
 */

public class FileSorterImplTest {

	private final FileSorterImpl fileSorter = new FileSorterImpl();

	private BufferedReader in = null;

	private File testInputFile;


	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Before
	public void setup() throws IOException, URISyntaxException {
		URL resource = getClass().getClassLoader().getResource("test-input.txt");
		testInputFile = new File(resource.toURI());

	}

	@After
	public void teardown() throws IOException {
		if (in != null) {
			in.close();
		}

		in = null;
	}




	
	@Test
	public void testSpliteAndSortChunkSuccess() throws IOException, URISyntaxException {
		String expectResult = "01234";

		fileSorter.spliteAndSortChunk(testInputFile, 100);

		URL sortedCunkResource = getClass().getClassLoader().getResource("test-sorted-chunk.txt");
		
		String content = FileUtils.readFileToString(new File(sortedCunkResource.toURI())).replace(System.getProperty("line.separator"), "");

		assertNotNull(sortedCunkResource);
		assertEquals(content, expectResult);
	}
	
	
	
	@Test
	public void testSpliteAndSortChunkFailed() throws IOException, URISyntaxException {
		String expectResult = "649520";

		fileSorter.spliteAndSortChunk(testInputFile, 100);

		URL sortedCunkResource = getClass().getClassLoader().getResource("test-sorted-chunk.txt");

		String content = FileUtils.readFileToString(new File(sortedCunkResource.toURI())).replace(System.getProperty("line.separator"), "");

		assertNotNull(sortedCunkResource);

		assertEquals(content, expectResult);
	}

	@Test
	public void testCreateFileFromArrayList() throws IOException, URISyntaxException {

		List<Integer> sorted = Arrays.asList(1582, 22, 500);
		Collections.sort(sorted);

		File arrayToFile = temporaryFolder.newFile("array-to-file.txt");		
		fileSorter.createFileFromArrayList(new FileOutputStream(arrayToFile), sorted);
		
		final String sortedStringFromFile = FileUtils.readFileToString(arrayToFile).replace(System.getProperty("line.separator"), "");
		 
	    assertEquals("225001582", sortedStringFromFile);
		
	}


}






