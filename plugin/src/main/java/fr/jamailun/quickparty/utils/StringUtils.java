package fr.jamailun.quickparty.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public final class StringUtils {
    private StringUtils() {}

    public static @NotNull Component parseString(@NotNull String raw) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(raw);
    }

    public static @NotNull List<String> splitWithDelimiters(@NotNull String string, @NotNull String @NotNull ... cuts) {
        List<String> out = List.of(string);
        for(String cut : cuts) {
            out = splitWithDelimiters(out, cut);
        }
        return out;
    }

    public static @NotNull List<String> splitWithDelimiters(@NotNull List<String> list, @NotNull String separator) {
        List<String> out = new ArrayList<>();
        for(String elem : list) {
            String[] parts = elem.splitWithDelimiters(separator, -1);
            out.addAll(List.of(parts));
        }
        return out;
    }

    public static @NotNull String formatError(@NotNull Throwable t) {
        StringBuilder sb = new StringBuilder();
        t.printStackTrace(new PrintWriter(new Writer() {
            @Override
            public void write(char @NotNull [] buf, int off, int len) {
                sb.append(String.copyValueOf(buf, off, len));
            }
            @Override
            public void write(@NotNull String str) {
                sb.append(str);
            }
            @Override public void flush() {}
            @Override public void close() {}
        }));
        return sb.toString();
    }

}
