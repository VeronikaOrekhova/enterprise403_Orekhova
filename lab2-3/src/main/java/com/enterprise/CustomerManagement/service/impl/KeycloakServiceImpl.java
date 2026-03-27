package com.enterprise.CustomerManagement.service.impl;

import com.enterprise.CustomerManagement.props.KeycloakProperties;
import com.enterprise.CustomerManagement.model.dto.request.LoginRequest;
import com.enterprise.CustomerManagement.model.dto.request.RegisterRequest;
import com.enterprise.CustomerManagement.model.dto.response.TokenResponse;
import com.enterprise.CustomerManagement.service.KeycloakService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KeycloakServiceImpl implements KeycloakService {

    private final KeycloakProperties props;
    private final RestTemplate restTemplate;

    private String getAdminToken() {
        String url = props.getServerUrl() + "/realms/master/protocol/openid-connect/token";

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", "admin-cli");
        form.add("username", props.getAdminUsername());
        form.add("password", props.getAdminPassword());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                url, new HttpEntity<>(form, headers), Map.class);

        return (String) response.getBody().get("access_token");
    }

    @Override
    public void register(RegisterRequest request) {
        String adminToken = getAdminToken();

        String createUserUrl = props.getServerUrl() + "/admin/realms/" + props.getRealm() + "/users";

        Map<String, Object> user = Map.of(
                "username", request.username(),
                "email", request.email(),
                "firstName", request.firstName(),
                "lastName", request.lastName(),
                "enabled", true,
                "credentials", List.of(Map.of(
                        "type", "password",
                        "value", request.password(),
                        "temporary", false
                ))
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Void> createResponse = restTemplate.postForEntity(
                createUserUrl, new HttpEntity<>(user, headers), Void.class);

        if (!createResponse.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Ошибка создания пользователя в Keycloak");
        }

        String userId = extractUserIdFromLocation(createResponse);

        String roleUrl = props.getServerUrl() + "/admin/realms/" + props.getRealm() + "/roles/ROLE_USER";
        ResponseEntity<Map> roleResponse = restTemplate.exchange(
                roleUrl, HttpMethod.GET, new HttpEntity<>(headers), Map.class);

        Map<String, Object> roleRepresentation = roleResponse.getBody();

        String assignRoleUrl = props.getServerUrl() + "/admin/realms/" + props.getRealm()
                + "/users/" + userId + "/role-mappings/realm";

        restTemplate.postForEntity(
                assignRoleUrl,
                new HttpEntity<>(List.of(roleRepresentation), headers),
                Void.class);
    }

    private String extractUserIdFromLocation(ResponseEntity<Void> response) {
        String location = response.getHeaders().getFirst(HttpHeaders.LOCATION);
        if (location == null) {
            throw new RuntimeException("Не удалось получить ID пользователя из Keycloak");
        }
        return location.substring(location.lastIndexOf("/") + 1);
    }

    @Override
    public TokenResponse login(LoginRequest request) {
        String url = props.getServerUrl() + "/realms/" + props.getRealm()
                + "/protocol/openid-connect/token";

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", props.getClientId());
        form.add("client_secret", props.getClientSecret());
        form.add("username", request.username());
        form.add("password", request.password());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        return restTemplate.postForObject(
                url, new HttpEntity<>(form, headers), TokenResponse.class);
    }
}