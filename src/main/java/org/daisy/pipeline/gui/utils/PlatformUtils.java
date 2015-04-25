package org.daisy.pipeline.gui.utils;

public class PlatformUtils {

	public static boolean isMac() {
		return System.getProperty("os.name").toLowerCase().contains("mac"); 
	}
	public static boolean isWin() {
		return System.getProperty("os.name").toLowerCase().contains("win");
	}
	public static boolean isUnix() {
		return System.getProperty("os.name").toLowerCase().contains("nix");
	}
	public static String getFileBrowserCommand() {
		if (isMac()) {
			return "open";
		}
		if (isWin()) {
			return "explorer";
		}
		if (isUnix()) {
			return "nautilus"; // TODO is this right?
		}
		return "";
	}
}
