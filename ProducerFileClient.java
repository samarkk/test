package producer;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ProducerFileClient {
    public static void main(String[] args) throws IOException {
        ProducerFileClient producerFileClient = new ProducerFileClient();
        producerFileClient.produceRecordsFromFileToTopic(args[0], args[1], args[2]);
    }
    // create configs
    private Properties createProducerConfigs(String bservers) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bservers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return props;
    }
    // create producer
    private KafkaProducer<String, String> createProducerFromConfigs(String bservers){
        Properties props = createProducerConfigs(bservers);
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(props);
        return producer;
    }
    // read from file and write to topic
    private void produceRecordsFromFileToTopic(String bservers, String fileLoc, String topic) throws IOException {
        KafkaProducer<String, String> producer = createProducerFromConfigs(bservers);
        FileReader fr = new FileReader(fileLoc);
        BufferedReader br = new BufferedReader(fr);
        br.readLine();
        String line = "";
        try{
            while((line = br.readLine()) != null){
                ProducerRecord<String, String> record = new ProducerRecord<>(topic, line);
                producer.send(record, new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata metadata, Exception exception) {
                        String recordDetails = "Producer record sent to topic: " + metadata.topic() +
                                "\nto partition: " + metadata.partition() +
                                "\nwith offset: " + metadata.offset();
                        System.out.println(recordDetails);
                    }
                });
            }
        } catch (Exception ex){
            System.out.println("Something gone wrong");
            ex.printStackTrace();
        } finally {
            producer.close();
        }
    }
}
