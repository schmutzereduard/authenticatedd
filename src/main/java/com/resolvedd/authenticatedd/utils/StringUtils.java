package com.resolvedd.authenticatedd.utils;

public class StringUtils {

    public static boolean isNullOrEmpty(String input) {
        return input == null || input.isEmpty();
    }

    public static String buildString(String ... inputs) {

        StringBuilder resultBuilder = new StringBuilder();

        for (String input : inputs) {
            resultBuilder.append(input);
        }

        return resultBuilder.toString();
    }
}
