package org.profitsoft.profitsoft2.utils;

import org.profitsoft.profitsoft2.utils.json.JsonParser;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

public class UtilService {

    public <T> List<T> getParsedJsonList(MultipartFile jsonFile, Class<T> className) throws IOException {

        return new JsonParser().parse(jsonFile, className);
    }


    public String getCurrentDate() {
        Date currentDate = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(currentDate);
    }

    public boolean isNotEmptyOrNull(String str) {
        return str != null && !str.isEmpty();
    }

    public boolean isNotEmptyOrNull(List<?> list) {
        return list != null && !list.isEmpty();
    }
}
