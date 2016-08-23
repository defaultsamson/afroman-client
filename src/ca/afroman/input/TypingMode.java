package ca.afroman.input;

import java.util.ArrayList;
import java.util.List;

import ca.afroman.client.ClientGame;

public enum TypingMode
{
	NONE,
	FULL,
	ONLY_NUMBERS,
	ONLY_LETTERS,
	ONLY_NUMBERS_AND_LETTERS,
	ONLY_NUMBERS_AND_COMMA;
	
	private static List<TypingKeyWrapper> keyModes;
	
	public static List<TypingKeyWrapper> getKeyModes()
	{
		if (keyModes == null)
		{
			InputHandler input = ClientGame.instance().input();
			List<TypingKeyWrapper> keyModes = new ArrayList<TypingKeyWrapper>();
			keyModes.add(new TypingKeyWrapper(input.space, " ", new TypingModeWrapper(TypingMode.FULL)));
			keyModes.add(new TypingKeyWrapper(input.period, ">", new TypingModeWrapper(TypingMode.FULL), ".", new TypingModeWrapper(TypingMode.FULL)));
			keyModes.add(new TypingKeyWrapper(input.comma, "<", new TypingModeWrapper(TypingMode.FULL), ",", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_COMMA)));
			keyModes.add(new TypingKeyWrapper(input.slash, "?", new TypingModeWrapper(TypingMode.FULL), "/", new TypingModeWrapper(TypingMode.FULL)));
			keyModes.add(new TypingKeyWrapper(input.backslash, "|", new TypingModeWrapper(TypingMode.FULL), "\\", new TypingModeWrapper(TypingMode.FULL)));
			keyModes.add(new TypingKeyWrapper(input.semicolon, ":", new TypingModeWrapper(TypingMode.FULL), ";", new TypingModeWrapper(TypingMode.FULL)));
			keyModes.add(new TypingKeyWrapper(input.hyphen, "_", new TypingModeWrapper(TypingMode.FULL), "-", new TypingModeWrapper(TypingMode.FULL)));
			keyModes.add(new TypingKeyWrapper(input.equals, "+", new TypingModeWrapper(TypingMode.FULL), "=", new TypingModeWrapper(TypingMode.FULL)));
			keyModes.add(new TypingKeyWrapper(input.zero, ")", new TypingModeWrapper(TypingMode.FULL), "0", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS, TypingMode.ONLY_NUMBERS_AND_COMMA, TypingMode.ONLY_NUMBERS)));
			keyModes.add(new TypingKeyWrapper(input.one, "!", new TypingModeWrapper(TypingMode.FULL), "1", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS, TypingMode.ONLY_NUMBERS_AND_COMMA, TypingMode.ONLY_NUMBERS)));
			keyModes.add(new TypingKeyWrapper(input.two, "@", new TypingModeWrapper(TypingMode.FULL), "2", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS, TypingMode.ONLY_NUMBERS_AND_COMMA, TypingMode.ONLY_NUMBERS)));
			keyModes.add(new TypingKeyWrapper(input.three, "#", new TypingModeWrapper(TypingMode.FULL), "3", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS, TypingMode.ONLY_NUMBERS_AND_COMMA, TypingMode.ONLY_NUMBERS)));
			keyModes.add(new TypingKeyWrapper(input.four, "$", new TypingModeWrapper(TypingMode.FULL), "4", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS, TypingMode.ONLY_NUMBERS_AND_COMMA, TypingMode.ONLY_NUMBERS)));
			keyModes.add(new TypingKeyWrapper(input.five, "%", new TypingModeWrapper(TypingMode.FULL), "5", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS, TypingMode.ONLY_NUMBERS_AND_COMMA, TypingMode.ONLY_NUMBERS)));
			keyModes.add(new TypingKeyWrapper(input.six, "^", new TypingModeWrapper(TypingMode.FULL), "6", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS, TypingMode.ONLY_NUMBERS_AND_COMMA, TypingMode.ONLY_NUMBERS)));
			keyModes.add(new TypingKeyWrapper(input.seven, "&", new TypingModeWrapper(TypingMode.FULL), "7", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS, TypingMode.ONLY_NUMBERS_AND_COMMA, TypingMode.ONLY_NUMBERS)));
			keyModes.add(new TypingKeyWrapper(input.eight, "*", new TypingModeWrapper(TypingMode.FULL), "8", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS, TypingMode.ONLY_NUMBERS_AND_COMMA, TypingMode.ONLY_NUMBERS)));
			keyModes.add(new TypingKeyWrapper(input.nine, "(", new TypingModeWrapper(TypingMode.FULL), "9", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS, TypingMode.ONLY_NUMBERS_AND_COMMA, TypingMode.ONLY_NUMBERS)));
			keyModes.add(new TypingKeyWrapper(input.a, "A", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS), "a", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS)));
			keyModes.add(new TypingKeyWrapper(input.b, "B", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS), "b", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS)));
			keyModes.add(new TypingKeyWrapper(input.c, "C", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS), "c", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS)));
			keyModes.add(new TypingKeyWrapper(input.d, "D", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS), "d", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS)));
			keyModes.add(new TypingKeyWrapper(input.e, "E", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS), "e", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS)));
			keyModes.add(new TypingKeyWrapper(input.f, "F", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS), "f", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS)));
			keyModes.add(new TypingKeyWrapper(input.g, "G", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS), "g", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS)));
			keyModes.add(new TypingKeyWrapper(input.h, "H", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS), "h", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS)));
			keyModes.add(new TypingKeyWrapper(input.i, "I", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS), "i", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS)));
			keyModes.add(new TypingKeyWrapper(input.j, "J", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS), "j", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS)));
			keyModes.add(new TypingKeyWrapper(input.k, "K", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS), "k", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS)));
			keyModes.add(new TypingKeyWrapper(input.l, "L", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS), "l", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS)));
			keyModes.add(new TypingKeyWrapper(input.m, "M", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS), "m", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS)));
			keyModes.add(new TypingKeyWrapper(input.n, "N", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS), "n", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS)));
			keyModes.add(new TypingKeyWrapper(input.o, "O", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS), "o", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS)));
			keyModes.add(new TypingKeyWrapper(input.p, "P", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS), "p", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS)));
			keyModes.add(new TypingKeyWrapper(input.q, "Q", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS), "q", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS)));
			keyModes.add(new TypingKeyWrapper(input.r, "R", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS), "r", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS)));
			keyModes.add(new TypingKeyWrapper(input.s, "S", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS), "s", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS)));
			keyModes.add(new TypingKeyWrapper(input.t, "T", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS), "t", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS)));
			keyModes.add(new TypingKeyWrapper(input.u, "U", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS), "u", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS)));
			keyModes.add(new TypingKeyWrapper(input.v, "V", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS), "v", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS)));
			keyModes.add(new TypingKeyWrapper(input.w, "W", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS), "w", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS)));
			keyModes.add(new TypingKeyWrapper(input.x, "X", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS), "x", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS)));
			keyModes.add(new TypingKeyWrapper(input.y, "Y", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS), "y", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS)));
			keyModes.add(new TypingKeyWrapper(input.z, "Z", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS), "z", new TypingModeWrapper(TypingMode.FULL, TypingMode.ONLY_NUMBERS_AND_LETTERS)));
			
			TypingMode.keyModes = keyModes;
		}
		
		return keyModes;
	}
}
