import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Scanner;

public class KafkaProducerTest {

    public static void main(String[] args) throws IOException {
        Properties props= new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        org.apache.kafka.clients.producer.KafkaProducer<String,String> producer=new org.apache.kafka.clients.producer.KafkaProducer<String,String>(props);
        readFromFileAndProduce(producer);
    }

    private static void readFromFileAndProduce(org.apache.kafka.clients.producer.KafkaProducer<String,String> producer) throws IOException {
        Path path = Paths.get("/home/samar/tfile.txt");
        Scanner scanner = new Scanner(path);
        System.out.println("Read text file using Scanner");

        while(scanner.hasNextLine()){
            //process each line
            String line = scanner.nextLine();
            System.out.println(line);
            ProducerRecord<String,String> record=new ProducerRecord("first_topic",line);

            producer.send(record);

        }
        producer.flush();
        producer.close();
        scanner.close();
    }
}
