package com.telemetry.server.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.telemetry.server.model.Telemetry;

public interface TelemetryRepository extends MongoRepository<Telemetry, String> {
    
    @Query("{uuid:'?0'}")
    Telemetry findItemByUuid(String uuid);
        
    public long count();

}
