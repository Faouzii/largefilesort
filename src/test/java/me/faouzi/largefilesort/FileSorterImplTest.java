package me.faouzi.largefilesort;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/*
 * @author Faouzi
 */

public class FileSorterImplTest {
	
	private final FileSorterImpl fileSorter = new FileSorterImpl();
	
	private BufferedReader in = null;
	
	@Before
    public void setup() throws IOException{
        in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/input.txt")));
    }

    @After
    public void teardown()throws IOException {
        if (in != null){
            in.close();
        }

        in = null;
    }
	
    @Test
    public void fileExists()
        throws IOException
    {
        String line = in.readLine();

        assertThat(line, notNullValue());
    }
	
}
