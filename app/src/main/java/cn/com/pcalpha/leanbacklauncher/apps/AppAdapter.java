package cn.com.pcalpha.leanbacklauncher.apps;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import cn.com.pcalpha.leanbacklauncher.R;
import cn.com.pcalpha.leanbacklauncher.utils.DensityConverter;
import cn.com.pcalpha.leanbacklauncher.utils.DrawableConverter;

/**
 * Created by caiyida on 2018/4/7.
 */

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.AppHolder> {

    private Context mContext;
    private DrawableConverter drawableConverter;
    private DensityConverter densityConverter;
    private ViewGroup mViewGroup;
    private LayoutInflater mLayoutInflater;

    private List<AppInfo> appList;
    private static List<String> blackList = new ArrayList<>();



    private View.OnClickListener onClickListener;
    private View.OnLongClickListener onLongClickListener;
    private View.OnKeyListener onKeyListener;

    static {
        blackList.add("信息");
        blackList.add("设置");
        blackList.add("TVLauncher");
    }

    public AppAdapter(Context context) {
        this.mContext = context;
        drawableConverter = new DrawableConverter(mContext);
        densityConverter = new DensityConverter(mContext);
        mLayoutInflater = LayoutInflater.from(context);

        //this.appList.add(0,);
        this.appList = this.getAppList();
//        for (int i = 0; i < 10; i++) {
//            AppInfo appInfo = new AppInfo();
//            appInfo.setName("a" + i);
//            appInfo.setLeanback(false);
//            appInfo.setIcon(d);
//            appList.add(appInfo);
//        }
        //ItemTouchHelper

    }

    @Override
    public AppAdapter.AppHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mViewGroup = parent;
        View view = mLayoutInflater.inflate(R.layout.app_item, parent, false);
        final AppHolder appHolder = new AppHolder(view);
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.setTranslationZ(10);
        view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.setTranslationZ(99);
                    ViewCompat.animate(appHolder.itemView)
                            .scaleX(1.2f)
                            .scaleY(1.2f)
                            //.translationZ(99)
                            .start();
                } else {
                    v.setTranslationZ(10);
                    ViewCompat.animate(appHolder.itemView)
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            //.translationZ(10)
                            .start();
                }
            }
        });
//        view.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                Toast.makeText(mContext, "long click", Toast.LENGTH_LONG).show();
//
//                return true;
//            }
//        });

        //view.setOnLongClickListener(onLongClickListener);
        view.setOnKeyListener(onKeyListener);
        return appHolder;//将布局设置给holder
    }

    @Override
    public void onBindViewHolder(AppAdapter.AppHolder holder, final int position) {
        if (holder instanceof AppHolder) {
            final AppInfo appModel = appList.get(position);
            AppHolder appHolder = (AppHolder) holder;

            boolean leanback = (
                    appModel.getIcon().getIntrinsicWidth()/
                    (appModel.getIcon().getIntrinsicHeight()*1f))>
                    1.76;
            if (leanback) {
                appHolder.appName.setText(appModel.getName());

                //// set padding
                appHolder.appIcon.setPadding(0, 0, 0, 0);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                params.gravity = Gravity.CENTER;
                appHolder.appIconImg.setLayoutParams(params);
                appHolder.appIconImg.setImageDrawable(appModel.getIcon());
                appHolder.appIconImg.setOutlineProvider(null);
                appHolder.appIconImg.setClipToOutline(false);
            } else {
                appHolder.appName.setText(appModel.getName());
                // set padding
                int padding = densityConverter.dp2px(6);
                appHolder.appIcon.setPadding(padding, padding, padding, padding);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        densityConverter.dp2px(60),
                        densityConverter.dp2px(60));
                params.gravity = Gravity.CENTER;
                appHolder.appIconImg.setLayoutParams(params);
                appHolder.appIconImg.setImageDrawable(appModel.getIcon());
                appHolder.appIconImg.setOutlineProvider(mAppIconImgOutline);
                appHolder.appIconImg.setClipToOutline(true);



                //get suitable background
                Bitmap bitmap = drawableConverter.drawableToBitmap(appModel.getIcon());
                Palette palette = Palette.from(bitmap).generate();
                //int back=palette.getDarkMutedColor(Color.WHITE);
                //int back=palette.getDarkVibrantColor(Color.WHITE);
                //int back=palette.getLightMutedColor(Color.WHITE);
                //int back=palette.getVibrantColor(Color.WHITE);
                //int back= palette.getMutedColor(Color.WHITE);
                int back=palette.getLightVibrantColor(Color.WHITE);
                appHolder.appIcon.setBackgroundColor(back);
            }
            appHolder.appIcon.setOutlineProvider(mAppIconOutline);
            appHolder.appIcon.setClipToOutline(true);

            appHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent launchIntent = mContext.getPackageManager().getLaunchIntentForPackage(appModel.getPackageName());
                    if (launchIntent != null) {
                        mContext.startActivity(launchIntent);
                    }
                }
            });

            appHolder.itemView.setOnKeyListener(onKeyListener);
        }
    }

    ViewOutlineProvider mAppIconImgOutline = new ViewOutlineProvider() {
        @Override
        public void getOutline(View view, Outline outline) {
            outline.setRoundRect(new Rect(0, 0, view.getWidth(), view.getHeight()), 40);
        }
    };

    ViewOutlineProvider mAppIconOutline = new ViewOutlineProvider() {
        @Override
        public void getOutline(View view, Outline outline) {
            outline.setRoundRect(new Rect(0, 0, view.getWidth(), view.getHeight()), 20);
        }
    };


    public void onItemMove(int fromPosition, int toPosition) {
        if (toPosition < 0 || toPosition >= appList.size()) {
            return;
        }
        notifyItemMoved(fromPosition, toPosition);
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(appList, i, i + 1);
            }
        } else if (fromPosition > toPosition) {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(appList, i, i - 1);
            }
        }
        //Collections.swap(appList, fromPosition, toPosition);

    }

    public void moveLaunchItems(int from, int to) {
        int offset = 1;
        if (from >= 0 && from <= this.appList.size() - 1 && to >= 0 && to <= this.appList.size() - 1) {
            AppInfo fromItem = (AppInfo) this.appList.get(from);
            this.appList.set(from, (AppInfo) this.appList.get(to));
            this.appList.set(to, fromItem);
            notifyItemMoved(from, to);
            int positionDifference = to - from;
            if (Math.abs(positionDifference) > 1) {
                if (positionDifference > 0) {
                    offset = -1;
                }
                notifyItemMoved(to + offset, from);
            }
//            switch (direction) {
//                case 17:
//                    eventCode = TvLauncherEventCode.MOVE_LAUNCH_ITEM_LEFT;
//                    break;
//                case 33:
//                    eventCode = TvLauncherEventCode.MOVE_LAUNCH_ITEM_UP;
//                    break;
//                case 66:
//                    eventCode = TvLauncherEventCode.MOVE_LAUNCH_ITEM_RIGHT;
//                    break;
//                case 130:
//                    eventCode = TvLauncherEventCode.MOVE_LAUNCH_ITEM_DOWN;
//                    break;
//                default:
//                    throw new IllegalArgumentException("Invalid direction: " + direction);
//            }
//            LogEvent logEvent = new LogEvent(eventCode).setVisualElementTag(TvLauncherConstants.LAUNCH_ITEM).setVisualElementRowIndex(to / 4).setVisualElementIndex(to % 4);
//            logEvent.getApplication().setPackageName(fromItem.getPackageName());
//            logEvent.pushParentVisualElementTag(fromItem.isGame() ? TvLauncherConstants.GAMES_CONTAINER : TvLauncherConstants.APPS_CONTAINER);
//            this.mEventLogger.log(logEvent);
        }
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }


    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    public void setOnKeyListener(View.OnKeyListener onKeyListener) {
        this.onKeyListener = onKeyListener;
    }

    public boolean isLeanback(String pkgName) {
        if (Build.VERSION.SDK_INT < 21) {
            return false;
        }
        PackageManager localPackageManager = mContext.getPackageManager();
        if (null == localPackageManager.getLeanbackLaunchIntentForPackage(pkgName)) {
            return false;
        } else {
            return true;
        }
    }

    public List<AppInfo> getAppList() {
        PackageManager localPackageManager = mContext.getPackageManager();
        Intent localIntent = new Intent("android.intent.action.MAIN");
        localIntent.addCategory("android.intent.category.LAUNCHER");
        List<ResolveInfo> localList = localPackageManager.queryIntentActivities(localIntent, 0);


        ArrayList<AppInfo> localArrayList = new ArrayList<>();
        Iterator<ResolveInfo> localIterator = localList.iterator();
        while (localIterator.hasNext()) {
            ResolveInfo localResolveInfo = (ResolveInfo) localIterator.next();

            String appName = localResolveInfo.activityInfo.loadLabel(localPackageManager).toString();

            AppInfo localAppBean = new AppInfo();
            localAppBean.setIcon(localResolveInfo.activityInfo.loadIcon(localPackageManager));
            localAppBean.setName(appName);
            localAppBean.setPackageName(localResolveInfo.activityInfo.packageName);
            //localAppBean.setDataDir(localResolveInfo.activityInfo.applicationInfo.publicSourceDir);
            //localAppBean.setLauncherName(localResolveInfo.activityInfo.name);
            localAppBean.setLeanback(isLeanback(localAppBean.getPackageName()));
            String pkgName = localResolveInfo.activityInfo.packageName;
            PackageInfo mPackageInfo;
            try {
                mPackageInfo = mContext.getPackageManager().getPackageInfo(pkgName, 0);
                if ((mPackageInfo.applicationInfo.flags & mPackageInfo.applicationInfo.FLAG_SYSTEM) > 0) {// 系统预装
                    localAppBean.setSys(true);
                    //continue;
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            if (blackList.contains(localAppBean.getName())) {
                continue;
            }
            localArrayList.add(localAppBean);
        }
        return localArrayList;
    }

    class AppHolder extends RecyclerView.ViewHolder {
        TextView appName;
        ImageView appIconImg;
        View appIcon;

        public AppHolder(View itemView) {
            super(itemView);
            appIcon = (View) itemView.findViewById(R.id.app_icon);
            appName = (TextView) itemView.findViewById(R.id.app_name);
            appIconImg = (ImageView) itemView.findViewById(R.id.app_icon_img);


//            itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
//                @Override
//                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//                    //创建弹出式菜单对象（最低版本11）
//                    menu.clearHeader();
//                    PopupMenu popup = new PopupMenu(v.getContext(), appIconImg);//第二个参数是绑定的那个view
//                    //获取菜单填充器
//                    MenuInflater inflater = popup.getMenuInflater();
//                    //填充菜单
//                    inflater.inflate(R.menu.app_menu, popup.getMenu());
//                    //绑定菜单项的点击事件
//                    //popup.setOnMenuItemClickListener(this);
//                    //显示(这一行代码不要忘记了)
//                    popup.show();
//                }
//            });


            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(mContext, "long click", Toast.LENGTH_LONG).show();

                    return true;
                }
            });




                    itemView.setOnContextClickListener(new View.OnContextClickListener() {
                @Override
                public boolean onContextClick(View v) {
                    return false;
                }
            });



//            itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
//                @Override
//                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//                    MenuItem delete = menu.add(Menu.CATEGORY_CONTAINER, 1, 1, "删除");
//                    MenuItem delete2 = menu.add(Menu.CATEGORY_CONTAINER, 2, 2, "删除2");
//                }
//            });

            //leanBackImageView = (ImageView) itemView.findViewById(R.id.leanback_app_icon);
        }
    }
}
