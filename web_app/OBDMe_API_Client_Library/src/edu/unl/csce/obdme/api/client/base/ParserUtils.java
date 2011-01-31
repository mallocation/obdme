package edu.unl.csce.obdme.api.client.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import android.util.Log;

public class ParserUtils {
	
	public static void closeReader(Reader r) {
		try {
			r.close();
		} catch (IOException ioe) {
			Log.e("HttpUtils", "Exception thrown closing reader.", ioe);
		}
	}
	
	public static void closeInputStream(InputStream is) {
		try {
			is.close();
		} catch (IOException ioe) {
			Log.e("HttpUtils", "Exception thrown closing input stream.", ioe);
		}
	}

}
