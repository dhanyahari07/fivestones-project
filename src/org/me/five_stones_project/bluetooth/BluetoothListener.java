package org.me.five_stones_project.bluetooth;

import java.io.IOException;

import org.me.five_stones_project.R;
import org.me.five_stones_project.activity.BluetoothGameActivity;
import org.me.five_stones_project.activity.MainActivity;
import org.me.five_stones_project.common.Properties;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Looper;

/**
 *
 * @author Tangl Andras
 */

public class BluetoothListener extends Thread {	
	private static BluetoothListener listener = null;

	public static void startListening() {
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		if (adapter != null && adapter.isEnabled()) {
		    listener = new BluetoothListener(adapter, false);
		    listener.start();
		}
	}
	
	public static void stopListening() {
		if(listener != null)
			listener.cancel();
	}
	
    private String mSocketType;
	private BluetoothSocket socket;
	private final BluetoothServerSocket mmServerSocket;

    public BluetoothListener(BluetoothAdapter adapter, boolean secure) {
        BluetoothServerSocket tmpSocket = null;
        mSocketType = secure ? "Secure" : "Insecure";

        try {
            tmpSocket = adapter.listenUsingRfcommWithServiceRecord(
            	Properties.NAME, Properties.MY_UUID);
        } 
        catch(IOException e) { }
        mmServerSocket = tmpSocket;
    }

    public void run() {
    	Looper.prepare();
    	
        setName("AcceptThread" + mSocketType);

        socket = null;

        while(true) {
            try {
                socket = mmServerSocket.accept();
            } 
            catch (IOException e) {
            	e.printStackTrace();
                break;
            }

            if (socket != null) {
            	new AlertDialog.Builder(MainActivity.getContext())
            		.setMessage(R.string.BTconnReq)
            		.setPositiveButton(R.string.BTconnReqAccept, new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							listener.interrupt();
							
							Properties.socket = socket;
							Properties.isServer = false;
							Intent intent = new Intent(MainActivity.getContext(), BluetoothGameActivity.class);
							MainActivity.getContext().startActivity(intent);
						}
					})
					.setNegativeButton(R.string.BTconnReqDeny, null).show();
            	Looper.loop();
            	Looper.myLooper().quit();
            }
        }
    }

    public void cancel() {
        try {
            mmServerSocket.close();
        } 
        catch (IOException e) { }
    }
}
