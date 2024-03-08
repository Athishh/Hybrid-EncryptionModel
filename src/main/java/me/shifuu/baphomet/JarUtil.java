package me.shifuu.baphomet;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class JarUtil {

    public static void writeJar(File outputJar, Map<String, byte[]> classBytes, Map<String, byte[]> nonClassEntries) {
        try (JarOutputStream jos = new JarOutputStream(Files.newOutputStream(outputJar.toPath()))) {
            for (Map.Entry<String, byte[]> entry : classBytes.entrySet()) {
                String entryKey = entry.getKey().endsWith(".class") ? entry.getKey() : entry.getKey() + ".class";
                JarEntry jarEntry = new JarEntry(entryKey);
                jos.putNextEntry(jarEntry);
                jos.write(entry.getValue());
                jos.closeEntry();
            }

            for (Map.Entry<String, byte[]> entry : nonClassEntries.entrySet()) {
                JarEntry jarEntry = new JarEntry(entry.getKey());
                jos.putNextEntry(jarEntry);
                jos.write(entry.getValue());
                jos.closeEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, byte[]> loadJar(File jarFile) throws IOException {
        Map<String, byte[]> classBytes = new HashMap<>();
        try (JarFile jar = new JarFile(jarFile)) {
            jar.stream()
                    .filter(entry -> entry.getName().endsWith(".class"))
                    .forEach(entry -> classBytes.put(entry.getName(), getBytes(jar, entry)));
        }
        return classBytes;
    }

    public static Map<String, byte[]> loadNonClassEntries(File jarFile) throws IOException {
        Map<String, byte[]> nonClassEntries = new HashMap<>();
        try (JarFile jar = new JarFile(jarFile)) {
            jar.stream()
                    .filter(entry -> !entry.getName().endsWith(".class"))
                    .forEach(entry -> nonClassEntries.put(entry.getName(), getBytes(jar, entry)));
        }
        return nonClassEntries;
    }

    private static byte[] getBytes(JarFile jar, JarEntry entry) {
        try (InputStream is = jar.getInputStream(entry)) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[16384];
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            return buffer.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
