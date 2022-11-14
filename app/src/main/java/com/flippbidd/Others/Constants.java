package com.flippbidd.Others;


import com.github.tamir7.contacts.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stratecore on 14/09/16.
 */


public class Constants {


    /* Font List*/
    public static final int requestSinglechat = 0;


    public static String vl_fullname = "Please enter Full Name.";
    public static String vl_username = "Please enter Username.";
    public static String vl_confirmpassword = "Please enter Confirm Password.";
    public static String vl_passwordnomatch = "Password and Confirm Password does not match.";
    public static String val_tremsandcondistion = "Please agree to the Terms & Conditions.";
    public static String val_tremsandcondistionprivacypolicy = "Please agree to the Terms & Conditions and Privacy Policy.";


    public static String vl_email = "Please enter Email Address.";
    public static String vl_invalid_email = "Please enter valid Email Address.";
    public static String vl_password = "Please enter Password.";
    public static String vl_mobile_number = "Please enter Mobile Number.";
    public static String vl_mobile_number_length = "Please enter valid 10-digit phone number";
    public static String vl_old_password = "Please enter Old Password.";
    public static String vl_new_password = "Please enter New Password.";
    public static String vl_confirm_password = "Please enter Confirm Password.";


    public static String ALREADY_BIDD_ALERT = "You have already placed a Bidd.";
    public static String NOT_UPLOAD_POF = "You have not uploaded a Proof of Funds into your profile.This may delay the Request Contact process.";
    public static String ENTER_PRICE = "Please enter price";
    public static String CHOSE_CONTRACT_VIA_OPTION = "Please select anyone option for request contract via.";

    /*property add*/
    public static String SELECT_PROPERTY_TYPE = "Please select anyone option for Property Type.";
    public static String SELECT_NUB_OF_BAD = "Please select anyone option for Number Of Beds.";
    public static String SELECT_NUB_OF_BATH = "Please select anyone option for Number Of Baths.";
    public static String ENTER_TITLE = "Please enter Title.";
    public static String ENTER_ADDRESS = "Please enter Address.";
    public static String ENTER_AREA = "Please enter Area.";
    public static String ENTER_CODE = "Please enter Area Code.";
    public static String ENTER_DESCRIPCTION = "Please enter Description.";

    //new feature
    public static String ENTER_PAY_OFF_AMOUNT = "Please enter PayOffAmount.";
    public static String ENTER_NOTE_BALANCE = "Please enter Note Balance.";
    public static String ENTER_SURRENDER_AGREEMENT = "Please enter Surrender Agreement.";
    public static String ENTER_FLIPPBIDD_SERVICE = "Please enter FlippBidd Services.";
    public static String ENTER_MUST_CLOSE_DATE = "Please select must close by date.";
    public static String ENTER_REFERRED_BY = "Please enter referred by.";
    public static String ENTER_INVESTOR_NAME = "Please enter investor name.";
    public static String ENTER_CAPITAL_ON_HAND = "Please enter capital on hand.";
    public static String ENTER_ATTORNEY_NAME = "Please enter attorney name.";
    public static String ENTER_ASSET_TYPE = "Please enter asset type.";
    public static String ENTER_ROI = "Please select ROI.";
    //end new feature


    /*rating*/
    public static String ENTER_COMMENT = "Please enter comments.";
    /*report content*/
    public static String SELECT_REPORT_TYPE = "Please select anyone option for Report Type.";
    /*request property*/
    public static String ENTER_CONTACT_NAME = "Please enter Contact Name";
    public static String ENTER_CONTACT_NUMBER = "Please enter Contact Phone Number";
    public static String ENTER_PROPERTY_ADDRESS = "Please enter Property Address";

    /*Upload doc and file*/
    public static String ENTER_DOC_NAME = "Please enter Document Type Name";
    public static String ENTER_LINK = "Please enter Link";
    public static String ENTER_VALID_LINK = "Please enter Valid Link";

    /*meeting schedule*/
    public static String SELECT_SCHEDULE_TYPE = "Please select anyone option for meeting schedule type";
    public static String SELECT_DATE = "Please select any date";
    public static String SELECT_STIME = "Please select start time ";
    public static String SELECT_ETIME = "Please select end time";
    public static String SELECT_DATE_ERROR = "Please select date after current date";


    public static int TRENDING_COUNTS = 10;
    public static int REQUEST_UNAVILABLE = 177;
    public static int REQUEST_UPLOAD_FUND = 1009;

    public static List<Contact> contactsList = new ArrayList<>();

    public interface TABS {
        int TAB_0 = 0;
        int TAB_1 = 1;
        int TAB_2 = 2;
    }

    public static final String BIDD_ACCEPT = "Bidd Accepted";
    public static final String BIDD_REJECT = "Bidd Rejected";
    public static final String BIDD_COUNTERED = "Bidd Countered";

    public static final String REQUEST_ACCEPT = "Request Accepted";
    public static final String REQUEST_REJECT = "Request Rejected";
    public static final String WATING_USER_REQUEST = "Waiting for admin approval";
    public static final String WATING_ADMIN_REQUEST = "Waiting for user's Approval";
    public static final String NEW_REQUEST = "Requested New Time";
    public static final String ACTION_BOTTOM_SHEET_OWNER = "open bottom sheet owner";
    public static final String ACTION_BOTTOM_SHEET_USER = "open bottom sheet user";

    public static final String ACCEPT = "Accept";
    public static final String REJECT = "Reject";
    public static final String NEWTIME = "New Time";
    public static final String COUNTERED = "Countered";

    public static final String UPDATE_PLAN_STATUS = "UPDATE_PLAN_STATUS";
    public static final String UPDATE_COUNTS = "UPDATE_COUNTS";
    public static final String COUNTS_DATA = "COUNTS_DATA";
    public static final String PLAN_STATUS = "PLAN_STATUS";


    public interface USER_TYPE {
        String USER_FREE = "Free User";
        String USER_FULL = "Full User";
        String USER_PRO = "Pro User";
    }

    public interface USER_PREFERENCES {
        String USER_PREFERENCE = "user_preference";
        String IS_USER_LOGIN = "is_user_login";
        String CONSTANT_FCM_TOKEN = "fcm_token";
        String USER_DETAILS = "user_details";
        String LATITUDE = "latitude";
        String LONGITUDE = "longitude";
        String RATE_APP = "rateapp";
        String SHARE_APP = "shareapp";
        String CREDITE_COUNT = "creditecount";
        String AUTH = "auth";
        String MOBILE_NUMBER = "MOBILE_NUMBER";
        String C_CODE = "C_CODE";
        String CC_CODE = "CC_CODE";
        String INTRO_1 = "intro_1";
        String INTRO_2 = "intro_2";
        String INTRO_3 = "intro_3";
        String INTRO_4 = "intro_4";
        String INTRO_5 = "intro_5";
        String INTRO_6 = "intro_6";
        String INTRO_7 = "intro_7";
        String INTRO_8 = "intro_8";
        String INTRO_9 = "intro_9";
        String INTRO_10 = "intro_10";
        String INTRO_11 = "intro_11";
        String INTRO_12 = "intro_12";
        String INTRO_13 = "intro_13";
        String INTRO_14 = "intro_14";
        String INTRO_15 = "intro_15";
        String INTRO_16 = "intro_16";

        String ACTION_VIEW_NOTIFICATION = "action_view_notification";
        String ACTION_CHAT_NOTIFICATION = "action_chat_notification";
        String ACTION_FRAGMENT_HANDEL = "action_frag_handel";
    }

    public interface EXTRA {
        String DATA = "data";
        String TITLE = "title";
        String LOGINID = "loginid";
        String PROPERTY_ADDRESS = "property_address";
        String OWNER_NAME = "owner_name";
        String OWNER_EMAIL = "owner_email";
        String MEETING_TYPE = "meeting_type";
        String NOTIFICATION_TIME_TYPE = "notification_time_type";
        String ADDRESS = "address";
        String DEAL_ID = "deal_id";
        String LAT = "lat";
        String LANG = "lang";
        String TYPE_PUSH = "type_push";
        String SCREEN_TYPE = "screen_type";
        String NOTIFICATION_ID = "notification_id";
        String EXPIRED_STATUS = "expired_status";
        String FROM_TO = "FROM_TO";
        String LOCATION = "Location";
    }

    public interface PUSH_TYPE {
        String PUSH_TEXT = "text";
        String PUSH_PHOTO = "photo";
        String PUSH_DOCUMENTS = "documents";
    }

    public interface ACTION {
        String DELETE_ACTION = "action_delete";
        String VIEW_ACTION = "action_view";
        String EDIT_ACTION = "action_edit";
        String MESSAGES_VIEW_ACTION = "action_messages";
        String PROPERTY_LIKES_ACTION = "action_likes";
        String PROPERTY_UNLIKES_ACTION = "action_unlikes";
        String PROPERTY_UNAVAILABLE_ACTION = "action_unavailable";
        String PROPERTY_AVAILABLE_ACTION = "action_available";

        String PROPERTY_MY_ACTION = "my_action_view";
        String PROFILE_VIEW_ACTION = "profile_action_view";
    }


    public interface SCREEN_SELCTION_NAME {
        String SELECTE_BUYER = "BUYER";
        String SELECTE_SELLER = "SELLER";

        String SELECTE_PROPERTY = "PROPERTY";

        String SELECTE_LANDLOARD = "LANDLORD";
        String SELECTE_RENTAL = "RENTAL";
        String SELECTE_HOMEOWNER = "HOMEOWNER";

        String SELECTE_SERVICE = "SERVICE";


        String SELECTE_LIKES_PROPERTY = "likes_property";
        String SELECTE_LIKES_RENTAL = "likes_rental";
        String SELECTE_LIKES_SERVICE = "likes_service";
    }


    public interface SELECTION_REQUEST_TYPE {
        String TYPE_BUILDING = "building";
        String TYPE_PROPERTY = "property";
        String TYPE_RENTAL = "rental";
        String TYPE_SERVICE = "service";
        String TYPE_STATE = "state";
        String TYPE_CITY = "city";
        String TYPE_LEASE = "lease";
        String TYPE_PROFESSION = "profession";
        String TYPE_HTOL = "highTolow";
        String TYPE_LTOH = "lowTohigh";
        String TYPE_NTOO = "newToold";
        String TYPE_OTON = "oldTonew";

    }

    public interface SCREEN_ACTION_TYPE {
        String ADD = "add";
        String EDIT = "edit";

    }

    public interface ACTION_HEADER_TYPE {

        String UNAVAILABLE = "Unavailable";
        String AVAILABLE = "Available";
        String DELETE = "Delete";
    }

    public static String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6ImJjNjgxZDE1Y2Q1YmFhMDBjNTVlZGE4NzEzYzU1ZjcxY2E2ZWZmNzgyMmVjMzRiMTlkNDlhOTY0YzExNDhlM2YwNThiZDE2MjRiY2UxMGVkIn0.eyJhdWQiOiIxIiwianRpIjoiYmM2ODFkMTVjZDViYWEwMGM1NWVkYTg3MTNjNTVmNzFjYTZlZmY3ODIyZWMzNGIxOWQ0OWE5NjRjMTE0OGUzZjA1OGJkMTYyNGJjZTEwZWQiLCJpYXQiOjE1NTAyODkwNTQsIm5iZiI6MTU1MDI4OTA1NCwiZXhwIjoxNTgxODI1MDU0LCJzdWIiOiIxMTYiLCJzY29wZXMiOltdfQ.PfMjo0QTvR44bY_wsKgowBYhmxC7DQ52DaPKskN1uQsnVwWv8mmMCtsS7GB7PHA4WW1mnoh_NrzQVvaXS5KXejZevO6OqlIvXaYznyot6g_bwjfxB52c91nf-irNaXEywXwUaSTAxi7QeFcY_-2B1gTYp3MgrOUveyMxxDB8IfRlD_r7eT_r_wGp0yUruemXYu54ZC78N8cOYvn_5-YPfceglL8ucaYoMYFKDSseXd9Zivirg2V9ndnqpBTMwbVL5iY3aJg-fvH8atJcwy3IY2yoTNPqMOCPlV3I7En2-Cpujg4Ff1aXOeL1KCVjIcrtHqg9K7H-UaLBUAH1e6DgJmGqQufJQsclPUjMjXJTcBdNTVUNaVQ_eA41PR8PNepg4EB55EZnVwudjMFR8wYNCNzE7XvzydWh_Ligtb_3cmL3zl6OmkbIYtwBFvkdksNNPvFEsnt6tJ-iaZWYNXHjhCSQRy-0LKq6s1lWJtvTlJ-HS2kkzwIm_CphiQArQfoZWUWaAqavkliX3GUj20i00b5OfE60t2FA6YuahoJsJ5s8kuQ1yiy1R_xtmEgM7xkHS369fcjTV_S_-A7MxdxbYst_PUuaQG_TAzC218TJs-lP0Cyoqy4ZbJ1S_4E1Z-t0iPmKHFC4lsWBV7DDVbwMi1mdmhwP0hilHU4Z5pNlbns";

    public interface SELECTION_HEADER_TITLE {
        String TITLE_MYPROFILE = "My Profile";
        String TITLE_INBOX = "Chat List";
        String TITLE_NOTIFICATION_FILTER = "Notification Filter";
        String TITLE_SETTING = "Settings";
        String TITLE_NOTIFICATION = "Flippbidd Alerts";
        String TITLE_MY_LIKES = "My Likes";
        String TITLE_FIND_MY_DEAL = "Find My Deal";
        String TITLE_MY_LOCATION = "My Location";
        String TITLE_MY_CALENDAR = "My Calendar";

        String TITLE_PROPERTIES = "Properties";
        String TITLE_MY_PROPERTIRS = "My Properties";

        String TITLE_MY_RENTAL = "My Lending";//Rentals
        String TITLE_RENTAL = "Lending";//Rentals

        String SELECTE_LANDLOARD = "Landlord";
        String SELECTE_TENANT = "Tenant";

        String SELECTE_HOMEOWNER = "Homeowner";
        String SELECTE_SERVICE = "Servicer";
        String SELECTE_MY_SERVICE = "My Services";
        String SELECTE_BUYER = "Buyer";
        String SELECTE_SELLER = "Seller";


        String FILE_VIEWER = "File Viewer";
        String TERMS_AND_CONDITION = "Terms & Condition";


    }

    public static final String no_data_found = "Welcome to FlippBidd! Add Your First ";
    public static final String no_data_found_append = " Here by Pressing the Property Upload Button.";
//    Pressing the '+' Button on Top

    public static final String[] cvFileExtensions = new String[]{"doc", "docx", "pdf", "xls", "xlsx"};


    public static final String POLICY_ = "https://www.flippbiddapp.com/privacy.php";
    public static final String CONDITION_ = "https://www.flippbiddapp.com/condition.php";
    public static final String AGGREMENTS_ = "https://www.flippbiddapp.com/EULA.html";
    public static final String[] bedAndBathCountArray = new String[]{"N/A", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10+"};

    public static final String FREE_VERSION = "FREE VERSION";
    public static final String PRO_VERSION = "PRO VERSION";

}

