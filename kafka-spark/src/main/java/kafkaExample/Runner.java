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

import java.nio.file.Files;
import java.nio.file.Paths;


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
        try {
            String path = "facts\\" + bookName + ".txt";
            String content = new String(Files.readAllBytes(Paths.get(path)), "UTF-8");
            return content;
        } catch (Exception e) {
            System.out.println(e);
            return "No Fact Found";
        }
    }
}
