package org.jkube.gitbeaver;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class YamlParser {

	private static final String COMMENT_MARKER = "#";
	private static final char LIST_MARKER = '-';
	private static final String MAP_MARKER = ":";

	public YamlNode parse(List<String> lines) {
		return parseRecursively(lines.stream()
				.map(this::removeComments)
				.filter(this::notBlank)
				.collect(Collectors.toList()));
	}

	private YamlNode parseRecursively(List<String> lines) {
		List<List<String>> blocks = extractBlocksByIndentation(lines);
		if (removeListPrefixInAll(blocks)) {
			YamlList result = new YamlList();
			blocks.forEach(block -> result.addElement(parseRecursively(block)));
			return result;
		} else if (areAllKeyValuePairs(blocks)) {
			YamlMap result = new YamlMap();
			blocks.forEach(block -> parseMapEntry(result, block));
			return result;
		} else if ((blocks.size() != 1) || (blocks.get(0).size() != 1)) {
			throw new YamlParsingException("expected a list or a map or a value");
		} else {
			return new YamlValue(blocks.get(0).get(0));
		}
	}

	private List<List<String>> extractBlocksByIndentation(List<String> lines) {
		System.out.println("------------------------------------------");
		lines.forEach(System.out::println);
		List<List<String>> result = new ArrayList<>();
		if (!lines.isEmpty()) {
			int firstindent = getIndentation(lines.get(0));
			int start = 0;
			for (int end = start + 1; end <= lines.size(); end++) {
				int indent = end < lines.size() ? getIndentation(lines.get(end)) : firstindent;
				if (indent < firstindent) {
					throw new YamlParsingException("encountered line with indentation "+indent+" in block with indentation "+firstindent+": "+lines.get(end));
				}
				if (indent == firstindent) {
					result.add(extractBlock(indent, lines.subList(start, end)));
					start = end;
				}
			}
		}
		return result;
	}

	private List<String> extractBlock(int indent, List<String> lines) {
		return lines.stream()
				.map(line -> line.substring(indent))
				.collect(Collectors.toList());
	}
	private String removeComments(String line) {
		int pos = line.indexOf(COMMENT_MARKER);
		return pos >= 0 ? line.substring(0, pos) : line;
	}

	private boolean notBlank(String line) {
		return !line.isBlank();
	}

	private int getIndentation(String line) {
		// blank lines not possible here
		int i = 0;
		if (!line.isBlank()) {
			while (line.charAt(i) == ' ') {
				i++;
			}
		}
		return i;
	}

	private boolean removeListPrefixInAll(List<List<String>> blocks) {
		Boolean isList = null;
		for (List<String> block : blocks) {
			// blocks always have at least one line
			String firstLine = block.get(0);
			// lines are not empty
			boolean found = firstLine.charAt(0) == LIST_MARKER;
			if (isList == null) {
				isList = found;
			} else {
				if (isList != found) {
					throw new YamlParsingException("Found mixture of list and non-list blocks");
				}
			}
			if (found) {
				block.set(0, " "+firstLine.substring(1));
			}
		}
		return (isList != null) && isList;
	}

	private boolean areAllKeyValuePairs(List<List<String>> blocks) {
		for (List<String> block : blocks) {
			// blocks always have at least one line
			String firstLine = block.get(0);
			if (!firstLine.contains(MAP_MARKER)) {
				return false;
			}
		}
		return true;
	}

	private void parseMapEntry(YamlMap map, List<String> block) {
		String firstLine = block.get(0);
		int pos = firstLine.indexOf(MAP_MARKER);
		String key = firstLine.substring(0, pos).trim();
		YamlNode value;
		if (pos == firstLine.length()-1) {
//			if (block.size() < 2) {
//				throw new YamlParsingException("Missing value for key "+key);
//			}
			value = parseRecursively(block.subList(1, block.size()));
		} else {
			if (block.size() > 1) {
				throw new YamlParsingException("Additional lines on block with value in line for key "+key);
			}
			value = new YamlValue(firstLine.substring(pos+1).trim());
		}
		map.put(key, value);
	}


}
