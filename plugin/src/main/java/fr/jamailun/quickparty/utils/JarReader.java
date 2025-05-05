package fr.jamailun.quickparty.utils;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Class made from <a href="https://stackoverflow.com/questions/1429172/how-to-list-the-files-inside-a-jar-file#answer-1435649">this answer</a>.
 */
public class JarReader {

    /**
     * Extract all <b>file names</b> with a specific prefix, from the jar.
     * @param folder the prefix (i.e. a folder)
     * @return a non-null list of file names.
     * @throws IOException if an error occurs.
     */
    public static @NotNull List<String> extractJarFiles(@NotNull String folder) throws IOException {
        CodeSource src = JarReader.class.getProtectionDomain().getCodeSource();
        Preconditions.checkState(src != null, "Code source is null ?");
        List<String> list = new ArrayList<>();

        URL jar = src.getLocation();
        ZipInputStream zip = new ZipInputStream( jar.openStream());
        ZipEntry ze;
        while((ze = zip.getNextEntry()) != null) {
            String entryName = ze.getName();
            if( entryName.startsWith(folder) ) {
                list.add(entryName);
            }
        }
        return list;
    }
}
