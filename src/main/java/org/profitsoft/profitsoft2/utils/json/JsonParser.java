package org.profitsoft.profitsoft2.utils.json;


import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.profitsoft.profitsoft2.database.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class JsonParser {
    /**
     * Parses a JSON file into a list of objects of the specified type.
     *
     * @param <T>       The type of objects to create.
     * @param jsonFile  The MultipartFile containing the JSON data.
     * @param className The class of the objects to create.
     * @return A list of objects created from the JSON, or an empty list if the JSON is empty.
     * @throws IOException If an error occurs while reading the JSON file.
     * @throws NullPointerException If className is null.
     */
    public <T> List<T> parse(MultipartFile jsonFile, Class<T> className) throws IOException {
        if (className == null) {
            throw new NullPointerException("Class cannot be null");
        }
        if (jsonFile == null) {
            throw new NullPointerException("MultipartFile cannot be null");
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(jsonFile.getInputStream()))) {
            return parseJsonToObjectList(reader, className);
        }
    }

    /**
     * Parses a JSON file into a list of objects of the specified type.
     *
     * @param reader    The BufferedReader used to read the JSON file.
     * @param valueType The type of objects to create from the JSON.
     * @param <T>       The type of objects to create.
     * @return A list of objects created from the JSON, or an empty list if the JSON is empty.
     * @throws IOException If an error occurs while reading the JSON file.
     */
    private <T> List<T> parseJsonToObjectList(BufferedReader reader, Class<T> valueType) {
        StringBuilder jsonBuilder = new StringBuilder();
        reader.lines().forEach(jsonBuilder::append);
        if (jsonBuilder.toString().isEmpty()) {
            return new ArrayList<>();
        }
        JSONArray jsonArr = new JSONArray(jsonBuilder.toString());
        return IntStream.range(0, jsonArr.length())
                .mapToObj(jsonArr::getJSONObject)
                .map(jsonObj -> {
                    try {
                        return createObjectFromJson(jsonObj, valueType);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * Creates an object of the specified type from a JSON object.
     *
     * @param jsonObj    The JSON object containing the data.
     * @param valueType  The type of the object to create.
     * @param <T>        The type of the object.
     * @return The created object, or null if an error occurred.
     */
    private <T> T createObjectFromJson(JSONObject jsonObj, Class<T> valueType) {
        try {
            T object = valueType.getDeclaredConstructor().newInstance();

            for (Field field : valueType.getDeclaredFields()) {
                String fieldName = field.getName();
                Object jsonValue = jsonObj.opt(fieldName);

                if (jsonValue != null) {
                    field.setAccessible(true);

                    if (field.getType().equals(String.class)) {
                        field.set(object, jsonValue.toString());
                    } else if (field.getType().equals(Integer.class)) {
                        field.set(object, Integer.parseInt(jsonValue.toString()));
                    } else if (field.getType().equals(LocalDate.class)) {
                        field.set(object, LocalDate.parse(jsonValue.toString()));
                    } else if (field.getType().equals(Boolean.class)) {
                        field.set(object, Boolean.parseBoolean(jsonValue.toString()));
                    } else if (field.getType().equals(Double.class)) {
                        field.set(object, Double.parseDouble(jsonValue.toString()));
                    } else if (field.getType().equals(User.class)) {
                        JSONObject userJson = jsonObj.getJSONObject("user");
                        User user = new User();
                        user.setId(userJson.getInt("id"));
                        field.set(object, user);
                    }
                } else {
                    throw new IllegalArgumentException("Field not found: " + fieldName);
                }
            }
            return object;
        } catch (Exception e) {
            return null;
        }
    }
}
