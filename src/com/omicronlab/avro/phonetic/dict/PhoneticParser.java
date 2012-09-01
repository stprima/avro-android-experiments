
package com.omicronlab.avro.phonetic.dict;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.omicronlab.avro.phonetic.dict.models.Data;
import com.omicronlab.avro.phonetic.dict.models.Match;
import com.omicronlab.avro.phonetic.dict.models.Pattern;
import com.omicronlab.avro.phonetic.dict.models.Rule;

public class PhoneticParser {

	private static PhoneticParser instance = null;
	private static PhoneticLoader loader = null;
	private static List<Pattern> patterns;
	private static String vowel = "";
	private static String consonant = "";
	private static String ignore = "";
	private boolean initialized = false;

	// Prevent initialization
	private PhoneticParser() {
		patterns = new ArrayList<Pattern>();
	}

	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	public static PhoneticParser getInstance() {
		if (instance == null) {
			instance = new PhoneticParser();
		}
		return instance;
	}

	public void setLoader(PhoneticLoader loader) {
		PhoneticParser.loader = loader;
	}

	public void init() throws Exception {
		if (loader == null) {
			new Exception("No PhoneticLoader loader available");
		}
		Data data = loader.getData();
		patterns = data.getPatterns();
		Collections.sort(patterns);

		vowel = data.getVowel();
		consonant = data.getConsonant();
		ignore = data.getIgnore();
		initialized = true;
	}

	public PhoneticResult parse(String input) {

		if (initialized == false) {
			try {
				this.init();
			} catch (Exception e) {
				System.err.println(e);
				System.err.println("Please handle the exception by calling init");
				System.exit(0);
			}
		}

		String fixed = "";
		for (char c : input.toCharArray()) {
			if (!this.isIgnore(c)) {
				fixed += Character.toLowerCase(c);
			}
		}

		// String output = "";
		PhoneticResult output = new PhoneticResult();
		ArrayList<String> temPatterns = new ArrayList<String>();

		for (int cur = 0; cur < fixed.length(); ++cur) {
			int start = cur, end = cur + 1, prev = start - 1;
			boolean matched = false;
			for (Pattern pattern : patterns) {
				end = cur + pattern.getFind().length();
				if (end <= fixed.length() && fixed.substring(start, end).equals(pattern.getFind())) {
					prev = start - 1;
					for (Rule rule : pattern.getRules()) {
						boolean replace = true;

						int chk = 0;

						for (Match match : rule.getMatches()) {
							if (match.getType().equals("suffix")) {
								chk = end;
							}
							// Prefix
							else {
								chk = prev;
							}

							// Beginning
							if (match.getScope().equals("punctuation")) {
								if (!((chk < 0 && match.getType().equals("prefix")) || (chk >= fixed.length() && match.getType().equals("suffix")) || this.isPunctuation(fixed
										.charAt(chk))) ^ match.isNegative()) {
									replace = false;
									break;
								}
							}
							// Vowel
							else if (match.getScope().equals("vowel")) {
								if (!(((chk >= 0 && match.getType().equals("prefix")) || (chk < fixed.length() && match.getType().equals("suffix"))) && this.isVowel(fixed
										.charAt(chk))) ^ match.isNegative()) {
									replace = false;
									break;
								}
							}
							// Consonant
							else if (match.getScope().equals("consonant")) {
								if (!(((chk >= 0 && match.getType().equals("prefix")) || (chk < fixed.length() && match.getType().equals("suffix"))) && this.isConsonant(fixed
										.charAt(chk))) ^ match.isNegative()) {
									replace = false;
									break;
								}
							}
							// Exact
							else if (match.getScope().equals("exact")) {
								int s, e;
								if (match.getType().equals("suffix")) {
									s = end;
									e = end + match.getValue().length();
								}
								// Prefix
								else {
									s = start - match.getValue().length();
									e = start;
								}
								if (!this.isExact(match.getValue(), fixed, s, e, match.isNegative())) {
									replace = false;
									break;
								}
							}
						}

						if (replace) {
							output.regex += rule.getReplace() + "(্[যবম])?(্?)([ঃঁ]?)";
							temPatterns.add(rule.getReplace());
							cur = end - 1;
							matched = true;
							break;
						}

					}

					if (matched == true)
						break;

					// Default
					output.regex += pattern.getReplace() + "(্[যবম])?(্?)([ঃঁ]?)";
					temPatterns.add(pattern.getReplace());
					cur = end - 1;
					matched = true;
					break;
				}
			}

			if (!matched) {
				output.regex += fixed.charAt(cur);
				temPatterns.add(fixed.charAt(cur) + "");
			}
			// System.out.printf("cur: %s, start: %s, end: %s, prev: %s\n", cur,
			// start, end, prev);
		}

		if (temPatterns.size() > 0) {
			output.regexScope = "^" + temPatterns.get(0) + ".*" + temPatterns.get(temPatterns.size() - 1) + "(্[যবম])?(্?)([ঃঁ]?)" + "$";
		}

		return output;
	}

	private boolean isVowel(char c) {
		return ((vowel.indexOf(Character.toLowerCase(c)) >= 0));
	}

	private boolean isConsonant(char c) {
		return ((consonant.indexOf(Character.toLowerCase(c)) >= 0));
	}

	private boolean isPunctuation(char c) {
		return (!(this.isVowel(c) || this.isConsonant(c)));
	}

	private boolean isExact(String needle, String heystack, int start, int end, boolean not) {
		return ((start >= 0 && end < heystack.length() && heystack.substring(start, end).equals(needle)) ^ not);
	}

	private boolean isIgnore(char c) {
		return (ignore.indexOf(Character.toLowerCase(c)) >= 0);
	}

}