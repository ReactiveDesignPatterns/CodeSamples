var http = require('http');

var counter = 0;

http.createServer(function (req, res) {
  counter++;
  res.writeHead(200, {'Content-Type': 'text/plain'});
  res.end('Sending response: ' + counter + ' via callback!\n');
}).listen(8888, '127.0.0.1');

console.log('Server up on 127.0.0.1:8888, send requests!');
