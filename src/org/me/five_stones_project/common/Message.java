package org.me.five_stones_project.common;

import android.graphics.Point;

/**
 *
 * @author Tangl Andras
 */

public class Message {
	public static final int NECESSARY_BYTES = 3;
	
	private byte x;
	private byte y;
	private byte grow;

	public Message() { }
	
	//NOTE: both the x and y coordinates are smaller than 20
	//see GameHandler class in ../game package
	public Message(Point point, Point grow) {
		x = (byte) point.x;
		y = (byte) point.y;
		this.grow = (byte) (10 * grow.x + grow.y);
	}
	
	public Point getPoint() {
		return new Point(x, y);
	}
	
	public Point getGrow() {
		int t = grow / 10;
		return new Point(t, grow  - t * 10);
	}
	
	private Message(byte x, byte y, byte grow) {
		this.x = x;
		this.y = y;
		this.grow = grow;
	}
	
	public byte[] getMessage() {
		return new byte[] { x, y, grow };
	}
	
	public static Message parse(byte[] message) {
		byte g = message.length == 3 ? message[2] : 0;
		Message msg = new Message(message[0], message[1], g);
		return msg;
	}
}
