package est412.wordstrainer.utils;

/**
 * Created by Anybody on 23.02.2017.
 */
public interface Settings {

    final String DICTIONARIES_DIRECTORY = "dir";
    final String LAST_DICTIONARY = "last";
    final String WIDTH = "width";
    final String HEIGHT = "height";
    final String FONT_0 = "font_lang0";
    final String FONT_EX_0 = "font_lang0Example";
    final String FONT_SIZE_0 = "font_size_lang0";
    final String FONT_SIZE_EX_0 = "font_size_lang0Example";
    final String FONT_1 = "font_lang1";
    final String FONT_EX_1 = "font_lang1Example";
    final String FONT_SIZE_1 = "font_size_lang1";
    final String FONT_SIZE_EX_1 = "font_size_lang1Example";

    String getSetting(String name);
    void setSetting(String name, String value);

}
