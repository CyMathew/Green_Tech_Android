package app.greentech;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Helper class for SQLite Database creation using static constants and simple DB Admin methods.
 * @author Cyril Mathew on 5/23/16.
 */

public class DBHelper extends SQLiteOpenHelper
{
    /**
     *  The name of the SQLite database created internally
     */
    private static final String DB_NAME = "GreenTech";

    /**
     *  The database version number.
     *  In order to update/upgrade an existing databse, the version number must be incremented.
     */
    private static final int DB_VERSION = 1;

    /**
     *  The table name found within the database in order to keep track of all stats
     */
    public final static String TABLE_STATS ="StatisticsRecord";

    /**
     *  A string constant for the date attribute
     */
    public final static String ATTR_DATE ="date";

    /**
     *  A string constant for the amount of paper recycled stat
     */
    public final static String ATTR_PAPER="numPaper";

    /**
     *  A string constant for the amount of plastic recycled stat
     */
    public final static String ATTR_PLASTIC="numPlastic";

    /**
     *  A string constant for the amount of aluminum recycled stat
     */
    public final static String ATTR_ALUMIN="numAlumin";

    /**
     *  A string constant for the amount of glass recycled stat
     */
    public final static String ATTR_GLASS="numGlass";

    /**
     *  A string constant for the total amount of items recycled in a day
     */
    public final static String ATTR_SUM = "total";

    /**
     * An SQL command string used to create a table within the database once the database has been created.
     */
    private static final String RECORDS_CREATE = "CREATE TABLE " + TABLE_STATS +
                                                "(" + ATTR_DATE + " TEXT PRIMARY KEY, " +
                                                    ATTR_PAPER + " integer DEFAULT 0," +
                                                    ATTR_PLASTIC + " integer DEFAULT 0," +
                                                    ATTR_ALUMIN + " integer DEFAULT 0," +
                                                    ATTR_GLASS+ " integer DEFAULT 0," +
                                                    ATTR_SUM + " integer DEFAULT 0)";

    /**
     * Sets up basic database information
     * @param context
     */
    public DBHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * Executes the command to create the StatisticsRecord table within the database
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(RECORDS_CREATE);
    }

    /**
     * Updates/Upgrades the database when new one is provided
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS MyEmployees");
        onCreate(db);
    }
}
