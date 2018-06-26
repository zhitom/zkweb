package com.yasenagat.zkweb.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtil {

	private static Properties p = new Properties();

	static {

		try {
			p.load(findOtherPathInputStream("zk.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static Properties getP() {
		return p;
	}

	public static void setP(Properties p) {
		ConfigUtil.p = p;
	}

	public static String getConfigMessage(String key) {

		if (key != null && key.trim().length() > 0) {
			return p.getProperty(key);
		}
		return null;
	}

	private static InputStream findOtherPathInputStream(String propFile) {

		InputStream inputStream = ConfigUtil.class.getClassLoader()
				.getResourceAsStream(propFile);
		if (inputStream != null)
			return inputStream;

		java.io.File f = null;
		String curDir = System.getProperty("user.dir");
		f = new java.io.File(curDir, propFile);
		if (f.exists())
			try {
				return new java.io.FileInputStream(f);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

		String classpath = System.getProperty("java.class.path");
		String[] cps = classpath.split(System.getProperty("path.separator"));

		for (int i = 0; i < cps.length; i++) {
			f = new java.io.File(cps[i], propFile);
			if (f.exists())
				break;
			f = null;
		}
		if (f != null)
			try {
				return new java.io.FileInputStream(f);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		return null;
	}

}
