package kafkaExample;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Properties;
import org.json.JSONObject;

public class Runner {
    public static void main(String[] args) throws InterruptedException {
//        Producer

        Properties prodProps = new Properties();
        prodProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        prodProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        prodProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(prodProps);

//      Consumer
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-group");
        KafkaConsumer consumer = new KafkaConsumer(props);
        consumer.subscribe(Arrays.asList("input"));
        System.out.println("Strarting:");
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
                JSONObject object = new JSONObject(record.value());
                String mostViewed = Runner.findMostVied(object);
                int total = Runner.countTotalViews(object);
                object.put("total", total);
                producer.send(new ProducerRecord<String, String>("exportToExcel", 0, "exportToExcel", object.toString()));
                producer.send(new ProducerRecord<String, String>("factAboutInterestingBook", 0, mostViewed, Runner.getFactAbout(mostViewed)));

            }
        }
    }

    private static int countTotalViews(JSONObject object) {
        int math = (int)object.get("Math");
        int chemistry = (int)object.get("Chemistry");
        int helloBook = (int)object.get("Hello Book");
        return math + chemistry + helloBook;
    }

    private static String findMostVied(JSONObject object) {
        int math = (int)object.get("Math");
        int chemistry = (int)object.get("Chemistry");
        int helloBook = (int)object.get("Hello Book");
        if (math >= chemistry && math >= helloBook) {
            return "Math";
        } else if (chemistry >= math && chemistry >= helloBook) {
            return "Chemistry";
        } else if (helloBook >= math && helloBook >= chemistry) {
            return "Hello Book";
        }
        return "";
    }

    private static String getFactAbout(String bookName) {
        String mathFact = "'Eleven plus two' is an anagram of 'twelve plus one' which is pretty fitting as the answer to both equations is 13";
        String chemistryFact = "Water is special, and we all know that, even if you cut class as a kid. When water freezes it expands, obviously, but if you think about it, it should not be so obvious. Typically, materials consolidate and shrink as temperatures drop, but water and its unique structure have a lot of space for energy to be released, resulting in its expansion.";
        String helloBookFact = "This technology could be a real benefit for NASA, as it can help in augmenting the effectiveness of navigation devices. In 1999, NASA made use of AR for the first time for flying the X-38 by utilizing the special AR dashboard for navigation.";
        switch(bookName) {
            case "Math":
                return mathFact;
            case "Chemistry":
                return chemistryFact;
            case "Hello Book":
                return helloBookFact;
        }
        return "No Fact Found";
    }
}
