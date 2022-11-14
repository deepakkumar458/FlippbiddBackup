package com.flippbidd.Bottoms;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;


import com.flippbidd.Adapter.BottomSheetCommonListAdapter;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.R;
import com.flippbidd.baseclass.BaseAppCompatActivity;
import com.flippbidd.baseclass.BaseBottomSheetDialogFragment;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

public class CommonListBottomSheet extends BaseBottomSheetDialogFragment implements BottomSheetCommonListAdapter.OnClickListener {


    Context mContext;
    DailogListener dailogListener;
    @BindView(R.id.recyclerviewAddCollection)
    RecyclerView recyclerviewAddCollection;
    @BindView(R.id.textTitle)
    CustomTextView textTitle;

    LinearLayoutManager linearLayoutManager;
    BottomSheetCommonListAdapter mBottomSheetCommanListAdapter;
    List<String> mListData;

    public static CommonListBottomSheet newInstance() {
        return new CommonListBottomSheet();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        textTitle.setVisibility(View.GONE);
        mBottomSheetCommanListAdapter = new BottomSheetCommonListAdapter(getActivity());
        //set layout manager
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerviewAddCollection.setLayoutManager(linearLayoutManager);
        recyclerviewAddCollection.setItemAnimator(new DefaultItemAnimator());
        //set data in adapter
        recyclerviewAddCollection.setAdapter(mBottomSheetCommanListAdapter);

        //add data
        if (mBottomSheetCommanListAdapter != null)
            mBottomSheetCommanListAdapter.addAll(mListData);

        //click init
        if (mBottomSheetCommanListAdapter != null)
            mBottomSheetCommanListAdapter.OnClickListener(this);
    }
    @OnClick(R.id.tv_menu_bottom_sheet_cancel)
    void onCancelClick() {
        if (dailogListener != null) {
            dailogListener.onSaveCollection(-1, "Cancel");
        }
    }

    @Override
    public int getLayoutResource() {
        return R.layout.add_collection_bottom_sheet_layout;
    }

    public void addListener(Context mBaseAppCompatActivity, DailogListener dailog, List<String> mListArr) {
        this.dailogListener = dailog;
        this.mContext = mBaseAppCompatActivity;
        this.mListData = mListArr;
    }

    @Override
    public void OnClickListener(int mPosition, String actionType) {
        if (dailogListener != null) {
            dailogListener.onSaveCollection(mPosition, actionType);
        }
    }

    public interface DailogListener {
        void onSaveCollection(int mPosition, String actionType);
    }

}