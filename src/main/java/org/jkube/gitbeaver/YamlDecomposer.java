package org.jkube.gitbeaver;

import org.jkube.gitbeaver.util.FileUtil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.jkube.gitbeaver.logging.Log.onException;

public class YamlDecomposer {

    private static final String INFO = "meta";
    private static final String VALUE = "value";
    private static final String ELEMENT = "element";
    private static final String ENTRY = "entry";
    private static final String LEVEL = "level";
    private static final String ORDER = "order";
    private static final String ANCESTOR_PREFIX = "ancestor-";
    private static final String KEY = "key";
    private static final String PATH = "path";
    private static final String SEP = " ";

    public void decompose(Path sourcePath, Path targetPath) {
        YamlNode yaml = new YamlParser().parse(FileUtil.readLines(sourcePath));
        writeRecursively(targetPath, yaml, new ArrayList<>(), null, 1, 0);
    }

    private void writeRecursively(Path path, YamlNode node, List<String> yamlPath, String parentkey, Integer order, int level) {
        FileUtil.createIfNotExists(path);
        String value = (node instanceof YamlValue) ? ((YamlValue)node).getValue() : null;
        writeInfo(path.resolve(INFO), yamlPath, parentkey, value, order, level);
        if (value != null) {
            writeValue(path.resolve(VALUE), value);
        } else if (node instanceof YamlList) {
            int i = 0;
            List<YamlNode> elements = ((YamlList) node).getElements();
            for (YamlNode element : elements) {
                writeRecursively(path.resolve(ELEMENT+getNumberSuffix(i, elements.size())), element, append(yamlPath, Integer.toString(i)), null, i, level+1);
                i++;
            }
        } else if (node instanceof YamlMap) {
            Map<String, YamlNode> map = ((YamlMap) node).getMap();
            int i = 0;
            for (Map.Entry<String, YamlNode> e : map.entrySet()) {
                writeRecursively(path.resolve(ENTRY+getNumberSuffix(i, map.size())), e.getValue(), append(yamlPath, e.getKey()), e.getKey(), i, level+1);
                i++;
            }
        }
    }

    private void writeValue(Path file, String value) {
        onException(() -> Files.write(file, List.of(value)))
                .fail("Could not write info to "+file);
    }

    private List<String> append(List<String> yamlPath, String add) {
        List<String> result = new ArrayList<>(yamlPath);
        result.add(add);
        return result;
    }

    private void writeInfo(Path file, List<String> yamlPath, String key, String value, int order, int level) {
        List<String> info = new ArrayList<>();
        if (key != null) {
            info.add(KEY + SEP + key);
        }
        if (value != null) {
            info.add(VALUE+SEP+value);
        }
        info.add(LEVEL+SEP+level);
        info.add(ORDER+SEP+order);
        if (!yamlPath.isEmpty()) {
            info.add(PATH+SEP+String.join(".", yamlPath));
        }
        int i = yamlPath.size();
        for (String ancestor : yamlPath) {
            info.add(ANCESTOR_PREFIX+ i +SEP+ancestor);
            i--;
        }
        onException(() -> Files.write(file, info))
                .fail("Could not write info to "+file);
    }

    private String getNumberSuffix(int value, int num) {
        String res = Integer.toString(value);
        int len = 0;
        while (num > 0) {
            num /= 10;
            len++;
        }
        if (res.length() < len) {
            res = "0".repeat(len - res.length()) + res;
        }
        return "-"+res;
    }
}
