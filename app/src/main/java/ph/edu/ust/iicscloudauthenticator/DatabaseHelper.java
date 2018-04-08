package ph.edu.ust.iicscloudauthenticator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Phillip on 23 Nov 2016.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private final static String DATABASE_NAME = "Login.DB";
    private final static String CREATE_TABLE = "CREATE TABLE LOGIN(ID INTEGER PRIMARY KEY AUTOINCREMENT, USERNAME TEXT, PASSWORD TEXT, BDAY TEXT, ADDRESS TEXT, EMAIL TEXT)";
    private final static String COL1 = "ID";
    private final static String COL2 = "USERNAME";
    private final static String COL3 = "PASSWORD";
    private final static String COL4 = "BDAY";
    private final static String COL5 = "ADDRESS";
    private final static String COL6 = "EMAIL";

    private final static String TABLE_NAME = "LOGIN";


    SQLiteDatabase db;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS LOGIN");
        onCreate(db);
    }

    public boolean insertUser(String userName, String passWord, String bDay, String address, String eMail){
        db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COL2, userName);
        v.put(COL3, passWord);
        v.put(COL4, bDay);
        v.put(COL5, address);
        v.put(COL6, eMail);
        long result = db.insert(TABLE_NAME, null, v);
        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    public Cursor getUserData(String username){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT BDAY,ADDRESS,EMAIL FROM "+TABLE_NAME+" WHERE USERNAME = '"+username+"'",null);
        return result;
    }

    public boolean checkUser(String etUser, String etPass){
        SQLiteDatabase db = this.getReadableDatabase();
        String a = etUser, b = etPass;
        boolean isLegit = false;
        Cursor result = db.rawQuery("SELECT USERNAME,PASSWORD FROM LOGIN",null);
        if(result.moveToFirst()){
            do{
                if(result.getString(0).contentEquals(a)&&result.getString(1).contentEquals(b)){
                    isLegit = true;
                }
            }while(result.moveToNext());
        }
        return isLegit;
    }

    public boolean updateUser(String username, String password, String bday, String address, String email){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL2, username);
        cv.put(COL3, password);
        cv.put(COL4, bday);
        cv.put(COL5, address);
        cv.put(COL6, email);
        db.update(TABLE_NAME, cv, "username = ?", new String[] {
                username
        } );
        return true;
    }

    public Cursor getAllData(){
        db = getWritableDatabase();
        Cursor res = db.rawQuery("SELECT ID,USERNAME,PASSWORD FROM LOGIN", null);
        return res;
    }

}

