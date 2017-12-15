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
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import ${package}.avro.Example;

public class ${mainClass} {
    private final static Logger logger = LogManager.getLogger(${mainClass}.class);

    public static void main(String[] args) {
        /* Zookeeper server URLs */
        final String bootstrapServers = args.length > 0 ? args[0] : "localhost:9092";
        /* URL of the Schema Registry */
        final String schemaRegistry = args.length > 1 ? args[1] : "http://localhost:8081";

        /* Client ID with which to register with Kafka */
        final String applicationId = "${artifactId}";

        /* The Kafka topic from which to read messages */
        final String inputTopic = "example";
        /* The Kafka topic to which to send messages */
        final String outputTopic = "example-stream";

        final Properties streamsProperties = new Properties();
        streamsProperties.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
        streamsProperties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        final SpecificAvroSerde<Example> exampleSerde = createSerde(schemaRegistry);

        final KStreamBuilder builder = new KStreamBuilder();
        final KStream<Long, Example> input = builder.stream(
                Serdes.Long(),
                exampleSerde,
                inputTopic
        );

        input.mapValues(value -> {
            final String payload = value.getMessage();

            final Example message = new Example();
            final String reversed = new StringBuffer(payload).reverse().toString();
            message.setMessage(reversed);

            logger.info("Reversed input: '" + payload + "' > '" + reversed + "'");

            return message;
        }).to(Serdes.Long(), exampleSerde, outputTopic);

        final KafkaStreams streams = new KafkaStreams(builder, streamsProperties);
        streams.start();
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
