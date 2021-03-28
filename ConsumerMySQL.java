package consumer;

import com.example.Nseforec;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;


public class ConsumerMySQL {
    private Connection connectToMySQL() {
        Connection conn = null;
        try {
            String url = "jdbc:mysql://localhost:3306/testdb?rewriteBatchedStatements=true";
            String user = "root";
            String password = "abcd";

            conn = DriverManager.getConnection(url, user, password);
        } catch (Exception ex) {
            System.out.println("Problem connecting: " + ex.getMessage());
        }
        return conn;
    }

    private Properties createConsumerConfigs() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
        props.put("schema.registry.url", "http://localhost:8081");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "nsefogr");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 50);
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true");
        return props;
    }

    private void subscribeAndConsume(String topic, Connection conn) throws SQLException {
        KafkaConsumer<String, com.example.Nseforec> consumer = new KafkaConsumer<String, Nseforec>(createConsumerConfigs());
        consumer.subscribe(Collections.singletonList(topic));
        int batchRecCount = 0;
        int batchNo = 0;
        while (true) {
            ConsumerRecords<String, Nseforec> records = consumer.poll(Duration.ofMillis(100));
            batchRecCount = records.count();
            if (batchRecCount > 0) {
                batchNo++;
                System.out.println("In batch no: " + batchNo + ", received: " + batchRecCount + " records");
                String mysqlStatementToExecute = "Insert into fotbl values ";
                String sql = "Insert ignore into fotbl(trdate, symbol, expirydt, instrument, optiontyp," +
                        "strikepr, closepr, settlepr, contracts, valinlakh, openint, choi) values " +
                        "(?,?,?,?,?,?,?,?,?,?,?,?)";
                PreparedStatement statement = conn.prepareStatement(sql);

                for (ConsumerRecord<String, Nseforec> record : records) {
                    Nseforec nseforec = record.value();
                    setStatement(statement, nseforec);
                    statement.addBatch();

                    System.out.println(nseforec.getSymobl() + "," +
                            nseforec.getExpiryDt() + "," +
                            nseforec.getClosepr());
                }
                statement.executeBatch();
            }

        }
    }

    private void setStatement(PreparedStatement stm, Nseforec record) throws SQLException {
        stm.setString(1, record.getTmstamp());
        stm.setString(2, record.getSymobl());
        stm.setString(3, record.getExpiryDt());
        stm.setString(4, record.getInstrument());
        stm.setString(5, record.getOptionTyp());
        stm.setFloat(6, record.getStrikePr());
        stm.setFloat(7, record.getClosepr());
        stm.setFloat(8, record.getSettlepr());
        stm.setInt(9, record.getContracts());
        stm.setFloat(10, record.getValinlakh());
        stm.setInt(11, record.getOpenint());
        stm.setInt(12, record.getChginoi());
//        stm.addBatch();
//        return stm;
    }

    public static void main(String[] args) throws SQLException {
        ConsumerMySQL consumerMySQL = new ConsumerMySQL();
        Connection connection = consumerMySQL.connectToMySQL();
//        System.out.println(connection.isValid(1000));
        consumerMySQL.subscribeAndConsume("nsefotopic_avro",connection);
    }


}
