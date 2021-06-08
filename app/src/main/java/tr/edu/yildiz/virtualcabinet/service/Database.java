package tr.edu.yildiz.virtualcabinet.service;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import java.util.ArrayList;
import java.util.Arrays;

import tr.edu.yildiz.virtualcabinet.R;
import tr.edu.yildiz.virtualcabinet.models.ActivityModel;
import tr.edu.yildiz.virtualcabinet.models.Clothes;
import tr.edu.yildiz.virtualcabinet.models.Combine;
import tr.edu.yildiz.virtualcabinet.models.Drawer;

public class Database {

    public static void addDrawer(Context context, int mode, Drawer drawer) {
        try {
            SQLiteDatabase database = context.openOrCreateDatabase("database", mode, null);
            database.execSQL("CREATE TABLE IF NOT EXISTS drawers (drawerName VARCHAR PRIMARY KEY,drawerColor VARCHAR)");
            String insert = "INSERT INTO drawers (drawerName, drawerColor) VALUES (?, ?)";
            SQLiteStatement sqLiteStatement = database.compileStatement(insert);
            sqLiteStatement.bindString(1, drawer.getName());
            sqLiteStatement.bindString(2, drawer.getColor());
            sqLiteStatement.execute();
            sqLiteStatement.close();
            database.close();
        } catch (Exception e) {
            if(e.toString().contains("no such table"))
                System.err.println("Drawer table has not created yet");
            else
                e.printStackTrace();
        }
    }

    public static void addActivity(Context context, int mode, ActivityModel activityModel) {
        try {
            SQLiteDatabase database = context.openOrCreateDatabase("database", mode, null);
            database.execSQL("CREATE TABLE IF NOT EXISTS activities (activityName VARCHAR PRIMARY KEY," +
                    "type VARCHAR, date VARCHAR, latitude NUMBER, longitude NUMBER, address VARCHAR, combineName VARCHAR," +
                    "FOREIGN KEY (combineName) REFERENCES combines(combineName))");
            String insert = "INSERT INTO activities (activityName, type, date, latitude, longitude, address,combineName) VALUES (?, ?, ?, ?, ?, ?, ?)";
            SQLiteStatement sqLiteStatement = database.compileStatement(insert);
            sqLiteStatement.bindString(1, activityModel.getName());
            sqLiteStatement.bindString(2, activityModel.getType());
            sqLiteStatement.bindString(3, activityModel.getDate());
            sqLiteStatement.bindDouble(4, activityModel.getLatitude());
            sqLiteStatement.bindDouble(5, activityModel.getLongitude());
            sqLiteStatement.bindString(6, activityModel.getAddress());
            sqLiteStatement.bindString(7, activityModel.getCombineName());
            sqLiteStatement.execute();
            sqLiteStatement.close();
            database.close();
        } catch (Exception e) {
            if(e.toString().contains("no such table"))
                System.err.println("Activity table has not created yet");
            else
                e.printStackTrace();
        }
    }

    public static ArrayList<ActivityModel> getActivities(Context context, int mode) {
        ArrayList<ActivityModel> activities = new ArrayList<>();
        try {
            SQLiteDatabase database = context.openOrCreateDatabase("database", mode, null);
            Cursor cursor = database.rawQuery("SELECT * FROM activities ORDER BY activityName", new String[]{});
            while (cursor.moveToNext()) {
                String activityName = cursor.getString(cursor.getColumnIndex("activityName"));
                String type = cursor.getString(cursor.getColumnIndex("type"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                Double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
                Double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
                String address = cursor.getString(cursor.getColumnIndex("address"));
                String combineName = cursor.getString(cursor.getColumnIndex("combineName"));
                activities.add(new ActivityModel(activityName,type,date,latitude,longitude,address,combineName));

            }
            cursor.close();
            database.close();
            return activities;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void removeActivity(Context context, int mode, String activityName) {
        try {
            SQLiteDatabase database = context.openOrCreateDatabase("database", mode, null);
            String delete = "DELETE FROM activities WHERE activityName = ?;";
            SQLiteStatement sqLiteStatement = database.compileStatement(delete);
            sqLiteStatement.bindString(1, activityName);
            sqLiteStatement.execute();
            sqLiteStatement.close();
            database.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public static ArrayList<Drawer> getDrawers(Context context, int mode) {
        ArrayList<Drawer> drawers = new ArrayList<>();
        try {
            SQLiteDatabase database = context.openOrCreateDatabase("database", mode, null);
            Cursor cursor = database.rawQuery("SELECT drawerName,drawerColor FROM drawers ORDER BY drawerName", new String[]{});
            while (cursor.moveToNext()) {
                String drawer_name = cursor.getString(cursor.getColumnIndex("drawerName"));
                String drawerColor = cursor.getString(cursor.getColumnIndex("drawerColor"));
                ArrayList<Clothes> clothes = getClothes(context,mode,drawer_name);
                drawers.add(new Drawer(drawer_name,drawerColor,clothes));
            }
            cursor.close();
            database.close();
            return drawers;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<String> getDrawerNames(Context context, int mode) {
        ArrayList<String> drawerNames = new ArrayList<>();
        try {
            SQLiteDatabase database = context.openOrCreateDatabase("database", mode, null);
            Cursor cursor = database.rawQuery("SELECT drawerName FROM drawers", new String[]{});
            while (cursor.moveToNext()) {
                String drawer_name = cursor.getString(cursor.getColumnIndex("drawerName"));
                drawerNames.add(drawer_name);
            }
            cursor.close();
            database.close();
            return drawerNames;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void updateDrawer(Context context, int mode, String drawerName, ArrayList<Clothes> clothes) {
        if(clothes!= null){
            for (Clothes clothesSingle:clothes){
                addClothes(context,mode, clothesSingle,drawerName);
            }
        }
    }

    public static void updateDrawerName(Context context, int mode, String newDrawerName, String oldDrawerName) {
        try {
            SQLiteDatabase database = context.openOrCreateDatabase("database", mode, null);
            String update = "UPDATE drawers SET drawerName=? WHERE drawerName = ?;";
            SQLiteStatement sqLiteStatement = database.compileStatement(update);
            sqLiteStatement.bindString(1, newDrawerName);
            sqLiteStatement.bindString(2, oldDrawerName);
            sqLiteStatement.execute();
            sqLiteStatement.close();
            update = "UPDATE clothes SET drawerName=? WHERE drawerName = ?;";
            sqLiteStatement = database.compileStatement(update);
            sqLiteStatement.bindString(1, newDrawerName);
            sqLiteStatement.bindString(2, oldDrawerName);
            sqLiteStatement.execute();
            sqLiteStatement.close();
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateActivityCombine(Context context, int mode, String activityName, String combineName) {
        try {
            SQLiteDatabase database = context.openOrCreateDatabase("database", mode, null);
            String update = "UPDATE activities SET combineName=? WHERE activityName = ?;";
            SQLiteStatement sqLiteStatement = database.compileStatement(update);
            sqLiteStatement.bindString(1, combineName);
            sqLiteStatement.bindString(2, activityName);
            sqLiteStatement.execute();
            sqLiteStatement.close();
            sqLiteStatement.close();
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeDrawer(Context context, int mode, String drawerName) {
        try {
            SQLiteDatabase database = context.openOrCreateDatabase("database", mode, null);
            String delete = "DELETE FROM drawers WHERE drawerName = ?;";
            SQLiteStatement sqLiteStatement = database.compileStatement(delete);
            sqLiteStatement.bindString(1, drawerName);
            sqLiteStatement.execute();
            sqLiteStatement.close();
            database.close();
            database = context.openOrCreateDatabase("database", mode, null);
            delete = "DELETE FROM clothes WHERE drawerName = ?;";
            sqLiteStatement = database.compileStatement(delete);
            sqLiteStatement.bindString(1, drawerName);
            sqLiteStatement.execute();
            sqLiteStatement.close();
            database.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addClothes(Context context, int mode, Clothes clothes, String drawerName) {
        try {
            SQLiteDatabase database = context.openOrCreateDatabase("database", mode, null);
            database.execSQL("CREATE TABLE IF NOT EXISTS clothes (attachmentPath VARCHAR PRIMARY KEY, " +
                    "wearingLocation VARCHAR, type VARCHAR, color VARCHAR, purchaseDate VARCHAR, " +
                    "clothesPattern VARCHAR, price NUMBER, drawerName VARCHAR," +
                    "FOREIGN KEY (drawerName) REFERENCES drawers(drawerName))");
            String insert = "INSERT INTO clothes (attachmentPath,wearingLocation,type,color,purchaseDate,clothesPattern,price,drawerName) VALUES (?,?,?,?,?,?,?,?)";
            SQLiteStatement sqLiteStatement = database.compileStatement(insert);
            sqLiteStatement.bindString(1, clothes.getAttachmentPath());
            sqLiteStatement.bindString(2, clothes.getWearingLocation());
            sqLiteStatement.bindString(3, clothes.getType());
            sqLiteStatement.bindString(4, clothes.getColor());
            sqLiteStatement.bindString(5, clothes.getPurchaseDate());
            sqLiteStatement.bindString(6, clothes.getClothesPattern());
            sqLiteStatement.bindDouble(7, clothes.getPrice());
            sqLiteStatement.bindString(8, drawerName);
            sqLiteStatement.execute();
            sqLiteStatement.close();
            database.close();
        }
        catch (SQLiteConstraintException e){
            System.out.println("Skipped already exists");
        }
        catch (Exception e) {
            if(e.toString().contains("no such table"))
                System.err.println("Clothes table has not created yet");
            else
                e.printStackTrace();
        }
    }

    public static void updateClothes(Context context, int mode, Clothes newClothes, String drawerName, String oldPath) {
        try {
            removeClothes(context,mode,oldPath);
            addClothes(context,mode,newClothes,drawerName);
            ArrayList<String> wearingLocations = new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.wearing_place)));
            SQLiteDatabase database = context.openOrCreateDatabase("database", mode, null);
            String update = "";
            if(newClothes.getWearingLocation().equals(wearingLocations.get(0))){
                update = "UPDATE combines SET topHeadPath=? WHERE topHeadPath = ?;";
            }
            else if(newClothes.getWearingLocation().equals(wearingLocations.get(1))){
                update = "UPDATE combines SET facePath=? WHERE facePath = ?;";
            }
            else if(newClothes.getWearingLocation().equals(wearingLocations.get(2))){
                update = "UPDATE combines SET upperBodyPath=? WHERE upperBodyPath = ?;";
            }
            else if(newClothes.getWearingLocation().equals(wearingLocations.get(3))){
                update = "UPDATE combines SET lowerBodyPath=? WHERE lowerBodyPath = ?;";
            }
            else if(newClothes.getWearingLocation().equals(wearingLocations.get(4))){
                update = "UPDATE combines SET feetPath=? WHERE feetPath = ?;";
            }
            SQLiteStatement sqLiteStatement = database.compileStatement(update);
            sqLiteStatement.bindString(1, newClothes.getAttachmentPath());
            sqLiteStatement.bindString(2, oldPath);
            sqLiteStatement.execute();
            sqLiteStatement.close();
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static ArrayList<Clothes> getClothes(Context context, int mode, String drawerName, String wearingLocation){
        ArrayList<Clothes> clothes = new ArrayList<>();
        try {
            SQLiteDatabase database = context.openOrCreateDatabase("database", mode, null);
            Cursor cursor;
            cursor = database.rawQuery("SELECT * FROM clothes WHERE drawerName =? AND wearingLocation=? ORDER BY type", new String[]{drawerName,wearingLocation});
            while (cursor.moveToNext()) {
                String attachmentPath = cursor.getString(cursor.getColumnIndex("attachmentPath"));
                wearingLocation = cursor.getString(cursor.getColumnIndex("wearingLocation"));
                String type = cursor.getString(cursor.getColumnIndex("type"));
                String color = cursor.getString(cursor.getColumnIndex("color"));
                String purchaseDate = cursor.getString(cursor.getColumnIndex("purchaseDate"));
                String clothesPattern = cursor.getString(cursor.getColumnIndex("clothesPattern"));
                Double price = cursor.getDouble(cursor.getColumnIndex("price"));
                clothes.add(new Clothes(attachmentPath,wearingLocation,type,color,purchaseDate,clothesPattern,price));
            }
            cursor.close();
            database.close();
            return clothes;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<Clothes> getClothes(Context context, int mode, String drawerName){
        ArrayList<Clothes> clothes = new ArrayList<>();
        String[] wearingPlaces = context.getResources().getStringArray(R.array.wearing_place);
        for(String place:wearingPlaces){
            ArrayList<Clothes> part = getClothes(context,mode,drawerName,place);
            if(part!=null){
                clothes.addAll(part);
            }
        }
        return clothes;
    }

    public static void removeClothes(Context context, int mode, String path) {
        try {
            SQLiteDatabase database = context.openOrCreateDatabase("database", mode, null);
            String delete = "DELETE FROM clothes WHERE attachmentPath = ?;";
            SQLiteStatement sqLiteStatement = database.compileStatement(delete);
            sqLiteStatement.bindString(1, path);
            sqLiteStatement.execute();
            sqLiteStatement.close();
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean addCombine(Context context, int mode, Combine combine) {
        try {
            SQLiteDatabase database = context.openOrCreateDatabase("database", mode, null);
            database.execSQL("CREATE TABLE IF NOT EXISTS combines (combineName VARCHAR PRIMARY KEY," +
                    "topHeadPath VARCHAR, facePath VARCHAR, upperBodyPath VARCHAR, lowerBodyPath VARCHAR," +
                    "feetPath VARCHAR)");
            String insert = "INSERT INTO combines (combineName, topHeadPath,facePath,upperBodyPath,lowerBodyPath,feetPath) VALUES (?, ?,?,?,?,?)";
            SQLiteStatement sqLiteStatement = database.compileStatement(insert);
            sqLiteStatement.bindString(1, combine.getName());
            sqLiteStatement.bindString(2, combine.getTopHeadPath());
            sqLiteStatement.bindString(3, combine.getFacePath());
            sqLiteStatement.bindString(4, combine.getUpperBodyPath());
            sqLiteStatement.bindString(5, combine.getLowerBodyPath());
            sqLiteStatement.bindString(6, combine.getFeetPath());
            sqLiteStatement.execute();
            sqLiteStatement.close();
            database.close();
            return true;
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
            return false;
        }
        catch (Exception e){
            if(e.toString().contains("no such table"))
                System.err.println("Combine table has not created yet");
            else
                e.printStackTrace();
            return false;
        }
    }

    private static int validation(SQLiteDatabase database,ArrayList<String> paths){
        int i = 0;
        for(String path:paths){
            Cursor cursor = database.rawQuery("SELECT attachmentPath FROM clothes WHERE attachmentPath =?", new String[]{path});
            String temp = null;
            while (cursor.moveToNext()) {
                temp = cursor.getString(cursor.getColumnIndex("attachmentPath"));
            }
            if(temp != null)
                i+=1;
            cursor.close();
        }
        return i;
    }

    public static ArrayList<Combine> getCombines(Context context, int mode){
        ArrayList<Combine> combines = new ArrayList<>();
        ArrayList<Combine> goners = new ArrayList<>();
        try {
            SQLiteDatabase database = context.openOrCreateDatabase("database", mode, null);
            Cursor cursor = database.rawQuery("SELECT * FROM combines ORDER BY combineName", new String[]{});
            while (cursor.moveToNext()) {
                String combineName = cursor.getString(cursor.getColumnIndex("combineName"));
                String topHeadPath = cursor.getString(cursor.getColumnIndex("topHeadPath"));
                String facePath = cursor.getString(cursor.getColumnIndex("facePath"));
                String upperBodyPath = cursor.getString(cursor.getColumnIndex("upperBodyPath"));
                String lowerBodyPath = cursor.getString(cursor.getColumnIndex("lowerBodyPath"));
                String feetPath = cursor.getString(cursor.getColumnIndex("feetPath"));
                Combine combine = new Combine(combineName,topHeadPath,facePath,upperBodyPath,lowerBodyPath,feetPath);
                if(validation(database,combine.generateArrayList())<2){
                    goners.add(combine);
                }
                else combines.add(combine);
            }
            cursor.close();
            database.close();
            for (Combine goner:goners){
                removeCombine(context,mode,goner.getName());
            }
            return combines;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Combine getSingleCombine(Context context, int mode,String combineNamePrev){
        ArrayList<Combine> combines = new ArrayList<>();
        try {
            SQLiteDatabase database = context.openOrCreateDatabase("database", mode, null);
            Cursor cursor = database.rawQuery("SELECT * FROM combines WHERE combineName = ?", new String[]{combineNamePrev});
            while (cursor.moveToNext()) {
                String combineName = cursor.getString(cursor.getColumnIndex("combineName"));
                String topHeadPath = cursor.getString(cursor.getColumnIndex("topHeadPath"));
                String facePath = cursor.getString(cursor.getColumnIndex("facePath"));
                String upperBodyPath = cursor.getString(cursor.getColumnIndex("upperBodyPath"));
                String lowerBodyPath = cursor.getString(cursor.getColumnIndex("lowerBodyPath"));
                String feetPath = cursor.getString(cursor.getColumnIndex("feetPath"));
                combines.add(new Combine(combineName,topHeadPath,facePath,upperBodyPath,lowerBodyPath,feetPath));
            }
            cursor.close();
            database.close();
            return combines.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // updates given question on sql
    public static void updateCombine(Context context, int mode, Combine combine) {
        try {
            SQLiteDatabase database = context.openOrCreateDatabase("database", mode, null);
            String update = "UPDATE combines SET topHeadPath=?, facePath=?,upperBodyPath=?, lowerBodyPath=?, feetPath=? WHERE combineName = ?;";
            SQLiteStatement sqLiteStatement = database.compileStatement(update);
            sqLiteStatement.bindString(1, combine.getTopHeadPath());
            sqLiteStatement.bindString(2, combine.getFacePath());
            sqLiteStatement.bindString(3, combine.getUpperBodyPath());
            sqLiteStatement.bindString(4, combine.getLowerBodyPath());
            sqLiteStatement.bindString(5, combine.getFeetPath());
            sqLiteStatement.bindString(6, combine.getName());
            sqLiteStatement.execute();
            sqLiteStatement.close();
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // updates given question on sql



    public static void removeCombine(Context context, int mode, String combineName) {
        try {
            SQLiteDatabase database = context.openOrCreateDatabase("database", mode, null);
            String delete = "DELETE FROM combines WHERE combineName = ?;";
            SQLiteStatement sqLiteStatement = database.compileStatement(delete);
            sqLiteStatement.bindString(1, combineName);
            sqLiteStatement.execute();
            sqLiteStatement.close();
            database.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
