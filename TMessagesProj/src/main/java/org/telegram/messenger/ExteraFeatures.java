package org.telegram.messenger;

import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/** Local-only privacy features. These settings never change Telegram server state. */
public final class ExteraFeatures {
    private static final String KEY_GHOST_MODE = "extera_ghost_mode";
    private static final String KEY_ANTI_DELETE = "extera_anti_delete";
    private static final String KEY_DELETED_MESSAGES = "extera_deleted_by_other";

    private ExteraFeatures() {}

    private static SharedPreferences prefs(int account) {
        return MessagesController.getMainSettings(account);
    }

    public static boolean isGhostModeEnabled(int account) {
        return prefs(account).getBoolean(KEY_GHOST_MODE, false);
    }

    public static void setGhostModeEnabled(int account, boolean enabled) {
        prefs(account).edit().putBoolean(KEY_GHOST_MODE, enabled).apply();
    }

    public static boolean isAntiDeleteEnabled(int account) {
        return prefs(account).getBoolean(KEY_ANTI_DELETE, true);
    }

    public static void setAntiDeleteEnabled(int account, boolean enabled) {
        prefs(account).edit().putBoolean(KEY_ANTI_DELETE, enabled).apply();
    }

    private static String messageKey(long dialogId, int messageId) {
        return dialogId + ":" + messageId;
    }

    public static boolean isDeletedByOther(int account, long dialogId, int messageId) {
        Set<String> values = prefs(account).getStringSet(KEY_DELETED_MESSAGES, null);
        return values != null && values.contains(messageKey(dialogId, messageId));
    }

    public static void markDeletedByOther(int account, long dialogId, int messageId) {
        SharedPreferences preferences = prefs(account);
        Set<String> old = preferences.getStringSet(KEY_DELETED_MESSAGES, null);
        HashSet<String> values = old == null ? new HashSet<>() : new HashSet<>(old);
        values.add(messageKey(dialogId, messageId));
        preferences.edit().putStringSet(KEY_DELETED_MESSAGES, values).apply();
    }

    public static void clearDeletedByOther(int account, long dialogId, int messageId) {
        SharedPreferences preferences = prefs(account);
        Set<String> old = preferences.getStringSet(KEY_DELETED_MESSAGES, null);
        if (old == null || old.isEmpty()) return;
        HashSet<String> values = new HashSet<>(old);
        if (values.remove(messageKey(dialogId, messageId))) {
            preferences.edit().putStringSet(KEY_DELETED_MESSAGES, values).apply();
        }
    }
  }
