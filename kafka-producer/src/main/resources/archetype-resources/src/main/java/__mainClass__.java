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
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import ${package}.avro.Example;

public class ${mainClass} {
    private final static Logger logger = LogManager.getLogger(${mainClass}.class);

    public static void main(String[] args) {
        /** Zookeeper server URLs */
        final String kafkaBroker = args.length > 0 ? args[0] : "localhost:9092";
        /** URL of the Schema Registry */
        final String schemaRegistry = args.length > 1 ? args[1] : "http://localhost:8081";

        /** Client ID with which to register with Kafka */
        final String clientId = "${artifactId}";

        /** The Kafka topic to which to send messages */
        final String outputTopic = "example";

        final Properties producerProperties = new Properties();
        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBroker);
        producerProperties.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);

        final SpecificAvroSerde<Example> helloSerde = createSerde(schemaRegistry);
        final KafkaProducer<Long, Example> producer = new KafkaProducer<>(
                producerProperties,
                Serdes.Long().serializer(),
                helloSerde.serializer()
        );

        for (long i = 0; i < 10; i++) {
            final Example message = new Example();
            message.setMessage("Hello from Kafka producer!");
            logger.info("Sending " + message.toString());
            producer.send(new ProducerRecord<>(outputTopic, i, message));
        }
        producer.close();
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
