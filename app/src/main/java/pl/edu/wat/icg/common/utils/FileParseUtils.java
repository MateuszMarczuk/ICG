package pl.edu.wat.icg.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FileParseUtils {
    public static Map<String, String> parseHeaders(String[] lines, int firstLine, int lastLine) {

        List<String> keys = Arrays.asList(lines[firstLine].split("\t"));
        List<String> values = Arrays.asList(lines[lastLine].split("\t"));

        Map<String, String> headers = new LinkedHashMap<>();

        for(int i = 0; i< keys.size(); i++) {
            headers.put(keys.get(i), values.get(i));
        }
        return headers;
    }

    public static List<String> parseLine(String[] lines, int line) {
        return Arrays.asList(lines[line].split("\t"));
    }

    public static List<List<String>> parseValues(String[] lines, int firstLine, int lastLine) {
        List<List<String>> values = new ArrayList<>();

        for(int i = firstLine; i<=lastLine; i++) {
            String[] value = lines[i].split("\t", -1);

            for(int paramIndex=0; paramIndex< value.length; paramIndex++) {
                if (values.size() <= paramIndex) {
                    values.add(new ArrayList<>());
                }
                if (!value[paramIndex].equals("***") && (!value[paramIndex].equals(""))) {
                    values.get(paramIndex).add(value[paramIndex].replace(",", "."));
                }
            }
        }
        return values;
    }
}
