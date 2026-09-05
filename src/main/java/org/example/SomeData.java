package org.example;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SomeData {
    private List<Price> prices = new ArrayList<>();

    public List<Price> getPrices() {
        return prices;
    }
    public void parse() {
        try {
            InputStream inputStream = SomeData.class
                    .getClassLoader()
                    .getResourceAsStream("data_price.json");
            JsonObject jsonObject = (JsonObject) Jsoner.deserialize(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8)
            );
            JsonArray items = (JsonArray) jsonObject.get("товары");
            for (Object item: items){
                JsonObject productJson = (JsonObject) item;
                String articul =  String.valueOf(productJson.get("Артикул"));
                Object priceObj = productJson.get("Цена");
                int price = 0;
                if (priceObj instanceof BigDecimal) {
                    price = ((BigDecimal) priceObj).intValue();
                } else if (priceObj instanceof Number) {
                    price = ((Number) priceObj).intValue();
                }
                prices.add(new Price(articul, price));
            }
        } catch (JsonException e) {
            System.err.println("Ошибка парсинга JSON!");
            e.printStackTrace();
        }
    }
}
