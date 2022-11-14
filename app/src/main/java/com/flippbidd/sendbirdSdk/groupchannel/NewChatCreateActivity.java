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
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.flippbidd.CustomClass.CustomAppCompatButton;
import com.flippbidd.CustomClass.CustomEditText;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.ContactList.Users;
import com.flippbidd.Model.RxJava2ApiCallHelper;
import com.flippbidd.Model.RxJava2ApiCallback;
import com.flippbidd.Model.Service.UserServices;
import com.flippbidd.Others.Constants;
import com.flippbidd.Others.LogUtils;
import com.flippbidd.Others.NetworkUtils;
import com.flippbidd.R;
import com.flippbidd.baseclass.BaseApplication;
import com.flippbidd.sendbirdSdk.groupchannel.tabs.FlippbiddUserFragments;
import com.flippbidd.sendbirdSdk.groupchannel.tabs.MyContactUserFragments;
import com.flippbidd.sendbirdSdk.view.BaseActivity;
import com.flippbidd.utils.PreferenceUtils;
import com.flippbidd.utils.ToastUtils;
import com.github.tamir7.contacts.Contact;
import com.github.tamir7.contacts.Contacts;
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelParams;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * An Activity to create a new Group Channel.
 * First displays a selectable list of users,
 * then shows an option to create a Distinct channel.
 */

public class NewChatCreateActivity extends BaseActivity
        implements FlippbiddUserFragments.UsersSelectedListener, MyContactUserFragments.ContactSelectedListener, SelectDistinctFragment.DistinctSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EXTRA_NEW_CHANNEL_URL = "EXTRA_NEW_CHANNEL_URL";

    static final int STATE_SELECT_USERS = 0;
    static final int STATE_SELECT_DISTINCT = 1;
    List<String> moSelectedContact;
    private Context moContext;
//    private Button mNextButton, mCreateButton;

    private List<Users> mSelectedIds;
    private boolean mIsDistinct = true;
    private CustomTextView mTvNewChatCancel, mTvInviteDone,mTvSyncNow;
    private CustomEditText mEdtBusinessSearch;
    private RelativeLayout moRlCreateNewGroup;
    private int mCurrentState;
    private AppBarLayout appBarId;

    /*sync contact*/
    private ConstraintLayout contactSyncMainView;
    private CustomAppCompatButton btnContactSync, btnNoThanksSync;
    private CheckBox checkBoxSyncTerm;
    private boolean isSelectedChecked = false;
    Disposable disposable;
    ProgressDialog progressDialog;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chat_create_layout);
        this.moContext = NewChatCreateActivity.this;
        mSelectedIds = new ArrayList<>();
        moSelectedContact = new ArrayList<>();


        appBarId = findViewById(R.id.appBarId);
        Toolbar toolbar = findViewById(R.id.toolbar_create_group_channel);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");

        mTvSyncNow = findViewById(R.id.tvSyncNow);
        contactSyncMainView = findViewById(R.id.contactSyncMainView);
        moRlCreateNewGroup = findViewById(R.id.rlCreateNewGroup);
        btnNoThanksSync = findViewById(R.id.btnNoThanksSync);
        btnContactSync = findViewById(R.id.btnContactSync);
        checkBoxSyncTerm = findViewById(R.id.checkBoxSyncTerm);

        if (PreferenceUtils.getContactSync()) {
            viewHideShow(false);
            initView(savedInstanceState);
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
        mTvInviteDone = findViewById(R.id.tvInviteDone);
        mTvInviteDone.setVisibility(View.GONE);
        mTvNewChatCancel = findViewById(R.id.tvNewChatCancel);
        mTvNewChatCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mTvSyncNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PreferenceUtils.getContactSync()) {
                    checkPermission(Manifest.permission.READ_CONTACTS, 909);
                } else {
                    viewHideShow(true);
                }
            }
        });

    }

    private void viewHideShow(boolean isContactSync) {
        if (isContactSync) {
            contactSyncMainView.setVisibility(View.VISIBLE);
            moRlCreateNewGroup.setVisibility(View.GONE);
        } else {
            contactSyncMainView.setVisibility(View.GONE);
            moRlCreateNewGroup.setVisibility(View.VISIBLE);
        }

    }

    private void initView(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Fragment fragment = SelectUserFragment.newInstance(false, true);
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction()
                    .replace(R.id.container_create_group_channel, fragment)
                    .commit();
        }

        moRlCreateNewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NewChatCreateActivity.this, NewCreateGroupActivity.class));
                finish();
            }
        });
        //invite members
        mTvInviteDone = findViewById(R.id.tvInviteDone);
        mTvInviteDone.setVisibility(View.GONE);
        mTvInviteDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (moSelectedContact.size() <= 0) {
                    return;
                }

                String contactsList;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    contactsList = String.join(",", moSelectedContact);
                } else {
                    contactsList = android.text.TextUtils.join(",", moSelectedContact);
                }

                Intent I = new Intent(Intent.ACTION_VIEW);
                I.setData(Uri.parse("smsto:"));
                I.setType("vnd.android-dir/mms-sms");
                I.putExtra("address", new String(contactsList));
                I.putExtra("sms_body", getString(R.string.string_share_sms_message));

                try {
                    startActivity(I);
                    finish();
                    Log.i("Sms Send", "");
                } catch (Exception e) {
                    Toast.makeText(NewChatCreateActivity.this, "Sms not send", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        /*if (id == R.id.item_refersh) {
            if (PreferenceUtils.getContactSync()) {
                checkPermission(Manifest.permission.READ_CONTACTS, 909);
            } else {
                viewHideShow(true);
            }
        }*/
        return super.onOptionsItemSelected(item);
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }*/


    void setState(int state) {
        if (state == STATE_SELECT_USERS) {
            mCurrentState = STATE_SELECT_USERS;
          /*  mCreateButton.setVisibility(View.VISIBLE);
            mNextButton.setVisibility(View.GONE);*/
//            mCreateButton.setVisibility(View.GONE);
//            mNextButton.setVisibility(View.VISIBLE);
        } else if (state == STATE_SELECT_DISTINCT) {
            mCurrentState = STATE_SELECT_DISTINCT;/*
            mCreateButton.setVisibility(View.VISIBLE);
            mNextButton.setVisibility(View.GONE);*/
        }
    }

    @Override
    public void onUserSelected(boolean selected, Users userId) {

        //new code for chat create
        mSelectedIds.add(userId);
        mIsDistinct = PreferenceUtils.getGroupChannelDistinct();
        createGroupChannel(mSelectedIds, mIsDistinct);
    }

    @Override
    public void onDistinctSelected(boolean distinct) {
        mIsDistinct = distinct;
    }

    /**
     * Creates a new Group Channel.
     * <p>
     * Note that if you have not included empty channels in your GroupChannelListQuery,
     * the channel will not be shown in the user's channel list until at least one message
     * has been sent inside.
     *
     * @param userIds  The users to be members of the new channel.
     * @param distinct Whether the channel is unique for the selected members.
     *                 If you attempt to create another Distinct channel with the same members,
     *                 the existing channel instance will be returned.
     */
    private void createGroupChannel(List<Users> userIds, boolean distinct) {
        //Other User
        List<String> mStrings = new ArrayList<>();
        mStrings.add(userIds.get(0).getUserId());

        //admin list
        List<User> mAdmin = new ArrayList<>();
        mAdmin.add(SendBird.getCurrentUser());

        GroupChannelParams params = new GroupChannelParams()
                .setPublic(false)
                .setEphemeral(false)
                .setDistinct(true)
                .addUserIds(mStrings)
                .setOperators(mAdmin)// Or .setOperators(List<User> operators)
                .setName(userIds.get(0).getNickname())
                .setCoverUrl("")
                .setData("")//.setCoverImage(FILE)            // Or .setCoverUrl(COVER_URL)
                .setCustomType("1_1");
        //setChannelUrl(chatURL)  // In a group channel, you can create a new channel by specifying its unique channel URL in a 'GroupChannelParams' object.
        GroupChannel.createChannel(params, new GroupChannel.GroupChannelCreateHandler() {
            @Override
            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                if (e != null) {
                    // Error!
                    if (e.getMessage().equalsIgnoreCase("\"channel_url\" violates unique constraint.")) {
//                                enterGroupChannel(chatURL);
                        Intent intent = new Intent();
                        intent.putExtra(EXTRA_NEW_CHANNEL_URL, groupChannel.getUrl());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                    return;
                }

                Intent intent = new Intent();
                intent.putExtra(EXTRA_NEW_CHANNEL_URL, groupChannel.getUrl());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

    @Override
    public void onContactSelected(boolean selected, Contact mContact) {
        if (selected) {
            if (mContact.getPhoneNumbers() != null && mContact.getPhoneNumbers().size() != 0) {
                moSelectedContact.add(mContact.getPhoneNumbers().get(0).getNumber());
            }
        } else {
            if (mContact.getPhoneNumbers() != null && mContact.getPhoneNumbers().size() != 0) {
                moSelectedContact.remove(mContact.getPhoneNumbers().get(0).getNumber());
            }
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        progressDialog = new ProgressDialog(moContext);
        progressDialog.setMessage("Syncing your contacts, please wait..");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
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
        if (!NetworkUtils.isInternetAvailable(NewChatCreateActivity.this)) {
            ToastUtils.shortToast(getString(R.string.no_internet));
            return;
        }
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getLoginID()));
        linkedHashMap.put("mobile_number", RequestBody.create(MediaType.parse("multipart/form-data"), contactList));
//        showProgressBar(true);

        UserServices userService = ApiFactory.getInstance(moContext).provideService(UserServices.class);
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
                    initView(null);
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
                NewChatCreateActivity.this,
                permission)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat
                    .requestPermissions(
                            NewChatCreateActivity.this,
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

