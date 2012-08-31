/*
	=============================================================================
	*****************************************************************************
	The contents of this file are subject to the Mozilla Public License
	Version 1.1 (the "License"); you may not use this file except in
	compliance with the License. You may obtain a copy of the License at
	http://www.mozilla.org/MPL/

	Software distributed under the License is distributed on an "AS IS"
	basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
	License for the specific language governing rights and limitations
	under the License.

	The Original Code is JAvroPhonetic

	The Initial Developer of the Original Code is
	Rifat Nabi <to.rifat@gmail.com>

	Copyright (C) OmicronLab (http://www.omicronlab.com). All Rights Reserved.


	Contributor(s): ______________________________________.

	*****************************************************************************
	=============================================================================
*/

package com.omicronlab.avro.phonetic.dict;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.omicronlab.avro.phonetic.dict.models.Data;
import com.omicronlab.avro.phonetic.dict.models.Match;
import com.omicronlab.avro.phonetic.dict.models.Pattern;
import com.omicronlab.avro.phonetic.dict.models.Rule;

public class PhoneticParser {
	
	private static volatile PhoneticParser instance = null;
	private static PhoneticLoader loader = null;
	private static List<Pattern> patterns;
	private static String vowel = "";
	private static String consonant = "";
	private static String casesensitive = "";
	private boolean initialized = false;
	
	// Prevent initialization
	private PhoneticParser() {
		patterns = new ArrayList<Pattern>();
	}

	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	
	public static PhoneticParser getInstance() {
		if(instance == null) {
			synchronized (PhoneticParser.class) {
				if(instance == null) {
					instance = new PhoneticParser();
				}
			}
		}
		return instance;
	}
	
	public synchronized void setLoader(PhoneticLoader loader) {
		PhoneticParser.loader = loader;
	}
	
	public synchronized void init() throws Exception {
		if(loader == null) {
			new Exception("No PhoneticLoader loader available");
		}
		Data data = loader.getData();
		patterns = data.getPatterns();
		Collections.sort(patterns);
		
		vowel = data.getVowel();
		consonant = data.getConsonant();
		casesensitive = data.getCasesensitive();
		initialized = true;
	}
	
	public String parse(String input) {
		
		if(initialized == false) {
			try {
				this.init();
			} catch(Exception e) {
				System.err.println(e);
				System.err.println("Please handle the exception by calling init");
				System.exit(0);
			}
		}
		
		String fixed = "";
		for(char c: input.toCharArray()) {
			if(this.isCaseSensitive(c)) {
				fixed += c;
			}
			else {
				fixed += Character.toLowerCase(c);
			}
		}
		
		String output = "";
		for(int cur = 0; cur < fixed.length(); ++cur) {
			int start = cur, end = cur + 1, prev = start - 1;
			boolean matched = false;
			for(Pattern pattern: patterns) {
				end = cur + pattern.getFind().length();
				if(end <= fixed.length() && fixed.substring(start, end).equals(pattern.getFind())) {
					prev = start - 1;
					for(Rule rule: pattern.getRules()) {
						boolean replace = true;
						
						int chk = 0;
						
						for(Match match: rule.getMatches()) {
							if(match.getType().equals("suffix")) {
								chk = end;
							} 
							// Prefix
							else {
								chk = prev;
							}
							
							// Beginning
							if(match.getScope().equals("punctuation")) {
								if(
									! (
										(chk < 0 && match.getType().equals("prefix")) || 
										(chk >= fixed.length() && match.getType().equals("suffix")) || 
										this.isPunctuation(fixed.charAt(chk))
									) ^ match.isNegative()
								) {
									replace = false;
									break;
								}
							}
							// Vowel
							else if(match.getScope().equals("vowel")) {
								if(
									! (
										(
											(chk >= 0 && match.getType().equals("prefix")) || 
											(chk < fixed.length() && match.getType().equals("suffix"))
										) && 
										this.isVowel(fixed.charAt(chk))
									) ^ match.isNegative()
								) {
									replace = false;
									break;
								}
							}
							// Consonant
							else if(match.getScope().equals("consonant")) {
								if(
									! (
										(
											(chk >= 0 && match.getType().equals("prefix")) || 
											(chk < fixed.length() && match.getType().equals("suffix"))
										) && 
										this.isConsonant(fixed.charAt(chk))
									) ^ match.isNegative()
								) {
									replace = false;
									break;
								}
							}
							// Exact
							else if(match.getScope().equals("exact")) {
								int s, e;
								if(match.getType().equals("suffix")) {
									s = end;
									e = end + match.getValue().length();
								} 
								// Prefix
								else {
									s = start - match.getValue().length();
									e = start;
								}
								if(!this.isExact(match.getValue(), fixed, s, e, match.isNegative())) {
									replace = false;
									break;
								}
							}
						}
						
						if(replace) {
							output += rule.getReplace();
							cur = end - 1;
							matched = true;
							break;
						}
						
					}

					if(matched == true) break;
					
					// Default
					output += pattern.getReplace();
					cur = end - 1;
					matched = true;
					break;
				}
			}
			
			if(!matched) {
				output += fixed.charAt(cur);
			}
			// System.out.printf("cur: %s, start: %s, end: %s, prev: %s\n", cur, start, end, prev);
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
	
	private boolean isCaseSensitive(char c) {
		return (casesensitive.indexOf(Character.toLowerCase(c)) >= 0);
	}
	
}