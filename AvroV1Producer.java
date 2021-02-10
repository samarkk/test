package producer;

import com.example.Customer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class AvroV1Producer {
//    static String bservers = "13.72.105.213:9092";
//    static String schemaRegistryURL = "http://13.72.105.213:8081";

    public static void main(String[] args) {
        Properties properties = new Properties();
        // normal producer
        properties.setProperty("bootstrap.servers", args[0]);
        properties.setProperty("acks", "all");
        properties.setProperty("retries", "10");
        // avro part
        properties.setProperty("key.serializer", StringSerializer.class.getName());
        properties.setProperty("value.serializer", KafkaAvroSerializer.class.getName());
        properties.setProperty("schema.registry.url", args[1]);


        Producer<String, Customer> producer = new KafkaProducer<String, Customer>(properties);

        String topic = args[2];

        // copied from avro examples
        Customer customer = Customer.newBuilder()
                .setAge(34)
                .setAutomatedEmail(false)
                .setFirstName("Raman")
                .setLastName("Shastri")
                .setWeight(73f)
                .setHeight(177f)
                .build();

        ProducerRecord<String, Customer> producerRecord = new ProducerRecord<String, Customer>(
                topic, customer
        );

        System.out.println(customer);
        producer.send(producerRecord, new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                if (exception == null) {
                    System.out.println(metadata);
                } else {
                    exception.printStackTrace();
                }
            }
        });

        producer.flush();
        producer.close();

    }
}
