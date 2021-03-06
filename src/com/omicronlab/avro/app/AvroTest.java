package com.omicronlab.avro.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

public class AvroTest extends Application  {

	private static Context context;
	private static AvroTest singleton;
	
	@Override
	public void onCreate() {
		super.onCreate();

		AvroTest.singleton = this;
		AvroTest.context = getApplicationContext();
		initializeApplication();
	}
	
	public static AvroTest getInstance() {
		return AvroTest.singleton;
	}
	
	private void initializeApplication() {
		File file = getFileStreamPath("test.dbs");
		if (!file.exists()) {
			copyAssets();
		}
	}

	public static Context getAppContext() {
		return AvroTest.context;
	}
	
	private void copyAssets() {
		AssetManager assetManager = getAssets();

		InputStream in = null;
		OutputStream out = null;
		try {
			in = assetManager.open("test.dbs");
			openFileOutput("test.dbs", 0).close();
			out = new FileOutputStream(getFileStreamPath("test.dbs").getAbsolutePath());
			copyFile(in, out);
			in.close();
			in = null;
			out.flush();
			out.close();
			out = null;
		} catch (Exception e) {
			Log.e("tag", e.getMessage());
		}
	}

	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}
}
