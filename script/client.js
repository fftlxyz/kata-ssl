const tls = require('tls');
const fs = require('fs');

const options = {
    key: fs.readFileSync('../src/main/resources/private/client-key.pem'),
    cert: fs.readFileSync('../src/main/resources/client-cert.pem'),
    ca: [fs.readFileSync('../src/main/resources/cacert.pem')],
    checkServerIdentity: () => undefined
};

var socket = tls.connect(9000, '127.0.0.1', options, () => {
    console.log('client connected',
        socket.authorized ? 'authorized' : 'unauthorized');
    process.stdin.pipe(socket);
    process.stdin.resume();
});
socket.setEncoding('utf8');
socket.on('data', (data) => {
    console.log(data);
});

socket.on('end', () => {
    console.log('Ended')
});