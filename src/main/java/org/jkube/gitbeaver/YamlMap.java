package org.jkube.gitbeaver;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class YamlMap extends YamlNode {

    private final Map<String, YamlNode> map = new LinkedHashMap<>();
    public void put(String key, YamlNode value) {
        map.put(key, value);
    }

    public  Map<String, YamlNode> getMap() {
        return map;
    }

}
