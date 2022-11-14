package com.flippbidd.Database;



import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface ContactLisDao {

    @Query("SELECT * FROM contactLis")
    List<ContactData> loadAllContactList();

    @Insert
    void insertContacts(ContactData person);

}
