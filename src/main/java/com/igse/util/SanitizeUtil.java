package com.igse.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.util.regex.Pattern;


public class SanitizeUtil {
    private SanitizeUtil() {
    }

    private static final Pattern SANITIZE_PATTERN = Pattern.compile("[<>;?'*|/]+");

    public static String sanitiseData(String data) {
        String cleanData = SANITIZE_PATTERN.matcher(data).replaceAll("_");
        if (StringUtils.isNotEmpty(cleanData)) {
            return StringEscapeUtils.escapeJava(StringEscapeUtils.escapeHtml3(cleanData)).replace('\t', '_')
                    .replace('\n', '_')
                    .replace('\r', '_');
        }
        return cleanData;
    }
}
