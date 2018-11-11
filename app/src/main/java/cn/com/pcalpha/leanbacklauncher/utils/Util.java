package cn.com.pcalpha.leanbacklauncher.utils;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.AudioManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;

import cn.com.pcalpha.leanbacklauncher.R;

public class Util {
    public static void clearWidget(Context paramContext) {
        PreferenceManager.getDefaultSharedPreferences(paramContext).edit().remove("widget_id").remove("widget_component_name").apply();
    }

    public static DisplayMetrics getDisplayMetrics(Context paramContext) {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        ((WindowManager) paramContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(localDisplayMetrics);
        return localDisplayMetrics;
    }

    public static long getInstallTimeForPackage(Context context, String pkgName) {
        PackageManager pkgMan = context.getPackageManager();
        if (pkgMan != null) {
            try {
                PackageInfo info = pkgMan.getPackageInfo(pkgName, 0);
                if (info != null) {
                    return info.firstInstallTime;
                }
            } catch (NameNotFoundException e) {
            }
        }
        Log.v("LeanbackLauncher", "Couldn't find install time for " + pkgName + " assuming it's right now");
        return System.currentTimeMillis();
    }


    public static Intent getSearchIntent() {
        return new Intent("android.intent.action.ASSIST").addFlags(Intent.FLAG_RECEIVER_VISIBLE_TO_INSTANT_APPS);
    }

    public static Bitmap getSizeCappedBitmap(Bitmap image, int maxWidth, int maxHeight) {
        if (image == null) {
            return null;
        }
        int imgWidth = image.getWidth();
        int imgHeight = image.getHeight();
        if ((imgWidth <= maxWidth && imgHeight <= maxHeight) || imgWidth <= 0 || imgHeight <= 0) {
            return image;
        }
        float scale = Math.min(1.0f, ((float) maxHeight) / ((float) imgHeight));
        if (((double) scale) >= 1.0d && imgWidth <= maxWidth) {
            return image;
        }
        float deltaW = ((float) Math.max(((int) (((float) imgWidth) * scale)) - maxWidth, 0)) / scale;
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap newImage = Bitmap.createBitmap(image, (int) (deltaW / 2.0f), 0, (int) (((float) imgWidth) - deltaW), imgHeight, matrix, true);
        if (newImage != null) {
            return newImage;
        }
        return image;
    }

    public static ComponentName getWidgetComponentName(Context ctx) {
        String name = PreferenceManager.getDefaultSharedPreferences(ctx).getString("widget_component_name", null);
        if (TextUtils.isEmpty(name)) {
            return null;
        }
        return ComponentName.unflattenFromString(name);
    }

    public static int getWidgetId(Context paramContext) {
        return PreferenceManager.getDefaultSharedPreferences(paramContext).getInt("widget_id", 0);
    }

    public static boolean initialRankingApplied(Context paramContext) {
        return PreferenceManager.getDefaultSharedPreferences(paramContext).getBoolean("launcher_oob_ranking_marker", false);
    }

    public static final boolean isConfirmKey(int keyCode) {
        switch (keyCode) {
            case 23:
            case 62:
            case 66:
            case 96:
            case 160:
                return true;
            default:
                return false;
        }
    }

    public static boolean isContentUri(Uri paramUri) {
        return ("content".equals(paramUri.getScheme())) || ("file".equals(paramUri.getScheme()));
    }

    public static boolean isContentUri(String paramString) {
        if (TextUtils.isEmpty(paramString)) {
            return false;
        }
        return isContentUri(Uri.parse(paramString));
    }

    public static boolean isInTouchExploration(Context context) {
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        return am.isEnabled() && am.isTouchExplorationEnabled();
    }

    public static boolean isPackagePresent(PackageManager pkgMan, String packageName) {
        try {
            if (pkgMan.getApplicationInfo(packageName, 0) != null) {
                return true;
            }
            return false;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public static boolean isSystemApp(Context context, String packageName) {
        try {
            if ((context.getPackageManager().getApplicationInfo(packageName, 0).flags & 1) != 0) {
                return true;
            }
            return false;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public static void playErrorSound(Context paramContext) {
        ((AudioManager) paramContext.getSystemService(Context.AUDIO_SERVICE)).playSoundEffect(9);
    }

    public static void setInitialRankingAppliedFlag(Context paramContext, boolean paramBoolean) {
        PreferenceManager.getDefaultSharedPreferences(paramContext).edit().putBoolean("launcher_oob_ranking_marker", paramBoolean).apply();
    }

    public static void setWidget(Context paramContext, int paramInt, ComponentName paramComponentName) {
        if ((paramInt == 0) || (paramComponentName == null)) {
            clearWidget(paramContext);
            return;
        }
        PreferenceManager.getDefaultSharedPreferences(paramContext).edit().putInt("widget_id", paramInt).putString("widget_component_name", paramComponentName.flattenToString()).apply();
    }

    public static void startActivity(Context context, PendingIntent intent) throws SendIntentException {
        context.startIntentSender(intent.getIntentSender(), null, Intent.FLAG_RECEIVER_FOREGROUND, Intent.FLAG_RECEIVER_FOREGROUND, 0);
    }

    private static boolean startActivitySafely(Context context, Intent intent) {
        try {
            context.startActivity(intent);
            return true;
        } catch (Throwable t) {
            Log.e("LeanbackLauncher", "Could not launch intent", t);
            //Toast.makeText(context, R.string.failed_launch, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public static boolean startSearchActivitySafely(Context context, Intent intent, int deviceId, boolean isKeyboardSearch) {
        intent.putExtra("android.intent.extra.ASSIST_INPUT_DEVICE_ID", deviceId);
        intent.putExtra("search_type", isKeyboardSearch ? 2 : 1);
        return startActivitySafely(context, intent);
    }

    public static boolean startSearchActivitySafely(Context context, Intent intent, boolean isKeyboardSearch) {
        intent.putExtra("search_type", isKeyboardSearch ? 2 : 1);
        return startActivitySafely(context, intent);
    }

    public static void main(String[] args) {
       boolean x =(320/(180*1f))==(16/9f);
        System.out.println(x);
    }
}
