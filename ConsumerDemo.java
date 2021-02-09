package consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class ConsumerDemo {
    public static void main(String[] args) {
        ConsumerDemo consumerDemo = new ConsumerDemo();
        consumerDemo.runPollLoopForTopic(args[0], args[1], args[2]);
    }
    // create consumer configs
    private Properties createConsumerConfigs(String bservers, String group){
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bservers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, group);
        return props;
    }
    // create a consumer
    private KafkaConsumer<String, String> createConsumer(String bservers, String group){
        Properties props = createConsumerConfigs(bservers, group);
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
        return consumer;
    }
    // poll loop
    private void runPollLoopForTopic(String bservers, String group, String topic){
        KafkaConsumer<String, String> consumer = createConsumer(bservers,group);
        consumer.subscribe(Collections.singletonList(topic));
        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                if(records.count() > 0) {
                    for (ConsumerRecord<String, String> record : records) {
                        String recordVal = record.value().toUpperCase();
                        System.out.println("Record key: " + record.key() + "\nValue: " + recordVal +
                                "\nPartition: " + record.partition() +
                                "\nOffset: " + record.offset());
                    }
                }
                consumer.commitSync();
            }
        } catch (Exception exception){
            System.out.println("Something went wroing");
            exception.printStackTrace();

        }finally {
            consumer.close();
        }
    }
}
