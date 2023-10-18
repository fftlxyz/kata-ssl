
 keytool -list -v -keystore ../src/main/resources/client_keystore.jks

# https://security.stackexchange.com/questions/3779/how-can-i-export-my-private-key-from-a-java-keytool-keystore


keytool -importkeystore \
    -srckeystore ../src/main/resources/client_keystore.jks \
    -destkeystore client-cert.p12 \
    -deststoretype PKCS12 \
    -srcalias client \
    -deststorepass katakata \
    -destkeypass katakata


openssl pkcs12 -in client-cert.p12  -nodes -nocerts -out client-key.pem
openssl pkcs12 -in client-cert.p12  -nokeys -out client-cert.pem

keytool -exportcert -alias cacert -keystore ../src/main/resources/server_truststore.jks -rfc -file ca-cert.pem

