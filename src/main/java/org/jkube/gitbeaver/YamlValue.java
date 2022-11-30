package org.jkube.gitbeaver;

public class YamlValue extends YamlNode {

    private final String value;
    public YamlValue(String string) {
        this.value = string;
    }

    public String getValue() {
        return value;
    }
}
