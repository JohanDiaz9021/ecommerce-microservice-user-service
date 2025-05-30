package com.selimhorri.app.e2e;

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
public class UserE2EFlowTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private String randomEmail() {
        return UUID.randomUUID() + "@example.com";
    }

    private String randomUsername() {
        return "user_" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Test
    public void testFullUserRegistrationFlow() {
        UserDto newUser = UserDto.builder()
                .firstName("E2E")
                .lastName("Tester")
                .email(randomEmail())
                .build();

        ResponseEntity<UserDto> userResp = restTemplate.postForEntity("/api/users", newUser, UserDto.class);
        assertThat(userResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        UserDto createdUser = userResp.getBody();
        assertThat(createdUser).isNotNull();
        Integer userId = createdUser.getUserId();

        CredentialDto newCredential = CredentialDto.builder()
                .username(randomUsername())
                .password("StrongPass123!")
                .roleBasedAuthority(com.selimhorri.app.domain.RoleBasedAuthority.ROLE_USER)
                .isEnabled(true)
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .userDto(createdUser)
                .build();

        ResponseEntity<CredentialDto> credResp = restTemplate.postForEntity("/api/credentials", newCredential, CredentialDto.class);
        assertThat(credResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        CredentialDto createdCred = credResp.getBody();
        assertThat(createdCred).isNotNull();

        VerificationTokenDto tokenDto = VerificationTokenDto.builder()
                .token("e2e-token-" + UUID.randomUUID())
                .expireDate(LocalDate.now().plusDays(7))
                .credentialDto(createdCred)
                .build();

        ResponseEntity<VerificationTokenDto> tokenResp = restTemplate.postForEntity("/api/verificationTokens", tokenDto, VerificationTokenDto.class);
        assertThat(tokenResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        VerificationTokenDto createdToken = tokenResp.getBody();
        assertThat(createdToken).isNotNull();

        ResponseEntity<UserDto> userGetResp = restTemplate.getForEntity("/api/users/" + userId, UserDto.class);
        assertThat(userGetResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(userGetResp.getBody().getFirstName()).isEqualTo("E2E");
    }

    @Test
    public void testUpdateUserProfile() {
        UserDto newUser = UserDto.builder()
                .firstName("UpdateTest")
                .lastName("User")
                .email(randomEmail())
                .build();

        UserDto createdUser = restTemplate.postForEntity("/api/users", newUser, UserDto.class).getBody();
        assertThat(createdUser).isNotNull();

        createdUser.setFirstName("UpdatedName");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserDto> updateEntity = new HttpEntity<>(createdUser, headers);

        ResponseEntity<UserDto> updateResp = restTemplate.exchange("/api/users/" + createdUser.getUserId(), HttpMethod.PUT, updateEntity, UserDto.class);
        assertThat(updateResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResp.getBody().getFirstName()).isEqualTo("UpdatedName");
    }

    @Test
    public void testAddAddressToUser() {
        UserDto newUser = UserDto.builder()
                .firstName("Address")
                .lastName("Tester")
                .email(randomEmail())
                .build();

        UserDto createdUser = restTemplate.postForEntity("/api/users", newUser, UserDto.class).getBody();
        assertThat(createdUser).isNotNull();

        AddressDto newAddress = AddressDto.builder()
                .fullAddress("123 E2E Street")
                .city("Cali")
                .postalCode("760001")
                .userDto(createdUser)
                .build();

        AddressDto createdAddress = restTemplate.postForEntity("/api/address", newAddress, AddressDto.class).getBody();
        assertThat(createdAddress).isNotNull();
        assertThat(createdAddress.getUserDto().getUserId()).isEqualTo(createdUser.getUserId());
    }

    @Test
    public void testDeleteUserCascades() {
        UserDto newUser = UserDto.builder()
                .firstName("Delete")
                .lastName("Cascade")
                .email(randomEmail())
                .build();

        UserDto createdUser = restTemplate.postForEntity("/api/users", newUser, UserDto.class).getBody();
        assertThat(createdUser).isNotNull();

        restTemplate.delete("/api/users/" + createdUser.getUserId());

        ResponseEntity<String> getResp = restTemplate.getForEntity("/api/users/" + createdUser.getUserId(), String.class);
        assertThat(getResp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testCompleteUserFlow() {
        UserDto user = UserDto.builder()
                .firstName("Complete")
                .lastName("Flow")
                .email(randomEmail())
                .build();

        UserDto createdUser = restTemplate.postForEntity("/api/users", user, UserDto.class).getBody();
        assertThat(createdUser).isNotNull();

        CredentialDto cred = CredentialDto.builder()
                .username(randomUsername())
                .password("CompletePass123")
                .roleBasedAuthority(com.selimhorri.app.domain.RoleBasedAuthority.ROLE_USER)
                .isEnabled(true)
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .userDto(createdUser)
                .build();

        CredentialDto createdCred = restTemplate.postForEntity("/api/credentials", cred, CredentialDto.class).getBody();
        assertThat(createdCred).isNotNull();

        VerificationTokenDto token = VerificationTokenDto.builder()
                .token("complete-token-" + UUID.randomUUID())
                .expireDate(LocalDate.now().plusDays(7))
                .credentialDto(createdCred)
                .build();

        VerificationTokenDto createdToken = restTemplate.postForEntity("/api/verificationTokens", token, VerificationTokenDto.class).getBody();
        assertThat(createdToken).isNotNull();

        createdUser.setFirstName("Completed");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserDto> updateEntity = new HttpEntity<>(createdUser, headers);

        UserDto updatedUser = restTemplate.exchange("/api/users/" + createdUser.getUserId(), HttpMethod.PUT, updateEntity, UserDto.class).getBody();
        assertThat(updatedUser.getFirstName()).isEqualTo("Completed");

        restTemplate.delete("/api/users/" + createdUser.getUserId());

        ResponseEntity<String> getAfterDelete = restTemplate.getForEntity("/api/users/" + createdUser.getUserId(), String.class);
        assertThat(getAfterDelete.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
