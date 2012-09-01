package com.omicronlab.avro.phonetic.search;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.garret.perst.Key;
import org.garret.perst.Storage;
import org.garret.perst.StorageFactory;

import android.util.Log;

import com.omicronlab.avro.app.AvroTest;
import com.omicronlab.avro.phonetic.dict.PhoneticJsonLoader;
import com.omicronlab.avro.phonetic.dict.PhoneticParser;
import com.omicronlab.avro.phonetic.dict.PhoneticResult;
import com.omicronlab.avro.phonetic.perst.models.AvroRootClass;
import com.omicronlab.avro.phonetic.perst.models.PatternPersistentClass;

public class DBSearch {

	private static DBSearch instance = null;
	private PhoneticParser regexParser = null;
	private Storage db = null;
	private AvroRootClass root = null;

	public ArrayList<String> search(String engT) {
		long startTime, endTime;

		startTime = System.currentTimeMillis();
		PhoneticResult pr = regexParser.parse(engT);
		endTime = System.currentTimeMillis();
		Log.i("Parser", (endTime - startTime) + "ms");

		String[] words = null;
		startTime = System.currentTimeMillis();
		PatternPersistentClass objp = root.strKeyIndex.get(new Key(pr.regexScope));
		if (objp != null) {
			words = objp.words;
		}
		endTime = System.currentTimeMillis();
		Log.i("Perst", (endTime - startTime) + "ms");

		startTime = System.currentTimeMillis();
		ArrayList<String> result = searchRegexInWords(words, pr.regex);
		endTime = System.currentTimeMillis();
		Log.i("Regex", (endTime - startTime) + "ms");
		return result;
	}

	private ArrayList<String> searchRegexInWords(String[] words, String regex) {
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
		int pagePoolSize = 20 * 1024 * 1024;

		db = StorageFactory.getInstance().createStorage();
		String dbpath = AvroTest.getAppContext().getFileStreamPath(AvroTest.DB_NAME).getAbsolutePath();
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
