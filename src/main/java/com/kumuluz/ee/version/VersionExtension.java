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

import com.kumuluz.ee.common.Extension;
import com.kumuluz.ee.common.ServletServer;
import com.kumuluz.ee.common.config.EeConfig;
import com.kumuluz.ee.common.dependencies.EeComponentDependencies;
import com.kumuluz.ee.common.dependencies.EeComponentDependency;
import com.kumuluz.ee.common.dependencies.EeComponentType;
import com.kumuluz.ee.common.dependencies.EeExtensionDef;
import com.kumuluz.ee.common.wrapper.KumuluzServerWrapper;
import com.kumuluz.ee.configuration.utils.ConfigurationUtil;
import com.kumuluz.ee.version.pojo.KeyValuePair;
import com.kumuluz.ee.version.pojo.VersionPojo;
import com.kumuluz.ee.version.servlet.VersionServlet;

import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParsingException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * VersionExtension class
 *
 * @author Klemen Kobau
 * @since 1.0.0
 */
@EeExtensionDef(name = "Version", group = "Information")
@EeComponentDependencies(value = {
        @EeComponentDependency(EeComponentType.JSON_P),
        @EeComponentDependency(EeComponentType.SERVLET)})
public class VersionExtension implements Extension {

    private static String versionFilePath = "VERSION.json";
    private static String endpoint = "/version";

    private static final Logger log = Logger.getLogger(VersionExtension.class.getName());

    private static VersionPojo versionPojo;

    public void init(KumuluzServerWrapper kumuluzServerWrapper, EeConfig eeConfig) {
        if (kumuluzServerWrapper.getServer() instanceof ServletServer) {
            log.info("Initializing version endpoint.");
            versionPojo = new VersionPojo();
            ServletServer servletServer = (ServletServer) kumuluzServerWrapper.getServer();


            ConfigurationUtil cfg = ConfigurationUtil.getInstance();

            Optional<String> cfgEndpoint = cfg.get("kumuluzee.version.endpoint");
            cfgEndpoint.ifPresent(value -> endpoint = value);

            Optional<String> cfgFilePath = cfg.get("kumuluzee.version.file-path");
            cfgFilePath.ifPresent(value -> versionFilePath = value);

            Optional<String> dockerImageName = cfg.get("kumuluzee.version.docker-image-name");
            dockerImageName.ifPresent(value -> versionPojo.addKeyValuePair(new KeyValuePair("docker_image_name", value)));

            Optional<List<String>> customKeys = cfg.getMapKeys("kumuluzee.version.custom-keys");
            customKeys.ifPresent(VersionExtension::addCustomKeysToVersionPojo);

            boolean success = initVersionDetails();
            if (success) {
                log.info("Initialized version endpoint at: " + endpoint);
                servletServer.registerServlet(VersionServlet.class, endpoint);
            } else
                log.severe("Versions endpoint not initialized due to error");
        }
    }

    /**
     * Adds the custom keys defined in KumuluzEE config file to the versionsPojo
     *
     * @param customKeys list of Strings that represent the names of the custom keys
     */
    private static void addCustomKeysToVersionPojo(List<String> customKeys) {
        ConfigurationUtil cfg = ConfigurationUtil.getInstance();

        for (String customKey : customKeys) {
            Optional<String> valuePair = cfg.get("kumuluzee.version.custom-keys." + customKey);
            valuePair.ifPresent(value -> versionPojo.addKeyValuePair(new KeyValuePair(customKey, value)));
        }
    }

    public void load() {

    }

    private static InputStream getVersionFileInputStream() throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream fileStream = classLoader.getResourceAsStream(versionFilePath);
        if (fileStream == null)
            throw new IOException("Version file not found at: " + versionFilePath);
        return fileStream;
    }

    /**
     * Finds version file, parses it and initializes versionPojo
     */
    private static boolean initVersionDetails() {
        StringBuilder responseStrBuilder = new StringBuilder();
        String response;

        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(VersionExtension.getVersionFileInputStream(), StandardCharsets.UTF_8.toString()));

            while ((response = reader.readLine()) != null) {
                responseStrBuilder.append(response);
            }
        } catch (UnsupportedEncodingException e) {
            log.severe("Cannot read from " + versionFilePath + ", unsupported encoding");
            return false;
        } catch (IOException e) {
            // assumes that VERSION.json is not provided
            return true;
        }

        final JsonParser parser = Json.createParser(new StringReader(responseStrBuilder.toString()));
        KeyValuePair pair = new KeyValuePair();
        List<String> unsetValues = new LinkedList<>();

        try {
            while (parser.hasNext()) {
                final JsonParser.Event event = parser.next();

                switch (event) {
                    case KEY_NAME:
                        pair = new KeyValuePair();
                        pair.setKey(parser.getString());
                        break;
                    case VALUE_STRING:
                        String value = parser.getString();

                        // add unfilled fields to list
                        if (value.length() == 0 || value.charAt(0) == '$') {
                            unsetValues.add(pair.getKey());
                        } else {
                            pair.setValue(value);
                            versionPojo.addKeyValuePair(pair);
                        }
                        break;
                }
            }
        } catch (JsonParsingException e) {
            log.severe("Invalid JSON in " + versionFilePath);
            return false;
        }

        if (unsetValues.size() > 0)
            log.warning("Unset keys in version file. Unset values: " + String.join(", ", unsetValues));
        return true;
    }

    public static VersionPojo getVersionPojo() {
        return versionPojo;
    }
}
