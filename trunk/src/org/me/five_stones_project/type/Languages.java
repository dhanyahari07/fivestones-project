package org.me.five_stones_project.type;

/**
 *
 * @author Tangl Andras
 */

public enum Languages {
	Undefined("", ""),
	English("English", "en"),
	Hungarian("Magyar", "hu");
	
	private final String locale;
	private final String description;
	
	Languages(String description, String locale) {
		this.locale = locale;
		this.description = description;
	}

	public String getLocale() {
		return locale;
	}

	public String getDescription() {
		return description;
	}
	
	public static String[] getDescriptions() {
		String[] descriptions = new String[values().length - 1];
		for(int i = 1; i < values().length; ++i)
			descriptions[i - 1] = values()[i].getDescription();
		return descriptions;
	}
	
	public static Languages findByDescription(String description) {
		for(Languages language : values())
			if(language.getDescription().equals(description))
				return language;
		return null;
	}
	
	public static Languages findByLocale(String locale) {
		for(Languages language : values())
			if(language.getLocale().equals(locale))
				return language;
		return null;
	}
}
