package com.example.filencrypt;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.filencrypt.models.ConcreteFilencryptFile;
import com.example.filencrypt.models.FilencryptFile;

public class Settings {
    public static final String PREFERENCES_NAME = "filencryptAppSettings";

    static boolean isAutoLockEnabled(Context context) {
        return preferences(context).getBoolean("auto_lock_enabled", true);
    }

    static int getAutoLockDelayMinutes(Context context) {
        return Integer.parseInt(preferences(context).getString("auto_lock_delay_minutes", "5"));
    }

    public static FilencryptFile getShareTargetDirectory(Context context) {
        String path = preferences(context).getString("share_target_directory", null);
        if (path == null) return ConcreteFilencryptFile.ROOT;
        FilencryptFile directory = new ConcreteFilencryptFile(path);
        if (!directory.exists()) return ConcreteFilencryptFile.ROOT;
        return directory;
    }

    public static boolean hasShareTargetDirectory(Context context) {
        String path = preferences(context).getString("share_target_directory", null);
        if (path == null) return false;
        FilencryptFile directory = new ConcreteFilencryptFile(path);
        return directory.exists();
    }

    public static void setShareTargetDirectory(Context context, FilencryptFile directory) {
        preferences(context).edit().putString("share_target_directory", directory.getPath()).apply();
    }

    private static SharedPreferences preferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }
}
