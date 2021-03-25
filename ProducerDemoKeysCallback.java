package producer;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class ProducerDemoKeysCallback {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Properties props = new Properties();
        props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, args[0]);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<String,
                String>(props);

        for (int i = 0; i < 10; i++) {
            ProducerRecord<String, String> record = new ProducerRecord<String
                    , String>(args[1], "id " + i, "hello world no " + i + " " +
                    "from" +
                    " the ide");

            producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    String sendDetails =
                            "Producer Record sent to topic: " + metadata.topic() +
                                    "\nPartition: " + metadata.partition() +
                                    "\nKey: " + record.key() +
                                    "\nOffset: " + metadata.offset() +
                                    "\nTimestamp: " + metadata.timestamp();
                    System.out.println(sendDetails);
                }
            }).get();
        }
        producer.close();
    }
}
