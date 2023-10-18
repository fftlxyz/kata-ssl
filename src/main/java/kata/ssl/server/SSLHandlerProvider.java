package kata.ssl.server;


import io.netty.handler.ssl.SslHandler;
import kata.ssl.SSLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;

public class SSLHandlerProvider {
    private static final Logger logger = LoggerFactory.getLogger(SSLHandlerProvider.class);

    private static final String PROTOCOL = "TLS";
    private static final String KEY_STORE = "server_keystore.jks";
    private static final String TRUST_STORE = "server_truststore.jks";


    private static final String KEYSTORE_PASSWORD = "katakata";
    private static final String CERT_PASSWORD = "katakata";

    private static final String TRUST_STORE_PWD = "katakata";

    private static SSLContext serverSSLContext = null;

    public static SslHandler getSSLHandler() {
        SSLEngine sslEngine = null;
        if (serverSSLContext == null) {
            logger.error("Server SSL context is null");
            System.exit(-1);
        } else {
            sslEngine = serverSSLContext.createSSLEngine();
            sslEngine.setUseClientMode(false);
            sslEngine.setNeedClientAuth(true);

        }
        return new SslHandler(sslEngine);
    }

    public static void initSSLContext() {
        logger.info("Initiating SSL context");
        try {
            KeyManager[] keyManagers = SSLUtils.getKeyManagers(KEY_STORE, KEYSTORE_PASSWORD, CERT_PASSWORD);
            TrustManager[] trustManagers = SSLUtils.getTrustManagers(TRUST_STORE, TRUST_STORE_PWD);
            serverSSLContext = SSLContext.getInstance(PROTOCOL);
            serverSSLContext.init(keyManagers, trustManagers, null);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            logger.error("Failed to initialize the server-side SSLContext", e);
            throw new RuntimeException(e);
        }
    }
}