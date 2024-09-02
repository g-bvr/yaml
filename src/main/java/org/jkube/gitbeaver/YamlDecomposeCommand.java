package org.jkube.gitbeaver;

import org.jkube.gitbeaver.util.FileUtil;

import java.nio.file.Path;
import java.util.Map;

import static org.jkube.gitbeaver.logging.Log.log;
import static org.jkube.gitbeaver.logging.Log.onException;

/**
 * Usage: resolve source target
 */
public class YamlDecomposeCommand extends AbstractCommand {

    private static final String YAML = "yaml";

    private static final String TARGET = "target";

    public YamlDecomposeCommand() {
        super("Decompose a yaml file into a folder tree");
        commandline("DECOMPOSE YAML "+YAML+" INTO "+TARGET);
        argument(YAML, "The path to the yaml file (relative to current workspace)");
        argument(TARGET, "The path of the result folder (relative to current workspace, will be created including ancestors if not present, will be cleared if not empty)");
    }

    @Override
    public void execute(Map<String, String> variables, WorkSpace workSpace, Map<String, String> arguments) {
        Path sourcePath = workSpace.getAbsolutePath(arguments.get(YAML));
        Path targetPath = workSpace.getAbsolutePath(arguments.get(TARGET));
        log("Resolving yaml file "+sourcePath+" to "+targetPath);
        FileUtil.createIfNotExists(targetPath.getParent());
        FileUtil.clear(targetPath);
        onException(() -> new YamlDecomposer().decompose(sourcePath, targetPath))
                .fail("Could not write resolved lines to "+targetPath);
    }
}
