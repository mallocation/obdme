package edu.unl.csce.obdme.database;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * The Class DatabaseExporter.
 */
public class DatabaseExporter {

	/** The context. */
	private Context context;
	
	/** The sql db. */
	private SQLiteDatabase sqlDB;
	
	/** The exporter. */
	private Exporter exporter;

	/**
	 * Instantiates a new database exporter.
	 *
	 * @param ctx the ctx
	 * @param db the db
	 */
	public DatabaseExporter( Context ctx, SQLiteDatabase db )
	{
		context = ctx;
		sqlDB = db;

		try
		{
			// create a file on the sdcard to export the
			// database contents to
			File databaseFile = new File(context.getExternalFilesDir(null), "cache.db");
			databaseFile.createNewFile();

			FileOutputStream fileOutputStream =  new FileOutputStream(databaseFile);
			BufferedOutputStream bos = new BufferedOutputStream( fileOutputStream );

			exporter = new Exporter( bos );
		} catch (FileNotFoundException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Export data.
	 */
	public void exportData( ) {
		log( "Exporting Data" );

		try
		{
			exporter.startDbExport( sqlDB.getPath() );

			// get the tables out of the given sqlite database
			String sql = "SELECT * FROM sqlite_master";

			Cursor cur = sqlDB.rawQuery( sql, new String[0] );
			cur.moveToFirst();

			String tableName;
			while ( cur.getPosition() < cur.getCount() ) {
				tableName = cur.getString( cur.getColumnIndex( "name" ) );
				log( "table name " + tableName );

				// don't process these two tables since they are used
				// for metadata
				if (!tableName.equals("android_metadata") && !tableName.equals("sqlite_sequence")) {
					exportTable( tableName );
				}

				cur.moveToNext();
			}
			
			exporter.endDbExport();
			exporter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Export table.
	 *
	 * @param tableName the table name
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void exportTable( String tableName ) throws IOException {
		exporter.startTable(tableName);

		// get everything from the table
		String sql = "select * from " + tableName;
		Cursor cur = sqlDB.rawQuery( sql, new String[0] );
		int numcols = cur.getColumnCount();

		log( "Start exporting table " + tableName );

		cur.moveToFirst();

		// move through the table, creating rows
		// and adding each column with name and value
		// to the row
		while( cur.getPosition() < cur.getCount() ) {
			exporter.startRow();
			String name;
			String val;
			
			for( int idx = 0; idx < numcols; idx++ ) {
				name = cur.getColumnName(idx);
				val = cur.getString( idx );
				log( "col '" + name + "' -- val '" + val + "'" );
				exporter.addColumn( name, val );
			}

			exporter.endRow();
			cur.moveToNext();
		}

		cur.close();

		exporter.endTable();
	}

	/**
	 * Log.
	 *
	 * @param msg the msg
	 */
	private void log( String msg ) {
		
	}

	/**
	 * The Class Exporter.
	 */
	class Exporter {
		
		/** The Constant CLOSING_WITH_TICK. */
		private static final String CLOSING_WITH_TICK = "'>";
		
		/** The Constant START_DB. */
		private static final String START_DB = "<export-database name='";
		
		/** The Constant END_DB. */
		private static final String END_DB = "</export-database>";
		
		/** The Constant START_TABLE. */
		private static final String START_TABLE = "<table name='";
		
		/** The Constant END_TABLE. */
		private static final String END_TABLE = "</table>";
		
		/** The Constant START_ROW. */
		private static final String START_ROW = "<row>";
		
		/** The Constant END_ROW. */
		private static final String END_ROW = "</row>";
		
		/** The Constant START_COL. */
		private static final String START_COL = "<col name='";
		
		/** The Constant END_COL. */
		private static final String END_COL = "</col>";

		/** The buff output stream. */
		private BufferedOutputStream buffOutputStream;

		/**
		 * Instantiates a new exporter.
		 *
		 * @throws FileNotFoundException the file not found exception
		 */
		public Exporter() throws FileNotFoundException {
			this( new BufferedOutputStream(
					context.openFileOutput(  context.getExternalFilesDir(null).toString() + "cache.db",
							Context.MODE_WORLD_READABLE ) ) );
		}

		/**
		 * Instantiates a new exporter.
		 *
		 * @param bos the bos
		 */
		public Exporter( BufferedOutputStream bos ) {
			buffOutputStream = bos;
		}

		/**
		 * Close.
		 *
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		public void close() throws IOException {
			if ( buffOutputStream != null ) {
				buffOutputStream.close();
			}
		}

		/**
		 * Start db export.
		 *
		 * @param dbName the db name
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		public void startDbExport( String dbName ) throws IOException {
			String stg = START_DB + dbName + CLOSING_WITH_TICK;
			buffOutputStream.write( stg.getBytes() );
		}

		/**
		 * End db export.
		 *
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		public void endDbExport() throws IOException {
			buffOutputStream.write( END_DB.getBytes() );
		}

		/**
		 * Start table.
		 *
		 * @param tableName the table name
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		public void startTable( String tableName ) throws IOException {
			String stg = START_TABLE + tableName + CLOSING_WITH_TICK;
			buffOutputStream.write( stg.getBytes() );
		}

		/**
		 * End table.
		 *
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		public void endTable() throws IOException {
			buffOutputStream.write( END_TABLE.getBytes() );
		}

		/**
		 * Start row.
		 *
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		public void startRow() throws IOException {
			buffOutputStream.write( START_ROW.getBytes() );
		}

		/**
		 * End row.
		 *
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		public void endRow() throws IOException {
			buffOutputStream.write( END_ROW.getBytes() );
		}

		/**
		 * Adds the column.
		 *
		 * @param name the name
		 * @param val the val
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		public void addColumn( String name, String val ) throws IOException {
			String stg = START_COL + name + CLOSING_WITH_TICK + val + END_COL;
			buffOutputStream.write( stg.getBytes() );
		}
	}
}