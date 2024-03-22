package com.example.corinlicense;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "appDatabase.db";
    private static final int DATABASE_VERSION = 3;
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE financial_data (" +
                "id INTEGER PRIMARY KEY," +
                "balanceCount TEXT," +
                "spentTodayCount TEXT," +
                "savingsCount TEXT," +
                "date DATE)";
        db.execSQL(CREATE_TABLE);

        String CREATE_NICKNAME_TABLE = "CREATE TABLE nickname_table (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nickname TEXT)";
        db.execSQL(CREATE_NICKNAME_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS financial_data");
        db.execSQL("DROP TABLE IF EXISTS nickname_table");
        onCreate(db);
    }

    public void saveFinancialData(BigDecimal balanceCount, BigDecimal spentTodayCount, BigDecimal savingsCount, Date date) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("balanceCount", balanceCount.toString());
        values.put("spentTodayCount", spentTodayCount.toString());
        values.put("savingsCount", savingsCount.toString());
        values.put("date", String.valueOf(date));

        Cursor cursor = db.query("financial_data", null, null, null, null, null, null);
        int numberOfRows = cursor.getCount();
        cursor.close();

        if (numberOfRows == 0) {
            db.insert("financial_data", null, values);
        } else {
            db.update("financial_data", values, null, null);
        }
        db.close();
    }
    public void saveNickname(String nickname) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nickname", nickname);

        db.insert("nickname_table", null, values);
        db.close();
    }

    public FinancialData getFinancialData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("financial_data", new String[]{"balanceCount", "spentTodayCount", "savingsCount", "date"}, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int balanceIndex = cursor.getColumnIndex("balanceCount");
            int spentIndex = cursor.getColumnIndex("spentTodayCount");
            int savingsIndex = cursor.getColumnIndex("savingsCount");
            int dateIndex = cursor.getColumnIndex("date");

            BigDecimal balance = BigDecimal.ZERO;
            BigDecimal spentToday = BigDecimal.ZERO;
            BigDecimal savings = BigDecimal.ZERO;
            Date date = new Date();

            if (balanceIndex != -1) {
                balance = new BigDecimal(cursor.getString(balanceIndex));
            }
            if (spentIndex != -1) {
                spentToday = new BigDecimal(cursor.getString(spentIndex));
            }
            if (savingsIndex != -1) {
                savings = new BigDecimal(cursor.getString(savingsIndex));
            }
            if (dateIndex != -1) {
                date = new Date(cursor.getString(dateIndex));
            }

            cursor.close();
            db.close();
            return new FinancialData(balance, spentToday, savings, date);
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return null;
    }

    public NicknameManager getNickname() {
        SQLiteDatabase db = this.getReadableDatabase();
        String nickname = "Default Nickname";

        Cursor cursor = db.query(
                "nickname_table",
                new String[]{"nickname"},
                null,
                null,
                null,
                null,
                "id DESC",
                "1"
        );

        if (cursor != null && cursor.moveToFirst()) {
            int index = cursor.getColumnIndex("nickname");
            if (index != -1) {
                nickname = cursor.getString(index);
            }
            cursor.close();
        }
        db.close();
        return new NicknameManager(nickname);
    }

    public List<FinancialTransaction> getLastFiveTransactions() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            List<FinancialTransaction> transactions = new ArrayList<>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Asumând că acesta este formatul datei

            try (SQLiteDatabase dbHelper = this.getReadableDatabase()) {
                cursor = dbHelper.rawQuery("SELECT * FROM financial_data ORDER BY id DESC LIMIT 5", null);
                if (cursor.moveToFirst()) {
                    do {
                        int idIndex = cursor.getColumnIndex("id");
                        int balanceCountIndex = cursor.getColumnIndex("balanceCount");
                        int spentTodayCountIndex = cursor.getColumnIndex("spentTodayCount");
                        int savingsCountIndex = cursor.getColumnIndex("savingsCount");
                        int dateIndex = cursor.getColumnIndex("date");

                        int id = idIndex != -1 ? cursor.getInt(idIndex) : 0;
                        String balanceCount = balanceCountIndex != -1 ? cursor.getString(balanceCountIndex) : null;
                        String spentTodayCount = spentTodayCountIndex != -1 ? cursor.getString(spentTodayCountIndex) : null;
                        String savingsCount = savingsCountIndex != -1 ? cursor.getString(savingsCountIndex) : null;
                        String dateString = dateIndex != -1 ? cursor.getString(dateIndex) : null;

                        Date date = null;
                        try {
                            if (dateString != null) {
                                date = dateFormat.parse(dateString);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace(); // Tratați excepția conform nevoilor dvs.
                        }

                        FinancialTransaction transaction = new FinancialTransaction(id, balanceCount, spentTodayCount, savingsCount, date);
                        transactions.add(transaction);
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            return transactions;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error while fetching transactions", e);
            return null;
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }


    }




    public void deleteAllFinancialData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("financial_data", null, null);
        db.close();

    }

}
