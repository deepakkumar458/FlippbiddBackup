package com.flippbidd.sendbirdSdk.groupchannel;

import android.Manifest;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.flippbidd.Adapter.Pager.TabFragments;
import com.flippbidd.CustomClass.CustomAppCompatButton;
import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.ContactList.Users;
import com.flippbidd.Model.Response.CommonResponse;
import com.flippbidd.Model.RxJava2ApiCallHelper;
import com.flippbidd.Model.RxJava2ApiCallback;
import com.flippbidd.Model.Service.UserServices;
import com.flippbidd.Others.Constants;
import com.flippbidd.Others.LogUtils;
import com.flippbidd.Others.NetworkUtils;
import com.flippbidd.R;
import com.flippbidd.baseclass.BaseApplication;
import com.flippbidd.sendbirdSdk.groupchannel.tabs.FlippbiddUserFragments1;
import com.flippbidd.sendbirdSdk.groupchannel.tabs.MyContactUserFragments1;
import com.flippbidd.sendbirdSdk.view.BaseActivity;
import com.flippbidd.utils.DialogUtils;
import com.flippbidd.utils.PreferenceUtils;
import com.flippbidd.utils.ToastUtils;
import com.github.tamir7.contacts.Contact;
import com.github.tamir7.contacts.Contacts;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Displays a selectable list of users within the app. Selected users will be invited into the
 * current channel.
 */

public class InviteMemberActivity extends BaseActivity implements FlippbiddUserFragments1.UsersSelectedListener, MyContactUserFragments1.ContactSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {

    private Toolbar mToolbar;

    private String mChannelUrl;
    private String mChannelMainId = "";
    private String mChannelPropertyId = "";
    private String mChannelOwnerEmail = "";
    private Button mInviteButton;

    private List<String> mSelectedUserIds;
    private List<String> mSelectedContact;
    Disposable disposable;
    GroupChannel mogroupChannel;
    //new code
    //new code for tab
    TabFragments mViewPagerAdapter;
    TabLayout mViewTabLyaout;
    ViewPager mViewViewPager;

    private LinearLayout linerView;
    private ConstraintLayout contactSyncMainView;
    private CustomAppCompatButton btnContactSync, btnNoThanksSync;
    private CheckBox checkBoxSyncTerm;
    private boolean isSelectedChecked = false;
    ProgressDialog progressDialog;
    private Context moContext;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        progressDialog = new ProgressDialog(moContext);
        progressDialog.setMessage("Syncing your contacts, please wait..");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.moContext = InviteMemberActivity.this;
        setContentView(R.layout.activity_invite_member);

        mSelectedUserIds = new ArrayList<>();
        mSelectedContact = new ArrayList<>();

        //id
        mViewTabLyaout = findViewById(R.id.viewTabLyaout);
        mViewViewPager = findViewById(R.id.viewViewPager);

        contactSyncMainView = findViewById(R.id.contactSyncMainView);
        linerView = findViewById(R.id.linerView);
        btnNoThanksSync = findViewById(R.id.btnNoThanksSync);
        btnContactSync = findViewById(R.id.btnContactSync);
        checkBoxSyncTerm = findViewById(R.id.checkBoxSyncTerm);


        mToolbar = (Toolbar) findViewById(R.id.toolbar_invite_member);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        }

        mChannelUrl = getIntent().getStringExtra(GroupChatFragment.EXTRA_CHANNEL_URL);
        mChannelMainId = getIntent().getStringExtra(GroupChatFragment.EXTRA_MAIN_ID);
        mChannelPropertyId = getIntent().getStringExtra(GroupChatFragment.EXTRA_PROPERTY_ID);
        mChannelOwnerEmail = getIntent().getStringExtra(GroupChatFragment.EXTRA_OWNER_EMAIL);

        if (PreferenceUtils.getContactSync()) {
            viewHideShow(false);
            initView();
        } else {
            viewHideShow(true);

            checkBoxSyncTerm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    isSelectedChecked = isChecked;
                }
            });


            btnContactSync.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isSelectedChecked) {
                        ToastUtils.shortToast("Please check terms and condition.");
                        return;
                    }
                    checkPermission(Manifest.permission.READ_CONTACTS, 909);
                }
            });
            btnNoThanksSync.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }


        if (SendBird.getConnectionState() == SendBird.ConnectionState.OPEN) {
//            setUpRecyclerView();

            //get channel details
            GroupChannel.getChannel(mChannelUrl, new GroupChannel.GroupChannelGetHandler() {
                @Override
                public void onResult(GroupChannel groupChannel, SendBirdException e) {
                    mogroupChannel = groupChannel;
                }
            });
        } else {
            DialogUtils.showConnectionRequireDialog(InviteMemberActivity.this);
        }


    }

    private void viewHideShow(boolean isContactSync) {
        if (isContactSync) {
            contactSyncMainView.setVisibility(View.VISIBLE);
            linerView.setVisibility(View.GONE);
        } else {
            contactSyncMainView.setVisibility(View.GONE);
            linerView.setVisibility(View.VISIBLE);
        }

    }


    private void sentMyContactMemeber() {

        if (mSelectedContact.size() <= 0) {
            return;
        }

        String contactsList;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            contactsList = String.join(",", mSelectedContact);
        } else {
            contactsList = android.text.TextUtils.join(",", mSelectedContact);
        }

        Intent I = new Intent(Intent.ACTION_VIEW);
        I.setData(Uri.parse("smsto:"));
        I.setType("vnd.android-dir/mms-sms");
        I.putExtra("address", new String(contactsList));
        I.putExtra("sms_body", getString(R.string.string_share_sms_message));

        try {
            startActivity(I);
            Log.i("Sms Send", "");
        } catch (Exception e) {
            Toast.makeText(InviteMemberActivity.this, "Sms not send", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        hideKeyboard();
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void inviteSelectedMembersWithUserIds(String memberCounts, String memeberId) {

        // Get channel instance from URL first.
        GroupChannel.getChannel(mChannelUrl, new GroupChannel.GroupChannelGetHandler() {
            @Override
            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                if (e != null) {
                    // Error!
                    return;
                }

                // Then invite the selected members to the channel.
                groupChannel.inviteWithUserIds(mSelectedUserIds, new GroupChannel.GroupChannelInviteHandler() {
                    @Override
                    public void onResult(SendBirdException e) {
                        if (e != null) {
                            // Error!
                            Toast.makeText(InviteMemberActivity.this, "something went wrong please try again", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (mChannelMainId.equalsIgnoreCase("")) {
                            setResult(RESULT_OK);
                            finish();
                            return;
                        }
                        updateChannel(true, memberCounts, memeberId);
                    }
                });
            }
        });

    }

    private void updateChannel(boolean isProgress, String userCounts, String selectedMemberID) {
        if (!NetworkUtils.isInternetAvailable(InviteMemberActivity.this)) {
            Toast.makeText(InviteMemberActivity.this, getString(R.string.no_internet), Toast.LENGTH_LONG).show();
            return;
        }
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("channel_main_id", RequestBody.create(MediaType.parse("multipart/form-data"), mChannelMainId));
        linkedHashMap.put("owner_id", RequestBody.create(MediaType.parse("multipart/form-data"), mChannelOwnerEmail));
        linkedHashMap.put("property_id", RequestBody.create(MediaType.parse("multipart/form-data"), mChannelPropertyId));
        linkedHashMap.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), "property"));
        linkedHashMap.put("status", RequestBody.create(MediaType.parse("multipart/form-data"), userCounts));

        showProgressBar(isProgress);

        UserServices userService = ApiFactory.getInstance(InviteMemberActivity.this).provideService(UserServices.class);
        Observable<CommonResponse> observable;
        observable = userService.updateChannelStatus(linkedHashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse response) {
                showProgressBar(false);
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFailed(Throwable throwable) {
                showProgressBar(false);
            }
        });
    }


    //new code add
    private void initView() {
        mViewPagerAdapter = new TabFragments(getSupportFragmentManager());

        FlippbiddUserFragments1 flippbiddUserFragments = new FlippbiddUserFragments1(true);
        MyContactUserFragments1 myContactUserFragments = new MyContactUserFragments1(true);

        mViewPagerAdapter.addFragment(flippbiddUserFragments, "Flippbidd User");
        mViewPagerAdapter.addFragment(myContactUserFragments, "My Contacts");
        mViewViewPager.setAdapter(mViewPagerAdapter);
        mViewTabLyaout.setupWithViewPager(mViewViewPager);


        mViewViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case Constants.TABS.TAB_0:
                        ((FlippbiddUserFragments1) mViewPagerAdapter.getItem(position)).refresh(false);
                        break;
                    case Constants.TABS.TAB_1:
                        ((MyContactUserFragments1) mViewPagerAdapter.getItem(position)).refresh(false);
                        break;

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


        //done button
        mInviteButton = (Button) findViewById(R.id.button_invite_member);
        mInviteButton.setVisibility(View.VISIBLE);
        mInviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();

                if(mInviteButton.getText().toString().equalsIgnoreCase("Done")){
                    //done action
                    //add member
                    if (mSelectedUserIds.size() > 0) {
                        int totalMemberCount = 0;
                        if (mogroupChannel != null) {
                            totalMemberCount = mogroupChannel.getMembers().size() + mSelectedUserIds.size();
                        } else {
                            totalMemberCount = mSelectedUserIds.size() + 2;
                        }
                        inviteSelectedMembersWithUserIds(String.valueOf(totalMemberCount), "");
                    } else {
                        //invite member
                        sentMyContactMemeber();
                    }
                }else {
                    //sync now
                    checkPermission(Manifest.permission.READ_CONTACTS, 909);
                }
            }
        });
//        mInviteButton.setEnabled(false);
    }

    @Override
    public void onUserSelected(boolean selected, Users moUser) {
        if (selected) {
            mSelectedUserIds.add((moUser.getUserId()));
        } else {
            mSelectedUserIds.remove(moUser.getUserId());
        }

        // If no users are selected, disable the invite button.
        if (mSelectedUserIds.size() > 0) {
//            mInviteButton.setEnabled(true);
            mInviteButton.setText("Done");
        } else {
//            mInviteButton.setEnabled(false);
            mInviteButton.setText("Sync Now");
        }
    }

    @Override
    public void onContactSelected(boolean selected, Contact userId) {

    }


    private static final int CONTACTS_LOADER_ID = 1;

    /*ontact list*/
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        // This is called when a new Loader needs to be created.
        if (id == CONTACTS_LOADER_ID) {
            return contactsLoader();
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        //The framework will take care of closing the
        // old cursor once we return.
        List<String> contacts = contactsFromCursor(cursor);
        if (contacts != null) {
            //call api for update contact on server
            String contactsList = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                contactsList = String.join(",", contacts);
            } else {
                contactsList = android.text.TextUtils.join(",", contacts);
            }

            if (progressDialog != null) {
                progressDialog.show();
            }
            callapiforcontactsyn(contactsList);
        }
    }


    private void callapiforcontactsyn(String contactList) {
        if (!NetworkUtils.isInternetAvailable(InviteMemberActivity.this)) {
            ToastUtils.shortToast(getString(R.string.no_internet));
            return;
        }
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getLoginID()));
        linkedHashMap.put("mobile_number", RequestBody.create(MediaType.parse("multipart/form-data"), contactList));
//        showProgressBar(true);

        UserServices userService = ApiFactory.getInstance(InviteMemberActivity.this).provideService(UserServices.class);
        Observable<JsonElement> observable;
        observable = userService.uploadContact(linkedHashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement response) {
//                showProgressBar(false);
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                LogUtils.LOGD("List", "onSuccess() called with: response = [" + response.toString() + "]");
                if (((JsonObject) response).get("success").getAsBoolean()) {
                    PreferenceUtils.setContactSync(true);
                    PreferenceUtils.setContactListResponse(((JsonObject) response).toString());
                    viewHideShow(false);
                    initView();
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
//                showProgressBar(false);
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
            }
        });
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private Loader<Cursor> contactsLoader() {
        Uri contactsUri = ContactsContract.Contacts.CONTENT_URI; // The content URI of the phone contacts

        String[] projection = {                                  // The columns to return for each row
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER
        };

        String selection = null;                                 //Selection criteria
        String[] selectionArgs = {};                             //Selection criteria
        String sortOrder = null;                                 //The sort order for the returned rows

        return new CursorLoader(
                getApplicationContext(),
                contactsUri,
                projection,
                selection,
                selectionArgs,
                sortOrder);
    }

    private List<String> contactsFromCursor(Cursor cursor) {
        List<String> contacts = new ArrayList<String>();
//        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, "upper(" + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + ") ASC");
        cursor = getContentResolver().query(ContactsContract.Data.CONTENT_URI
                , new String[]{ContactsContract.Data._ID
                        , ContactsContract.Data.DISPLAY_NAME
                        , ContactsContract.CommonDataKinds.Phone.NUMBER
                        , ContactsContract.CommonDataKinds.Phone.TYPE}
                , ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                        + "' AND " + ContactsContract.RawContacts.ACCOUNT_TYPE + "= ?"
                , new String[]{"com.whatsapp"}
                , "upper(" + ContactsContract.Data.DISPLAY_NAME + ") ASC");

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contacts.add(phonenumber);
        }
        cursor.close();
        return contacts;
    }


    // Function to check and request permission
    public void checkPermission(String permission, int requestCode) {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(
                InviteMemberActivity.this,
                permission)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat
                    .requestPermissions(
                            InviteMemberActivity.this,
                            new String[]{permission},
                            requestCode);
        } else {
            contactSyn();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 909) {
            // Checking whether user granted the permission or not.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                contactSyn();
            } else {
                checkPermission(Manifest.permission.READ_CONTACTS, 909);
            }
        }
    }

    private void contactSyn() {
        if (progressDialog != null) {
            progressDialog.show();
        }
        List<Contact> contacts = Contacts.getQuery().find();
        Constants.contactsList.addAll(contacts);
        List<String> contactlis = new ArrayList<>();
        for (int no = 0; no < contacts.size(); no++) {
            if (contacts.get(no).getPhoneNumbers() != null && contacts.get(no).getPhoneNumbers().size() != 0) {
                contactlis.add(contacts.get(no).getPhoneNumbers().get(0).getNormalizedNumber());
            }
        }

        if (contactlis != null) {
            //call api for update contact on server
            String contactsList = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                contactsList = String.join(",", contactlis);
            } else {
                contactsList = android.text.TextUtils.join(",", contactlis);
            }
            callapiforcontactsyn(contactsList);
        }
    }

    /*end*/
}
