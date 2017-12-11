package com.api;
//import com.google.api.client.http.HttpHeaders;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class GoogleDriveAccess {

    static void driveReadOnly(String access_token) {
        HttpHeaders headers = new HttpHeaders();
        RestTemplate restTemplate = new RestTemplate();
        final String url = "https://www.googleapis.com/drive/v3/about";
        headers.add("Authorization", "Bearer "+access_token);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
        .queryParam("alt", "json")
        .queryParam("fields", "user")
        .queryParam("prettyPrint", true)
        .queryParam("quotaUser", "user");

        HttpEntity<?> entity = new HttpEntity<>(headers);

        HttpEntity<String> response = restTemplate.exchange(
                                    builder.build().encode().toUri(),
                                    HttpMethod.GET,
                                    entity,String.class);
        System.out.printf("FileResponse: "+response);
        ListOfFlies(access_token);
    }
    static void ListOfFlies(String access_token) {
        HttpHeaders headers = new HttpHeaders();
        RestTemplate restTemplate = new RestTemplate();
        final String url = "https://www.googleapis.com/drive/v2/files?q=root";
        headers.add("Authorization", "Bearer "+access_token);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("alt", "json")
                .queryParam("fields", "user")
                .queryParam("prettyPrint", true)
                .queryParam("quotaUser", "user");

        HttpEntity<?> entity = new HttpEntity<>(headers);

        HttpEntity<String> response = restTemplate.exchange(
                builder.build().encode().toUri(),
                HttpMethod.GET,
                entity,String.class);
        System.out.printf("Files: "+response);
    }
}
