package org.telegram.messenger;

import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;

/** Local-only Extera settings/state. */
public final class ExteraFeatures {
    private static final String KEY_GHOST_MODE = "extera_ghost_mode";
    private static final String KEY_ANTI_DELETE = "extera_anti_delete";
    private static final String KEY_DELETED_MESSAGES = "extera_deleted_by_other";
    private static final String KEY_GHOST_STORIES = "extera_ghost_stories";
    private static final String KEY_HIDE_ADS = "extera_hide_ads";
    private static final String KEY_PROTECTED_CONTENT = "extera_protected_content";
    private static final String KEY_SAVE_SELF_DESTRUCT = "extera_save_self_destruct";
    private static final String KEY_REPEAT_ENABLED = "extera_repeat_enabled";
    private static final String KEY_FAVORITES = "extera_favorites";
    private static final String KEY_DELETED_DIALOGS = "extera_deleted_dialogs";
    private static final String KEY_EDITED_ORIGINALS = "extera_edited_originals";
    private static final String KEY_MUTE_ALL = "extera_mute_all";
    private static final String KEY_SHOW_HIDDEN_MEMBERS = "extera_show_hidden_members";
    private static final String KEY_LOCKED_DIALOGS = "extera_locked_dialogs";
    private static final String KEY_LOCK_PIN_HASH = "extera_lock_pin_hash";
    private static final String KEY_UNLOCKED_SESSION = "extera_unlocked_session";

    private ExteraFeatures() {}

    private static SharedPreferences prefs(int account) {
        return MessagesController.getMainSettings(account);
    }

    public static boolean isGhostModeEnabled(int account) { return prefs(account).getBoolean(KEY_GHOST_MODE, false); }
    public static void setGhostModeEnabled(int account, boolean enabled) { prefs(account).edit().putBoolean(KEY_GHOST_MODE, enabled).apply(); }

    public static boolean isAntiDeleteEnabled(int account) { return prefs(account).getBoolean(KEY_ANTI_DELETE, true); }
    public static void setAntiDeleteEnabled(int account, boolean enabled) { prefs(account).edit().putBoolean(KEY_ANTI_DELETE, enabled).apply(); }

    public static boolean isGhostStoriesEnabled(int account) { return prefs(account).getBoolean(KEY_GHOST_STORIES, false); }
    public static void setGhostStoriesEnabled(int account, boolean enabled) { prefs(account).edit().putBoolean(KEY_GHOST_STORIES, enabled).apply(); }

    public static boolean isHideAdsEnabled(int account) { return prefs(account).getBoolean(KEY_HIDE_ADS, true); }
    public static void setHideAdsEnabled(int account, boolean enabled) { prefs(account).edit().putBoolean(KEY_HIDE_ADS, enabled).apply(); }

    public static boolean isProtectedContentEnabled(int account) { return prefs(account).getBoolean(KEY_PROTECTED_CONTENT, false); }
    public static void setProtectedContentEnabled(int account, boolean enabled) { prefs(account).edit().putBoolean(KEY_PROTECTED_CONTENT, enabled).apply(); }

    public static boolean isSaveSelfDestructEnabled(int account) { return prefs(account).getBoolean(KEY_SAVE_SELF_DESTRUCT, false); }
    public static void setSaveSelfDestructEnabled(int account, boolean enabled) { prefs(account).edit().putBoolean(KEY_SAVE_SELF_DESTRUCT, enabled).apply(); }

    public static boolean isRepeatPostingEnabled(int account) { return prefs(account).getBoolean(KEY_REPEAT_ENABLED, false); }
    public static void setRepeatPostingEnabled(int account, boolean enabled) { prefs(account).edit().putBoolean(KEY_REPEAT_ENABLED, enabled).apply(); }

    private static String messageKey(long dialogId, int messageId) { return dialogId + ":" + messageId; }
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
        if (values.remove(messageKey(dialogId, messageId))) preferences.edit().putStringSet(KEY_DELETED_MESSAGES, values).apply();
    }

    public static boolean isFavorite(int account, long dialogId) {
        Set<String> values = prefs(account).getStringSet(KEY_FAVORITES, null);
        return values != null && values.contains(String.valueOf(dialogId));
    }
    public static void setFavorite(int account, long dialogId, boolean favorite) {
        SharedPreferences preferences = prefs(account);
        Set<String> old = preferences.getStringSet(KEY_FAVORITES, null);
        HashSet<String> values = old == null ? new HashSet<>() : new HashSet<>(old);
        String key = String.valueOf(dialogId);
        if (favorite) values.add(key); else values.remove(key);
        preferences.edit().putStringSet(KEY_FAVORITES, values).apply();
    }

    public static void markDialogDeletedByOther(int account, long dialogId) {
        SharedPreferences preferences = prefs(account);
        Set<String> old = preferences.getStringSet(KEY_DELETED_DIALOGS, null);
        HashSet<String> values = old == null ? new HashSet<>() : new HashSet<>(old);
        values.add(String.valueOf(dialogId));
        preferences.edit().putStringSet(KEY_DELETED_DIALOGS, values).apply();
    }
    public static boolean isDialogDeletedByOther(int account, long dialogId) {
        Set<String> values = prefs(account).getStringSet(KEY_DELETED_DIALOGS, null);
        return values != null && values.contains(String.valueOf(dialogId));
    }

    public static void saveOriginalEditedText(int account, long dialogId, int messageId, String text) {
        if (text == null) return;
        prefs(account).edit().putString(KEY_EDITED_ORIGINALS + "_" + messageKey(dialogId, messageId), text).apply();
    }
    public static String getOriginalEditedText(int account, long dialogId, int messageId) {
        return prefs(account).getString(KEY_EDITED_ORIGINALS + "_" + messageKey(dialogId, messageId), null);
    }

    public static boolean isMuteAllEnabled(int account) { return prefs(account).getBoolean(KEY_MUTE_ALL, false); }
    public static void setMuteAllEnabled(int account, boolean enabled) { prefs(account).edit().putBoolean(KEY_MUTE_ALL, enabled).apply(); }

    public static boolean isShowHiddenMembersEnabled(int account) { return prefs(account).getBoolean(KEY_SHOW_HIDDEN_MEMBERS, false); }
    public static void setShowHiddenMembersEnabled(int account, boolean enabled) { prefs(account).edit().putBoolean(KEY_SHOW_HIDDEN_MEMBERS, enabled).apply(); }

    private static String hashPin(String pin) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(pin.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder out = new StringBuilder(digest.length * 2);
            for (byte b : digest) out.append(String.format(java.util.Locale.US, "%02x", b & 0xff));
            return out.toString();
        } catch (Exception e) {
            return pin;
        }
    }

    public static boolean hasLockPin(int account) { return prefs(account).contains(KEY_LOCK_PIN_HASH); }

    public static boolean setLockPinIfValid(int account, String pin) {
        if (pin == null || !pin.matches("\\d{4,12}")) return false;
        prefs(account).edit().putString(KEY_LOCK_PIN_HASH, hashPin(pin)).apply();
        return true;
    }

    public static boolean verifyLockPin(int account, String pin) {
        if (!hasLockPin(account) || pin == null) return false;
        return hashPin(pin).equals(prefs(account).getString(KEY_LOCK_PIN_HASH, ""));
    }

    public static boolean isDialogLocked(int account, long dialogId) {
        Set<String> values = prefs(account).getStringSet(KEY_LOCKED_DIALOGS, null);
        return values != null && values.contains(String.valueOf(dialogId));
    }

    public static void setDialogLocked(int account, long dialogId, boolean locked) {
        SharedPreferences preferences = prefs(account);
        Set<String> old = preferences.getStringSet(KEY_LOCKED_DIALOGS, null);
        HashSet<String> values = old == null ? new HashSet<>() : new HashSet<>(old);
        String key = String.valueOf(dialogId);
        if (locked) values.add(key); else values.remove(key);
        preferences.edit().putStringSet(KEY_LOCKED_DIALOGS, values).apply();
    }

    public static boolean isDialogUnlockedForSession(int account, long dialogId) {
        Set<String> values = prefs(account).getStringSet(KEY_UNLOCKED_SESSION, null);
        return values != null && values.contains(String.valueOf(dialogId));
    }

    public static void setDialogUnlockedForSession(int account, long dialogId, boolean unlocked) {
        SharedPreferences preferences = prefs(account);
        Set<String> old = preferences.getStringSet(KEY_UNLOCKED_SESSION, null);
        HashSet<String> values = old == null ? new HashSet<>() : new HashSet<>(old);
        String key = String.valueOf(dialogId);
        if (unlocked) values.add(key); else values.remove(key);
        preferences.edit().putStringSet(KEY_UNLOCKED_SESSION, values).apply();
    }

    public static void clearUnlockedDialogsForSession(int account) {
        prefs(account).edit().remove(KEY_UNLOCKED_SESSION).apply();
    }

    public static boolean shouldSuppressNotification(int account, long dialogId) {
        return isMuteAllEnabled(account) || (isDialogLocked(account, dialogId) && !isDialogUnlockedForSession(account, dialogId));
    }

    public static String formatCustomStatus(int account, TLRPC.User user, boolean[] isOnline, boolean[] madeShorter) {
        if (user == null || user.status == null || UserObject.isDeleted(user) || user instanceof TLRPC.TL_userEmpty) {
            return "يمكن حظرك أو الحساب متروك";
        }
        int currentTime = ConnectionsManager.getInstance(account).getCurrentTime();
        if (MessagesController.getInstance(account).onlinePrivacy.containsKey(user.id)) {
            if (isOnline != null) isOnline[0] = true;
            return "موجود الآن ()";
        }
        if (user.status instanceof TLRPC.TL_userStatusOnline && user.status.expires > currentTime) {
            if (isOnline != null) isOnline[0] = true;
            return "موجود الآن ()";
        }
        if (user.status instanceof TLRPC.TL_userStatusRecently || user.status.expires == -100 || user.status.expires == -1000) {
            return "قبل شوية كان موجود";
        }
        if (user.status instanceof TLRPC.TL_userStatusLastWeek || user.status.expires == -101 || user.status.expires == -1001) {
            return "قبل أسبوع كان موجود";
        }
        if (user.status instanceof TLRPC.TL_userStatusLastMonth || user.status.expires == -102 || user.status.expires == -1002) {
            return "يمكن حظرك أو الحساب متروك";
        }
        if (user.status.expires > 0) {
            long diff = Math.max(0, currentTime - user.status.expires);
            if (diff < 5 * 60) return "قبل شوية كان موجود";
            if (diff < 7 * 86400L) return "قبل " + Math.max(1, diff / 86400L) + " يوم كان موجود";
            return "قبل أسبوع كان موجود";
        }
        return "يمكن حظرك أو الحساب متروك";
    }

    public static boolean isIncomingSelfDestructMedia(int account, TLRPC.Message message) {
        if (!isSaveSelfDestructEnabled(account) || message == null || message.out || message.media == null) return false;
        return message.ttl > 0 || message.media.ttl_seconds > 0 || message.destroyTime != 0 || message.destroyTimeMillis != 0;
    }

    /** Best-effort local normalization of one-time media. Server-side deletion policies are not changed. */
    public static void normalizeSelfDestructMedia(int account, TLRPC.Message message) {
        if (!isSaveSelfDestructEnabled(account) || message == null || message.out || message.media == null) return;
        if (message.media.ttl_seconds != 0) message.media.ttl_seconds = 0;
        if (message.ttl != 0) message.ttl = 0;
        message.destroyTime = 0;
        message.destroyTimeMillis = 0;
    }
}
