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

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.TimeZone;

public class WordCountTumblingWindow {
    public static void main(String[] args) {
        new WordCountTumblingWindow().createWCAppAndRun();
    }

    private void createWCAppAndRun() {
        Logger logger = LoggerFactory.getLogger(WordCountApp.class.getName());
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "wcapptw");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> textLines = builder.stream("wcitw");

        KTable<Windowed<String>, Long> wordCounts = textLines
                .mapValues(line -> line.toLowerCase())
                .flatMapValues(line -> Arrays.asList(line.split("\\W+")))
                .selectKey((key, word) -> word)
                .groupByKey()
                .windowedBy(TimeWindows.of(Duration.ofSeconds(5)))
                .count();

        StringSerializer stringSerializer = new StringSerializer();
        StringDeserializer stringDeserializer = new StringDeserializer();
        Serde<String> stringSerde = Serdes.serdeFrom(stringSerializer, stringDeserializer);

//        TimeWindowedSerializer<String> windowedSerializer = new TimeWindowedSerializer<>(stringSerializer);
//        TimeWindowedDeserializer<String> windowedDeserializer = new TimeWindowedDeserializer<>(stringDeserializer);
//        Serde<Windowed<String>> windowedSerde = Serdes.serdeFrom(windowedSerializer, windowedDeserializer);
//        wordCounts.toStream().to("wcotw", Produced.with(windowedSerde, Serdes.Long()));
        wordCounts.toStream()
                .map((Windowed<String> key, Long count) -> new KeyValue<>(key.toString() + "      " +
                        key.window().startTime().toString() + "-" +
                        key.window().endTime().toString(),
                        count.toString()))
                .to("wcotw", Produced.with(Serdes.String(), Serdes.String()));
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

    private String windowedKeyToString(Windowed<String> key) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ssZZZZ");
        sdf.setTimeZone(TimeZone.getTimeZone("IST"));
        return String.format("[%s@%s/%s]",
                key.key(),
                sdf.format(key.window().startTime().getEpochSecond()),
                sdf.format(key.window().endTime().getEpochSecond()));
    }
}
