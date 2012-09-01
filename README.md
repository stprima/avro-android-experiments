# Avro Android Experiments


This is not a production repository. I'll use it for testing, benchmarking and playing with Avro Phonetic on Android Platform. 

Expect horrible sphaggeti codes with zero comments, I didn't write them for your pleasure. Period.

Forking will do no good unless you want to contribute.

#### myNotes:

1. [Perst](http://www.mcobject.com/perst) performs way better than sqlite! As I/O is significantly better now, I can skip in memory database. Small heap size restriction on Android (prior Honeycomb) is no more a concern.
2. Average word lookup still takes 200+ms with `java.util.regex` on Galaxy Y.  Need to check with different regex persers based on this [benchmark](http://www.tusker.org/regex/regex_benchmark.html).
	1. Rex (the fastest library mentioned in the benchmark) is discontinued,sites shows 404. I downloaded that using coder search engines, but it doesn't work, fails to match any pattern.
	2. ICU4J is blazing fast, but it's UnicodeSet is not exactly a regex parser. If I choose to continue with that, all existing patterns may need to be changed. :-S (But it will be a huge performance gain.)

3. Need to check with a different approach with Perst OODBMS. Instead of linked word lookup, I'll place words right inside the pattern array. Should be some hundreds times faster, but db size may be huge! (God save us)
4. Important: Pattern for 'o' needs to be adjusted for ending items.