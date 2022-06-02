## Large file sorter (Automation-hero coding challenge) ##

## Overview
This is a JAVA library made for sorting large files containing integres when the files cannot be loaded in the main momory (RAM)

The implemented algorithem proced in 3 steps : 

1 - Read the input file and devide it into chunks which can fit in the main memory

2 - Sort each chunk using Arrays.sort();

3 - Merge sorted chunks together in one single output file using HashMaps and LinkedList


 
## How to use 

You can sort a large file using the main method which is sortFile();

this method is overloaded and can be used with multiple params : 

### public void sortFile(File largeFile) : 
- Takes the input file as a param
- Default max RAM memory in MB is 100MB
- output file will be output.txt and will reside in the inputFile dir


### public void sortFile(File largeFile, long maxRamMemoryInMb) :
- Takes the input file as a param
- Takes max RAM Memory a param
- output file will be output.txt and will reside in the inputFile dir


### public void sortFile(File largeFile, File outputFile) throws IOException;
- Takes the input file as a param
- Takes the output File as param
- Default max RAM memory in MB is 100MB


### public void sortFile(File largeFile ,long maxRamMemoryInMb , File outputFile) throws IOException;
- Takes the input file as a param
- Takes max RAM Memory a param
- Takes the output File as param



**TODO**
- add more unit tests
- Publish the tool as an open source maven dependency
 