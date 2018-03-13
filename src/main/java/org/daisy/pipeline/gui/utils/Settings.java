package org.daisy.pipeline.gui.utils;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Settings {
    
    private static Preferences userPrefs, 
                                sysPrefs;
    
    enum Types {
        STRING,
        BOOLEAN;
    }
    
    public enum Prefs {
        LAST_OUT_DIR(PrefCategories.JOB_OPTIONS, Types.STRING, "Last Output Directory", ""),
        DEF_OUT_DIR(PrefCategories.JOB_OPTIONS, Types.STRING, "Default Output Directory", ""),
        DEF_OUT_DIR_ENABLED(PrefCategories.JOB_OPTIONS, Types.BOOLEAN, "Default Output Directory Enabled", "false"),
        LAST_IN_DIR(PrefCategories.JOB_OPTIONS, Types.STRING, "Last Input Directory", ""),
        DEF_IN_DIR(PrefCategories.JOB_OPTIONS, Types.STRING, "Default Input Directory", ""),
        DEF_IN_DIR_ENABLED(PrefCategories.JOB_OPTIONS, Types.BOOLEAN, "Default Input Directory Enabled", "false");
        
        PrefCategories category;
        String key, def;
        Types type;
        Prefs(PrefCategories category, Types type, String key, String def) {
            this.category = category;
            this.type = type;
            this.key = key;
            this.def = def;
        }
        public PrefCategories category() {return category;}
        public String key() {return key;}
        
        public String defString() {
            return def;
        }
        public boolean defBoolean() {
            return Boolean.parseBoolean(def);
        }
        
    }
    
    public enum PrefCategories {
        JOB_OPTIONS("Job Options", false);
        
        String val;
        boolean isSystem;
        PrefCategories(String val, boolean isSystem) {
            this.val = val;
            this.isSystem = isSystem;
        }
        public String val() {return val;}
        public boolean isSystem() {return isSystem;}
    }

    
/*-------------------------------------------------------------------------------------------------------*/
    
    public static void init() {
        userPrefs = Preferences.userRoot().node("daisy/pipeline");
        sysPrefs = Preferences.systemRoot().node("daisy/pipeline");
        buildDefaults();
    }
    
    
    private static void buildDefaults() {
        try {
            // Create Category nodes if they don't exist
            for (PrefCategories category: PrefCategories.values()) {
                if (category.isSystem && !sysPrefs.nodeExists(category.val))
                        sysPrefs.node(category.val);
                else if (!category.isSystem && !userPrefs.nodeExists(category.val))
                        userPrefs.node(category.val);
            }
            
            // Add default prefs if they don't exist
            for (Prefs pref: Prefs.values())
                switch (pref.type) {
                    case STRING:
                        if (getString(pref).equals(pref.defString()))
                            putString(pref, pref.defString());
                        break;
                    case BOOLEAN:
                        if (getBoolean(pref) == pref.defBoolean())
                            putBoolean(pref, pref.defBoolean());
                        break;
                }
            
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }
    }
    
    
/*-------------------------------------------------------------------------------------------------------*/
    
    public static String getString(Prefs pref) {
        if (pref.category.isSystem)
            return sysPrefs.node(pref.category.val).get(pref.key, pref.defString());
        else
            return userPrefs.node(pref.category.val).get(pref.key, pref.defString());
    }
    
    public static boolean getBoolean(Prefs pref) {
        if (pref.category.isSystem)
            return sysPrefs.node(pref.category.val).getBoolean(pref.key, pref.defBoolean());
        else
            return userPrefs.node(pref.category.val).getBoolean(pref.key, pref.defBoolean());
    }
    
    public static void putString(Prefs pref, String newValue) {
        if (pref.category.isSystem)
            sysPrefs.node(pref.category.val).put(pref.key, newValue);
        else
            userPrefs.node(pref.category.val).put(pref.key, newValue);
    }
    
    public static void putBoolean(Prefs pref, Boolean newValue) {
        if (pref.category.isSystem)
            sysPrefs.node(pref.category.val).putBoolean(pref.key, newValue);
        else
            userPrefs.node(pref.category.val).putBoolean(pref.key, newValue);
    }

    
/*-------------------------------------------------------------------------------------------------------*/
    // Unit Testing
    public static void main(String[] args) {
        Settings.init();
        Settings.putString(Prefs.LAST_OUT_DIR, "test1");
        Settings.putString(Prefs.DEF_OUT_DIR, "test");
        Settings.putString(Prefs.DEF_OUT_DIR, "test2");
        String test1 = Settings.getString(Prefs.LAST_OUT_DIR),
                test2 = Settings.getString(Prefs.DEF_OUT_DIR);
        
        assert test1.equals("test1"): "Expected: \"test1\", Output: \"" + test1 + "\""; 
        assert test2.equals("test2"): "Expected: \"test2\", Output: \"" + test2 + "\""; 
    }
}
