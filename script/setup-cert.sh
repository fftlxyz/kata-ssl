
openssl req -new -x509 -extensions v3_ca -keyout private/cakey.pem -out cacert.pem -days 365 -config ./openssl.cnf

openssl req -new -nodes -out server-req.pem -keyout private/server-key.pem -days 365 -config openssl.cnf
openssl ca -out server-cert.pem -days 365 -config openssl.cnf -infiles server-req.pem

openssl req -new -nodes -out client-req.pem -keyout private/client-key.pem -days 365 -config openssl.cnf
openssl ca -out client-cert.pem -days 365 -config openssl.cnf -infiles client-req.pem

 openssl pkcs12 -export -in client-cert.pem -inkey private/client-key.pem -certfile cacert.pem -name "Client" -out client-cert.p12

 openssl pkcs12 -export -in server-cert.pem -inkey private/server-key.pem -certfile cacert.pem -name "Server" -out server-cert.p12


keytool -importkeystore -deststorepass katakata -destkeypass katakata -destkeystore server_keystore.jks -srckeystore server-cert.p12 -srcstoretype PKCS12 -srcstorepass katakata -alias server

keytool -import -v -trustcacerts -keystore client_truststore.jks -storepass katakata -alias server -file server-cert.pem

keytool -import -v -trustcacerts -keystore server_truststore.jks -storepass katakata -file cacert.pem -alias cacert


keytool -importkeystore  -srckeystore client-cert.p12 -srcstoretype pkcs12 -destkeystore client_keystore.jks -deststoretype jks -deststorepass katakata  -srcstorepass katakata

# The JKS keystore uses a proprietary format. It is recommended to migrate to PKCS12 which is an industry standard format using "keytool -importkeystore -srckeystore client_keystore.jks -destkeystore client_keystore.jks -deststoretype pkcs12".