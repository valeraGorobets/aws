const XLSXChart = require ("xlsx-chart");
const fs = require ("fs");
const debounce = require('debounce');

const TOPIC = 'exportToExcel';

const kafka = require('kafka-node'),
    Consumer = kafka.Consumer,
    client = new kafka.KafkaClient(),
    consumer = new Consumer(client,
        [{topic: TOPIC, offset: 0}],
        {
            autoCommit: false
        }
    );

consumer.on('message', function (message) {
    console.log(message);
    printToExcel(message);
});

consumer.on('error', function (err) {
    console.log('Error:',err);
});

consumer.on('offsetOutOfRange', function (err) {
    console.log('offsetOutOfRange:',err);
});

printToExcel = debounce(printToExcel, 1000);

function printToExcel(message) {
    const xlsxChart = new XLSXChart ();
    const value = JSON.parse(message.value);
    const chartTitle = `Total books watched: ${value.total} times`;
    delete value.total;
    const opts = {
        chart: "pie",
        titles: [
            chartTitle
        ],
        fields: Object.keys(value),
        data: {
            [chartTitle]: value,
        },
        chartTitle,
    };
    xlsxChart.generate (opts, function (err, data) {
        if (err) {
            console.error (err);
        } else {
            fs.writeFileSync ("pie.xlsx", data);
            console.log ("pie.xlsx created.");
        }
    });
};