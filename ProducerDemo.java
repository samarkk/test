package producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class ProducerDemo {
    public static void main(String[] args) {
        // create producer configuration
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "http://skkvm.eastus.cloudapp.azure.com:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        // create a producer
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(props);
        // create producer recrds
        ProducerRecord<String, String> record = new ProducerRecord<>("ide_topic", "hello from the ide");
        // send them to a topic to the vm
        producer.send(record);
        producer.flush();
        producer.close();
    }
}
