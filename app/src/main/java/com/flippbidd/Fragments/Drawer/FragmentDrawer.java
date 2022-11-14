package com.flippbidd.Fragments.Drawer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.flippbidd.Adapter.DrawerAdapter;
import com.flippbidd.CommonClass.ImplCommon;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Model.Response.CommonList.CommonData;
import com.flippbidd.Model.Response.UserDetails;
import com.flippbidd.Others.Constants;
import com.flippbidd.Others.UserPreference;
import com.flippbidd.R;
import com.flippbidd.activity.Property.PostNewProperty;
import com.flippbidd.activity.inapppurchase.InAppPurchaseActivity;
import com.flippbidd.baseclass.BaseAppCompatActivity;
import com.flippbidd.baseclass.BaseFragment;
import com.flippbidd.interfaces.IfaceCommon;
import com.flippbidd.utils.PreferenceUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;


/**
 * Created by Stratecore on 2/24/2017.
 */

public class FragmentDrawer extends BaseFragment {

    private static String TAG = FragmentDrawer.class.getSimpleName();

    private ActionBarDrawerToggle mDrawerToggle;
    public static DrawerLayout mDrawerLayout;
    public static DrawerAdapter adapter;
    public static View containerView;
    private FragmentDrawerListener drawerListener;
    UserDetails mUserDetails;


    @BindView(R.id.drawerList)
    RecyclerView recyclerView;
    @BindView(R.id.relativeSubmitProperty)
    RelativeLayout relativeSubmitProperty;

    CustomTextView tvUpgradeToProLic;

    public static int status_drawer;

    private IfaceCommon ifaceCommon = new ImplCommon();

    public FragmentDrawer() {
    }

    public void setDrawerListener(FragmentDrawerListener listener) {
        this.drawerListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerLocalBroadcast();
        tvUpgradeToProLic = view.findViewById(R.id.tvUpgradeToProLic);
        mUserDetails = UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail();
        adapter = new DrawerAdapter(getActivity(), ifaceCommon.abstract_drawerleft(mBaseAppCompatActivity));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter.setSelected(0);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                drawerListener.onDrawerItemSelected(view, position);
                mDrawerLayout.closeDrawer(containerView);

                adapter.setOnItemClickLister(new DrawerAdapter.OnItemSelecteListener() {
                    @Override
                    public void onItemSelected(View v, int position) {
                        adapter.setSelected(position);
                    }
                });

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        relativeSubmitProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    CommonData mPropertyData = new CommonData();
                    ((BaseAppCompatActivity) mBaseAppCompatActivity).startActivityIfNeeded(PostNewProperty.getIntentActivity(mBaseAppCompatActivity, mPropertyData, Constants.SCREEN_ACTION_TYPE.ADD), Constants.REQUEST_UPLOAD_FUND, true);
                    mDrawerLayout.closeDrawer(containerView);
                } catch (Exception e) {

                }
            }
        });

        //check plan status
        if (PreferenceUtils.getPlanVersionStatus()) {
            tvUpgradeToProLic.setVisibility(View.GONE);
        } else {
            tvUpgradeToProLic.setVisibility(View.VISIBLE);
        }
        tvUpgradeToProLic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawer(containerView);
                //open in app activity
                getActivity().startActivityIfNeeded(new Intent(getActivity(), InAppPurchaseActivity.class), 0);
            }
        });
    }


    public void notifiyData() {
        adapter.notifyDataSetChanged();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_drawer;
    }


    public void setUp(int fragmentId, final DrawerLayout drawerLayout, final Toolbar toolbar, final LinearLayout len_drawer_view) {

        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerLayout.setScrimColor(Color.TRANSPARENT);


        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                containerView.setClickable(true);
                getActivity().invalidateOptionsMenu();
                status_drawer = 1;
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                containerView.setClickable(false);
                getActivity().invalidateOptionsMenu();
                status_drawer = 1;
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                float slideX = drawerView.getWidth() * slideOffset;
//                len_drawer_view.setTranslationX(slideOffset * drawerView.getWidth());
                len_drawer_view.setTranslationX(slideX);
//                len_drawer_view.setScaleX(1 - slideOffset);
//                len_drawer_view.setScaleY(1 - slideOffset);

                mDrawerLayout.bringChildToFront(drawerView);
                mDrawerLayout.requestLayout();

            }

        };
        mDrawerLayout.setScrimColor(Color.TRANSPARENT);
        mDrawerLayout.setDrawerElevation(0f);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
//        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }


    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    /*
     * On Tuch Fragment Drawer
     */
    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public interface FragmentDrawerListener {
        void onDrawerItemSelected(View view, int position);
    }

    public void registerLocalBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.UPDATE_COUNTS);
        intentFilter.addAction(Constants.UPDATE_PLAN_STATUS);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                broadcastReceiver,
                intentFilter
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (adapter != null) {
                if (intent.hasExtra(Constants.COUNTS_DATA))
                    adapter.updateCounts(intent.getStringExtra(Constants.COUNTS_DATA));

                if (intent.hasExtra(Constants.PLAN_STATUS)) {
                    boolean values = intent.getBooleanExtra(Constants.PLAN_STATUS, false);
                    if (values) {
                        tvUpgradeToProLic.setVisibility(View.GONE);
                    } else {
                        tvUpgradeToProLic.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    };
}
