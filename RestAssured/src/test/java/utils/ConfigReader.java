package utils;

import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private static Properties properties;

    static {
        try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties")) {
            properties = new Properties();
            if (input == null) {
                throw new RuntimeException("Arquivo config.properties não encontrado no classpath.");
            }
            properties.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar configurações: " + e.getMessage());
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}