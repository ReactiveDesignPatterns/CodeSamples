// 代码清单15-5
// Listing 15.5 Request–response based on a one-way messaging protocol

// #snip
var amqp = require('amqplib/callback_api');

amqp.connect('amqp://localhost', function (err, conn) {
    conn.createChannel(function (err, ch) {
        var q = 'rpc_queue';
        ch.assertQueue(q, {durable: false});
        ch.prefetch(1);
        ch.consume(q, function reply(msg) {
            console.log("got request: %s", msg.content.toString());
            ch.sendToQueue(msg.properties.replyTo,
                new Buffer('got it!'),
                {correlationId: msg.properties.correlationId});
            ch.ack(msg);
        });
    });
});
// #snip