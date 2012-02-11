package org.me.five_stones_project.internet;

import org.me.five_stones_project.common.MapFactory;

/**
 *
 * @author Tangl Andras
 */

public class PendingThread extends Thread {
	private String id;
	private String url;
	private int timeout;
	private boolean running = true;
	private PendingListener listener;
	
	public PendingThread(String url, String id, 
			int timeout, PendingListener listener) {
		this.id = id;
		this.url = url;
		this.timeout = timeout;
		this.listener = listener;
	}
	
	public void terminate() {
		running = false;
	}

	@Override
	public void run() {
		int attempts = 0;
		String result = "";
		boolean ok = false;
		while(!result.equals("timeout")) {
			try {				
				if(!running) 
					return;
				
				if(!result.equals("")) {
					ok = true;
					break;
				}
				
				result = WebService.executeRequest(url, 
					MapFactory.createMap(new String[] { "id", "attempt"}, 
					new String[] { id, Integer.toString(attempts++) }));
								
				Thread.sleep(timeout);
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
		
		if(ok)
			listener.onSuccess(result);
		else
			listener.onFailed();
	}
}
