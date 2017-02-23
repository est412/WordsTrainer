package est412.wordstrainer.utils;

import java.io.*;
import java.util.Properties;

/**
 * Created by Anybody on 23.02.2017.
 */
public class PropertiesFileSettings implements Settings {

    private static final String PROPERTIES_FILE_NAME = "WordsTrainer.properties";

    private static Properties settings = new Properties();
    private static PropertiesFileSettings INSTANCE;

    private PropertiesFileSettings() {
    }

    public static PropertiesFileSettings getInstance() throws IOException {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        INSTANCE = new PropertiesFileSettings();
        File file = new File(PROPERTIES_FILE_NAME);
        if (!file.exists()) {
            file.createNewFile();
        }
        try (InputStream inputStream = new FileInputStream(file)) {
            settings.load(inputStream);
        }
        return INSTANCE;
    }

    @Override
    public String getSetting(String name) {
        String value = settings.getProperty(name);
        if (value == null || "".equals(value)) {
            value = DefaultSettings.getSetting(name);
        }
        return value;
    }

    @Override
    public void setSetting(String name, String value) {
        settings.setProperty(name, value);
        try {
            saveSettings();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveSettings() throws IOException {
        File file = new File(PROPERTIES_FILE_NAME);
        if (!file.exists()) {
            file.createNewFile();
        }
        try (OutputStream outputStream = new FileOutputStream(file)) {
            settings.store(outputStream, null);
        }
    }

}
