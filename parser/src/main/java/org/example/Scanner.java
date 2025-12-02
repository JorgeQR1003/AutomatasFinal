package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.AbstractMap.SimpleEntry;

public class Scanner {
    
    public static List<Entry<String, String>> readTokens(String filePath) {
        ObjectMapper mapper = new ObjectMapper();
        List<Entry<String, String>> tokenList = new ArrayList<>();
        
        try {
            List<Map<String, String>> rawTokens = mapper.readValue(new File(filePath), new TypeReference<List<Map<String, String>>>(){});
            
            for (Map<String, String> tokenMap : rawTokens) {
                String token = tokenMap.get("token");
                String lexema = tokenMap.get("lexema");
                tokenList.add(new SimpleEntry<>(token, lexema));
            }
            
        } catch (IOException e) {
            System.err.println("Error reading tokens file: " + e.getMessage());
            e.printStackTrace();
        }
        
        return tokenList;
    }
}
