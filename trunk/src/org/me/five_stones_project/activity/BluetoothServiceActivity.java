package org.me.five_stones_project.activity;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.me.five_stones_project.common.Properties;


import org.me.five_stones_project.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 *
 * @author Tangl Andras
 */

public class BluetoothServiceActivity extends Activity implements OnItemClickListener {
	private static final int RETURN = 0;
	private static final int REQUEST_ENABLE_BT = 1;

	private ConnectThread thread;
	private ProgressDialog dialog;
	private BroadcastReceiver mReceiver;
	private Set<BluetoothDevice> devices;
	private HashMap<String, String> deviceMap; 
	private ArrayAdapter<String> arrayAdapter;
	private BluetoothAdapter mBluetoothAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.devicelist);
		
		arrayAdapter = new ArrayAdapter<String>(this, R.layout.devicename);
		arrayAdapter.setNotifyOnChange(true);

        ListView view = (ListView) findViewById(R.id.deviceListView);
        view.setAdapter(arrayAdapter);
        view.setOnItemClickListener(this);

    	devices = new HashSet<BluetoothDevice>();
    	deviceMap = new HashMap<String, String>();
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
		    new AlertDialog.Builder(this)
		    .setMessage(R.string.noBTdevice)
		    .setNeutralButton(R.string.hback, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					finish();
				}
			}).show();
		}
		else if(!mBluetoothAdapter.isEnabled()) {
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		else
			findDevices();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		mReceiver = new BroadcastReceiver() {
		    public void onReceive(Context context, Intent intent) {
		        String action = intent.getAction();
		        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
		            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		            devices.add(device);
		        }
		        else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
		        	dialog.dismiss();

		        	for(BluetoothDevice device : devices) {
		        		arrayAdapter.add(device.getName());
		        		deviceMap.put(device.getName(), device.getAddress());
		        	}
		        }
		    }
		};

		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(mReceiver, filter); 
		
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		try {
			unregisterReceiver(mReceiver);
		} catch(IllegalArgumentException e) { } 
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.refreshmenu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.refresh:
	    	findDevices();
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_ENABLE_BT) {
			//the user does not turn on the bluetooth radio so the activity should finishes
			if(resultCode == 0)
				finish();
			else
				findDevices();
		}
		else if(requestCode == RETURN) {
			finish();
		}
	}
	
	public void startGameActivity() {
		Intent intent = new Intent(this, BluetoothGameActivity.class);
		startActivityForResult(intent, RETURN);
	}
	
	private void findDevices() {
		devices.clear();
		deviceMap.clear();
		arrayAdapter.clear();
		
		dialog = ProgressDialog.show(this, "", getResources().getString(R.string.loading), true);
		
		devices.addAll(mBluetoothAdapter.getBondedDevices());		

		mBluetoothAdapter.startDiscovery();
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
		String name = ((TextView) view).getText().toString();
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceMap.get(name));
		thread = new ConnectThread(device);
		thread.start();
	}
	
	private class ConnectThread extends Thread {
	    private final BluetoothSocket mmSocket;
	 
	    public ConnectThread(BluetoothDevice device) {
	        BluetoothSocket tempSocket = null;
	 
	        try {	        	
	        	mBluetoothAdapter.cancelDiscovery();
	        	
	        	//Method m = device.getClass().getMethod("createRfcommSocket", new Class[] { int.class });
	        	//tempSocket = (BluetoothSocket) m.invoke(device, 1);

	        	tempSocket = device.createRfcommSocketToServiceRecord(Properties.MY_UUID);
	        } 
	        catch (Exception e) {
	        	tempSocket = null;
				e.printStackTrace();
	            Toast.makeText(getBaseContext(), R.string.BTcannotConnect, 1000).show();
			}
	        mmSocket = tempSocket;
	    }
	 
	    @Override
	    public void run() {
	    	if(mmSocket == null)
	    		return;
	    	
	        try {
	            mmSocket.connect();
	            mmSocket.getInputStream().read();
	        } catch (IOException connectException) {
	            try {
	                connectException.printStackTrace();
	                mmSocket.close();
	            } catch (IOException closeException) { 
	            	closeException.printStackTrace();
	            }
	            runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
			            Toast.makeText(getBaseContext(), R.string.BTcannotConnect, 1000).show();
					}
				});
	            return;
	        }
	 
			Properties.isServer = true;
	        Properties.socket = mmSocket;
	        
	        startGameActivity();
	    }
	 
	    public void cancel() {
	        try {
	            mmSocket.close();
	        } catch (IOException e) { }
	    }
	}
}
