package com.omicronlab.avro.test;

import com.omicronlab.avro.phonetic.dict.PhoneticJsonLoader;
import com.omicronlab.avro.phonetic.dict.PhoneticParser;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	private TextView tvMem, tvTime;
	private Button butTest, butGc;
	private EditText editText1;

	private String[] tempArr;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tvMem = (TextView) findViewById(R.id.tvMem);
		tvTime = (TextView) findViewById(R.id.tvTime);
		butTest = (Button) findViewById(R.id.button1);
		butGc = (Button) findViewById(R.id.button2);
		editText1 = (EditText) findViewById(R.id.editText1);

		calculateMemory();

		butTest.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Log.i("Test", PhoneticParser.getInstance().parse("test"));
			}
		});

		butGc.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Runtime.getRuntime().gc();
				calculateMemory();
			}
		});
		
		PhoneticParser.getInstance().setLoader(new PhoneticJsonLoader());
		try {
			PhoneticParser.getInstance().init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void calculateMemory() {
		tvMem.setText(("Mem used: " + ((Runtime.getRuntime().totalMemory() / (1024 * 1024)) - (Runtime.getRuntime().freeMemory() / (1024 * 1024))) + "/" + (Runtime.getRuntime().totalMemory() / (1024 * 1024)) + " MB"));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
