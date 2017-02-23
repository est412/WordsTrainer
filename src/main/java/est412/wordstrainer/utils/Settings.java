package est412.wordstrainer.utils;

/**
 * Created by Anybody on 23.02.2017.
 */
public interface Settings {

    final String DICTIONARIES_DIRECTORY = "dir";
    final String LAST_DICTIONARY = "last";
    final String WIDTH = "width";
    final String HEIGHT = "height";

    String getSetting(String name);
    void setSetting(String name, String value);

}
