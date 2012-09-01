
package com.omicronlab.avro.phonetic.dict;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.omicronlab.avro.phonetic.dict.models.Data;

public class PhoneticJsonLoader implements PhoneticLoader {
	
	private InputStream is = null;
	
	public PhoneticJsonLoader() {
		try {
			this.is = Data.class.getResource("phonetic.json").openStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public PhoneticJsonLoader(InputStream is) {
		this.is = is;
	}
	
	public PhoneticJsonLoader(String path) throws IOException {
		this.is = new URL(path).openStream();
	}
	
    public Data getData() throws JsonSyntaxException, JsonIOException, FileNotFoundException {
    	Gson gson = new Gson();
    	Data data = (Data) gson.fromJson(new InputStreamReader(this.is), Data.class);
		return data;
    }
}
