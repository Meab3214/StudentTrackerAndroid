package com.example.studenttracker;

import android.content.*;import android.database.*;import android.database.sqlite.*;

public class StudentDb extends SQLiteOpenHelper{
 public static final String[] CLASSES={"1م أ","1م ب","2م أ","2م ب","3م أ","3م ب"};
 public static final int WEEKS=19;
 public StudentDb(Context c){super(c,"student_tracker.db",null,1);}
 public void onCreate(SQLiteDatabase d){
  d.execSQL("CREATE TABLE students(id INTEGER PRIMARY KEY AUTOINCREMENT,student_no INTEGER,name TEXT NOT NULL,class_name TEXT NOT NULL,UNIQUE(class_name,student_no))");
  d.execSQL("CREATE TABLE attendance(student_id INTEGER,week INTEGER,session INTEGER,status TEXT,UNIQUE(student_id,week,session))");
  d.execSQL("CREATE TABLE grades(student_id INTEGER,period INTEGER,participation REAL DEFAULT 0,activities REAL DEFAULT 0,homework REAL DEFAULT 0,project REAL DEFAULT 0,short_tests REAL DEFAULT 0,practical REAL DEFAULT 0,UNIQUE(student_id,period))");
 }
 public void onUpgrade(SQLiteDatabase d,int a,int b){}
 public Cursor students(String c){return getReadableDatabase().rawQuery("SELECT id,student_no,name FROM students WHERE class_name=? ORDER BY student_no",new String[]{c});}
 public int count(String c){Cursor x=getReadableDatabase().rawQuery("SELECT count(*) FROM students WHERE class_name=?",new String[]{c});x.moveToFirst();int n=x.getInt(0);x.close();return n;}
 public long add(int no,String name,String c){ContentValues v=new ContentValues();v.put("student_no",no);v.put("name",name.trim());v.put("class_name",c);return getWritableDatabase().insert("students",null,v);}
 public int update(long id,int no,String name){ContentValues v=new ContentValues();v.put("student_no",no);v.put("name",name.trim());return getWritableDatabase().update("students",v,"id=?",new String[]{String.valueOf(id)});}
 public void delete(long id){SQLiteDatabase d=getWritableDatabase();d.delete("attendance","student_id=?",new String[]{""+id});d.delete("grades","student_id=?",new String[]{""+id});d.delete("students","id=?",new String[]{""+id});}
 public void attendance(long id,int w,int s,String st){ContentValues v=new ContentValues();v.put("student_id",id);v.put("week",w);v.put("session",s);v.put("status",st);getWritableDatabase().insertWithOnConflict("attendance",null,v,SQLiteDatabase.CONFLICT_REPLACE);}
 public String attendance(long id,int w,int s){Cursor c=getReadableDatabase().rawQuery("SELECT status FROM attendance WHERE student_id=? AND week=? AND session=?",new String[]{""+id,""+w,""+s});String r="";if(c.moveToFirst())r=c.getString(0);c.close();return r;}
 public void grades(long id,int p,double a,double b,double c,double d,double e,double f){ContentValues v=new ContentValues();v.put("student_id",id);v.put("period",p);v.put("participation",a);v.put("activities",b);v.put("homework",c);v.put("project",d);v.put("short_tests",e);v.put("practical",f);getWritableDatabase().insertWithOnConflict("grades",null,v,SQLiteDatabase.CONFLICT_REPLACE);}
 public double[] grades(long id,int p){double[]r=new double[6];Cursor c=getReadableDatabase().rawQuery("SELECT participation,activities,homework,project,short_tests,practical FROM grades WHERE student_id=? AND period=?",new String[]{""+id,""+p});if(c.moveToFirst())for(int i=0;i<6;i++)r[i]=c.getDouble(i);c.close();return r;}
 public double avg(String cls,int p){Cursor c=getReadableDatabase().rawQuery("SELECT AVG(participation+activities+homework+project+short_tests+practical) FROM grades g JOIN students s ON s.id=g.student_id WHERE s.class_name=? AND period=?",new String[]{cls,""+p});double r=0;if(c.moveToFirst()&&!c.isNull(0))r=c.getDouble(0);c.close();return r;}
 public int marked(String cls){Cursor c=getReadableDatabase().rawQuery("SELECT count(*) FROM attendance a JOIN students s ON s.id=a.student_id WHERE s.class_name=?",new String[]{cls});c.moveToFirst();int n=c.getInt(0);c.close();return n;}
 public int present(String cls){Cursor c=getReadableDatabase().rawQuery("SELECT count(*) FROM attendance a JOIN students s ON s.id=a.student_id WHERE s.class_name=? AND status='present'",new String[]{cls});c.moveToFirst();int n=c.getInt(0);c.close();return n;}
}
