package com.yas.customer.service;

import com.yas.commonlibrary.exception.AccessDeniedException;
import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.commonlibrary.exception.WrongEmailFormatException;
import com.yas.customer.config.KeycloakPropsConfig;
import com.yas.customer.viewmodel.customer.*;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.ArgumentCaptor;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    private UsersResource usersResource;

    private CustomerService customerService;

    private RealmResource realmResource;

    private static final String USER_NAME = "test-username";

    private static final String REALM_NAME = "test-realm";

    private static final String VALID_EMAIL = "valid@example.com";

    private static final String ACCESS_DENIED_MESSAGE = "Access denied";

    @BeforeEach
    void setUp() {
        Keycloak keycloak = mock(Keycloak.class);
        KeycloakPropsConfig keycloakPropsConfig = mock(KeycloakPropsConfig.class);
        realmResource = mock(RealmResource.class);
        when(keycloakPropsConfig.getRealm()).thenReturn(REALM_NAME);
        when(keycloak.realm(REALM_NAME)).thenReturn(realmResource);
        usersResource = mock(UsersResource.class);
        customerService = new CustomerService(keycloak, keycloakPropsConfig);
        when(realmResource.users()).thenReturn(usersResource);
    }

    private List<UserRepresentation> getUserRepresentations() {

        UserRepresentation user1 = new UserRepresentation();
        user1.setId("1");
        user1.setUsername("user1");
        user1.setEmail(VALID_EMAIL);
        user1.setFirstName("FirstName1");
        user1.setLastName("LastName1");
        user1.setEnabled(true);
        user1.setCreatedTimestamp(946684800000L);

        UserRepresentation user2 = new UserRepresentation();
        user2.setId("2");
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setFirstName("FirstName2");
        user2.setLastName("LastName2");
        user2.setEnabled(true);
        user2.setCreatedTimestamp(946684800000L);

        List<UserRepresentation> userList = new ArrayList<>();
        userList.add(user1);
        userList.add(user2);

        return userList;
    }

    private UserRepresentation getUserRepresentation() {
        UserRepresentation userRep = new UserRepresentation();
        userRep.setFirstName("John");
        userRep.setLastName("Doe");
        userRep.setEmail("john.doe@example.com");
        return userRep;
    }

    private CustomerProfileRequestVm getCustomerProfileRequestVm() {
        return new CustomerProfileRequestVm("John", "Doe", "john.doe@example.com");
    }

    @Test
    void testGetCustomers_existsUserRepresentation_returnCustomerListVm() {

        when(usersResource.search(any(), anyInt(), anyInt())).thenReturn(getUserRepresentations());
        when(usersResource.count()).thenReturn(2);

        CustomerListVm customerListVm = customerService.getCustomers(1);

        assertThat(customerListVm.totalUser()).isEqualTo(2);
        assertThat(customerListVm.totalPage()).isEqualTo(1);
        assertThat(customerListVm.customers()).hasSize(2);

    }

    @Test
    void testGetCustomers_isUserRepresentationEmpty_returnCustomerListVm() {

        when(usersResource.search(any(), anyInt(), anyInt())).thenReturn(List.of());
        when(usersResource.count()).thenReturn(0);

        CustomerListVm customerListVm = customerService.getCustomers(1);

        assertThat(customerListVm.totalUser()).isZero();
        assertThat(customerListVm.totalPage()).isZero();
        assertThat(customerListVm.customers()).isEmpty();
    }

    @Test
    void testGetCustomers_hasError_throwForbiddenException() {

        when(usersResource.search(any(), anyInt(), anyInt()))
            .thenThrow(new AccessDeniedException(ACCESS_DENIED_MESSAGE));

        AccessDeniedException thrown = assertThrows(AccessDeniedException.class,
            () -> customerService.getCustomers(1));

        assertTrue(thrown.getMessage().contains(ACCESS_DENIED_MESSAGE));
    }

    @Test
    void testUpdateCustomer_isNormalCase_methodSuccess() {
        UserRepresentation userRepresentation = getUserRepresentation();
        UserResource userResource = mock(UserResource.class);
        when(usersResource.get(USER_NAME)).thenReturn(userResource);
        when(userResource.toRepresentation()).thenReturn(userRepresentation);

        ArgumentCaptor<UserRepresentation> argumentCaptor = ArgumentCaptor.forClass(UserRepresentation.class);
        customerService.updateCustomer(USER_NAME, getCustomerProfileRequestVm());

        verify(userResource).update(argumentCaptor.capture());
        UserRepresentation actual = argumentCaptor.getValue();
        assertThat(actual.getFirstName()).isEqualTo(userRepresentation.getFirstName());
        assertThat(actual.getLastName()).isEqualTo(userRepresentation.getLastName());
        assertThat(actual.getEmail()).isEqualTo(userRepresentation.getEmail());
    }

    @Test
    void testUpdateCustomer_isUserNotFound_ThrowNotFoundException() {
        UserResource userResource = mock(UserResource.class);
        when(usersResource.get(USER_NAME)).thenReturn(userResource);
        when(userResource.toRepresentation()).thenReturn(null);

        CustomerProfileRequestVm customerProfileRequestVm = getCustomerProfileRequestVm();
        NotFoundException thrown = assertThrows(NotFoundException.class,
            () -> customerService.updateCustomer(USER_NAME, customerProfileRequestVm));
        assertTrue(thrown.getMessage().contains("User not found"));
    }

    @Test
    void testDeleteCustomer_isNormalCase_methodSuccess() {
        UserRepresentation userRepresentation = getUserRepresentation();
        UserResource userResource = mock(UserResource.class);
        when(usersResource.get(USER_NAME)).thenReturn(userResource);
        when(userResource.toRepresentation()).thenReturn(userRepresentation);

        ArgumentCaptor<UserRepresentation> argumentCaptor = ArgumentCaptor.forClass(UserRepresentation.class);
        customerService.deleteCustomer(USER_NAME);

        verify(userResource).update(argumentCaptor.capture());
        UserRepresentation actual = argumentCaptor.getValue();
        assertFalse(actual.isEnabled());
    }

    @Test
    void testGetCustomerByEmail_isNormalCase_returnCustomerAdminVm() {
        when(usersResource.search(VALID_EMAIL, true)).thenReturn(getUserRepresentations());
        CustomerAdminVm adminVm = customerService.getCustomerByEmail(VALID_EMAIL);
        assertThat(adminVm.email()).isEqualTo(VALID_EMAIL);
        assertThat(adminVm.id()).isEqualTo("1");
        assertThat(adminVm.username()).isEqualTo("user1");
    }

    @Test
    void testGetCustomerByEmail_isInValidEmail_throwWrongEmailFormatException() {
        WrongEmailFormatException thrown = assertThrows(WrongEmailFormatException.class,
            () -> customerService.getCustomerByEmail("invalid-email"));
        assertTrue(thrown.getMessage().contains("Wrong email format for invalid-email"));
    }

    @Test
    void testGetCustomerByEmail_searchResultIsEmpty_throwNotFoundException() {
        when(usersResource.search(VALID_EMAIL, true)).thenReturn(List.of());
        NotFoundException thrown = assertThrows(NotFoundException.class,
            () -> customerService.getCustomerByEmail(VALID_EMAIL));
        assertTrue(thrown.getMessage().contains("User with email " + VALID_EMAIL + " not found"));
    }

    @Test
    void testGetCustomerByEmail_isAbnormalCase_throwForbiddenException() {

        when(usersResource.search(VALID_EMAIL, true))
            .thenThrow(new AccessDeniedException(ACCESS_DENIED_MESSAGE));

        AccessDeniedException thrown = assertThrows(AccessDeniedException.class,
            () -> customerService.getCustomerByEmail(VALID_EMAIL));

        assertTrue(thrown.getMessage().contains(ACCESS_DENIED_MESSAGE));
    }

    @Test
    void testGetCustomerProfile_isNormalCase_ReturnCustomerVm() {

        UserRepresentation userRepresentation = getUserRepresentation();
        UserResource userResource = mock(UserResource.class);
        when(usersResource.get(USER_NAME)).thenReturn(userResource);
        when(userResource.toRepresentation()).thenReturn(userRepresentation);

        CustomerVm customerVm = customerService.getCustomerProfile(USER_NAME);

        assertThat(customerVm.firstName()).isEqualTo(userRepresentation.getFirstName());
        assertThat(customerVm.lastName()).isEqualTo(userRepresentation.getLastName());
        assertThat(customerVm.email()).isEqualTo(userRepresentation.getEmail());
    }

    @Test
    void testGetCustomerProfile_isAbnormalCase_throwForbiddenException() {

        when(usersResource.get(USER_NAME))
            .thenThrow(new AccessDeniedException(ACCESS_DENIED_MESSAGE));

        AccessDeniedException thrown = assertThrows(AccessDeniedException.class,
            () -> customerService.getCustomerProfile(USER_NAME));

        assertTrue(thrown.getMessage().contains(ACCESS_DENIED_MESSAGE));
    }

    @Test
    void testCreateGuestUser_isNormalCase_returnGuestUserVm() {

        Response response = mock(Response.class);

        when(usersResource.create(any(UserRepresentation.class))).thenReturn(response);
        URI uri = mock(URI.class);
        when(response.getLocation()).thenReturn(uri);
        when(response.getStatusInfo()).thenReturn(Response.Status.CREATED);
        when(uri.getPath()).thenReturn("/test/1");

        UserResource userResource = mock(UserResource.class);
        when(usersResource.get("1")).thenReturn(userResource);

        RolesResource rolesResource = mock(RolesResource.class);
        when(realmResource.roles()).thenReturn(rolesResource);
        RoleResource roleResource = mock(RoleResource.class);
        when(rolesResource.get("GUEST")).thenReturn(roleResource);
        when(roleResource.toRepresentation()).thenReturn(mock(RoleRepresentation.class));

        RoleMappingResource roleMappingResource = mock(RoleMappingResource.class);
        when(userResource.roles()).thenReturn(roleMappingResource);
        when(roleMappingResource.realmLevel()).thenReturn(mock(RoleScopeResource.class));

        GuestUserVm guestUserVm = customerService.createGuestUser();

        assertThat(guestUserVm.userId()).isEqualTo("1");
        assertThat(guestUserVm.email()).contains("_guest@yas.com");
        assertThat(guestUserVm.password()).isEqualTo("GUEST");
    }

    @Test
    void testCreateUser_isNormalCase_returnCustomerPostVm() {
        CustomerPostVm customerPostVm = new CustomerPostVm("user1", "test@gmail.com", "John",
            "Doe", "123", "ADMIN");
        Response response = mock(Response.class);

        when(usersResource.create(any(UserRepresentation.class))).thenReturn(response);
        URI uri = mock(URI.class);
        when(response.getLocation()).thenReturn(uri);
        when(response.getStatusInfo()).thenReturn(Response.Status.CREATED);
        when(uri.getPath()).thenReturn("/test/1");

        UserResource userResource = mock(UserResource.class);
        when(usersResource.get("1")).thenReturn(userResource);

        when(realmResource.users().search(anyString(), anyBoolean())).thenReturn(Collections.emptyList());
        when(realmResource.users().search(any(), any(), any(), anyString(), any(), any())).thenReturn(Collections.emptyList());

        RolesResource rolesResource = mock(RolesResource.class);
        when(realmResource.roles()).thenReturn(rolesResource);
        RoleResource roleResource = mock(RoleResource.class);
        when(rolesResource.get("ADMIN")).thenReturn(roleResource);
        when(roleResource.toRepresentation()).thenReturn(mock(RoleRepresentation.class));

        RoleMappingResource roleMappingResource = mock(RoleMappingResource.class);
        when(userResource.roles()).thenReturn(roleMappingResource);
        when(roleMappingResource.realmLevel()).thenReturn(mock(RoleScopeResource.class));

        CustomerVm customerVm = customerService.create(customerPostVm);

        assertThat(customerVm.username()).isEqualTo("user1");
        assertThat(customerVm.email()).isEqualTo("test@gmail.com");
        assertThat(customerVm.firstName()).contains("John");
        assertThat(customerVm.lastName()).isEqualTo("Doe");
    }

    @Test
    void testCreateUser_whenUsernameAlreadyExisted_thenThrowDuplicateException() {
        CustomerPostVm customerPostVm = new CustomerPostVm("user1", "test@gmail.com", "John",
            "Doe", "123", "ADMIN");

        when(realmResource.users().search(anyString(), anyBoolean()))
            .thenReturn(Collections.singletonList(mock(UserRepresentation.class)));

        assertThrows(DuplicatedException.class, () -> customerService.create(customerPostVm));
    }
}
