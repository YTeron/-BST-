package org.example;

import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class App {
    public static void main(String[] args) {
        System.out.println("=" .repeat(80));
        System.out.println("📊 СРАВНЕНИЕ ПОИСКА: СПИСОК vs BST");
        System.out.println("=".repeat(80));

        // 1. ЗАГРУЖАЕМ ДАННЫЕ
        SomeData data = new SomeData();
        data.parse();
        List<Price> prices = data.getPrices();

        System.out.println("\n📂 ЗАГРУЖЕНЫ ДАННЫЕ:");
        System.out.println("   Количество записей: " + prices.size());

        // Выводим все данные
        for (Price p : prices) {
            System.out.printf("   Артикул: %-12s | Цена: %d руб.%n",
                    p.getArticul(), p.getPrice());
        }

        // 2. СТРОИМ BST
        System.out.println("\n🌳 ПОСТРОЕНИЕ BST:");
        BST bst = new BST();

        long buildStart = System.nanoTime();
        for (Price p : prices) {
            bst.insert(p.articul, p.price);
        }
        long buildEnd = System.nanoTime();

        System.out.println("   Время построения: " + (buildEnd - buildStart) / 1_000_000 + " мс");
        System.out.println("   Размер дерева: " + bst.getSize());
        System.out.println("   Высота дерева: " + bst.getHeight());
        System.out.println("   ⚠️  Вырождено в список: " + (bst.getHeight() == bst.getSize() ? "ДА" : "НЕТ"));

        // 3. ТЕСТИРУЕМ ПОИСК ДЛЯ РАЗНЫХ АРТИКУЛОВ
        System.out.println("\n" + "=" .repeat(80));
        System.out.println("🔍 СРАВНЕНИЕ СКОРОСТИ ПОИСКА");
        System.out.println("=" .repeat(80));

        System.out.println("\n📊 ПОИСК РАЗНЫХ АРТИКУЛОВ:");
        System.out.println("-".repeat(80));
        System.out.printf("%-15s | %-12s | %-12s | %-12s | %-15s%n",
                "Артикул", "Список (нс)", "BST (нс)", "Сравнений", "Ускорение");
        System.out.println("-".repeat(80));

        // Тестируем разные артикулы
        String[] testArticuls = {
                prices.get(0).articul,              // первый
                prices.get(1).articul,              // второй
                prices.get(prices.size()/4).articul, // 25%
                prices.get(prices.size()/2).articul, // 50%
                prices.get(3*prices.size()/4).articul, // 75%
                prices.get(prices.size()-1).articul, // последний
                "ART_99999"                         // несуществующий
        };

        for (String articul : testArticuls) {
            // Поиск в списке (линейный)
            long listStart = System.nanoTime();
            int listPrice = findInList(prices, articul);
            long listEnd = System.nanoTime();
            long listTime = listEnd - listStart;

            // Поиск в BST
            long bstStart = System.nanoTime();
            int bstPrice = bst.search(articul);
            long bstEnd = System.nanoTime();
            long bstTime = bstEnd - bstStart;

            // Вывод
            String priceStr1 = listPrice != -1 ? String.valueOf(listPrice) : "не найден";
            String priceStr2 = bstPrice != -1 ? String.valueOf(bstPrice) : "не найден";
            String speedup = listTime > 0 ? String.format("%.1fx", (double)listTime / bstTime) : "N/A";

            System.out.printf("%-15s | %-12d | %-12d | %-12d | %-15s%n",
                    articul, listTime, bstTime, bst.getComparisons(), speedup);
        }

        // 4. ЗАМЕР ДЛЯ РАЗНЫХ РАЗМЕРОВ
        System.out.println("\n" + "=" .repeat(80));
        System.out.println("📉 ЗАМЕР СКОРОСТИ ПРИ РОСТЕ ДАННЫХ");
        System.out.println("=" .repeat(80));

        System.out.println("\n📊 ВРЕМЯ ПОИСКА ПОСЛЕДНЕГО ЭЛЕМЕНТА:");
        System.out.println("-".repeat(80));
        System.out.printf("%-10s | %-15s | %-15s | %-15s | %-10s%n",
                "Размер", "Список (нс)", "BST (нс)", "Сравнений", "O(n)");
        System.out.println("-".repeat(80));

        int[] sizes = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};

        for (int size : sizes) {
            if (size > prices.size()) break;

            // Берем подсписок из size элементов
            List<Price> subList = prices.subList(0, size);

            // Строим BST из подсписка
            BST testBst = new BST();
            for (Price p : subList) {
                testBst.insert(p.articul, p.price);
            }

            // Ищем последний элемент
            String lastArt = subList.get(size - 1).articul;

            // Поиск в списке
            long listStart = System.nanoTime();
            findInList(subList, lastArt);
            long listEnd = System.nanoTime();
            long listTime = listEnd - listStart;

            // Поиск в BST
            long bstStart = System.nanoTime();
            testBst.search(lastArt);
            long bstEnd = System.nanoTime();
            long bstTime = bstEnd - bstStart;

            System.out.printf("%-10d | %-15d | %-15d | %-15d | O(%d)%n",
                    size, listTime, bstTime, testBst.getComparisons(), size);
        }

        // 5. 1000 ПОИСКОВ ДЛЯ ТОЧНОСТИ
        System.out.println("\n" + "=" .repeat(80));
        System.out.println("⚡ ТЕСТ: 1000 ПОИСКОВ");
        System.out.println("=" .repeat(80));

        String searchArt = prices.get(prices.size() - 1).articul; // последний (худший случай)
        int iterations = 1000;

        // 1000 поисков в списке
        long listStartTotal = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            findInList(prices, searchArt);
        }
        long listEndTotal = System.nanoTime();
        long listTotalTime = listEndTotal - listStartTotal;

        // 1000 поисков в BST
        long bstStartTotal = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            bst.search(searchArt);
        }
        long bstEndTotal = System.nanoTime();
        long bstTotalTime = bstEndTotal - bstStartTotal;

        System.out.println("\n📊 РЕЗУЛЬТАТЫ ДЛЯ " + iterations + " ПОИСКОВ АРТИКУЛА " + searchArt + ":");
        System.out.println("-".repeat(80));
        System.out.printf("%-15s | %-15s | %-15s | %-10s%n",
                "Метод", "Общее время", "Среднее время", "Сравнений");
        System.out.println("-".repeat(80));
        System.out.printf("%-15s | %-15d | %-15.2f | %-10d%n",
                "Список", listTotalTime, (double)listTotalTime / iterations, prices.size());
        System.out.printf("%-15s | %-15d | %-15.2f | %-10d%n",
                "BST", bstTotalTime, (double)bstTotalTime / iterations, bst.getComparisons());
        System.out.println("-".repeat(80));

        System.out.printf("\n⚡ УСКОРЕНИЕ BST в %.1f раз\n", (double)listTotalTime / bstTotalTime);
        System.out.printf("📊 Сравнений: Список = %d, BST = %d\n", prices.size(), bst.getComparisons());
        System.out.printf("📉 Список в %.1f раз медленнее\n", (double)bst.getComparisons() / prices.size());

        // 6. ВИЗУАЛИЗАЦИЯ СРАВНЕНИЯ
        System.out.println("\n" + "=" .repeat(80));
        System.out.println("📊 ВИЗУАЛИЗАЦИЯ СРАВНЕНИЯ");
        System.out.println("=" .repeat(80));

        System.out.println("\n📈 ГРАФИК ВРЕМЕНИ ПОИСКА (10 ПОИСКОВ):");
        System.out.println("-".repeat(80));
        System.out.printf("%-15s | %-10s | %-10s%n", "Артикул", "Список", "BST");
        System.out.println("-".repeat(80));

        String[] visualArticuls = {
                prices.get(0).articul,
                prices.get(2).articul,
                prices.get(4).articul,
                prices.get(6).articul,
                prices.get(8).articul,
                prices.get(10).articul
        };

        for (String art : visualArticuls) {
            // 10 поисков в списке
            long listTime10 = 0;
            for (int i = 0; i < 10; i++) {
                long start = System.nanoTime();
                findInList(prices, art);
                long end = System.nanoTime();
                listTime10 += (end - start);
            }
            listTime10 /= 10;

            // 10 поисков в BST
            long bstTime10 = 0;
            for (int i = 0; i < 10; i++) {
                long start = System.nanoTime();
                bst.search(art);
                long end = System.nanoTime();
                bstTime10 += (end - start);
            }
            bstTime10 /= 10;

            // Создаем бары
            int listBar = (int)Math.min(listTime10 / 1000, 40);
            int bstBar = (int)Math.min(bstTime10 / 1000, 40);

            String listStr = "█".repeat(listBar);
            String bstStr = "█".repeat(bstBar);

            System.out.printf("%-15s | %-10s | %-10s%n",
                    art, listStr + " " + listTime10 + "нс", bstStr + " " + bstTime10 + "нс");
        }
    }

    // ===== МЕТОД ДЛЯ ПОИСКА В СПИСКЕ =====
    private static int findInList(List<Price> prices, String articul) {
        for (Price p : prices) {
            if (p.articul.equals(articul)) {
                return p.price;
            }
        }
        return -1;
    }
}