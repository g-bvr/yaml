package org.jkube.gitbeaver;

import org.jkube.gitbeaver.util.FileUtil;

import java.nio.file.Path;
import java.util.Map;

import static org.jkube.logging.Log.log;
import static org.jkube.logging.Log.onException;

/**
 * Usage: resolve source target
 */
public class YamlDecomposeCommand extends AbstractCommand {

    private static final String YAML = "yaml";

    private static final String TARGET = "target";

    public YamlDecomposeCommand() {
        super("Decompose a yaml file into a folder tree");
        commandline("YAML DECOMPOSE "+YAML+" INTO "+TARGET);
        argument(YAML, "The path to the yaml file (relative to current workspace)");
        argument(TARGET, "The path of the result folder (relative to current workspace, will be created including ancestors if not present, yet)");
    }

    @Override
    public void execute(Map<String, String> variables, WorkSpace workSpace, Map<String, String> arguments) {
        Path sourcePath = workSpace.getAbsolutePath(YAML);
        Path targetPath = workSpace.getAbsolutePath(TARGET);
        log("Resolving yaml file "+sourcePath+" to "+targetPath);
        FileUtil.createIfNotExists(targetPath.getParent());
        onException(() -> new YamlDecomposer().decompose(sourcePath, targetPath))
                .fail("Could not write resolved lines to "+targetPath);
    }
}
