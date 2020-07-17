package com.softglu.bharatshare.service;

import android.content.SharedPreferences;

import com.softglu.bharatshare.util.AppUtils;
import com.softglu.bharatshare.util.NotificationUtils;
import com.softglu.bharatshare.db.AccessDatabase;

abstract public class Service extends android.app.Service
{
    private NotificationUtils mNotificationUtils;

    public AccessDatabase getDatabase()
    {
        return AppUtils.getDatabase(this);
    }

    public SharedPreferences getDefaultPreferences()
    {
        return AppUtils.getDefaultPreferences(getApplicationContext());
    }

    public NotificationUtils getNotificationUtils()
    {
        if (mNotificationUtils == null)
            mNotificationUtils = new NotificationUtils(getApplicationContext(), getDatabase(), getDefaultPreferences());

        return mNotificationUtils;
    }
}
