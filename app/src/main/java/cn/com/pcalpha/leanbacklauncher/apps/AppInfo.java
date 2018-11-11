package cn.com.pcalpha.leanbacklauncher.apps;

import android.graphics.drawable.Drawable;

/**
 * Created by caiyida on 2018/4/7.
 */

public class AppInfo implements Comparable<AppInfo>{
    private String id;
    private String name;
    private String packageName;
    private Drawable icon;
    private int position;
    private boolean isSys;
    private boolean isLeanback;

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isSys() {
        return isSys;
    }

    public void setSys(boolean sys) {
        isSys = sys;
    }

    public boolean isLeanback() {
        return isLeanback;
    }

    public void setLeanback(boolean leanback) {
        isLeanback = leanback;
    }


    @Override
    public int compareTo(AppInfo o) {
        if(null==o){
            return -1;
        }
        if(o.getPosition()>this.getPosition()){
            return 1;
        }else if(o.getPosition()==this.getPosition()){
            return 0;
        }else{
            return -1;
        }
    }
}
