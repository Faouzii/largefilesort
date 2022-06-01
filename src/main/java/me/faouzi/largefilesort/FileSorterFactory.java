package me.faouzi.largefilesort;

/**
 * Factory for creating instances of {@link FileSorter}.
 * @author Faouzi
 */
public final class FileSorterFactory {
	
	private FileSorterFactory() {	
	}
	

    public static FileSorter createFileSorter() {
        return new FileSorterImpl();
    }

}
