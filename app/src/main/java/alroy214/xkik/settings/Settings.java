package alroy214.xkik.settings;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.crossbowffs.remotepreferences.RemotePreferences;
import com.google.gson.Gson;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import alroy214.xkik.enums.Colors;
import alroy214.xkik.enums.Message;
import de.robv.android.xposed.XSharedPreferences;

import static alroy214.xkik.utilities.HookKeys.kikVersion;
import static alroy214.xkik.utilities.PrefsKeys.AUTO_LOOP;
import static alroy214.xkik.utilities.PrefsKeys.AUTO_MUTE;
import static alroy214.xkik.utilities.PrefsKeys.AUTO_PLAY;
import static alroy214.xkik.utilities.PrefsKeys.BETA;
import static alroy214.xkik.utilities.PrefsKeys.BYPASS_SAVE;
import static alroy214.xkik.utilities.PrefsKeys.CUSTOM_INCOMING;
import static alroy214.xkik.utilities.PrefsKeys.CUSTOM_OUTGOING;
import static alroy214.xkik.utilities.PrefsKeys.DARK_BG;
import static alroy214.xkik.utilities.PrefsKeys.DATE_FORMAT;
import static alroy214.xkik.utilities.PrefsKeys.DEV_MODE;
import static alroy214.xkik.utilities.PrefsKeys.DISABLE_FWD;
import static alroy214.xkik.utilities.PrefsKeys.DISABLE_SAVE;
import static alroy214.xkik.utilities.PrefsKeys.DO_THE_WAVE;
import static alroy214.xkik.utilities.PrefsKeys.FAKE_CAMERA;
import static alroy214.xkik.utilities.PrefsKeys.FILE_LIST;
import static alroy214.xkik.utilities.PrefsKeys.GRAPHICS_ENABLED;
import static alroy214.xkik.utilities.PrefsKeys.INNER_WAVE;
import static alroy214.xkik.utilities.PrefsKeys.IS_PREF_DEFAULT;
import static alroy214.xkik.utilities.PrefsKeys.LONG_CAM;
import static alroy214.xkik.utilities.PrefsKeys.LURKING_TOAST;
import static alroy214.xkik.utilities.PrefsKeys.NO_HOOK;
import static alroy214.xkik.utilities.PrefsKeys.NO_MEET;
import static alroy214.xkik.utilities.PrefsKeys.NO_READ_RECEIPT;
import static alroy214.xkik.utilities.PrefsKeys.NO_TYPING;
import static alroy214.xkik.utilities.PrefsKeys.OUTGOING_GRADIENT;
import static alroy214.xkik.utilities.PrefsKeys.SCROLLING_TXT;
import static alroy214.xkik.utilities.PrefsKeys.UNFILTERED_GIFS;
import static alroy214.xkik.utilities.PrefsKeys.WHOS_LURKING;
import static alroy214.xkik.utilities.Statics.APP_DEFAULT_PREFERENCE;
import static alroy214.xkik.utilities.Statics.APP_PREFERENCE_PACKAGE;


/**
 * Settings class
 */

public class Settings {

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private int current_version = 1;

    private transient AppCompatActivity creator;
    private ConcurrentHashMap<String, LongStringArray> whoRead = new ConcurrentHashMap<>();
    private  SharedPreferences sharedPreferences;
    private  SharedPreferences.Editor editor;

    public Settings(final Context context) {
        sharedPreferences = new RemotePreferences(context,
                APP_PREFERENCE_PACKAGE, APP_DEFAULT_PREFERENCE);
        editor = sharedPreferences.edit();
        editor.putString("test_key","working"); // Testing key for logs
        editor.apply();
    }
    public Settings(SharedPreferences pref) {
        sharedPreferences = pref;
        if(!(pref instanceof XSharedPreferences)) {
            editor = sharedPreferences.edit();
            editor.putString("test_key","working"); // Testing key for logs
            editor.apply();
        }
    }

    public boolean checkLoadSettings(JSONObject tweaksObject, String versionNumber) {
        if(sharedPreferences.getBoolean(IS_PREF_DEFAULT, false)){
            return true;
        }
        boolean successfullyLoaded = true;
        editor.putBoolean(IS_PREF_DEFAULT, true).commit();
        JSONObject tweakList = null;
        try {
            tweakList = tweaksObject.getJSONObject(versionNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (tweakList == null) {
            try {
                tweakList = tweaksObject.getJSONObject(tweaksObject.getString(kikVersion));
                successfullyLoaded = false;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }
        try {
            Iterator<String> temp = tweakList.keys();
            while (temp.hasNext()) {
                String key = temp.next();
                editor.putString(key, String.valueOf(tweakList.get(key)));
            }
            editor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
            successfullyLoaded = false;
        }
        return successfullyLoaded;
    }

    private void trySave() {
        try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() throws IOException {
        FileUtils.writeStringToFile(getSaveFile(), new Gson().toJson(this), "UTF-8", false);

    }

    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity app activity
     */
    public static void verifyStoragePermissions(AppCompatActivity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public boolean getBypassSave() {
        return sharedPreferences.getBoolean(BYPASS_SAVE, false);
    }

    public void setBypassSave(boolean bypass) {
        editor.putBoolean(BYPASS_SAVE, bypass);
        editor.commit();
    }

    /**
     * Loads settings
     *
     * @return Settings object
     * @throws IOException
     */ /*
    public static Settings load() throws IOException {
        File destination = new File(getSaveDir().getPath() + File.separator + "backup"
                + File.separator + getSaveFile().getName());
        if (getSaveFile().exists()) {
            if (getSaveFile().length() != 0L) {
                try {
                    FileUtils.copyFile(getSaveFile(), destination);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    FileUtils.copyFile(destination, getSaveFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return new Gson().fromJson(FileUtils.readFileToString(getSaveFile(), "UTF-8"), Settings.class);
        } else if (destination.exists() && destination.length() != 0) {
            try {
                FileUtils.copyFile(destination, getSaveFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new Gson().fromJson(FileUtils.readFileToString(getSaveFile(), "UTF-8"), Settings.class);
        } else {
            Settings set = new Settings();
            set.save();
            return set;
        }
    } */

    /**
     * Gets save directory
     *
     * @return Save directory as file
     */
    public static File getSaveDir() {
        File savedir = new File(Environment.getDataDirectory().getPath() + File.separator + "XKik" + File.separator);
        if (!savedir.exists()) {
            savedir.mkdir();
        }
        return savedir;
    }

    /**
     * Gets save file
     *
     * @return save file
     */
    private static File getSaveFile() {
        return new File(getSaveDir().getPath() + File.separator + "config.json");
    }

    public boolean getLurkingToast() {
        return sharedPreferences.getBoolean(LURKING_TOAST, false);
    }

    public void setLurkingToast(boolean lurkingToast) {
        editor.putBoolean(LURKING_TOAST, lurkingToast);
        editor.commit();
    }

    public boolean getScrollingtxt() {
        return sharedPreferences.getBoolean(SCROLLING_TXT, false);
    }

    public void setScrollingtxt(boolean scrollingtxt) {
        editor.putBoolean(SCROLLING_TXT, scrollingtxt);
        editor.commit();
    }

    public boolean getNoHook() {
        return sharedPreferences.getBoolean(NO_HOOK, false);
    }

    public void setNoHook(boolean noHook) {
        editor.putBoolean(NO_HOOK, noHook);
        editor.commit();
    }


    public boolean isBETA() {
        return sharedPreferences.getBoolean(BETA, false);
    }

    public void setBETA(boolean beta) {
        editor.putBoolean(BETA, beta);
        editor.commit();
    }

    public boolean getDarkBg() {
        return sharedPreferences.getBoolean(DARK_BG, false);
    }

    public void setDarkBg(boolean darkBg) {
        editor.putBoolean(DARK_BG, darkBg);
        editor.commit();
    }

    public AppCompatActivity getCreator() {
        return creator;
    }

    public void setCreator(AppCompatActivity creator) {
        this.creator = creator;
    }

    public boolean getLongCam() {
        return sharedPreferences.getBoolean(LONG_CAM, false);
    }

    public void setLongCam(boolean longCam) {
        editor.putBoolean(LONG_CAM, longCam);
        editor.commit();
    }

    public boolean getNoMeet() {
        return sharedPreferences.getBoolean(NO_MEET, false);
    }

    public void setNoMeet(boolean value) {
        editor.putBoolean(NO_MEET, value);
        editor.commit();
    }

    public boolean getNoReadReceipt() {
        return sharedPreferences.getBoolean(NO_READ_RECEIPT, false);
    }

    public void setNoReadReceipt(boolean value) {
        editor.putBoolean(NO_READ_RECEIPT, value);
        editor.commit();
    }

    public Set<String> getFileList() {
        return sharedPreferences.getStringSet(FILE_LIST, Collections.<String>emptySet());
    }

    public void setFileList(Set<String> value) {
        editor.putStringSet(FILE_LIST, value);
        editor.commit();
    }

    public boolean getNoTyping() {
        return sharedPreferences.getBoolean(NO_TYPING, false);
    }

    public void setNoTyping(boolean value) {
        editor.putBoolean(NO_TYPING, value);
        editor.commit();
    }

    public boolean getGraphics() {
        return sharedPreferences.getBoolean(GRAPHICS_ENABLED, false);
    }

    public void setGraphics(boolean b) {
        editor.putBoolean(GRAPHICS_ENABLED, b);
        editor.commit();
    }

    public boolean getTheWave() {
        return sharedPreferences.getBoolean(DO_THE_WAVE, false);
    }

    public void setTheWave(boolean b) {
        editor.putBoolean(DO_THE_WAVE, b);
        editor.commit();
    }

    public boolean getInnerWave() {
        return sharedPreferences.getBoolean(INNER_WAVE, false);
    }

    public void setInnerWave(boolean b) {
        editor.putBoolean(INNER_WAVE, b);
        editor.commit();
    }

    public boolean getOutgoing() {
        return sharedPreferences.getBoolean(CUSTOM_OUTGOING, false);
    }

    public void setOutgoing(boolean b) {
        editor.putBoolean(CUSTOM_OUTGOING, b);
        editor.commit();
    }

    public boolean getIncoming() {
        return sharedPreferences.getBoolean(CUSTOM_INCOMING, false);
    }

    public void setIncoming(boolean b) {
        editor.putBoolean(CUSTOM_INCOMING, b);
        editor.commit();
    }

    public boolean getGradient() {
        return sharedPreferences.getBoolean(OUTGOING_GRADIENT, false);
    }

    public void setGradient(boolean b) {
        editor.putBoolean(OUTGOING_GRADIENT, b);
        editor.commit();
    }

    public boolean getWhosLurking() {
        return sharedPreferences.getBoolean(WHOS_LURKING, false);
    }

    public void setWhosLurking(boolean b) {
        editor.putBoolean(WHOS_LURKING, b);
        editor.commit();
    }

    public boolean getDev() {
        return sharedPreferences.getBoolean(DEV_MODE, false);
    }

    public void setDev(boolean b) {
        editor.putBoolean(DEV_MODE, b);
        editor.commit();
    }

    public boolean getFakeGif() {
        return sharedPreferences.getBoolean(FAKE_CAMERA, false);
    }

    public void setFakeGif(boolean b) {
        editor.putBoolean(FAKE_CAMERA, b);
        editor.commit();
    }

    public boolean getFakeCam() {
        return sharedPreferences.getBoolean(FAKE_CAMERA, false);
    }

    public void setFakeCam(boolean b) {
        editor.putBoolean(FAKE_CAMERA, b);
        editor.commit();
    }

    public int getDateFormat() {
        return sharedPreferences.getInt(DATE_FORMAT, 0);
    }

    public void setDateFormat(int fmt) {
        editor.putInt(DATE_FORMAT, fmt);
        editor.commit();
    }

    public void setColor(String id, int color) {
        editor.putInt(id, color);
        editor.commit();
    }

    public void setColor(Colors id, int color) {
        editor.putInt(id.getColor(), color);
        editor.commit();
    }

    public boolean getDisableSave() {
        return sharedPreferences.getBoolean(DISABLE_SAVE, false);
    }

    public void setDisableSave(boolean disableSave) {
        editor.putBoolean(DISABLE_SAVE, disableSave);
        editor.commit();
    }

    public boolean getDisableFwd() {
        return sharedPreferences.getBoolean(DISABLE_FWD, false);
    }

    public void setDisableFwd(boolean disableFwd) {
        editor.putBoolean(DISABLE_FWD, disableFwd);
        editor.commit();
    }

    public boolean getAutoLoop() {
        return sharedPreferences.getBoolean(AUTO_LOOP, false);
    }

    public void setAutoLoop(boolean AutoLoop) {
        editor.putBoolean(AUTO_LOOP, AutoLoop);
        editor.commit();
    }

    public boolean getAutoMute() {
        return sharedPreferences.getBoolean(AUTO_MUTE, false);
    }

    public void setAutoMute(boolean autoMute) {
        editor.putBoolean(AUTO_MUTE, autoMute);
        editor.commit();
    }

    public boolean getAutoPlay() {
        return sharedPreferences.getBoolean(AUTO_PLAY, false);
    }

    public void setAutoPlay(boolean autoPlay) {
        editor.putBoolean(AUTO_PLAY, autoPlay);
        editor.commit();
    }

    public boolean getUnfilteredGIFs() {
        return sharedPreferences.getBoolean(UNFILTERED_GIFS, false);
    }

    public void setUnfilteredGIFs(boolean unfilteredGIFs) {
        editor.putBoolean(UNFILTERED_GIFS, unfilteredGIFs);
        editor.commit();
    }

    /**
     * Reset a color to it's default value
     *
     * @param id Color ID
     */
    public void resetColor(String id) {
        editor.remove(id);
        editor.commit();
    }

    public boolean containsString(String id) {
        return sharedPreferences.contains(id);
    }

    public String getString(String id, String val) {
        return sharedPreferences.getString(id, val);
    }

    public String getString(Message id) {
        return sharedPreferences.getString(id.toString(), "");
    }

    public void setString(String id, String val) {
        editor.putString(id, val);
        editor.commit();
    }

    /**
     * Reset a string to it's default value
     *
     * @param id String ID
     */
    public void resetString(String id) {
        editor.remove(id);
        editor.commit();
    }

    public int getColor(Colors color) {
        return sharedPreferences.getInt(color.getColor(), -1);
    }
    public int getColor(String color) {
        return sharedPreferences.getInt(color, -1);
    }
}
