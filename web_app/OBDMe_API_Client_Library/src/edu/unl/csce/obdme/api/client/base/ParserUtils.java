package edu.unl.csce.obdme.api.client.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import android.util.Log;

/**
 * The Class ParserUtils.
 */
public class ParserUtils {
	
	/**
	 * Close reader.
	 *
	 * @param r the reader
	 */
	public static void closeReader(Reader r) {
		try {
			r.close();
		} catch (IOException ioe) {
			Log.e("HttpUtils", "Exception thrown closing reader.", ioe);
		}
	}
	
	/**
	 * Close input stream.
	 *
	 * @param is the InputStream
	 */
	public static void closeInputStream(InputStream is) {
		try {
			is.close();
		} catch (IOException ioe) {
			Log.e("HttpUtils", "Exception thrown closing input stream.", ioe);
		}
	}

}
