package app.greentech;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Cyril on 5/23/16.
 */
public class DBHelper extends SQLiteOpenHelper
{
    private static final String DB_NAME = "GreenTech";
    private static final int DB_VERSION = 1;

    public final static String TABLE_STATS ="StatisticsRecord";

    public final static String ATTR_DATE ="date";
    public final static String ATTR_PAPER="numPaper";
    public final static String ATTR_PLASTIC="numPlastic";
    public final static String ATTR_ALUMIN="numAlumin";
    public final static String ATTR_GLASS="numGlass";
    public final static String ATTR_SUM = "total";

    private static final String RECORDS_CREATE = "CREATE TABLE " + TABLE_STATS +
                                                "(" + ATTR_DATE + " TEXT PRIMARY KEY, " +
                                                    ATTR_PAPER + " integer DEFAULT 0," +
                                                    ATTR_PLASTIC + " integer DEFAULT 0," +
                                                    ATTR_ALUMIN + " integer DEFAULT 0," +
                                                    ATTR_GLASS+ " integer DEFAULT 0," +
                                                    ATTR_SUM + " integer DEFAULT 0)";

    public DBHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(RECORDS_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS MyEmployees");
        onCreate(db);
    }
}
