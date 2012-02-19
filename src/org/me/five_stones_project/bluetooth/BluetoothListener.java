package org.me.five_stones_project.bluetooth;

import java.io.IOException;

import org.me.five_stones_project.activity.BluetoothGameActivity;
import org.me.five_stones_project.common.Properties;


import org.me.five_stones_project.R;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
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

	public static void startListening(Context ctx) {
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		if (adapter != null && adapter.isEnabled()) {
		    listener = new BluetoothListener(ctx, adapter, false);
		    listener.start();
		}
	}
	
	public static void stopListening() {
		if(listener != null)
			listener.cancel();
	}
	
	private Context ctx;
    private String mSocketType;
	private BluetoothSocket socket;
	private final BluetoothServerSocket mmServerSocket;

    private BluetoothListener(Context ctx, BluetoothAdapter adapter, boolean secure) {
    	this.ctx = ctx;
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
            	new AlertDialog.Builder(ctx)
            		.setMessage(R.string.BTconnReq)
            		.setPositiveButton(R.string.BTconnReqAccept, new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							listener.interrupt();
							
							try {
								socket.getOutputStream().write(new byte[] { 0 });
								socket.getOutputStream().flush();
								
								Properties.socket = socket;
								Properties.isServer = false;
								Intent intent = new Intent(ctx, BluetoothGameActivity.class);
								ctx.startActivity(intent);
							} catch (IOException e) {
								e.printStackTrace();
							}
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
