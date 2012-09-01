package com.omicronlab.avro.phonetic.search;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.garret.perst.Key;
import org.garret.perst.Storage;
import org.garret.perst.StorageFactory;

import com.omicronlab.avro.app.AvroTest;
import com.omicronlab.avro.phonetic.dict.PhoneticJsonLoader;
import com.omicronlab.avro.phonetic.dict.PhoneticParser;
import com.omicronlab.avro.phonetic.dict.PhoneticResult;
import com.omicronlab.avro.phonetic.perst.models.AvroRootClass;
import com.omicronlab.avro.phonetic.perst.models.PatternPersistentClass;
import com.omicronlab.avro.phonetic.perst.models.WordPersistentClass;

public class DBSearch {

	private static DBSearch instance = null;
	private PhoneticParser regexParser = null;
	private Storage db = null;
	private AvroRootClass root = null;

	public ArrayList<String> search(String engT) {
		PhoneticResult pr = regexParser.parse(engT);
		ArrayList<String> words = null;

		PatternPersistentClass objp = root.strKeyIndex.get(new Key(pr.regexScope));
		if (objp != null) {
			words = populateWords(objp.words);
		}

		ArrayList<String> result = searchRegexInWords(words, pr.regex);
		return result;
	}

	private ArrayList<String> searchRegexInWords(ArrayList<String> words, String regex) {
		ArrayList<String> retWords = new ArrayList<String>();

		if (words != null) {
			Pattern pattern = Pattern.compile(regex);

			for (String w : words) {
				if (pattern.matcher(w).matches()) {
					retWords.add(w);
				}
			}
		}
		return retWords;
	}

	private ArrayList<String> populateWords(Integer[] ids) {
		ArrayList<String> words = new ArrayList<String>();

		if (ids.length > 0) {
			for (int i = 0; i < ids.length; i++) {
				WordPersistentClass objw = root.intKeyIndex.get(new Key(ids[i].intValue()));
				words.add(objw.word);
			}
		}

		return words;
	}

	private DBSearch() {
		regexParser = PhoneticParser.getInstance();
		regexParser.setLoader(new PhoneticJsonLoader());
		try {
			regexParser.init();
		} catch (Exception e) {
			e.printStackTrace();
		}

		initDb();
	}

	private void initDb() {
		int pagePoolSize = 2 * 1024 * 1024;

		db = StorageFactory.getInstance().createStorage();
		String dbpath = AvroTest.getAppContext().getFileStreamPath("test.dbs").getAbsolutePath();
		db.open(dbpath, pagePoolSize);

		root = (AvroRootClass) db.getRoot(); // get storage root
	}

	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	public static DBSearch getInstance() {
		if (instance == null) {
			instance = new DBSearch();
		}
		return instance;
	}
}
