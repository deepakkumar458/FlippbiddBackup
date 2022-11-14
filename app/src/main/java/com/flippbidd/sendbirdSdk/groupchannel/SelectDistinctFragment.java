package com.flippbidd.sendbirdSdk.groupchannel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;


import com.flippbidd.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


/**
 * A fragment displaying an option to set the channel as Distinct.
 */
public class SelectDistinctFragment extends Fragment {

    private CheckBox mCheckBox;
    private DistinctSelectedListener mListener;

    interface DistinctSelectedListener {
        void onDistinctSelected(boolean distinct);
    }

    static SelectDistinctFragment newInstance() {
        return new SelectDistinctFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_select_distinct, container, false);

        ((NewChatCreateActivity) getActivity()).setState(NewChatCreateActivity.STATE_SELECT_DISTINCT);

        mListener = (NewChatCreateActivity) getActivity();

        mCheckBox = (CheckBox) rootView.findViewById(R.id.checkbox_select_distinct);
        mCheckBox.setChecked(true);
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mListener.onDistinctSelected(isChecked);
            }
        });

        return rootView;
    }
}
