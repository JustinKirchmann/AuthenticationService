package auth.config;

import com.eventstore.dbclient.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KurrentDbConfig {

    @Value("${kurrentdb.connection-string:esdb://localhost:2113?tls=false}")
    private String connectionString;

    @Bean
    public EventStoreDBClient eventStoreDBClient() {
        try {
            EventStoreDBClientSettings settings =
                EventStoreDBConnectionString.parseOrThrow(connectionString);
            return EventStoreDBClient.create(settings);
        } catch (Exception e) {
            throw new IllegalStateException("Invalid KurrentDB connection string", e);
        }
    }
}
