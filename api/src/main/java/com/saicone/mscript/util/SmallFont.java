/*
 *  MIT License.
 *
 *  Copyright (c) 2026 Rubenicos
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package com.saicone.mscript.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.tag.Modifying;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@ApiStatus.Internal
public final class SmallFont {

    public static final List<Character> SMALL_ALPHABET = List.of(
            'ᴀ',
            'ʙ',
            'ᴄ',
            'ᴅ',
            'ᴇ',
            'ꜰ',
            'ɢ',
            'ʜ',
            'ɪ',
            'ᴊ',
            'ᴋ',
            'ʟ',
            'ᴍ',
            'ɴ',
            'ᴏ',
            'ᴘ',
            'ꞯ',
            'ʀ',
            'ꜱ',
            'ᴛ',
            'ᴜ',
            'ᴠ',
            'ᴡ',
            'х',
            'ʏ',
            'ᴢ'
    );
    public static final List<Character> SMALL_NUMBER = List.of(
            'ø',
            'ı',
            'ƨ',
            'ɜ',
            'д',
            'ѕ',
            'ь',
            '⁊',
            'ɵ',
            'ɘ'
    );
    public static final Map<Character, Character> SMALL = new HashMap<>();
    static {
        char lowercase = 'a';
        for (Character c : SMALL_ALPHABET) {
            SMALL.put(lowercase, c);
            lowercase++;
        }

        char uppercase = 'A';
        for (Character c : SMALL_ALPHABET) {
            SMALL.put(uppercase, c);
            uppercase++;
        }

        char num = '0';
        for (Character c : SMALL_NUMBER) {
            SMALL.put(num, c);
            num++;
        }
    }

    public static final List<Character> SUPERSCRIPT_NUMBER = List.of(
            '⁰',
            '¹',
            '²',
            '³',
            '⁴',
            '⁵',
            '⁶',
            '⁷',
            '⁸',
            '⁹'
    );
    public static final Map<Character, Character> SUPERSCRIPT = new HashMap<>();
    static {
        char num = '0';
        for (Character c : SUPERSCRIPT_NUMBER) {
            SUPERSCRIPT.put(num, c);
            num++;
        }
        SUPERSCRIPT.put('+', '⁺');
        SUPERSCRIPT.put('-', '⁻');
        SUPERSCRIPT.put('=', '⁼');
        SUPERSCRIPT.put('(', '⁽');
        SUPERSCRIPT.put(')', '⁾');
    }

    public static final List<Character> SUBSCRIPT_NUMBER = List.of(
            '₀',
            '₁',
            '₂',
            '₃',
            '₄',
            '₅',
            '₆',
            '₇',
            '₈',
            '₉'
    );
    public static final Map<Character, Character> SUBSCRIPT = new HashMap<>();
    static {
        char num = '0';
        for (Character c : SUBSCRIPT_NUMBER) {
            SUBSCRIPT.put(num, c);
            num++;
        }
        SUBSCRIPT.put('+', '₊');
        SUBSCRIPT.put('-', '₋');
        SUBSCRIPT.put('=', '₌');
        SUBSCRIPT.put('(', '₍');
        SUBSCRIPT.put(')', '₎');
    }

    @FunctionalInterface
    public interface Tag extends Modifying, Function<Character, Character> {
        @Override
        default Component apply(@NotNull Component current, int depth) {
            if (depth == 0) {
                return Component.empty();
            }
            if (current instanceof TextComponent) {
                final String content = ((TextComponent) current).content();
                final String parsed = parse(content);
                if (!content.equals(parsed)) {
                    current = ((TextComponent) current).content(parsed);
                }
            }
            return current;
        }

        @NotNull
        private String parse(@NotNull String s) {
            final StringBuilder builder = new StringBuilder(s.length());
            for (int i = 0; i < s.length(); i++) {
                builder.append(apply(s.charAt(i)));
            }
            return builder.toString();
        }
    }

    public static final Tag TAG_SMALL = SmallFont::getSmall;
    public static final Tag TAG_SMALL_ALL = SmallFont::getSmallAll;
    public static final Tag TAG_SMALL_SUPERSCRIPT = SmallFont::getSmallSuperscript;
    public static final Tag TAG_SMALL_SUBSCRIPT = SmallFont::getSmallSubscript;

    public static char getSmall(char c) {
        final int index = Character.toUpperCase(c) - 'A';
        return (index >= 0 && index < SMALL_ALPHABET.size()) ? SMALL_ALPHABET.get(index) : c;
    }

    @NotNull
    public static Character getSmallAll(@NotNull Character c) {
        return SMALL.getOrDefault(c, c);
    }

    @NotNull
    public static Character getSmallSuperscript(@NotNull Character c) {
        final Character small = SUPERSCRIPT.get(c);
        return small != null ? small : getSmall(c);
    }

    @NotNull
    public static Character getSmallSubscript(@NotNull Character c) {
        final Character small = SUBSCRIPT.get(c);
        return small != null ? small : getSmall(c);
    }

    SmallFont() {
    }
}
