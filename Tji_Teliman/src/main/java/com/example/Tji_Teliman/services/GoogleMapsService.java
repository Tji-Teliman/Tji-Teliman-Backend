package com.example.Tji_Teliman.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
public class GoogleMapsService {

    @Value("${google.maps.api.key:}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public record PlaceDetails(Double lat, Double lng, String formattedAddress) {}

    public PlaceDetails fetchPlaceDetails(String placeId) {
        if (placeId == null || placeId.isBlank()) return null;
        if (apiKey == null || apiKey.isBlank()) return null;

        String url = "https://maps.googleapis.com/maps/api/place/details/json?place_id=" + placeId + "&fields=geometry/location,formatted_address&key=" + apiKey;
        ResponseEntity<Map> resp = restTemplate.getForEntity(url, Map.class);
        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) return null;

        Object status = resp.getBody().get("status");
        if (status == null || !"OK".equals(status.toString())) return null;

        Map<String, Object> result = (Map<String, Object>) resp.getBody().get("result");
        if (result == null) return null;
        Map<String, Object> geometry = (Map<String, Object>) result.get("geometry");
        if (geometry == null) return null;
        Map<String, Object> location = (Map<String, Object>) geometry.get("location");
        if (location == null) return null;
        Double lat = location.get("lat") instanceof Number ? ((Number) location.get("lat")).doubleValue() : null;
        Double lng = location.get("lng") instanceof Number ? ((Number) location.get("lng")).doubleValue() : null;
        String formatted = result.get("formatted_address") == null ? null : result.get("formatted_address").toString();
        return new PlaceDetails(lat, lng, formatted);
    }
}


