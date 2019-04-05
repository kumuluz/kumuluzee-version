package com.kumuluz.ee.version.pojo;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import java.util.LinkedList;
import java.util.List;

public class VersionPojo {
    private List<KeyValuePair> keyValuePairs;

    public VersionPojo() {
        keyValuePairs = new LinkedList<>();
    }

    public List<KeyValuePair> getKeyValuePairs() {
        return keyValuePairs;
    }

    public void addKeyValuePair(KeyValuePair pair) {
        keyValuePairs.add(pair);
    }

    public String toJSON() {
        JsonObjectBuilder json = Json.createObjectBuilder();

        for (KeyValuePair keyValuePair : keyValuePairs) {
            json.add(keyValuePair.getKey(), keyValuePair.getValue());
        }
        return json.build().toString();
    }
}
