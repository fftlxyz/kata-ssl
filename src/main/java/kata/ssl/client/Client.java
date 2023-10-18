package kata.ssl.client;

import kata.ssl.SSLUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Client {

    private static final String KEY_STORE = "client_keystore.jks";
    private static final String TRUST_STORE = "client_truststore.jks";


    private static final String KEYSTORE_PASSWORD = "katakata";
    private static final String CERT_PASSWORD = "katakata";

    private static final String TRUST_STORE_PWD = "katakata";


    public static void main(String[] args) throws Exception {


        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS);

        KeyManager[] keyManagers = SSLUtils.getKeyManagers(KEY_STORE, KEYSTORE_PASSWORD, CERT_PASSWORD);

        TrustManager[] trustManagers = SSLUtils.getTrustManagers(TRUST_STORE, TRUST_STORE_PWD);
        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:"
                    + Arrays.toString(trustManagers));
        }
        X509TrustManager trustManager = (X509TrustManager) trustManagers[0];

        // setup socket factory
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagers, trustManagers, null);
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();//拿到SSLSocketFactory

        builder.sslSocketFactory(sslSocketFactory, trustManager);

        // skip host name verify
        builder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });

        OkHttpClient client = builder.build();

        System.out.println(get(client, "https://localhost:9000/get"));
    }


    public static String get(OkHttpClient client, String url) throws Exception {
        Request.Builder builder = new Request.Builder().url(url);
        Request request = builder.build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("response failed" + response);
            }
            assert response.body() != null;
            return new String(response.body().bytes());
        }
    }


}
