package com.cakmods.loader;

import android.content.Context;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class AutoLoader {

    private static final String BASE_URL =
            "https://imgui.ngocthinhmodder.site/loadlibon/";

    private static final String VERSION_URL =
            BASE_URL + "version.txt";

    public static void start(Context context) {
        new Thread(() -> {
            try {

                String version = getVersion();

                String soName =
                        "libCakMods_" + version + ".so";

                File libFile =
                        new File(context.getFilesDir(), soName);

                if (!libFile.exists()) {

                    File tempFile =
                            new File(context.getFilesDir(),
                                    soName + ".tmp");

                    if (tempFile.exists()) {
                        tempFile.delete();
                    }

                    downloadFile(tempFile, version);

                    if (!verifyFile(tempFile)) {
                        tempFile.delete();
                        return;
                    }

                    if (!tempFile.renameTo(libFile)) {
                        throw new Exception(
                                "Failed rename " + tempFile);
                    }
                }

                libFile.setReadable(true, false);
                libFile.setExecutable(true, false);

                System.load(libFile.getAbsolutePath());

            } catch (Throwable e) {
                e.printStackTrace();
            }
        }, "CakModsLoader").start();
    }

    private static String getVersion() throws Exception {

        URL url = new URL(VERSION_URL);

        HttpURLConnection conn =
                (HttpURLConnection) url.openConnection();

        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);

        BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(
                                conn.getInputStream()));

        String version = reader.readLine();

        reader.close();
        conn.disconnect();

        if (version == null || version.trim().isEmpty()) {
            throw new Exception("Invalid version");
        }

        return version.trim();
    }

    private static void downloadFile(
            File outputFile,
            String version) throws Exception {

        String soUrl =
                BASE_URL +
                        "libCakMods_" +
                        version +
                        ".so";

        URL url = new URL(soUrl);

        HttpURLConnection connection =
                (HttpURLConnection) url.openConnection();

        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);

        connection.setRequestProperty(
                "User-Agent",
                "Mozilla/5.0");

        int responseCode =
                connection.getResponseCode();

        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new Exception(
                    "HTTP " + responseCode);
        }

        InputStream input =
                connection.getInputStream();

        FileOutputStream output =
                new FileOutputStream(outputFile);

        byte[] buffer = new byte[8192];

        int count;

        while ((count = input.read(buffer)) != -1) {
            output.write(buffer, 0, count);
        }

        output.flush();
        output.close();

        input.close();

        connection.disconnect();
    }

    private static boolean verifyFile(File file) {

        if (!file.exists()) {
            return false;
        }

        if (file.length() < 1024) {
            return false;
        }

        try {

            FileInputStream fis =
                    new FileInputStream(file);

            byte[] magic = new byte[4];

            int read = fis.read(magic);

            fis.close();

            return read == 4 &&
                    magic[0] == 0x7F &&
                    magic[1] == 'E' &&
                    magic[2] == 'L' &&
                    magic[3] == 'F';

        } catch (Exception e) {
            return false;
        }
    }
}