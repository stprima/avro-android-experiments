# Avro Android Experiments


This is not a production repository. I'll use it for testing, benchmarking and playing with Avro Phonetic on Android Platform. 

Expect horrible sphaggeti codes with zero comments, I didn't write them for your pleasure. Period.

Forking will do no good unless you want to contribute.

#### myNotes:

1. [Perst](http://www.mcobject.com/perst) performs way better than sqlite! As I/O is significantly better now, I can skip in memory database. Small heap size restriction on Android (prior Honeycomb) is no more a concern.
2. Average word lookup still takes 200+ms with `java.util.regex` on Galaxy Y.  Need to check with different regex persers based on this [benchmark](http://www.tusker.org/regex/regex_benchmark.html).
