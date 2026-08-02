package com.saicone.mscript.util;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@ApiStatus.Internal
public final class Strings {

    public static boolean isQuote(int c) {
        return c == '\'' || c == '"' || c == '`';
    }

    @NotNull
    public static String[] splitQuoted(@NotNull String s, char separator) {
        final List<String> result = new ArrayList<>();
        final StringBuilder token = new StringBuilder();
        boolean inQuotes = false;
        char quoteChar = 0;
        boolean escape = false;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (escape) {
                token.append(c);
                escape = false;
                continue;
            }

            if (c == '\\') {
                escape = true;
                continue;
            }

            if (inQuotes) {
                if (c == quoteChar) {
                    inQuotes = false;
                } else {
                    token.append(c);
                }
            } else {
                if (isQuote(c)) {
                    inQuotes = true;
                    quoteChar = c;
                } else if (c == separator) {
                    result.add(token.toString());
                    token.setLength(0);
                } else {
                    token.append(c);
                }
            }
        }

        result.add(token.toString());

        return result.toArray(new String[0]);
    }

    Strings() {
    }
}
