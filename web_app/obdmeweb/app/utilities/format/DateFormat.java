package utilities.format;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormat {
	
	private static final String DATE_FORMAT = "yyyy-MM-dd";
	
	public static String formatDateToObdmeStandard(Date date) {
		SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
		return df.format(date);
	}
	
	public static Date parseDateFromObdmeStandard(String dateToParse) {
		SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
		try {
			return df.parse(dateToParse);
		} catch (ParseException e) {
			return null;
		}
	}

}
