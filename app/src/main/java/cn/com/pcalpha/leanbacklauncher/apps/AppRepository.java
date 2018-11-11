package cn.com.pcalpha.leanbacklauncher.apps;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.com.pcalpha.leanbacklauncher.utils.DrawableConverter;

public class AppRepository extends SQLiteOpenHelper {
    private static String TAG = "AppRepository";

    private Context mContext;
    private DrawableConverter drawableConverter;

    private static final String DB_NAME = "App.db";
    private static final String TABLE_APP = "app";
    private static final String TABLE_APP_BLACKLIST = "app_blacklist";
    private static final String COLUMN_PACKAGE_NAME = "package_name";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_ICON = "icon";
    private static final String COLUMN_POSITION = "position";
    private static final String COLUMN_IS_LEANBACK = "is_leanback";
    private static final String COLUMN_IS_SYS = "is_sys";
    private static final String[] COLUMNS = new String[]{
            COLUMN_PACKAGE_NAME,
            COLUMN_NAME,
            COLUMN_ICON,
            COLUMN_POSITION,
            COLUMN_IS_LEANBACK,
            COLUMN_IS_SYS
    };


    private AppRepository(Context context) {
        super(context, DB_NAME, null, 10);
        this.mContext = context;
        this.drawableConverter = new DrawableConverter(mContext);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        recreateTable(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        recreateTable(db);
    }

    private void createTable(SQLiteDatabase db){
        db.execSQL("CREATE TABLE IF NOT EXISTS app ( 'package_name' TEXT PRIMARY KEY, 'name' TEXT, 'icon' blob, 'position' INTEGER , 'is_leanback' INTEGER NOT NULL, 'is_sys' INTEGER NOT NULL) ");
        db.execSQL("CREATE TABLE IF NOT EXISTS app_blacklist ( 'package_name' TEXT PRIMARY KEY) ");
    }

    private void recreateTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APP_BLACKLIST);
        createTable(db);
    }

    private static AppRepository sAppsRepository = null;
    public static AppRepository getInstance(Context paramContext) {
        if (sAppsRepository == null) {
            synchronized (AppRepository.class) {
                if (sAppsRepository == null) {
                    sAppsRepository = new AppRepository(paramContext.getApplicationContext());
                }
            }
        }
        return sAppsRepository;
    }

    public AppInfo get(String packageName){
        SQLiteDatabase db = getReadableDatabase();
        List<AppInfo> appInfoList = new ArrayList<>();
        try {
            Cursor cursor = db.query(TABLE_APP, COLUMNS, "package_name = ?", new String[]{packageName}, null, null, null);
            Integer indexPackageName = cursor.getColumnIndex(COLUMN_PACKAGE_NAME);
            Integer indexName = cursor.getColumnIndex(COLUMN_NAME);
            Integer indexPosition = cursor.getColumnIndex(COLUMN_POSITION);
            Integer indexIcon = cursor.getColumnIndex(COLUMN_ICON);
            Integer indexIsLeanback = cursor.getColumnIndex(COLUMN_IS_LEANBACK);
            Integer indexIsSys = cursor.getColumnIndex(COLUMN_IS_SYS);

            while (cursor.moveToNext()) {
                AppInfo appInfo = new AppInfo();
                appInfo.setPackageName(cursor.getString(indexPackageName));
                appInfo.setName(cursor.getString(indexName));
                appInfo.setPosition(cursor.getInt(indexPosition));
                appInfo.setIcon(drawableConverter.getDrawableFromByteArray(cursor.getBlob(indexIcon)));
                appInfo.setLeanback(cursor.getInt(indexIsLeanback) > 0);
                appInfo.setSys(cursor.getInt(indexIsSys) > 0);

                appInfoList.add(appInfo);
            }

            if (null == appInfoList || appInfoList.size() == 0) {
                return null;
            }
            return appInfoList.get(0);
        } catch (SQLiteException e) {
            Log.e("AppRepository", "Could not find appIcon from database", e);
        } finally {
            db.endTransaction();
        }
        return null;
    }

    public List<AppInfo> findAll(){
        SQLiteDatabase db = getReadableDatabase();
        List<AppInfo> appInfoList = new ArrayList<>();
        try {
            Cursor cursor = db.query(TABLE_APP, COLUMNS, null, null, null, null, null);
            Integer indexPackageName = cursor.getColumnIndex(COLUMN_PACKAGE_NAME);
            Integer indexName = cursor.getColumnIndex(COLUMN_NAME);
            Integer indexPosition = cursor.getColumnIndex(COLUMN_POSITION);
            Integer indexIcon = cursor.getColumnIndex(COLUMN_ICON);
            Integer indexIsLeanback = cursor.getColumnIndex(COLUMN_IS_LEANBACK);
            Integer indexIsSys = cursor.getColumnIndex(COLUMN_IS_SYS);

            while (cursor.moveToNext()) {
                AppInfo appInfo = new AppInfo();
                appInfo.setPackageName(cursor.getString(indexPackageName));
                appInfo.setName(cursor.getString(indexName));
                appInfo.setPosition(cursor.getInt(indexPosition));
                appInfo.setIcon(drawableConverter.getDrawableFromByteArray(cursor.getBlob(indexIcon)));
                appInfo.setLeanback(cursor.getInt(indexIsLeanback) > 0);
                appInfo.setSys(cursor.getInt(indexIsSys) > 0);

                appInfoList.add(appInfo);
            }
            Collections.sort(appInfoList);
            return appInfoList;
        } catch (SQLiteException e) {
            Log.e("AppRepository", "Could not find appIcon from database", e);
        } finally {
            db.endTransaction();
        }
        return appInfoList;
    }





    class InsertTask extends AsyncTask<Void, Void, Void> {
        private AppInfo mAppInfo;
        private OnTaskCompletedListener mOnTaskCompletedListener;

        public InsertTask(AppInfo appInfo,OnTaskCompletedListener onTaskCompletedListener) {
            this.mAppInfo = appInfo;
            this.mOnTaskCompletedListener = onTaskCompletedListener;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ContentValues values = new ContentValues();

            values.put(COLUMN_PACKAGE_NAME, mAppInfo.getPackageName());
            values.put(COLUMN_NAME, mAppInfo.getName());
            values.put(COLUMN_ICON, drawableConverter.getBitmapAsByteArray(mAppInfo.getIcon()));
            values.put(COLUMN_POSITION, mAppInfo.getPosition());
            values.put(COLUMN_IS_LEANBACK, mAppInfo.isLeanback());
            values.put(COLUMN_IS_SYS, mAppInfo.isSys());

            SQLiteDatabase db = getWritableDatabase();
            try {
                if (db.insert(TABLE_APP, null, values) >= 0) {
                    db.setTransactionSuccessful();
                    return null;
                }
                throw new SQLiteException("Unable to insert into database.");
            } catch (SQLiteException e) {
                Log.e("AppRepository", "Could not insert appIcon into database : " + mAppInfo.getPackageName(), e);
            } finally {
                db.endTransaction();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(null!=this.mOnTaskCompletedListener){
                this.mOnTaskCompletedListener.onTaskCompleted();
            }
        }
    }

    class UpdateTask extends AsyncTask<Void, Void, Void> {
        private AppInfo mAppInfo;
        private OnTaskCompletedListener mOnTaskCompletedListener;

        public UpdateTask(AppInfo appInfo,OnTaskCompletedListener onTaskCompletedListener) {
            this.mAppInfo = appInfo;
            this.mOnTaskCompletedListener = onTaskCompletedListener;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_PACKAGE_NAME, mAppInfo.getPackageName());
            values.put(COLUMN_NAME, mAppInfo.getName());
            values.put(COLUMN_ICON, drawableConverter.getBitmapAsByteArray(mAppInfo.getIcon()));
            values.put(COLUMN_POSITION, mAppInfo.getPosition());
            values.put(COLUMN_IS_LEANBACK, mAppInfo.isLeanback());
            values.put(COLUMN_IS_SYS, mAppInfo.isSys());

            SQLiteDatabase db = getWritableDatabase();
            try {
                if (db.update(TABLE_APP, values, "package_name = ?", new String[]{this.mAppInfo.getPackageName()}) == 0) {
                    Log.e("InstallingLaunchItemsDbHelper", "Missing package requested for update : " + this.mAppInfo.getPackageName());
                }
                db.setTransactionSuccessful();
            } catch (SQLiteException e) {
                Log.e("AppRepository", "Could not update appIcon into database : " + mAppInfo.getPackageName(), e);
            } finally {
                db.endTransaction();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(null!=this.mOnTaskCompletedListener){
                this.mOnTaskCompletedListener.onTaskCompleted();
            }
        }
    }

    class DeleteTask extends AsyncTask<Void, Void, Void> {
        private AppInfo mAppInfo;
        private OnTaskCompletedListener mOnTaskCompletedListener;

        public DeleteTask(AppInfo appInfo,OnTaskCompletedListener onTaskCompletedListener) {
            this.mAppInfo = appInfo;
            this.mOnTaskCompletedListener = onTaskCompletedListener;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            SQLiteDatabase db = getWritableDatabase();
            try {
                db.delete(TABLE_APP, "package_name = ?", new String[]{this.mAppInfo.getPackageName()});
                db.setTransactionSuccessful();
            } catch (SQLiteException e) {
                Log.e("AppRepository", "Could not delete appIcon into database : " + mAppInfo.getPackageName(), e);
            } finally {
                db.endTransaction();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(null!=this.mOnTaskCompletedListener){
                this.mOnTaskCompletedListener.onTaskCompleted();
            }
        }
    }


    interface OnTaskCompletedListener {
        void onTaskCompleted();
    }


}
