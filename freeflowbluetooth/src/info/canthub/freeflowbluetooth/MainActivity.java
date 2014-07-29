package info.canthub.freeflowbluetooth;

import info.canthub.freeflowbluetooth.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;


import info.canthub.freeflowbluetooth.displayDataActivity;
import info.canthub.freeflowbluetooth.database.MySQLiteHelper;
import info.canthub.freeflowbluetooth.database.MySQLiteHelper.DatabaseHelper;
import info.canthub.freeflowbluetooth.model.Measurement;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends Activity {

	private Button On,Connect,Visible,list;
	private TextView SerialConsole;
	private BluetoothAdapter BA;
	private Set<BluetoothDevice>pairedDevices;
	private ListView lv;
	private BluetoothServerSocket bsvrsock;
	private BluetoothSocket bsock;
	private UUID uuid;

	private FreeflowGraph ffgraph;
	private String incomingdata;
	private String newdataline;

	MySQLiteHelper db;
	DatabaseHelper mDbHelper;
	
	private MenuItem clearDatabase;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		On = (Button)findViewById(R.id.onButton);
		Connect = (Button)findViewById(R.id.makeVisibleButton);
		Visible = (Button)findViewById(R.id.listDevicesButton);
		list = (Button)findViewById(R.id.connectButton);
		lv = (ListView)findViewById(R.id.pairedDevicesList);
		SerialConsole = (TextView)findViewById(R.id.serialStatus);
		BA = BluetoothAdapter.getDefaultAdapter();
		uuid = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee");
		ffgraph = (FreeflowGraph)findViewById(R.id.outputGraph);
		if (BA == null) finish();

		incomingdata = "";

		db = new MySQLiteHelper(this);
		db.addMeasurement((float) 10.0); //tests adding stuff to database :P

		displayButton();

	}



	@Override
	protected void onPause(){
		super.onPause();
		db.close();
	}



	@Override
	protected void onDestroy(){
		super.onDestroy();
		db.close();
	}



	public void on(View view){
		if (!BA.isEnabled()) {
			Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(turnOn, 0);
			Toast.makeText(getApplicationContext(),"Turned on" 
					,Toast.LENGTH_LONG).show();
		}
		else{
			Toast.makeText(getApplicationContext(),"Already on",
					Toast.LENGTH_LONG).show();
		}
	}



	public void list(View view){
		pairedDevices = BA.getBondedDevices();

		ArrayList list = new ArrayList();
		for(BluetoothDevice bt : pairedDevices)
			list.add(bt.getName());

		Toast.makeText(getApplicationContext(),"Showing Paired Devices",
				Toast.LENGTH_SHORT).show();
		final ArrayAdapter adapter = new ArrayAdapter
				(this,android.R.layout.simple_list_item_1, list);
		lv.setAdapter(adapter);

	}



	public void connect(View view){
		try {
			bsvrsock = BA.listenUsingRfcommWithServiceRecord("Freeflow Bluetooth", uuid);
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(),"Unable to create socket!" ,
					Toast.LENGTH_LONG).show();
			return;
		}
		try {
			bsock = bsvrsock.accept();
			bsvrsock.close();
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(),"Socket accept failed!" ,
					Toast.LENGTH_LONG).show();
			return;
		}

		Toast.makeText(getApplicationContext(),"Connected!" ,
				Toast.LENGTH_LONG).show();

		SerialConsole.setText("Awaiting communication...");
		(new Timer()).schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					InputStream inp = bsock.getInputStream();
					while (inp.available() > 0) {
						char c = (char)inp.read();
						if (c == ("\n").charAt(0)) {
							newdataline = incomingdata;
							incomingdata = "";
							MainActivity.this.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									ArrayList<Float> dataFloats = ffgraph.giveData(newdataline);
									db.addMeasurement(dataFloats.get(0));
									SerialConsole.setText("Received:\n"+newdataline);
								}
							});
						}
						incomingdata = incomingdata + String.valueOf(c);
					}
				} catch (IOException e) {
					MainActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							SerialConsole.setText("Could not read from socket.");
						}
					});
					this.cancel();
				}
			}
		}, 500, 40);
	} 
	
	

	public void visible(View view){
		Intent getVisible = new Intent(BluetoothAdapter.
				ACTION_REQUEST_DISCOVERABLE);
		startActivityForResult(getVisible, 0);
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		clearDatabase = menu.add("Clear database");
		return true;
	}
	
	
	
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i("BLAH", "called onOptionsItemSelected; selected item: " + item);
        if (item == clearDatabase)
        	db.destroyDatabase();
        return true;
    }



	//Button that starts a new activity that displays data
	private void displayButton(){
		final Button disp = (Button) findViewById(R.id.disp);
		disp.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				displayData();	
			}
		});
	}

	
	
	//starts a new activity that displays data
	public void displayData(){
		Intent intent = new Intent(this, displayDataActivity.class);
		startActivity(intent);
	}

}