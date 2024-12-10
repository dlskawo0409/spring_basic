package com.dlskawo0409.demo.common.util;

import java.util.regex.Pattern;

public class RegexUtil {
    public static boolean matches(String regex, String input) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(input).matches();
    }
}