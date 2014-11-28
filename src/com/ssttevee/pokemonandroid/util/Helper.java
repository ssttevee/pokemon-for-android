package com.ssttevee.pokemonandroid.util;

import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.TypedValue;
import com.ssttevee.pokemonandroid.PokeDroidApplication;

public class Helper {
	public static Resources res;
	public static DataManager dataMgr;
	public static PokeDroidApplication app;

	public static class Fonts {
		private static Typeface pkmnfl = null;

		public static Typeface getPkmnFL() {
			if(pkmnfl == null) pkmnfl = Typeface.createFromAsset(res.getAssets(), "fonts/pkmnfl.ttf");
			return pkmnfl;
		}
	}

	public Helper(Resources r) {
		res = r;
	}

	public static int dpToPx(int dp) {
		return (int) Math.floor(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics()));
	}

	public static int spToPx(int sp) {
		return (int) Math.floor(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, res.getDisplayMetrics()));
	}

	public static double getDoubleFromDb(SQLiteDatabase db, String tableName, String column, String where, String order){
		double number = 0;
		Cursor c = db.query(tableName, new String[] {column}, where, null, null, null, order);
		if(c.moveToFirst()) {
			number = c.getDouble(0);
		}
		c.close();
		return number;
	}

	public static int getIntegerFromDb(SQLiteDatabase db, String tableName, String column, String where, String order){
		int number = 0;
		Cursor c = db.query(tableName, new String[] {column}, where, null, null, null, order);
		if(c.moveToFirst()){
			number = c.getInt(0);
		}
		c.close();
		return number;
	}

	public static String getStringFromDb(SQLiteDatabase db, String tableName, String column, String where, String order){
		String str = "";
		Cursor c = db.query(tableName, new String[] {column}, where, null, null, null, order);
		if(c.moveToFirst())
			str = c.getString(0);
		c.close();
		return str;
	}
}
