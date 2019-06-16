const express = require('express');
const kafka = require('kafka-node');
const bodyParser = require('body-parser');

const CONSUMERS_TOPIC = 'factAboutInterestingBook';
const PRODUCERS_TOPIC = 'input';
let factAboutInterestingBook = {
    factAboutInterestingBook: 'Loading...',
    bookName: '',
};

const app = express();

app.all("/*", function(request, response, next) {
    response.header("Access-Control-Allow-Origin", "*");
    response.header("Access-Control-Allow-Headers", "Cache-Control, Pragma, Origin, Authorization, Content-Type, X-Requested-With");
    response.header("Access-Control-Allow-Methods", "GET, PUT, POST");
    return next();
});

app.use( bodyParser.json() );       // to support JSON-encoded bodies
app.use(bodyParser.urlencoded({     // to support URL-encoded bodies
    extended: true
}));

app.get('/',function(req,res){
    res.json({greeting:'Kafka Producer'})
});

app.listen(5001,function(){
    console.log('Kafka producer running at 5001')
});

const Producer = kafka.Producer,
    producer = new Producer(new kafka.KafkaClient());

producer.on('ready', function () {
    console.log('Producer is ready');
});

producer.on('error', function (err) {
    console.log('Producer is in error state');
    console.log(err);
});

const Consumer = kafka.Consumer,
    consumer = new Consumer(new kafka.KafkaClient(),
        [{topic: CONSUMERS_TOPIC, offset: 0}],
        {
            autoCommit: false
        }
    );

consumer.on('message', function (message) {
    console.log(message);
    factAboutInterestingBook = message;
});

consumer.on('error', function (err) {
    console.log('Error:',err);
});

consumer.on('offsetOutOfRange', function (err) {
    console.log('offsetOutOfRange:',err);
});

app.post('/sendStatisticsToKafka',function(req,res){
    const data = JSON.stringify(req.body.data);
    payloads = [
        { topic: PRODUCERS_TOPIC, messages: data , partition: 0 }
    ];
    producer.send(payloads, function (err, data) {
        res.json(data);
    });
});

app.get('/getFact',function(req,res){
    res.json({ factAboutInterestingBook });
});
