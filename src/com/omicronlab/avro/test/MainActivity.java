package com.omicronlab.avro.test;

import java.util.ArrayList;

import com.omicronlab.avro.phonetic.search.DBSearch;

import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;

import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	private TextView tvMem, tvTime, tvResult;
	private EditText editText1;

	private DBSearch dbsearch;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tvMem = (TextView) findViewById(R.id.tvMem);
		tvTime = (TextView) findViewById(R.id.tvTime);
		tvResult = (TextView) findViewById(R.id.textResult);
		editText1 = (EditText) findViewById(R.id.editText1);

		dbsearch = DBSearch.getInstance();
		
		editText1.addTextChangedListener(new TextWatcher() {
			  
			    public void afterTextChanged(Editable s) {
			        //((TextView)findViewById(R.id.numcaratteri)).setText(String.format(getString(R.string.caratteri), s.length()));
			    	search();
			    }
			 
			    public void beforeTextChanged(CharSequence s, int start, int count,
			            int after) {
			        // TODO Auto-generated method stub
			    }
			 
			    public void onTextChanged(CharSequence s, int start, int before,
			            int count) {
			        // TODO Auto-generated method stub
			    }
			 
			});

		calculateMemory();
	}

	private void calculateMemory() {
		tvMem.setText(("Mem used: " + ((Runtime.getRuntime().totalMemory() / (1024 * 1024)) - (Runtime.getRuntime().freeMemory() / (1024 * 1024))) + "/"
				+ (Runtime.getRuntime().totalMemory() / (1024 * 1024)) + " MB"));
	}
	
	private void search(){
		String en = editText1.getText().toString();
		
		if (en.length()<=0){
			return;
		}
		
		long startTime = System.currentTimeMillis();
		ArrayList<String> bnl = dbsearch.search(en);
		long endTime = System.currentTimeMillis();
		String bn = "";
		for (String w : bnl) {
			bn += w + "\n";
		}
		tvResult.setText(bn);
		tvTime.setText("Taken: " + (endTime - startTime) + "ms");
		calculateMemory();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
