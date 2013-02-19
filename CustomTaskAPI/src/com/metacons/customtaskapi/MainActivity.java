package com.metacons.customtaskapi;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity implements
		CustomTaskFinishedListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		CustomTask task = new CustomTask(this, this);
		task.execute("http://erayince.com.tr");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void taskFinished(Message msg) {
		
		if (msg != null) {
			Log.i("what", "" + msg.what);
		}else {
			Log.i("what", "msg null");
		}
	}

}
