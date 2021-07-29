/**
 * Created by ilkayaktas on 12.07.2021 at 14:10.
 */

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class KafkaProducerApplication {

    private final Producer<String, String> producer;
    final String outTopic;

    public KafkaProducerApplication(final Producer<String, String> producer,
                                    final String topic) {
        this.producer = producer;
        outTopic = topic;
    }

    public Future<RecordMetadata> produce(final String key, final String value) {
        final ProducerRecord<String, String> producerRecord = new ProducerRecord<>(outTopic, key, value);
        return producer.send(producerRecord);
    }

    public void shutdown() {
        producer.close();
    }

    public static Properties loadProperties(String fileName) throws IOException {
        final Properties envProps = new Properties();
        final FileInputStream input = new FileInputStream(fileName);
        envProps.load(input);
        input.close();

        return envProps;
    }

    public void printMetadata(final Collection<Future<RecordMetadata>> metadata,
                              final String fileName) {
        System.out.println("Offsets and timestamps committed in batch from " + fileName);
        metadata.forEach(m -> {
            try {
                final RecordMetadata recordMetadata = m.get();
                System.out.println("Record written to offset " + recordMetadata.offset() + " timestamp " + recordMetadata.timestamp());
            } catch (InterruptedException | ExecutionException e) {
                if (e instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            throw new IllegalArgumentException(
                    "This program takes one arguments: the path to an environment configuration file.");
        }

        final Properties props = KafkaProducerApplication.loadProperties(args[0]);
        final String topic = props.getProperty("input.topic.name");
        final Producer<String, String> producer = new KafkaProducer<>(props);
        final KafkaProducerApplication producerApp = new KafkaProducerApplication(producer, topic);

        try {
            boolean keepMoving = true;

            List<String> keys = Arrays.asList("key1", "key2", "key3");
            while(keepMoving){
                String str = UUID.randomUUID().toString();
                RecordMetadata metadata = producerApp.produce(keys.get(new Random().nextInt(3)), str).get();
                System.out.println("Record written to offset " + metadata.offset() + " timestamp " + metadata.timestamp());

                Thread.sleep(500);
            }


        }
        finally {
            producerApp.shutdown();
        }
    }
}