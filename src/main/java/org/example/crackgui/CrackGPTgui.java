package org.example.crackgui;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class CrackGPTgui extends CrackGPT {

    private static final String URL = "http://localhost:11434/api/generate";
    private static final String CONTENT_TYPE_JSON = "application/json";

    public String crackGPT(String userInput, String selectedLanguage) {
        try {
            String prompt = userInput + "\nPlease answer everything in " + selectedLanguage + ".";

            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "gemma");
            requestBody.put("prompt", prompt);
            requestBody.put("stream", false);

            URL url = new URL(URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", CONTENT_TYPE_JSON);
            con.setDoOutput(true);

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = requestBody.toJSONString().getBytes();
                os.write(input, 0, input.length);
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }

                    System.out.println(response);
                    return parseResponse(response.toString());
                }
            } else {
                try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(con.getErrorStream()))) {
                    StringBuilder errorMessage = new StringBuilder();
                    String errorLine;

                    while ((errorLine = errorReader.readLine()) != null) {
                        errorMessage.append(errorLine);
                    }

                    return "Server responded with status code " + responseCode + ": " + errorMessage.toString();
                }
            }
        } catch (IOException | ParseException e) {
            return "Error: " + e.getMessage();
        }
    }

    private String parseResponse(String response) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject jsonResponse = (JSONObject) parser.parse(response);
        Object responseObject = jsonResponse.get("response");

        if (responseObject != null) {
            System.out.println(responseObject.toString());
            return responseObject.toString();
        } else {
            return "Server response is missing or invalid.";
        }
    }
}
