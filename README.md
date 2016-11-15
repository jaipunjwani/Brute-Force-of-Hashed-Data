# Description
This project contains code that generates possible nyc yellow taxi driver ID and medallion licenses, hashes them using MD5 algorithm, and creates a lookup table to find each license's corresponding hash. NYC Yellow Taxi Data set for 2013 contained these hashes values, which were publicly [deanonymized](https://tech.vijayp.ca/of-taxis-and-rainbows-f6bc289679a1#.pwo3p72k0). 

# MEMORY & STORAGE
This program is memory-intensive as it generates over 20 million licenses, hashes them, stores the hash-license pair in a hash table for easy lookup, and finally SERIALIZES the files so it can be reloaded from memory after the first use. You may have to increase your IDE's memory buffer to allow this program to run. Additionally, a few large files will be stored on your computer (~1.2 GB in total).

# ETHICAL USAGE
I took on this project because I love cryptography. However, this project also made me aware of the importance of analyzing public data within ethical means. While the internet has tons of information about people and their activities, we should use this information in a way that keeps identities protected and safe. 

Below are two papers that were noteworthy on the ethics of public data:
(1) [“But the data is already public”: on the ethics of research in Facebook](http://link.springer.com/article/10.1007/s10676-010-9227-5)
(2) [Where are Human Subjects in Big Data Research? The Emerging Ethics Divide](http://link.springer.com/article/10.1007/s10676-010-9227-5)



