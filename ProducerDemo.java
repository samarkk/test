package producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

public class ProducerDemo {
    public static void main(String[] args) {
//        produceARecordToVM(args[0]);
        ProducerDemo producerDemo = new ProducerDemo();
//        producerDemo.produceARecordToVM(args[0]);
        producerDemo.produceMultipleKeyedRecordsToVM(args[0]);
    }

    private Properties createProducerConfigs(String bservers) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bservers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return props;
    }

    private void produceARecordToVM(String bservers) {
        // create producer configuration
        Properties props = createProducerConfigs(bservers);
        // create a producer
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(props);
        // create producer recrds
        ProducerRecord<String, String> record = new ProducerRecord<>("ide_topic", "hello from the ide");
        // send them to a topic to the vm
        producer.send(record);
        producer.flush();
        producer.close();
    }

    private ArrayList<ProducerRecord> createProducerRecords() {
        String[] cities = new String[]{"Mumbai", "Delhi", "Chennai", "Kanput", "Lucknow", "Kotkata"};
        String[] statuses = new String[]{"W", "D", "L"};
        ArrayList<ProducerRecord> records = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            String keyCity = cities[new Random().nextInt(cities.length)];
            String rivalCity = cities[new Random().nextInt(cities.length)];
            String status = statuses[new Random().nextInt(statuses.length)];
            ProducerRecord<String, String> record = new ProducerRecord<>("kidetopic",
                    keyCity, keyCity + " played against " + rivalCity + " and outcome was: " + status);
            records.add(record);
        }
        return records;
    }

    private void produceMultipleKeyedRecordsToVM(String bservers) {
        // configs
        Properties props = createProducerConfigs(bservers);
        // producer
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(props);
        // records
        ArrayList<ProducerRecord> records = createProducerRecords();
        try {
            records.forEach(rec -> producer.send(rec));
        } catch (Exception ex) {
            System.out.println("Some problem occured");
            ex.printStackTrace();
        } finally {
            producer.close();
        }
    }
}
