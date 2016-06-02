package app.greentech;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Cyril on 5/23/16.
 */
public class StatsDataSource
{
    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase database;


    public StatsDataSource(Context context)
    {
        dbHelper = new DBHelper(context);
    }

    public void open()
    {
        Log.i("GT_INFO", "Database opened");
        database = dbHelper.getWritableDatabase();
    }

    public void close()
    {
        Log.i("GT_INFO", "Database closed");
        dbHelper.close();
    }

    public void add(String type)
    {
        open();
        ContentValues values;
        Cursor todayTuple = getToday();

        if(todayTuple.getCount() > 0)
        {
            values = new ContentValues();
            values.put(getType(type), getTypeValue(type, getCurrentDate()) +1);
            values.put(DBHelper.ATTR_SUM, getTypeValue("Total", getCurrentDate()) + 1);
            database.update(DBHelper.TABLE_STATS, values, DBHelper.ATTR_DATE + "= '" + getCurrentDate()+ "'", null);

        }
        else
        {
            values = new ContentValues();
            values.put(DBHelper.ATTR_DATE, getCurrentDate());
            values.put(getType(type), 1);
            values.put(DBHelper.ATTR_SUM, 1);
            database.insert(DBHelper.TABLE_STATS, null, values);

        }

    }

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
            return 0;
        }
    }

    public String getCurrentDate()
    {
        Cursor cursor = database.rawQuery("SELECT date('now', 'localtime');", null);
        cursor.moveToFirst();
        return cursor.getString(0);

    }

    public Cursor getToday()
    {
        Cursor cursor = database.rawQuery("SELECT * FROM " + DBHelper.TABLE_STATS +
                                          " WHERE date = '"  + getCurrentDate() + "';", null);

        return cursor;
    }

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
            return 0;
        }

    }

    public int getAmount(String type, String date)
    {
        return getTypeValue(type, date);
    }
}
