package com.m2z.activity.tracker.controller;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

public class PropertiesClient {

    public static final String CONSUMER_KEY = "consumer_key";
    public static final String PRIVATE_KEY = "private_key";
    public static final String REQUEST_TOKEN = "request_token";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String SECRET = "secret";
    public static final String JIRA_HOME = "jira_home";


    private final static Map<String, String> DEFAULT_PROPERTY_VALUES = ImmutableMap.<String, String>builder()
            .put(JIRA_HOME, "https://auth.atlassian.com")
            .put(CONSUMER_KEY, "OauthKey")
            .put(PRIVATE_KEY, "AAABAHXRbo27V9OD436tMpjmG649sK0jXv3CWQdmlJfsh6zzaZIDjXxAa/EzI7ty 1j+eoGd+VuAW8DhS7W02kgB1ZDZuerFeHJIzOrhNMsNCC0tf00BpxLAHqxD4WD5B AhZdqS3HlTVPzsMzzsxY5lOJ+q0n3klHg8lOBuEwsxWwkhF6sEOJu5k5s21z+zvX 5i8ymVkD7DkdB+l1cBX074zL4Ov98Tp+EOQ7qFh1yebAHRPuP32RdNq6g3Ozw4eR oP9BK7JN0FbCXtGuP5Q07pwHWESkTutccZk8J8tLbbf1HnDTbNJN/hms8/yU9RP2 7gY6mzea3j43l5W6KA/63Mgi4gEAAACBAOzp3X/uch9tB4uZUpuA0EFYtOBmy4CS tjGSTReW3iXalzJYHWL3Rsn25b7cNcEPZt9r2b3LSbzlPfSvFcdVy0XZKb09y5UC I1c7xqAXJZaFP+KK2Aj1E3mh6jPd605NuNvbxa2qpywXUMa5Xbrtok/WRu47oMvV eLi4VdHcXizVAAAAgQCNhff9t3xRFp7zt/1HO5WySs6KCugJgMDtbHPvFPyA59Yd 7p9rReUm1FNDPUlGbdosw5AZxxTfekD60ZoISTqab5C5+1nFFj53t8Qv5x/UGrUd kyQfriRbDxVrbRlsaSHVwfH3RiafGOB0AuvRIvbCQdMMsVMPW+TPNknRSLTjgQAA AIBYb0EDZZb995cOMn0m6l6B6cRXmK0SQtillWrS8QOsNIk1BOgt21QqPu8J1310 M+WBeap3SjxOU9P5VQn+QKSyYiJeUvKg4+igmRMzVGQwHnStzSfwtP5y740bGtAM z4o/zGt9nukJWtahqsgkizvMe12560jZKtVphj+fereQAw==")
            .build();

    private final String fileUrl;
    private final String propFileName = "config.properties";

    public PropertiesClient() throws Exception {
        fileUrl = "./" + propFileName;
    }

    public Map<String, String> getPropertiesOrDefaults() {
        try {
            Map<String, String> map = toMap(tryGetProperties());
            map.putAll(Maps.difference(map, DEFAULT_PROPERTY_VALUES).entriesOnlyOnRight());
            return map;
        } catch (FileNotFoundException e) {
            tryCreateDefaultFile();
            return new HashMap<>(DEFAULT_PROPERTY_VALUES);
        } catch (IOException e) {
            return new HashMap<>(DEFAULT_PROPERTY_VALUES);
        }
    }

    private Map<String, String> toMap(Properties properties) {
        return properties.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(o -> o.getKey().toString(), t -> t.getValue().toString()));
    }

    private Properties toProperties(Map<String, String> propertiesMap) {
        Properties properties = new Properties();
        propertiesMap.entrySet()
                .stream()
                .forEach(entry -> properties.put(entry.getKey(), entry.getValue()));
        return properties;
    }

    private Properties tryGetProperties() throws IOException {
        InputStream inputStream = new FileInputStream(new File(fileUrl));
        Properties prop = new Properties();
        prop.load(inputStream);
        return prop;
    }

    public void savePropertiesToFile(Map<String, String> properties) {
        OutputStream outputStream = null;
        File file = new File(fileUrl);

        try {
            outputStream = new FileOutputStream(file);
            Properties p = toProperties(properties);
            p.store(outputStream, null);
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            closeQuietly(outputStream);
        }
    }

    public void tryCreateDefaultFile() {
        System.out.println("Creating default properties file: " + propFileName);
        tryCreateFile().ifPresent(file -> savePropertiesToFile(DEFAULT_PROPERTY_VALUES));
    }

    private Optional<File> tryCreateFile() {
        try {
            File file = new File(fileUrl);
            file.createNewFile();
            return Optional.of(file);
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            // ignored
        }
    }
}
