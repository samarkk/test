package streams;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Properties;

public class WordCountApp {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(WordCountApp.class.getName());
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "wcapp");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> textLines = builder.stream("wci");

        KTable<String, Long> wordCounts = textLines
                .mapValues(line -> line.toLowerCase())
                .flatMapValues(line -> Arrays.asList(line.split("\\W+")))
                .selectKey((key, word) -> word)
                .groupByKey()
                .count();

        wordCounts.toStream().to("wco", Produced.with(Serdes.String(), Serdes.Long()));
        KafkaStreams streams = new KafkaStreams(builder.build(), props);

        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

        while (true) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException iex) {
                break;
            }
        }
    }
}
