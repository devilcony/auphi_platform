package com.aofei.kettle.utils;

public class ColorUtils {

	public static String toHex(int r, int g, int b) {
		String R = Integer.toHexString(r);
		R = R.length() < 2 ? ('0' + R) : R;
		String B = Integer.toHexString(g);
		B = B.length() < 2 ? ('0' + B) : B;
		String G = Integer.toHexString(b);
		G = G.length() < 2 ? ('0' + G) : G;
		return '#' + R + B + G;
	}

	public static int r(String color) {
		if(color == null)
			return 0;

		color = color.trim();
		if(color.startsWith("#"))
			color = color.substring(1);

		if(color.length() < 2)
			return 0;

		Integer i = Integer.parseInt(color.substring(0, 2), 16);
		return i;
	}

	public static int g(String color) {
		if(color == null)
			return 0;

		color = color.trim();
		if(color.startsWith("#"))
			color = color.substring(1);

		if(color.length() < 4)
			return 0;

		Integer i = Integer.parseInt(color.substring(2, 4), 16);
		return i;
	}

	public static int b(String color) {
		if(color == null)
			return 0;

		color = color.trim();
		if(color.startsWith("#"))
			color = color.substring(1);

		if(color.length() < 6)
			return 0;

		Integer i = Integer.parseInt(color.substring(4, 6), 16);
		return i;
	}

	public static void main(String[] args) {
		System.out.println(r("#646464"));
	}
}
