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
package com.kumuluz.ee.version;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds version information and functions to transform it to JSON format
 *
 * @author Klemen Kobau, gpor0
 * @since 1.0.0
 */
public class VersionInfo {

    private static final VersionInfo INSTANCE = new VersionInfo();

    private Map<String, String> versionPropertyMap;

    public VersionInfo() {
        versionPropertyMap = new HashMap<>();
    }

    public static VersionInfo getInstance() {
        return INSTANCE;
    }

    public Map<String, String> getMap() {
        return Collections.unmodifiableMap(versionPropertyMap);
    }

    protected void put(String key, String val) {
        versionPropertyMap.put(key, val);
    }

    public String get(String key) {
        return versionPropertyMap.get(key);
    }

    public String toJSON() {
        final JsonObjectBuilder json = Json.createObjectBuilder();

        versionPropertyMap.entrySet().forEach(e -> {
            json.add(e.getKey(), e.getValue());
        });

        return json.build().toString();
    }
}
