package org.jkube.gitbeaver;

import java.util.ArrayList;
import java.util.List;

public class YamlList extends YamlNode {

    private final List<YamlNode> elements = new ArrayList<>();
    public void addElement(YamlNode element) {
        elements.add(element);
    }

    public List<YamlNode> getElements() {
        return elements;
    }

}
