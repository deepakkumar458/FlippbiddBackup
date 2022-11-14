package com.flippbidd.CommonClass;


import androidx.fragment.app.FragmentActivity;

import com.flippbidd.Model.ModelDrawerLeft;
import com.flippbidd.R;
import com.flippbidd.interfaces.IfaceCommon;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stratecore on 17/11/16.
 */
public class ImplCommon implements IfaceCommon {

    public List abstract_drawerleft(FragmentActivity activity) {
/*Home, My Profile, My calendar, find my deal, My Uploads, Flippbidd Alerts, My likes, Notification Filter, Settings, Logout*/
        ModelDrawerLeft modelDrawerLeft;
        List al_drawer = new ArrayList<>();
        al_drawer.add(new ModelDrawerLeft(activity.getResources().getString(R.string.String_title_home), "#FFFFFF", "#FBAF41", "home", "home"));
        al_drawer.add(new ModelDrawerLeft(activity.getResources().getString(R.string.String_title_myprofile), "#989898", "#FBAF41", "my_profile", "my_profile"));
        al_drawer.add(new ModelDrawerLeft(activity.getResources().getString(R.string.String_title_my_calender), "#989898", "#FBAF41", "ic_calendar", "ic_calendar"));
        al_drawer.add(new ModelDrawerLeft(activity.getResources().getString(R.string.String_title_findmydeal), "#989898", "#FBAF41", "ic_fmd", "ic_fmd"));
        al_drawer.add(new ModelDrawerLeft(activity.getResources().getString(R.string.String_title_myuploaded), "#989898", "#FBAF41", "buy_sell", "buy_sell"));
//        al_drawer.add(new ModelDrawerLeft(activity.getResources().getString(R.string.String_title_inbox), "#989898.", "#FBAF41", "inbox", "inbox"));
        al_drawer.add(new ModelDrawerLeft(activity.getResources().getString(R.string.String_title_notification), "#989898.", "#FBAF41", "notifications", "notifications"));
        al_drawer.add(new ModelDrawerLeft(activity.getResources().getString(R.string.String_title_mylikes), "#989898", "#FBAF41", "my_likes", "my_likes"));
        al_drawer.add(new ModelDrawerLeft(activity.getResources().getString(R.string.String_title_createfilter), "#989898", "#FBAF41", "create_filters", "create_filters"));

//        al_drawer.add(new ModelDrawerLeft(activity.getResources().getString(R.string.String_title_rental), "#989898", "#FBAF41", "rentals", "rentals"));
//        al_drawer.add(new ModelDrawerLeft(activity.getResources().getString(R.string.String_title_home_services), "#989898", "#FBAF41", "ic_service", "ic_service"));
//        al_drawer.add(new ModelDrawerLeft(activity.getResources().getString(R.string.String_title_purchase), "#989898.", "#FBAF41", "purchase", "purchase"));
        al_drawer.add(new ModelDrawerLeft(activity.getResources().getString(R.string.String_title_setting), "#989898", "#FBAF41", "ic_settings", "ic_settings"));
        al_drawer.add(new ModelDrawerLeft(activity.getResources().getString(R.string.String_title_logout), "#989898", "#FBAF41", "ic_logout", "ic_logout"));
        return al_drawer;
    }

}