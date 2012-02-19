package org.me.five_stones_project.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.me.five_stones_project.type.Descriptions;


import android.content.Context;

/**
 *
 * @author Tangl Andras
 */

public class HighScore {	
	private int wins = 0;
	private int loses = 0;
	private Date date = null;
	private Descriptions level;
	/**
	 * time in milliseconds
	 */
	private long time = Long.MAX_VALUE;

	public HighScore() { }
	
	public HighScore(Descriptions level) { 
		this.level = level;
	}

	public int getWins() {
		return wins;
	}

	public void increaseWins() {
		this.wins++;
	}

	public int getLoses() {
		return loses;
	}

	public void increaseLoses() {
		this.loses++;
	}

	public void setWins(int wins) {
		this.wins = wins;
	}

	public void setLoses(int loses) {
		this.loses = loses;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Descriptions getLevel() {
		return level;
	}

	public void setLevel(Descriptions level) {
		this.level = level;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
		
	public String getFormattedElapseTime() {
		if(time == Long.MAX_VALUE)
			return "0:0";
		
		int min = (int)time / 60000;
        int sec = (int)(time / 1000 - min * 60);
        return Integer.toString(min) + ":" + Integer.toString(sec);
	}
	
	public String getFormattedDate() {
		if(date == null)
			return "-";
		DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd-HH:mm");
        return dateFormat.format(date);
	}
	
	public String toString(Context ctx) {
		return "<wins>"+ Integer.toString(wins) + "</>" +
				"<loses>"+ Integer.toString(loses) + "</>" +
				"<level>"+ level.getDescription(ctx) + "</>" +
				"<time>"+ Long.toString(time) + "</>" +
				"<date>"+ getFormattedDate() + "</>";
	}
	
	public static HighScore parseHighScore(Context ctx, String s) {
		HighScore highScore = new HighScore();
		
		String[] content = s.split("</>");
		for(String part : content) {
			String[] val = part.split(">");
			if(val[0].equals("<wins"))
				highScore.setWins(Integer.parseInt(val[1]));
			if(val[0].equals("<loses"))
				highScore.setLoses(Integer.parseInt(val[1]));
			if(val[0].equals("<level"))
				highScore.setLevel(Descriptions.findByDescription(ctx, val[1]));
			if(val[0].equals("<time"))
				highScore.setTime(Long.parseLong(val[1]));
			if(val[0].equals("<date")) {
				if(!val[1].equals("-")) {
				DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd-HH:mm");
					try {
						highScore.setDate(dateFormat.parse(val[1]));
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return highScore;
	}
}
