package com.demohcx.healthcontroller;


import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@RestController
public class HealthCheck {

    public static boolean HealthResult=true;






    private static AdminClient adminClient;

    public HealthCheck() {
        adminClient = kafkaAdminClient();

    }

    @GetMapping(value = "/service/health")
    public ResponseEntity<Object> serviceHealth() {
        return new ResponseEntity<>(Response(), HttpStatus.OK);
    }

    @GetMapping(value = "/health")
    public ResponseEntity<Object> health() {

        return new ResponseEntity<>(Response2(), HttpStatus.OK);

    }

    public HashMap<String, Object> Response() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("timestamp", String.valueOf(Instant.now()));
        map.put("result", Map.of("healthy", "true"));
        return map;
    }

    public HashMap<String, Object> Response2() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("timestamp", String.valueOf(Instant.now()));
        map.put("result", healthCheck());
        return map;
    }

    public static HashMap<String, Object> healthCheck() {

        ArrayList<Map<String, Object>> allchecks = new ArrayList<>();
        allchecks.add(checkHealthKafka(isHealthy()));
        allchecks.add(checkHealthPostgreSQl(isHealth()));
        for (Map<String, Object> checks : allchecks)
            if ((boolean) checks.get("healthy")) {
                HealthResult = true;
            } else {
                HealthResult = false;
                break;
            }
        HashMap<String, Object> map = new HashMap<>();
        map.put("checks", allchecks);
        return map;

    }

    private static Map<String, Object> checkHealthKafka(Boolean isHealthy) {
        return new HashMap<>() {{
            put("name", "kafka");
            put("healthy", isHealthy);
        }};
    }

    private static Map<String, Object> checkHealthPostgreSQl(Boolean isHealth) {
        return new HashMap<>() {{
            put("name", "postgreSQL");
            put("healthy", isHealth);
        }};
    }


    public static boolean isHealthy() {
        try {

            adminClient.listTopics(new ListTopicsOptions().timeoutMs(5000)).listings().get();

            return true;
        } catch (Exception ex) {
            return false;
        }

    }

    public static boolean isHealth() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");
            conn.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private AdminClient kafkaAdminClient() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "127.0.0.1:9092");
        return AdminClient.create(properties);
    }




}
