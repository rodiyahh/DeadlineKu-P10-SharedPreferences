package com.example.deadlineku;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Database SQLite untuk data yang banyak dan terus bertambah: daftar tugas.
 * Deadline disimpan sebagai teks yyyy-MM-dd supaya mudah diurutkan.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "deadlineku.db";
    private static final int DB_VERSION = 1;

    public static class Tugas {
        public long id;
        public String judul;
        public String matkul;
        public String deadline;
    }

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE tugas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "judul TEXT NOT NULL, " +
                "matkul TEXT NOT NULL, " +
                "deadline TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tugas");
        onCreate(db);
    }

    public void tambah(String judul, String matkul, String deadline) {
        ContentValues cv = new ContentValues();
        cv.put("judul", judul);
        cv.put("matkul", matkul);
        cv.put("deadline", deadline);
        getWritableDatabase().insert("tugas", null, cv);
    }

    public void hapus(long id) {
        getWritableDatabase().delete("tugas", "id = ?", new String[]{String.valueOf(id)});
    }

    /** @param terdekatDulu true = deadline terdekat di atas, false = terjauh di atas */
    public List<Tugas> semua(boolean terdekatDulu) {
        List<Tugas> list = new ArrayList<>();
        String urut = terdekatDulu ? "deadline ASC" : "deadline DESC";
        Cursor c = getReadableDatabase().query("tugas", null, null, null, null, null, urut);
        while (c.moveToNext()) {
            Tugas t = new Tugas();
            t.id = c.getLong(c.getColumnIndexOrThrow("id"));
            t.judul = c.getString(c.getColumnIndexOrThrow("judul"));
            t.matkul = c.getString(c.getColumnIndexOrThrow("matkul"));
            t.deadline = c.getString(c.getColumnIndexOrThrow("deadline"));
            list.add(t);
        }
        c.close();
        return list;
    }
}
