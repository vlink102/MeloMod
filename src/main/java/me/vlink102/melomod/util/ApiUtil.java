package me.vlink102.melomod.util;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.vlink102.melomod.MeloMod;
import me.vlink102.melomod.config.MeloConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.GZIPInputStream;

public class ApiUtil {
    private static final Gson gson = new Gson();
    private static final ExecutorService executorService = Executors.newFixedThreadPool(3);
    private static final String USER_AGENT = MeloMod.MODID + "/" + MeloMod.VERSION;

    public static class Request {

        private final List<NameValuePair> queryArguments = new ArrayList<>();
        private String baseUrl = null;
        private boolean shouldGunzip = false;
        private String method = "GET";

        public Request method(String method) {
            this.method = method;
            return this;
        }

        public Request url(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Request queryArgument(String key, String value) {
            queryArguments.add(new BasicNameValuePair(key, value));
            return this;
        }

        public Request queryArguments(Collection<NameValuePair> queryArguments) {
            this.queryArguments.addAll(queryArguments);
            return this;
        }

        public Request gunzip() {
            shouldGunzip = true;
            return this;
        }

        private CompletableFuture<URL> buildUrl() {
            CompletableFuture<URL> fut = new CompletableFuture<>();
            try {
                fut.complete(new URIBuilder(baseUrl)
                        .addParameters(queryArguments)
                        .build()
                        .toURL());
            } catch (URISyntaxException |
                     MalformedURLException |
                     NullPointerException e) { // Using CompletableFuture as an exception monad, isn't that exiting?
                fut.completeExceptionally(e);
            }
            return fut;
        }

        public CompletableFuture<String> requestString() {
            return buildUrl().thenApplyAsync(url -> {
                try {
                    InputStream inputStream = null;
                    URLConnection conn = null;
                    try {
                        conn = url.openConnection();
                        if (conn instanceof HttpURLConnection) {
                            ((HttpURLConnection) conn).setRequestMethod(method);
                        }
                        conn.setConnectTimeout(10000);
                        conn.setReadTimeout(10000);
                        conn.setRequestProperty("User-Agent", USER_AGENT);
                        conn.setRequestProperty("API-Key", MeloConfiguration.apiKey);

                        inputStream = conn.getInputStream();

                        if (shouldGunzip) {
                            inputStream = new GZIPInputStream(inputStream);
                        }

                        // While the assumption of UTF8 isn't always true; it *should* always be true.
                        // Not in the sense that this will hold in most cases (although that as well),
                        // but in the sense that any violation of this better have a good reason.
                        return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                    } finally {
                        try {
                            if (inputStream != null) {
                                inputStream.close();
                            }
                        } finally {
                            if (conn instanceof HttpURLConnection) {
                                ((HttpURLConnection) conn).disconnect();
                            }
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e); // We can rethrow, since supplyAsync catches exceptions.
                }
            }, executorService);
        }

        public CompletableFuture<JsonObject> requestJson() {
            return requestJson(JsonObject.class);
        }

        public <T> CompletableFuture<T> requestJson(Class<? extends T> clazz) {
            return requestString().thenApply(str -> gson.fromJson(str, clazz));
        }

    }

    public Request request() {
        return new Request();
    }

    public Request newHypixelApiRequest(String apiPath) {
        return newAnonymousHypixelApiRequest(apiPath)
                .queryArgument("key", MeloConfiguration.apiKey);
    }

    public Request newAnonymousHypixelApiRequest(String apiPath) {
        return new Request()
                .url("https://api.hypixel.net/" + apiPath);
    }

}