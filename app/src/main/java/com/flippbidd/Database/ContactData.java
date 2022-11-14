package com.flippbidd.Database;


import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "contactLis")
public class ContactData {

    @PrimaryKey(autoGenerate = true)
    public int _ID;
    public String userId;
    public String contactName;
    public String contactMobile;

    @Ignore
    public ContactData(String userId,
                       String contactName,
                       String contactMobile) {
        this._ID = _ID;
        this.contactName = contactName;
        this.contactMobile = contactMobile;
    }


    public ContactData(int _ID,
                     String userId,
                     String contactName,
                     String contactMobile) {
        this._ID = _ID;
        this.contactName = contactName;
        this.contactMobile = contactMobile;
    }

    public int get_ID() {
        return _ID;
    }

    public void set_ID(int _ID) {
        this._ID = _ID;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactMobile() {
        return contactMobile;
    }

    public void setContactMobile(String contactMobile) {
        this.contactMobile = contactMobile;
    }
}
