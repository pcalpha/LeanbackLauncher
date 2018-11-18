package cn.com.pcalpha.leanbacklauncher.apps;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.com.pcalpha.leanbacklauncher.R;

public class AppFragment extends Fragment {

    private Context mContext;

    private RecyclerView mAppListView;
    private AppAdapter mAppAdapter;
    private boolean mEditMode = false;//编辑模式
    private static final int spanCount=5;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.app_fragment, container, false);


        mAppListView = (RecyclerView) view.findViewById(R.id.app_list_view);

        final GridLayoutManager mLayoutManager = new GridLayoutManager(mContext, spanCount);
        mLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        //mLayoutManager.setAutoMeasureEnabled(true);

        mAppListView.setLayoutManager(mLayoutManager);

//        mAppListView.addItemDecoration(new SpaceItemDecoration(
//                DensityConverter.dp2px(mContext, 10),
//                DensityConverter.dp2px(mContext, 10)));


        mAppAdapter = new AppAdapter(mContext);
//        mAppAdapter.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                mAppListView.setBackgroundColor(Color.argb(128, 0, 0, 0));
//                mEditMode = true;
//                return true;
//            }
//        });

        mAppAdapter.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (mEditMode) {
                    int selectPosition = mAppListView.getChildAdapterPosition(mLayoutManager.getFocusedChild());
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                            int toPosition = selectPosition + 1;
                            mAppAdapter.moveLaunchItems(selectPosition, toPosition);
                            return true;
                        }
                        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                            int toPosition = selectPosition - 1;
                            mAppAdapter.moveLaunchItems(selectPosition, toPosition);
                            return true;
                        }
                        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                            int toPosition = selectPosition + spanCount;
                            mAppAdapter.moveLaunchItems(selectPosition, toPosition);
                            return true;
                        }
                        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                            int toPosition = selectPosition - spanCount;
                            mAppAdapter.moveLaunchItems(selectPosition, toPosition);
                            return true;
                        }
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            mAppListView.setBackgroundColor(Color.argb(0, 0, 0, 0));
                            mEditMode = false;
                            return true;
                        }
                    }
                } else {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        if (keyCode == KeyEvent.KEYCODE_MENU) {
                            mContext.startActivity(new Intent("android.settings.SETTINGS"));
                            return true;
                        }
                    }
                }
                return false;
            }
        });

        mAppListView.setAdapter(mAppAdapter);
        return view;
    }




}
