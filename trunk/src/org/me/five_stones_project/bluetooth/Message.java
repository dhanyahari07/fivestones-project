package com.tungi.android.malom.bluetooth;

import android.graphics.Point;

/**
 *
 * @author Tangl Andras
 */

public class Message {
	public static final int NECESSARY_BYTES = 2;
	
	private byte x;
	private byte y;

	public Message() { }
	
	//NOTE: both the x and y coordinates are smaller than 20
	//see GameHandler class in ../game package
	public Message(Point point) {
		x = (byte) point.x;
		y = (byte) point.y;
	}
	
	public Point getPoint() {
		return new Point(x, y);
	}
	
	private Message(byte x, byte y) {
		this.x = x;
		this.y = y;
	}
	
	public byte[] getMessage() {
		return new byte[] { x, y };
	}
	
	public static Message parse(byte[] message) {
		Message msg = new Message(message[0], message[1]);
		return msg;
	}
}
