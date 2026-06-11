package com.inventory.backend_java.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.inventory.backend_java.DatabaseConnection;

public class DatasetImportService {

    private static final int PRODUCT_LIMIT = 500;
    private static final int PRODUCT_2023_LIMIT = 100;
    private static final int TRANSACTION_LIMIT = 800;
    private static final String IMPORT_KEY = "amazon-india-week6-v4";
    private static final Path DATA_DIR = Path.of("src", "main", "resources", "data");

    private static final String AMAZON_PRODUCTS_2023 =
            "amz_in_total_products_data_processed.csv";
    private static final String AMAZON_LISTINGS_2019 =
            "home_sdf_marketing_sample_for_amazon_in-ecommerce__20191001_20191031__30k_data.csv";

    public void importIfNeeded() {
        Path productsFile = DATA_DIR.resolve(AMAZON_PRODUCTS_2023);
        Path listingsFile = DATA_DIR.resolve(AMAZON_LISTINGS_2019);

        if (!Files.exists(productsFile) || !Files.exists(listingsFile)) {
            System.out.println("Week 6 Amazon India dataset import skipped.");
            System.out.println("Place these CSV files inside " + DATA_DIR.toAbsolutePath() + ":");
            System.out.println("- " + AMAZON_PRODUCTS_2023);
            System.out.println("- " + AMAZON_LISTINGS_2019);
            System.out.println("Existing database data will be used as a development fallback.");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            if (connection == null) {
                System.out.println("Week 6 dataset import skipped because database connection failed.");
                return;
            }

            ensureImportTable(connection);
            if (isAlreadyImported(connection)) {
                System.out.println("Week 6 Amazon India dataset already imported. Skipping reload.");
                return;
            }

            connection.setAutoCommit(false);
            clearInventoryTables(connection);
            List<Integer> supplierIds = insertIndianSuppliers(connection);
            Map<String, ImportedProduct> productsByAsin = importProducts(connection, productsFile, supplierIds);
            importListingProducts(connection, listingsFile, supplierIds, productsByAsin);
            int transactions = importTransactions(connection, listingsFile, productsByAsin);
            markImported(connection, productsByAsin.size(), transactions);
            connection.commit();

            System.out.println("Week 6 Amazon India dataset import complete.");
            System.out.println("Imported products: " + productsByAsin.size());
            System.out.println("Imported derived transactions: " + transactions);
        } catch (Exception e) {
            System.out.println("Week 6 dataset import failed. Existing APIs will remain available.");
            e.printStackTrace();
        }
    }

    private void ensureImportTable(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS dataset_import_status (
                        import_key TEXT PRIMARY KEY,
                        product_count INTEGER NOT NULL,
                        transaction_count INTEGER NOT NULL,
                        imported_at DATETIME DEFAULT CURRENT_TIMESTAMP
                    )
                    """);
        }
    }

    private boolean isAlreadyImported(Connection connection) throws SQLException {
        String sql = "SELECT COUNT(*) FROM dataset_import_status WHERE import_key = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, IMPORT_KEY);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        }
    }

    private void clearInventoryTables(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM stock_transactions");
            statement.executeUpdate("DELETE FROM products");
            statement.executeUpdate("DELETE FROM suppliers");
            statement.executeUpdate("DELETE FROM sqlite_sequence WHERE name IN ('stock_transactions','products','suppliers')");
        }
    }

    private List<Integer> insertIndianSuppliers(Connection connection) throws SQLException {
        String[][] suppliers = {
                {"Reliance Retail Supply Chain", "Amit Sharma", "022-4000-1101", "supply@relianceretail.example", "Mumbai"},
                {"Dmart Distribution", "Priya Nair", "040-4000-1102", "distribution@dmart.example", "Hyderabad"},
                {"Metro Cash & Carry India", "Rahul Verma", "080-4000-1103", "sourcing@metroindia.example", "Bengaluru"},
                {"BigBasket Supply Hub", "Sneha Iyer", "044-4000-1104", "hub@bigbasket.example", "Chennai"},
                {"More Retail Supply Chain", "Neha Kapoor", "011-4000-1105", "supply@more-retail.example", "Delhi"},
                {"Flipkart Wholesale", "Vikram Singh", "020-4000-1106", "wholesale@flipkart.example", "Pune"}
        };

        List<Integer> ids = new ArrayList<>();
        String sql = "INSERT INTO suppliers (supplier_name, contact_person, phone, email, address) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (String[] supplier : suppliers) {
                // Supplier mapping is generated only to connect real Amazon India products to inventory suppliers.
                statement.setString(1, supplier[0]);
                statement.setString(2, supplier[1]);
                statement.setString(3, supplier[2]);
                statement.setString(4, supplier[3]);
                statement.setString(5, supplier[4]);
                statement.executeUpdate();
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        ids.add(keys.getInt(1));
                    }
                }
            }
        }
        return ids;
    }

    private Map<String, ImportedProduct> importProducts(Connection connection, Path productsFile, List<Integer> supplierIds)
            throws IOException, SQLException {
        Map<String, ImportedProduct> productsByAsin = new LinkedHashMap<>();

        String sql = """
                INSERT INTO products (product_name, category, quantity, price, reorder_level, supplier_id)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (BufferedReader reader = Files.newBufferedReader(productsFile, StandardCharsets.UTF_8);
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            Map<String, Integer> headers = readHeader(reader);
            warnIfMissing(headers, productsFile.getFileName().toString(), "asin", "title", "categoryName", "price");

            String line;
            int rowIndex = 0;
            while ((line = reader.readLine()) != null && productsByAsin.size() < PRODUCT_2023_LIMIT) {
                rowIndex++;
                Map<String, String> row = mapRow(headers, parseCsvLine(line));

                String asin = value(row, "asin");
                String title = cleanText(value(row, "title"));
                String category = cleanCategory(value(row, "categoryName"));
                Double price = cleanPrice(value(row, "price"));
                double rating = cleanDouble(value(row, "stars"), 0.0);
                int reviews = cleanInt(value(row, "reviews"), 0);
                int bought = cleanInt(value(row, "boughtInLastMonth"), 0);

                if (asin.isBlank() || title.isBlank() || looksGarbled(title) || price == null || price <= 0) {
                    continue;
                }

                int supplierId = supplierIds.get(Math.abs(category.hashCode()) % supplierIds.size());
                int startingStock = calculateOpeningStock(category, price, rating, reviews, bought, rowIndex);
                int plannedSold = calculateSoldQuantity(price, rating, reviews, bought, rowIndex);
                int reorderLevel = calculateReorderLevel(startingStock, price, category);
                int finalStock = calculateFinalStock(startingStock, plannedSold, reorderLevel, rowIndex);

                // Product catalog data is imported from the Amazon India Products 2023 Kaggle dataset.
                statement.setString(1, title);
                statement.setString(2, category);
                statement.setInt(3, finalStock);
                statement.setDouble(4, round(price));
                statement.setInt(5, reorderLevel);
                statement.setInt(6, supplierId);
                statement.executeUpdate();

                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        productsByAsin.put(asin, new ImportedProduct(
                                keys.getInt(1), title, category, round(price), startingStock, plannedSold));
                    }
                }
            }
        }

        if (productsByAsin.isEmpty()) {
            System.out.println("No valid products imported from " + productsFile.getFileName()
                    + ". Check product title/category/price columns.");
        }

        return productsByAsin;
    }

    private void importListingProducts(Connection connection, Path listingsFile, List<Integer> supplierIds,
            Map<String, ImportedProduct> productsByAsin) throws IOException, SQLException {
        String sql = """
                INSERT INTO products (product_name, category, quantity, price, reorder_level, supplier_id)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (BufferedReader reader = Files.newBufferedReader(listingsFile, StandardCharsets.UTF_8);
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            Map<String, Integer> headers = readHeader(reader);
            String line;
            int rowIndex = 0;
            while ((line = reader.readLine()) != null && productsByAsin.size() < PRODUCT_LIMIT) {
                rowIndex++;
                Map<String, String> row = mapRow(headers, parseCsvLine(line));

                String asin = value(row, "Product Asin");
                if (asin.isBlank() || productsByAsin.containsKey(asin)) {
                    continue;
                }

                String title = cleanText(value(row, "Product Title"));
                String category = cleanCategory(value(row, "Category"));
                Double price = cleanPrice(value(row, "Price"));
                if (title.isBlank() || looksGarbled(title) || price == null || price <= 0) {
                    continue;
                }

                int supplierId = supplierIds.get(Math.abs(category.hashCode()) % supplierIds.size());
                int startingStock = calculateOpeningStock(category, price, 0, 0, 0, rowIndex);
                int plannedSold = calculateListingQuantity(row, null, rowIndex) * 4;
                int reorderLevel = calculateReorderLevel(startingStock, price, category);
                int finalStock = calculateFinalStock(startingStock, plannedSold, reorderLevel, rowIndex);

                // Additional catalog rows are imported from the 2019 Amazon India listing dataset.
                statement.setString(1, title);
                statement.setString(2, category);
                statement.setInt(3, finalStock);
                statement.setDouble(4, round(price));
                statement.setInt(5, reorderLevel);
                statement.setInt(6, supplierId);
                statement.executeUpdate();

                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        productsByAsin.put(asin, new ImportedProduct(
                                keys.getInt(1), title, category, round(price), startingStock, plannedSold));
                    }
                }
            }
        }
    }

    private int importTransactions(Connection connection, Path listingsFile, Map<String, ImportedProduct> productsByAsin)
            throws IOException, SQLException {
        if (productsByAsin.isEmpty()) {
            return 0;
        }

        String sql = """
                INSERT INTO stock_transactions (product_id, transaction_type, quantity, transaction_date, notes)
                VALUES (?, ?, ?, ?, ?)
                """;

        int inserted = 0;
        try (BufferedReader reader = Files.newBufferedReader(listingsFile, StandardCharsets.UTF_8);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            Map<String, Integer> headers = readHeader(reader);
            warnIfMissing(headers, listingsFile.getFileName().toString(),
                    "Product Asin", "Product Title", "Category", "Price", "Crawl Timestamp");

            String line;
            int rowIndex = 0;
            while ((line = reader.readLine()) != null && inserted < TRANSACTION_LIMIT) {
                rowIndex++;
                Map<String, String> row = mapRow(headers, parseCsvLine(line));

                String asin = value(row, "Product Asin");
                ImportedProduct product = productsByAsin.get(asin);
                if (product == null) {
                    continue;
                }

                Double rowPrice = cleanPrice(value(row, "Price"));
                double unitPrice = rowPrice != null && rowPrice > 0 ? rowPrice : product.price();
                int quantity = calculateListingQuantity(row, product, rowIndex);
                String date = cleanDate(value(row, "Crawl Timestamp"), rowIndex);
                double amount = round(unitPrice * quantity);

                // Transactions are derived from real Amazon India 2019 listing rows.
                // The source is a product-listing dataset, not a true order-history dataset.
                statement.setInt(1, product.productId());
                statement.setString(2, "Stock Out");
                statement.setInt(3, quantity);
                statement.setString(4, date);
                statement.setString(5, "Amazon India listing-derived sale | Amount INR " + amount
                        + " | Source category: " + cleanCategory(value(row, "Category")));
                statement.executeUpdate();
                inserted++;
            }
        }

        if (inserted == 0) {
            System.out.println("No matching ASIN transactions found in " + listingsFile.getFileName()
                    + ". Product endpoints will still work with imported catalog data.");
        }

        return inserted;
    }

    private void markImported(Connection connection, int productCount, int transactionCount) throws SQLException {
        String sql = """
                INSERT OR REPLACE INTO dataset_import_status (import_key, product_count, transaction_count)
                VALUES (?, ?, ?)
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, IMPORT_KEY);
            statement.setInt(2, productCount);
            statement.setInt(3, transactionCount);
            statement.executeUpdate();
        }
    }

    private Map<String, Integer> readHeader(BufferedReader reader) throws IOException {
        String headerLine = reader.readLine();
        Map<String, Integer> headers = new HashMap<>();
        if (headerLine == null) {
            return headers;
        }
        List<String> names = parseCsvLine(headerLine);
        for (int i = 0; i < names.size(); i++) {
            headers.put(normalizeHeader(names.get(i)), i);
        }
        return headers;
    }

    private Map<String, String> mapRow(Map<String, Integer> headers, List<String> values) {
        Map<String, String> row = new HashMap<>();
        for (Map.Entry<String, Integer> entry : headers.entrySet()) {
            int index = entry.getValue();
            row.put(entry.getKey(), index < values.size() ? values.get(index).trim() : "");
        }
        return row;
    }

    private List<String> parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                if (quoted && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    quoted = !quoted;
                }
            } else if (ch == ',' && !quoted) {
                values.add(current.toString());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }
        values.add(current.toString());
        return values;
    }

    private String value(Map<String, String> row, String key) {
        return row.getOrDefault(normalizeHeader(key), "");
    }

    private String normalizeHeader(String header) {
        return header == null ? "" : header.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }

    private void warnIfMissing(Map<String, Integer> headers, String fileName, String... required) {
        for (String column : required) {
            if (!headers.containsKey(normalizeHeader(column))) {
                System.out.println("Dataset column not found in " + fileName + ": " + column);
            }
        }
    }

    private String cleanText(String value) {
        if (value == null) {
            return "";
        }
        return value.replaceAll("\\s+", " ").trim();
    }

    private String cleanCategory(String value) {
        String category = cleanText(value);
        if (looksGarbled(category)) {
            return "Amazon India Products";
        }
        return category.isBlank() ? "Amazon India Products" : category;
    }

    private boolean looksGarbled(String value) {
        if (value == null) {
            return false;
        }
        int nonAscii = 0;
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) > 127) {
                nonAscii++;
            }
        }
        return value.contains("à¤") || value.contains("à¥") || value.contains("\uFFFD")
                || (!value.isBlank() && nonAscii > value.length() / 3);
    }

    private Double cleanPrice(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String cleaned = value.replace("₹", "")
                .replace(",", "")
                .replace("\"", "")
                .trim();
        try {
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private double cleanDouble(String value, double defaultValue) {
        try {
            return value == null || value.isBlank() ? defaultValue : Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private int cleanInt(String value, int defaultValue) {
        try {
            if (value == null || value.isBlank()) {
                return defaultValue;
            }
            return (int) Double.parseDouble(value.replace(",", "").trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private int calculateOpeningStock(String category, double price, double rating, int reviews, int bought, int rowIndex) {
        int popularity = bought > 0 ? Math.min(120, bought / 10) : Math.min(60, reviews / 250);
        int ratingBoost = rating >= 4.0 ? 20 : rating >= 3.0 ? 10 : 0;
        int categoryBoost = category.toLowerCase(Locale.ROOT).matches(".*(grocery|home|kitchen|beauty|personal|accessor).*")
                ? 25 : 10;
        int pricePenalty = price > 50000 ? 80 : price > 10000 ? 45 : price > 3000 ? 20 : 0;
        int stableVariation = rowIndex % 17;
        return Math.max(8, 90 + popularity + ratingBoost + categoryBoost + stableVariation - pricePenalty);
    }

    private int calculateSoldQuantity(double price, double rating, int reviews, int bought, int rowIndex) {
        int demand = bought > 0 ? Math.max(1, bought / 25) : Math.max(1, reviews / 800);
        int ratingDemand = rating >= 4.0 ? 6 : rating >= 3.0 ? 3 : 1;
        int priceLimit = price > 50000 ? 2 : price > 10000 ? 5 : price > 3000 ? 10 : 18;
        return Math.max(1, Math.min(70, demand + ratingDemand + (rowIndex % priceLimit)));
    }

    private int calculateReorderLevel(int startingStock, double price, String category) {
        int base = Math.max(5, startingStock / 5);
        if (price > 10000) {
            return Math.max(3, base / 2);
        }
        if (category.toLowerCase(Locale.ROOT).matches(".*(grocery|beauty|personal|home).*")) {
            return base + 8;
        }
        return base;
    }

    private int calculateFinalStock(int startingStock, int plannedSold, int reorderLevel, int rowIndex) {
        if (rowIndex % 19 == 0) {
            return Math.max(0, reorderLevel - (rowIndex % 5));
        }
        return Math.max(0, startingStock - plannedSold);
    }

    private int calculateListingQuantity(Map<String, String> row, ImportedProduct product, int rowIndex) {
        int base = product == null ? 2 : Math.max(1, product.plannedSold() / 8);
        String stock = value(row, "Stock Availibility").toLowerCase(Locale.ROOT);
        int stockSignal = stock.contains("yes") ? 2 : 1;
        return Math.max(1, Math.min(12, base + stockSignal + (rowIndex % 4)));
    }

    private String cleanDate(String value, int rowIndex) {
        if (value != null && value.length() >= 19) {
            return value.substring(0, 19);
        }
        int day = (rowIndex % 28) + 1;
        return "2019-10-" + String.format("%02d", day) + " 10:00:00";
    }

    private double round(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private record ImportedProduct(
            int productId,
            String productName,
            String category,
            double price,
            int openingStock,
            int plannedSold) {
    }
}
