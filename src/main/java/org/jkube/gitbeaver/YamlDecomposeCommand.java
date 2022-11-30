package org.jkube.gitbeaver;

import org.jkube.gitbeaver.util.FileUtil;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.jkube.logging.Log.log;
import static org.jkube.logging.Log.onException;

/**
 * Usage: resolve source target
 */
public class YamlDecomposeCommand extends AbstractCommand {

    public YamlDecomposeCommand() {
        super(2,2, "yaml", "decompose");
    }

    @Override
    public void execute(Map<String, String> variables, WorkSpace workSpace, List<String> arguments) {
        Path sourcePath = workSpace.getAbsolutePath(arguments.get(0));
        Path targetPath = workSpace.getAbsolutePath(arguments.get(1));
        log("Resolving yaml file "+sourcePath+" to "+targetPath);
        FileUtil.createIfNotExists(targetPath.getParent());
        onException(() -> new YamlDecomposer().decompose(sourcePath, targetPath))
                .fail("Could not write resolved lines to "+targetPath);
    }
}
