package org.me.five_stones_project.common;

import java.util.UUID;

import android.bluetooth.BluetoothSocket;

/**
 *
 * @author Tangl Andras
 */

public class Properties {
	public static final String NAME = "BluetoothFiveStones";

	public static final UUID MY_UUID =
        UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

	public static boolean isServer;
	public static BluetoothSocket socket;
	
	public static char[] characterTable = {
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
		'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
		'u', 'v', 'w', 'x', 'y', 'z', 'á', 'é', 'ó', 'ö',
		'õ', 'ú', 'ü', 'û', '.', ':', '-', '_', ',', ';',
		'?', '>', '*', '<', '#', '&', '@', '{', '}', 'ð',
		'Ð', '[', ']', '³', '£', '$', 'ß', '¤', '×', '÷',
		'€', '|', '\\', '\'', '"', '+', '!', '%', '/', '=',
		'(', ')', '¨', '¸', '´', 'ÿ', '`', '²', '°', '¢',
		'^', '¡', '~', 'A', 'B', 'C', 'D', 'E', 'F', 'G',
		'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
		'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'Á',
		'É', 'Ó', 'Ö', 'Õ', 'Ú', 'Ü', 'Û', ' ', //128
	};
	
	public static int getPosition(char c) {
		for(int i = 0; i < characterTable.length; ++i)
			if(characterTable[i] == c)
				return i;
		return -1;
	}
}
