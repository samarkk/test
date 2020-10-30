package streams;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class WordCountMultiple {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(WordCountApp.class.getName());
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "wcappmult");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> textLines = builder.stream("wcimult");

        KTable<String, Long> wordCounts = textLines
                .mapValues(line -> line.toLowerCase())
                .flatMapValues(line -> Arrays.asList(line.split("\\W+")))
                .selectKey((key, word) -> word)
                .groupByKey()
                .count();

        wordCounts.toStream().to("wcomult", Produced.with(Serdes.String(), Serdes.Long()));

        KTable<Windowed<String>, Long> wordCountsTW = textLines
                .mapValues(line -> line.toLowerCase())
                .flatMapValues(line -> Arrays.asList(line.split("\\W+")))
                .selectKey((key, word) -> word)
                .groupByKey()
                .windowedBy(TimeWindows.of(Duration.ofSeconds(5)))
                .count();

        StringSerializer stringSerializer = new StringSerializer();
        StringDeserializer stringDeserializer = new StringDeserializer();
        Serde<String> stringSerde = Serdes.serdeFrom(stringSerializer, stringDeserializer);

        wordCountsTW.toStream()
                .map((Windowed<String> key, Long count) -> new KeyValue<>(key.toString() + "      " +
                        key.window().startTime().toString() + "-" +
                        key.window().endTime().toString(),
                        count.toString()))
                .to("wcomtw", Produced.with(Serdes.String(), Serdes.String()));

        KafkaStreams streams = new KafkaStreams(builder.build(), props);

        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException iex) {
                break;
            }
        }
    }
}
