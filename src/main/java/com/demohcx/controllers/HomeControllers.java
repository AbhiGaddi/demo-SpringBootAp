package com.demohcx.controllers;


import exception.ClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Repository
@RestController
public class HomeControllers {

    @Autowired
    JdbcTemplate jdbcTemplate;

    /*NotifierList */
    @RequestMapping(value = "/notification/topic/list", method = RequestMethod.GET)
    public ResponseEntity<String> notifierList() throws IOException {
        File file = ResourceUtils.getFile("/home/stpl/IdeaProjects/demo-SpringBootAp/src/main/resources/project.json");
        String content = new String(Files.readAllBytes(file.toPath()));
        return ResponseEntity.ok(content);
    }

    /* Subscribe */
    @RequestMapping(value = "/notification/subscribe", method = RequestMethod.POST)
    public ResponseEntity subscribe(@RequestBody Map<String,Object> body) {
        try {
            validateRequest(body);
            String senderCode = (String) body.get("sender_code");
            String recipientCode = (String) body.get("recipient_code");
            String topicCode = (String) body.get("topic_code");
            String subscriptionId = senderCode + "-" + topicCode + "-" + recipientCode;
            int result = jdbcTemplate.update("INSERT INTO notifierlist(subscription_id,sender_code,recipient_code ,topic_code, subscription_status,created_on,updated_on) SELECT '" + subscriptionId + "', '" + senderCode + "', '" + recipientCode + "', '" + topicCode + "','Active',NOW(),NULL WHERE NOT EXISTS( SELECT subscription_id FROM notifierlist WHERE  subscription_id='" + subscriptionId + "')");
            if (result == 0)
                throw new ClientException("subscription already exist");
            else {
                Map<String, String> responseMap = Map.of("subscriptionId", subscriptionId, "subscription_status", "Active");
                return ResponseEntity.ok(responseMap);
            }
        } catch (ClientException ex) {
            return ResponseEntity.badRequest().body(Map.of("Status", "Fail", "Message", ex.getMessage()));
        }

    }

    /* Unsubscribe  */
    @RequestMapping(value = "/notification/unsubscribe", method = RequestMethod.POST)
    public ResponseEntity unsubscribe(@RequestBody Map<String,Object> body) {
        try {
            validateRequest(body);
            String senderCode = (String) body.get("sender_code");
            String recipientCode = (String) body.get("recipient_code");
            String topicCode = (String) body.get("topic_code");
            String subscriptionId = senderCode + "-" + topicCode + "-" + recipientCode;
            String updateQuery = "UPDATE notifierlist SET subscription_status = 'InActive',updated_on=NOW() WHERE subscription_id ='" + subscriptionId + "'";
            int result = jdbcTemplate.update(updateQuery);
            if (result == 0)
                throw new ClientException("subscription does not exist");
            else {
                Map<String, String> responseMap = Map.of("subscriptionId", subscriptionId, "subscription_status", "InActive");
                return ResponseEntity.ok(responseMap);
            }
        } catch (ClientException ex) {
            return ResponseEntity.badRequest().body(Map.of("Status", "Fail", "Message", ex.getMessage()));
        }
    }

    /*SubscriptionList */
    @RequestMapping(value = "/notification/subscription/list", method = RequestMethod.POST)
    public ResponseEntity subscriptionList(@RequestBody Map<String,Object> body) {
        try {
            System.out.println(body);
            if(body.isEmpty())
                throw new ClientException("receipientcode is missing or empty ");
            validateProperty(body, "recipient_code");
            String recipientCode = (String) body.get("recipient_code");
            System.out.println(recipientCode);
            String selectSql = "SELECT * FROM notifierlist WHERE recipient_code='" + recipientCode + "'";
            List<Map<String, Object>> searchQuery = jdbcTemplate.queryForList(selectSql);
            return ResponseEntity.ok(searchQuery);

        } catch (ClientException ex) {
            return ResponseEntity.badRequest().body(Map.of("Status", "Fail", "Message", ex.getMessage()));
        }
    }


    public void validateRequest(Map<String, Object> body) throws ClientException {
        if (body.isEmpty())
            throw new ClientException("Json request body is Empty");
        List<String> validateList = Arrays.asList("sender_code", "recipient_code", "topic_code");
        for (String property : validateList) {
            validateProperty(body, property);
        }
    }

    private void validateProperty(Map<String,Object> body, String property) throws ClientException {
        if (!body.containsKey(property) || ((String) body.getOrDefault(property, "")).isEmpty())
            throw new ClientException("'" + property + "' is missing or empty");
    }


}











