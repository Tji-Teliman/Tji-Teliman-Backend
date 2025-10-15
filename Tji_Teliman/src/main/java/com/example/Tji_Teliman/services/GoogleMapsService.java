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
    
    public record ReverseGeocodeResult(String placeId, String formattedAddress) {}

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

    /**
     * Géocodage inverse : convertit latitude/longitude en placeId et adresse
     * @param lat latitude
     * @param lng longitude
     * @return ReverseGeocodeResult avec placeId et adresse formatée, ou null si erreur
     */
    public ReverseGeocodeResult reverseGeocode(Double lat, Double lng) {
        if (lat == null || lng == null) return null;
        if (apiKey == null || apiKey.isBlank()) return null;

        String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lng + "&key=" + apiKey;
        ResponseEntity<Map> resp = restTemplate.getForEntity(url, Map.class);
        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) return null;

        Object status = resp.getBody().get("status");
        if (status == null || !"OK".equals(status.toString())) return null;

        java.util.List<Map<String, Object>> results = (java.util.List<Map<String, Object>>) resp.getBody().get("results");
        if (results == null || results.isEmpty()) return null;

        // Prendre le premier résultat (le plus précis)
        Map<String, Object> firstResult = results.get(0);
        String placeId = firstResult.get("place_id") == null ? null : firstResult.get("place_id").toString();
        String formattedAddress = firstResult.get("formatted_address") == null ? null : firstResult.get("formatted_address").toString();
        
        return new ReverseGeocodeResult(placeId, formattedAddress);
    }
}


