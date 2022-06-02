## Large file sorter (Automation-hero coding challenge) ##

## Overview
This is a JAVA library made for sorting large files containing integres when the files cannot be loaded in the main momory (RAM)

The implemented algorithem proced in 3 steps : 

1 - Read the input file and devide it into chunks which can fit in the main memory
2 - Sort each chunk using Arrays.sort();
3 - Merge sorted chunks together in one single output file using HashMaps and LinkedList

 



**TODO**
- add more unit tests
- Publish the tool as an open source maven dependency
 