package org.me.five_stones_project.type;

import org.me.five_stones_project.R;

import android.content.Context;

/**
 *
 * @author Tangl Andras
 */

public enum Descriptions {
	None(R.string._none, null),
	Style(R.string._style, null),
	Level(R.string._level, null),

	Classic(R.string.classic, Style),
	Gomoku(R.string.gomoku, Style),
	Modern(R.string.modern, Style),

	Beginner(R.string.beginner, Level),
	Average(R.string.average, Level),
	Normal(R.string.normal, Level), 
	Hard(R.string.hard, Level),
	VeryHard(R.string.veryHard, Level),

	Quality(R.string._quality, null),
	
	Low(R.string.lowq, Quality),
	High(R.string.highq, Quality);

	private final int description;
	private final Descriptions parent;

	Descriptions(int description, Descriptions parent) {
		this.parent = parent;
		this.description = description;
	}

	public String getDescription(Context ctx) {
		return resolveStringResource(ctx, description);
	}
	
	public Descriptions getParent() {
		return parent;
	}
	
	public static Descriptions findByDescription(Context ctx, String value) {
		for(Descriptions description : values()) 
			if(description.getDescription(ctx).equals(value))
				return description;
		
		return Descriptions.None;
	}

	public static String[] getDescriptions(Context ctx, Descriptions parent) {
		int i = 0;
		String[] descriptions = new String[values().length];
		for (Descriptions description : values())
			if(parent.equals(description.getParent()))
				descriptions[i++] = description.getDescription(ctx);

		String[] real = new String[i];
		System.arraycopy(descriptions, 0, real, 0, i);
		
		return real;
	}

	private static String resolveStringResource(Context ctx, int resId) {
		return ctx.getResources().getString(resId);
	}
	
	@Override
	public String toString() {
		return Integer.toString(description);
	}
}
