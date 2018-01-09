
// 代码清单15-6
// Listing 15.6 Listening for a response with the same correlation ID as the original request

// #snip
var uuid = require('node-uuid');
amqp.connect('amqp://localhost', function(err, conn) {
    conn.createChannel(function(err, ch) {
        ch.assertQueue('responses', {}, function(err, q) {
            var corr = uuid.v1();
            ch.consume(q.queue, function(msg) {
                if (msg.properties.correlationId === corr) {
                    console.log('got response: %s', msg.content.toString());
                    setTimeout(function() { conn.close(); process.exit(0) }, 500);
                }
            }, {noAck: true});
            ch.sendToQueue('rpc_queue',
                new Buffer('hello'),
                { correlationId: corr, replyTo: q.queue });
        });
    });
});
// #snip