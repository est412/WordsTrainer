package est412.wordstrainer.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anybody on 23.02.2017.
 */
public final class DefaultSettings {

    private static Map<String, String> defautls = new HashMap<>();
    static
    {
        defautls.put(Settings.DICTIONARIES_DIRECTORY, ".");
        defautls.put(Settings.LAST_DICTIONARY, "");
        defautls.put(Settings.WIDTH, null);
        defautls.put(Settings.HEIGHT, null);
    }

    public static String getSetting(String name) {
        return defautls.get(name);
    }

}
