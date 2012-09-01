# Avro Android Experiments


This is not a production repository. I'll use it for testing, benchmarking and playing with Avro Phonetic on Android Platform. 

Expect horrible sphaggeti codes with zero comments, I didn't write them for your viewing pleasure. Period.

Forking will do no good unless you want to contribute.

#### myNotes:

1. [Perst](http://www.mcobject.com/perst) performs way better than sqlite! As Database I/O is significantly better now, I can skip in memory database. Small heap size restriction on Android (prior Honeycomb) is no more a concern.
2. Average word lookup still takes ~~300+~~ 150+ ms with `java.util.regex` on Galaxy Y.  Need to check with different regex persers based on this [benchmark](http://www.tusker.org/regex/regex_benchmark.html).
	1. `Rex` (the fastest library mentioned in the benchmark) is discontinued,sites shows 404. I downloaded the library using code search engines, but it doesn't work, fails to match any pattern.
	2. `ICU4J` is blazing fast on average lookup (*30ms vs. 150ms*), but it's `UnicodeSet` class is not exactly a regex parser. If I choose to continue with that, all existing patterns may need to be changed. :-S
	3. `monq.jfa.Regexp`, `dk.brics.automaton.RegExp`, `jregex.Pattern` are either slower or shows near equal performance compared to built-in regex under Android. `com.karneim.util.collection.regex.Pattern` site shows 404. Discarded them all.

3. Need to check with a different approach with Perst OODBMS. Instead of linked word lookup, I'll place words right inside the pattern array. Should be some hundreds times faster, ~~but db size may be huge~~! **Update:** Test done. DB size increased in tolerable limit, but database I/O performance gain was huge (*20ms vs. 200 ms*). Planned to use this.
4. Important: Pattern for 'o' needs to be adjusted when it is the last item. (Hint: remove ([ওোঅ]|(অ্য)|(য়ো?))***?***)