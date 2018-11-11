package cn.com.pcalpha.leanbacklauncher.utils;

import android.content.ContentValues;
import android.content.Context;
import android.util.TypedValue;

/**
 * 常用单位转换的辅助类
 */
public class DensityConverter {
    private Context mContext;

    public DensityConverter(Context context) {
        this.mContext = context;
    }

    /**
     * dp转px
     *
     * @param dpVal
     * @return
     */
    public int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, mContext.getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     *
     * @param spVal
     * @return
     */
    public int sp2px(float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, mContext.getResources().getDisplayMetrics());
    }

    /**
     * px转dp
     *
     * @param pxVal
     * @return
     */
    public float px2dp(float pxVal) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (pxVal / scale);
    }

    /**
     * px转sp
     *
     * @param pxVal
     * @return
     */
    public float px2sp(float pxVal) {
        return (pxVal / mContext.getResources().getDisplayMetrics().scaledDensity);
    }
}