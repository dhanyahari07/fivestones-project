package org.me.five_stones_project.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.me.five_stones_project.R;
import org.me.five_stones_project.activity.GameActivity;

import android.bluetooth.BluetoothSocket;
import android.os.Looper;
import android.widget.Toast;

/**
 *
 * @author Tangl Andras
 */

public class ConnectedThread extends Thread {
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private final BluetoothSocket mmSocket;
    
    private BluetoothEnemy callback;
 
    public ConnectedThread(BluetoothSocket socket, BluetoothEnemy callback) {
        mmSocket = socket;
    	this.callback = callback;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
 
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } 
        catch (IOException e) { }
 
        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }
 
    public void run() {
    	Looper.prepare();
    	
        byte[] buffer = new byte[Message.NECESSARY_BYTES];  
        int bytes; 
 
        while (true) {
            try {
                bytes = mmInStream.read(buffer);
                if(bytes != -1)
                	callback.processMessage(Message.parse(buffer));
            } catch (IOException e) {
            	Toast.makeText(GameActivity.getInstance(), R.string.connectionLost, 1000).show();
            	
            	GameActivity.getInstance().finish();
            	Looper.loop();
            	Looper.myLooper().quit();
                break;
            }
        }
    }
 
    public void write(Message msg) {
        try {
            mmOutStream.write(msg.getMessage());
        } catch (IOException e) { }
    }
 
    public void cancel() {
        try {
        	mmInStream.close();
        	mmOutStream.close();
            mmSocket.close();
        } catch (IOException e) { }
    }
}