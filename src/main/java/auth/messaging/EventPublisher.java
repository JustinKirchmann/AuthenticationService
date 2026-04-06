package auth.messaging;

import com.eventstore.dbclient.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(EventPublisher.class);

    private final EventStoreDBClient client;
    private final ObjectMapper        mapper;

    public EventPublisher(EventStoreDBClient client, ObjectMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    public void publish(String stream, String eventType, Object payload) {
        try {
            byte[]    data  = mapper.writeValueAsBytes(payload);
            EventData event = EventData.builderAsJson(eventType, data).build();
            client.appendToStream(stream, AppendToStreamOptions.get(), event).get();
            log.info("Published [{}] to stream [{}]", eventType, stream);
        } catch (Exception e) {
            // messaging must never break the main flow
            log.error("Failed to publish event [{}]: {}", eventType, e.getMessage());
        }
    }
}