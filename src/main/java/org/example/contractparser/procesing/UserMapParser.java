package org.example.contractparser.procesing;

import java.io.IOException;
import java.util.Map;

public interface UserMapParser {

    Map<String, String> extractMap(String imagePath) throws IOException;
}
