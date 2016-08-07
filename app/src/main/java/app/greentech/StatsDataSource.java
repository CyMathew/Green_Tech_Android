package app.greentech;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Performs most common database operations
 * @author Cyril Mathew on 5/23/16.
 */
public class StatsDataSource
{
    /**
     * Custom SQLite class to help create the database and group similar functionality and constants
     */
    private SQLiteOpenHelper dbHelper;

    /**
     * Database variable
     */
    private SQLiteDatabase database;

    /**
     * Constructor for the class
     * @param context
     */
    public StatsDataSource(Context context)
    {
        dbHelper = new DBHelper(context);
    }

    /**
     * Opens the connection to the database for access.
     */
    public void open()
    {
        Log.i("DB_INFO", "Database opened");
        database = dbHelper.getWritableDatabase();
    }

    /**
     * Closes the connection to the database from access in order to stop memory leaks and save battery
     */
    public void close()
    {
        Log.i("DB_INFO", "Database closed");
        dbHelper.close();
    }

    /**
     * Get the attribute of the specified type. Helper method for converting from string to database attribute name
     * @param type
     * @return Returns the proper string name from the database.
     */
    private String getType(String type)
    {
        String colName = "";

        switch(type)
        {
            case "Paper":
                colName = DBHelper.ATTR_PAPER;
                break;
            case "Plastic":
                colName = DBHelper.ATTR_PLASTIC;
                break;
            case "Aluminum":
                colName = DBHelper.ATTR_ALUMIN;
                break;
            case "Glass":
                colName = DBHelper.ATTR_GLASS;
                break;
            case "Total":
                colName = DBHelper.ATTR_SUM;
                break;
        }

        return colName;
    }

    /**
     * SQL Command to get the value of a specified type from a given date
     * @param type
     * @param date
     * @return The value of the specifed type is returned, else 0.
     */
    private int getTypeValue(String type, String date)
    {
        try
        {
            Cursor cursor = database.rawQuery("SELECT " + getType(type) + " FROM " + DBHelper.TABLE_STATS +
                    " WHERE date = '" + date + "';", null);


            cursor.moveToFirst();
            return cursor.getInt(0);
        }
        catch (CursorIndexOutOfBoundsException e)
        {
            // If a value can't be found return 0 since the value doesn't exist.
            return 0;
        }
    }

    /**
     * Database command to add the specified type of recycling done to the records
     * @param type
     */
    public void addToStats(String type)
    {
        open();                             //Open the database connection
        ContentValues values;
        Cursor todayTuple = getToday();     //Store reference to today's values

        if(todayTuple.getCount() > 0)       //If there is already an existing record for today
        {
            values = new ContentValues();
            values.put(getType(type), getTypeValue(type, getCurrentDate()) +1);
            values.put(DBHelper.ATTR_SUM, getTypeValue("Total", getCurrentDate()) + 1);
            database.update(DBHelper.TABLE_STATS, values, DBHelper.ATTR_DATE + "= '" + getCurrentDate()+ "'", null);

        }
        else                                //Else create a new record for today
        {
            values = new ContentValues();
            values.put(DBHelper.ATTR_DATE, getCurrentDate());
            values.put(getType(type), 1);
            values.put(DBHelper.ATTR_SUM, 1);
            database.insert(DBHelper.TABLE_STATS, null, values);

        }

    }


    /**
     * Simple SQL command to get local date from database. Useful way to get date without resorting to using another class
     * @return Returns the date string
     */
    public String getCurrentDate()
    {
        Cursor cursor = database.rawQuery("SELECT date('now', 'localtime');", null);
        cursor.moveToFirst();
        return cursor.getString(0);

    }

    /**
     * Gets today's record from database
     * @return Reference to today's tuple
     */
    public Cursor getToday()
    {
        Cursor cursor = database.rawQuery("SELECT * FROM " + DBHelper.TABLE_STATS +
                                          " WHERE date = '"  + getCurrentDate() + "';", null);

        return cursor;
    }

    /**
     * Gets total from a specified date using SQL command
     * @param date
     * @return Amount in integer if tuple exists, else 0.
     */
    public int getTotal(String date)
    {
        try
        {
            Cursor cursor = database.rawQuery("SELECT total FROM " + DBHelper.TABLE_STATS +
                    " WHERE date = '" + date + "';", null);


            cursor.moveToFirst();
            return cursor.getInt(0);
        }
        catch (CursorIndexOutOfBoundsException e)
        {
            // If a value can't be found return 0 since the value doesn't exist.
            return 0;
        }

    }

    /**
     * Public method for retrieving values using given date and type.
     * @param type
     * @param date
     * @return Amount, in integer, of a given type and from given date.
     */
    public int getAmount(String type, String date)
    {
        return getTypeValue(type, date);
    }
}
