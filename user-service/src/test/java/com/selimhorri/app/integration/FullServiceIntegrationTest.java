/*package com.selimhorri.app.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.UUID;

import com.selimhorri.app.dto.AddressDto;
import com.selimhorri.app.dto.CredentialDto;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.dto.VerificationTokenDto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FullServiceIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;


    private String randomEmail() {
        return UUID.randomUUID() + "@example.com";
    }

    @Test
    public void userCrudIntegration() {
        UserDto newUser = UserDto.builder()
                .firstName("Integration")
                .lastName("User")
                .email(randomEmail())
                .build();

        ResponseEntity<UserDto> createResponse = restTemplate.postForEntity("/api/users", newUser, UserDto.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        UserDto createdUser = createResponse.getBody();
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getUserId()).isNotNull();

        Integer userId = createdUser.getUserId();

        ResponseEntity<UserDto> readResponse = restTemplate.getForEntity("/api/users/" + userId, UserDto.class);
        assertThat(readResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(readResponse.getBody().getFirstName()).isEqualTo("Integration");

        createdUser.setFirstName("UpdatedName");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserDto> updateEntity = new HttpEntity<>(createdUser, headers);

        ResponseEntity<UserDto> updateResponse = restTemplate.exchange("/api/users/" + userId, HttpMethod.PUT, updateEntity, UserDto.class);
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody().getFirstName()).isEqualTo("UpdatedName");

        restTemplate.delete("/api/users/" + userId);

        ResponseEntity<String> verifyDelete = restTemplate.getForEntity("/api/users/" + userId, String.class);
        assertThat(verifyDelete.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }


    private String randomUsername() {
        return "user_" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Test
    public void credentialCrudIntegration() {
        CredentialDto newCredential = CredentialDto.builder()
                .username(randomUsername())
                .password("pass123")
                .roleBasedAuthority(com.selimhorri.app.domain.RoleBasedAuthority.ROLE_USER)
                .isEnabled(true)
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .build();

        ResponseEntity<CredentialDto> createResponse = restTemplate.postForEntity("/api/credentials", newCredential, CredentialDto.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        CredentialDto createdCredential = createResponse.getBody();
        assertThat(createdCredential).isNotNull();
        assertThat(createdCredential.getCredentialId()).isNotNull();

        Integer credentialId = createdCredential.getCredentialId();

        ResponseEntity<CredentialDto> readResponse = restTemplate.getForEntity("/api/credentials/" + credentialId, CredentialDto.class);
        assertThat(readResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(readResponse.getBody().getUsername()).isEqualTo(createdCredential.getUsername());

        createdCredential.setPassword("newpass456");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CredentialDto> updateEntity = new HttpEntity<>(createdCredential, headers);

        ResponseEntity<CredentialDto> updateResponse = restTemplate.exchange("/api/credentials/" + credentialId, HttpMethod.PUT, updateEntity, CredentialDto.class);
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody().getPassword()).isEqualTo("newpass456");

        restTemplate.delete("/api/credentials/" + credentialId);

        ResponseEntity<String> verifyDelete = restTemplate.getForEntity("/api/credentials/" + credentialId, String.class);
        assertThat(verifyDelete.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }


    private String randomPostalCode() {
        return "PC-" + UUID.randomUUID().toString().substring(0, 5);
    }

    @Test
    public void addressCrudIntegration() {
        AddressDto newAddress = AddressDto.builder()
                .fullAddress("Integration Test Street 123")
                .city("Medellín")
                .postalCode(randomPostalCode())
                .build();

        ResponseEntity<AddressDto> createResponse = restTemplate.postForEntity("/api/address", newAddress, AddressDto.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        AddressDto createdAddress = createResponse.getBody();
        assertThat(createdAddress).isNotNull();
        assertThat(createdAddress.getAddressId()).isNotNull();

        Integer addressId = createdAddress.getAddressId();

        ResponseEntity<AddressDto> readResponse = restTemplate.getForEntity("/api/address/" + addressId, AddressDto.class);
        assertThat(readResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(readResponse.getBody().getFullAddress()).isEqualTo("Integration Test Street 123");

        createdAddress.setCity("Bogotá");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AddressDto> updateEntity = new HttpEntity<>(createdAddress, headers);

        ResponseEntity<AddressDto> updateResponse = restTemplate.exchange("/api/address/" + addressId, HttpMethod.PUT, updateEntity, AddressDto.class);
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody().getCity()).isEqualTo("Bogotá");

        restTemplate.delete("/api/address/" + addressId);

        ResponseEntity<String> verifyDelete = restTemplate.getForEntity("/api/address/" + addressId, String.class);
        assertThat(verifyDelete.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void verificationTokenCrudIntegration() {
        CredentialDto credential = CredentialDto.builder()
                .username("integrationuser")
                .password("pass123")
                .roleBasedAuthority(com.selimhorri.app.domain.RoleBasedAuthority.ROLE_USER)
                .isEnabled(true)
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .build();

        ResponseEntity<CredentialDto> createCredentialResponse = restTemplate.postForEntity("/api/credentials", credential, CredentialDto.class);
        assertThat(createCredentialResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        CredentialDto createdCredential = createCredentialResponse.getBody();

        VerificationTokenDto tokenDto = VerificationTokenDto.builder()
                .token("token_integration_123")
                .expireDate(LocalDate.now().plusDays(7))
                .credentialDto(createdCredential)
                .build();

        ResponseEntity<VerificationTokenDto> createResponse = restTemplate.postForEntity("/api/verificationTokens", tokenDto, VerificationTokenDto.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        VerificationTokenDto createdToken = createResponse.getBody();
        assertThat(createdToken).isNotNull();
        assertThat(createdToken.getVerificationTokenId()).isNotNull();

        Integer tokenId = createdToken.getVerificationTokenId();

        ResponseEntity<VerificationTokenDto> readResponse = restTemplate.getForEntity("/api/verificationTokens/" + tokenId, VerificationTokenDto.class);
        assertThat(readResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(readResponse.getBody().getToken()).isEqualTo("token_integration_123");

        createdToken.setToken("updated_token");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<VerificationTokenDto> updateEntity = new HttpEntity<>(createdToken, headers);

        ResponseEntity<VerificationTokenDto> updateResponse = restTemplate.exchange("/api/verificationTokens/" + tokenId, HttpMethod.PUT, updateEntity, VerificationTokenDto.class);
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody().getToken()).isEqualTo("updated_token");

        restTemplate.delete("/api/verificationTokens/" + tokenId);

        ResponseEntity<String> verifyDelete = restTemplate.getForEntity("/api/verificationTokens/" + tokenId, String.class);
        assertThat(verifyDelete.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
*/