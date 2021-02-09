package producer;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

public class ProducerDemo {
    public static void main(String[] args) {
//        produceARecordToVM(args[0]);
        ProducerDemo producerDemo = new ProducerDemo();
//        producerDemo.produceARecordToVM(args[0]);
//        producerDemo.produceMultipleKeyedRecordsToVM(args[0]);
        producerDemo.produceMultipleKeyedRecordsWithCBToVM(args[0]);
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

    private ArrayList<ProducerRecord> createProducerRecords() throws InterruptedException {
        String[] cities = new String[]{"Mumbai", "Delhi", "Chennai", "Kanpur", "Lucknow", "Kolkata"};
        String[] statuses = new String[]{"W", "D", "L"};
        ArrayList<ProducerRecord> records = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            String keyCity = cities[new Random().nextInt(cities.length)];
            String rivalCity = cities[new Random().nextInt(cities.length)];
            String status = statuses[new Random().nextInt(statuses.length)];
            ProducerRecord<String, String> record = new ProducerRecord<>("kidetopic",
                    keyCity, keyCity + " played against " + rivalCity + " and outcome was: " + status);
            records.add(record);
            Thread.sleep(2000);
        }
        return records;
    }

    private void produceMultipleKeyedRecordsToVM(String bservers) throws InterruptedException {
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

    private void produceMultipleKeyedRecordsWithCBToVM(String bservers) {
        // configs
        Properties props = createProducerConfigs(bservers);
        // producer
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(props);
        // records
        try {
            while(true){
                ArrayList<ProducerRecord> records = createProducerRecords();
                records.forEach(rec -> {
                    System.out.println("Sending record for key: " + rec.key());
                    producer.send(rec, new ProducerDemoCallBack());
                });
            }
        } catch (Exception ex) {
            System.out.println("Some problem occured");
            ex.printStackTrace();
        } finally {
            producer.close();
        }
    }

    private class ProducerDemoCallBack implements Callback{
        @Override
        public void onCompletion(RecordMetadata metadata, Exception exception) {
            String recordDetails = "Producer record sent to topic: " + metadata.topic() +
                    "\nto partition: " + metadata.partition() +
                    "\nwith offset: " + metadata.offset();
            System.out.println(recordDetails);
        }
    }
}


