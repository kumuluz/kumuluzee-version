/*
 *  Copyright (c) 2014-2017 Kumuluz and/or its affiliates
 *  and other contributors as indicated by the @author tags and
 *  the contributor list.
 *
 *  Licensed under the MIT License (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  https://opensource.org/licenses/MIT
 *
 *  The software is provided "AS IS", WITHOUT WARRANTY OF ANY KIND, express or
 *  implied, including but not limited to the warranties of merchantability,
 *  fitness for a particular purpose and noninfringement. in no event shall the
 *  authors or copyright holders be liable for any claim, damages or other
 *  liability, whether in an action of contract, tort or otherwise, arising from,
 *  out of or in connection with the software or the use or other dealings in the
 *  software. See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.kumuluz.ee.version.pojo;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import java.util.LinkedList;
import java.util.List;

/**
 * List of key value pairs, holds functions to transform it to JSON format
 *
 * @author Klemen Kobau
 * @since 1.0.0
 */
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
