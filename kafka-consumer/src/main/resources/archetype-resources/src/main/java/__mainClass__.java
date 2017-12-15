#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import ${package}.avro.Example;

public class ${mainClass} {
    private final static Logger logger = LogManager.getLogger(${mainClass}.class);

    public static void main(String[] args) {
        /** Zookeeper server URLs */
        final String bootstrapServers = args.length > 0 ? args[0] : "localhost:9092";
        /** URL of the Schema Registry */
        final String schemaRegistry = args.length > 1 ? args[1] : "http://localhost:8081";

        /** ID of the consumer group with which to register with Kafka */
        final String groupId = "${artifact-id}";

        /** The Kafka topic to which to send messages */
        final String inputTopic = "example";

        final Properties consumerProperties = new Properties();
        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        consumerProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);

        final SpecificAvroSerde<Example> exampleSerde = createSerde(schemaRegistry);
        final KafkaConsumer<Long, Example> consumer = new KafkaConsumer<>(
                consumerProperties,
                Serdes.Long().deserializer(),
                exampleSerde.deserializer()
        );
        consumer.subscribe(Collections.singleton(inputTopic));

        try {
            while (true) {
                final ConsumerRecords<Long, Example> consumerRecords = consumer.poll(1000);
                if (consumerRecords.count() == 0) {
                    continue;
                }
                consumerRecords.forEach(record -> {
                    logger.info("Received record #" + record.key() + ": " + record.value().toString());
                });
            }
        } catch (Exception e) {
            logger.error("Caught Exception", e);
        } finally {
            consumer.close();
            logger.info("Done");
        }
    }

    /**
     * Creates a serialiser/deserialiser for the given type, registering the Avro schema with the schema registry.
     *
     * @param schemaRegistryUrl the schema registry to register the schema with
     * @param <T>               the type for which to create the serialiser/deserialiser
     * @return                  the matching serialiser/deserialiser
     */
    private static <T extends SpecificRecord> SpecificAvroSerde<T> createSerde(final String schemaRegistryUrl) {
        final SpecificAvroSerde<T> serde = new SpecificAvroSerde<>();
        final Map<String, String> serdeConfig = Collections.singletonMap(
                AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
                schemaRegistryUrl
        );
        serde.configure(serdeConfig, false);
        return serde;
    }
}
