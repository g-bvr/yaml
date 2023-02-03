package org.jkube.gitbeaver;

import org.jkube.gitbeaver.plugin.SimplePlugin;

public class YamlPlugin extends SimplePlugin {

    public YamlPlugin() {
        super("Parses and deconstructs yaml files", YamlDecomposeCommand.class);
    }

}
