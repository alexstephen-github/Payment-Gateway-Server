package com.paygateway.mock.support;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.UUID;

/**
 * Small toolbox of random-value generators used to populate mock responses.
 * All methods are stateless and backed by {@link ThreadLocalRandom}.
 */
public final class Rand {

    private static final char[] ID_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

    private static final String[] CURRENCIES = {"USD", "EUR", "GBP", "CAD", "AUD", "JPY", "INR", "SGD"};
    private static final String[] CARD_BRANDS = {"visa", "mastercard", "amex", "discover", "unionpay", "jcb", "diners"};
    private static final String[] CARD_FUNDING = {"credit", "debit", "prepaid", "unknown"};
    private static final String[] COUNTRIES = {"US", "GB", "DE", "FR", "CA", "AU", "IN", "SG", "JP", "NL"};
    private static final String[] BANKS = {"Chase", "Bank of America", "Wells Fargo", "Citibank", "HSBC", "Barclays"};
    private static final String[] FIRST_NAMES = {"Alex", "Jordan", "Taylor", "Morgan", "Casey", "Riley", "Sam", "Jamie", "Drew", "Quinn"};
    private static final String[] LAST_NAMES = {"Smith", "Johnson", "Lee", "Patel", "Garcia", "Brown", "Nguyen", "Kim", "Khan", "Davis"};
    private static final String[] CITIES = {"San Francisco", "New York", "London", "Berlin", "Toronto", "Sydney", "Mumbai", "Singapore"};
    private static final String[] STREETS = {"Market St", "Main St", "Broadway", "King St", "Queen Ave", "Elm St", "Oak Rd"};

    private Rand() {
    }

    private static ThreadLocalRandom r() {
        return ThreadLocalRandom.current();
    }

    /** Resource id such as {@code pay_4Fk9Q2...} (prefix + 24 random chars). */
    public static String id(String prefix) {
        StringBuilder sb = new StringBuilder(prefix).append('_');
        for (int i = 0; i < 24; i++) {
            sb.append(ID_CHARS[r().nextInt(ID_CHARS.length)]);
        }
        return sb.toString();
    }

    public static String uuid() {
        return UUID.randomUUID().toString();
    }

    public static int intRange(int minInclusive, int maxInclusive) {
        return r().nextInt(minInclusive, maxInclusive + 1);
    }

    /** Monetary amount in the smallest currency unit. */
    public static int amount() {
        return intRange(100, 500_000);
    }

    public static boolean bool() {
        return r().nextBoolean();
    }

    public static boolean chance(double probability) {
        return r().nextDouble() < probability;
    }

    @SafeVarargs
    public static <T> T pick(T... values) {
        return values[r().nextInt(values.length)];
    }

    public static String currency() {
        return pick(CURRENCIES);
    }

    public static String cardBrand() {
        return pick(CARD_BRANDS);
    }

    public static String cardFunding() {
        return pick(CARD_FUNDING);
    }

    public static String country() {
        return pick(COUNTRIES);
    }

    public static String bankName() {
        return pick(BANKS);
    }

    public static String fullName() {
        return pick(FIRST_NAMES) + " " + pick(LAST_NAMES);
    }

    public static String email() {
        return (pick(FIRST_NAMES) + "." + pick(LAST_NAMES) + r().nextInt(1000)).toLowerCase()
                + "@example.com";
    }

    public static String phone() {
        return String.format("+1%010d", r().nextLong(1_000_000_000L));
    }

    public static String digits(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(r().nextInt(10));
        }
        return sb.toString();
    }

    public static String last4() {
        return digits(4);
    }

    public static int expMonth() {
        return intRange(1, 12);
    }

    public static int expYear() {
        return intRange(2026, 2034);
    }

    /** Timestamp within the last ~30 days. */
    public static Instant recentInstant() {
        return Instant.now().minus(r().nextInt(0, 30 * 24 * 60), ChronoUnit.MINUTES);
    }

    /** Timestamp within the next ~30 days. */
    public static Instant futureInstant() {
        return Instant.now().plus(r().nextInt(60, 30 * 24 * 60), ChronoUnit.MINUTES);
    }

    public static Instant now() {
        return Instant.now();
    }

    /** A handful of random metadata key/value pairs. */
    public static Map<String, String> metadata() {
        Map<String, String> meta = new LinkedHashMap<>();
        meta.put("order_id", id("order").substring(0, 12));
        meta.put("source", pick("web", "mobile", "api", "pos"));
        return meta;
    }

    public static String streetLine() {
        return intRange(1, 9999) + " " + pick(STREETS);
    }

    public static String city() {
        return pick(CITIES);
    }

    public static String postalCode() {
        return digits(5);
    }
}
