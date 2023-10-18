package kata.ssl;

import kata.ssl.client.Client;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

public class SSLUtils {

    public static KeyStore getStore(final String storeFileName, final char[] password) throws
            KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        final KeyStore store = KeyStore.getInstance(KeyStore.getDefaultType());
        URL url = Client.class.getClassLoader().getResource(storeFileName);
        assert url != null;
        try (InputStream inputStream = url.openStream()) {
            store.load(inputStream, password);
        }
        return store;
    }

    public static KeyManager[] getKeyManagers(String keyStoreFile, String keyStorePassword, String certPassword) {
        try {
            KeyStore ks = SSLUtils.getStore(keyStoreFile, keyStorePassword.toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            // Set up key manager factory to use our key store
            kmf.init(ks, certPassword.toCharArray());
            return kmf.getKeyManagers();
        } catch (KeyStoreException | IOException | CertificateException | NoSuchAlgorithmException |
                 UnrecoverableKeyException e) {
            throw new RuntimeException(e);
        }
    }


    public static TrustManager[] getTrustManagers(String keyStoreFile, String trustStorePassword) {
        try {
            KeyStore trustKeyStore = SSLUtils.getStore(keyStoreFile, trustStorePassword.toCharArray());
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustKeyStore);//通过keyStore得到信任管理器
            return trustManagerFactory.getTrustManagers();
        } catch (KeyStoreException | IOException | CertificateException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
