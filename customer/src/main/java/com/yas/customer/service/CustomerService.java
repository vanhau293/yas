package com.yas.customer.service;

import com.yas.commonlibrary.exception.AccessDeniedException;
import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.ForbiddenException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.commonlibrary.exception.WrongEmailFormatException;
import com.yas.customer.config.KeycloakPropsConfig;
import com.yas.customer.utils.Constants;
import com.yas.customer.viewmodel.customer.CustomerAdminVm;
import com.yas.customer.viewmodel.customer.CustomerListVm;
import com.yas.customer.viewmodel.customer.CustomerPostVm;
import com.yas.customer.viewmodel.customer.CustomerProfileRequestVm;
import com.yas.customer.viewmodel.customer.CustomerVm;
import com.yas.customer.viewmodel.customer.GuestUserVm;
import jakarta.ws.rs.core.Response;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import org.apache.commons.validator.routines.EmailValidator;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private static final String ERROR_FORMAT = "%s: Client %s don't have access right for this resource";
    private static final int USER_PER_PAGE = 10;
    private static final String GUEST = "GUEST";
    private final Keycloak keycloak;
    private final KeycloakPropsConfig keycloakPropsConfig;

    public CustomerService(Keycloak keycloak, KeycloakPropsConfig keycloakPropsConfig) {
        this.keycloak = keycloak;
        this.keycloakPropsConfig = keycloakPropsConfig;
    }

    public static CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }

    public CustomerListVm getCustomers(int pageNo) {
        try {
            List<CustomerAdminVm> result = keycloak.realm(keycloakPropsConfig.getRealm()).users()
                .search(null, pageNo * USER_PER_PAGE, USER_PER_PAGE).stream()
                .filter(UserRepresentation::isEnabled)
                .map(CustomerAdminVm::fromUserRepresentation)
                .toList();
            int totalUser = result.size();

            return new CustomerListVm(totalUser, result, (totalUser + USER_PER_PAGE - 1) / USER_PER_PAGE);
        } catch (ForbiddenException exception) {
            throw new AccessDeniedException(
                String.format(ERROR_FORMAT, exception.getMessage(), keycloakPropsConfig.getResource()));
        }
    }

    public void updateCustomer(String id, CustomerProfileRequestVm requestVm) {
        UserRepresentation userRepresentation =
            keycloak.realm(keycloakPropsConfig.getRealm()).users().get(id).toRepresentation();
        if (userRepresentation != null) {
            userRepresentation.setFirstName(requestVm.firstName());
            userRepresentation.setLastName(requestVm.lastName());
            userRepresentation.setEmail(requestVm.email());
            RealmResource realmResource = keycloak.realm(keycloakPropsConfig.getRealm());
            UserResource userResource = realmResource.users().get(id);
            userResource.update(userRepresentation);
        } else {
            throw new NotFoundException(Constants.ErrorCode.USER_NOT_FOUND);
        }
    }

    public void deleteCustomer(String id) {
        UserRepresentation userRepresentation =
            keycloak.realm(keycloakPropsConfig.getRealm()).users().get(id).toRepresentation();
        if (userRepresentation != null) {
            RealmResource realmResource = keycloak.realm(keycloakPropsConfig.getRealm());
            UserResource userResource = realmResource.users().get(id);
            userRepresentation.setEnabled(false);
            userResource.update(userRepresentation);
        } else {
            throw new NotFoundException(Constants.ErrorCode.USER_NOT_FOUND);
        }
    }

    public CustomerAdminVm getCustomerByEmail(String email) {
        try {
            if (EmailValidator.getInstance().isValid(email)) {
                List<UserRepresentation> searchResult =
                    keycloak.realm(keycloakPropsConfig.getRealm()).users().search(email, true);
                if (searchResult.isEmpty()) {
                    throw new NotFoundException(Constants.ErrorCode.USER_WITH_EMAIL_NOT_FOUND, email);
                }
                return CustomerAdminVm.fromUserRepresentation(searchResult.getFirst());
            } else {
                throw new WrongEmailFormatException(Constants.ErrorCode.WRONG_EMAIL_FORMAT, email);
            }
        } catch (ForbiddenException exception) {
            throw new AccessDeniedException(
                String.format(ERROR_FORMAT, exception.getMessage(), keycloakPropsConfig.getResource()));
        }
    }

    public CustomerVm getCustomerProfile(String userId) {
        try {
            return CustomerVm.fromUserRepresentation(
                keycloak.realm(keycloakPropsConfig.getRealm()).users().get(userId).toRepresentation());

        } catch (ForbiddenException exception) {
            throw new AccessDeniedException(
                String.format(ERROR_FORMAT, exception.getMessage(), keycloakPropsConfig.getResource()));
        }
    }

    public GuestUserVm createGuestUser() {
        // Get realm
        RealmResource realmResource = keycloak.realm(keycloakPropsConfig.getRealm());
        String randomGuestName = generateSafeString();
        String guestUserEmail = randomGuestName + "_guest@yas.com";
        CredentialRepresentation credential = createPasswordCredentials(GUEST);

        // Define user
        UserRepresentation user = new UserRepresentation();
        user.setUsername(guestUserEmail);
        user.setFirstName(GUEST);
        user.setLastName(randomGuestName);
        user.setEmail(guestUserEmail);
        user.setCredentials(Collections.singletonList(credential));
        user.setEnabled(true);
        Response response = realmResource.users().create(user);

        // get new user
        String userId = CreatedResponseUtil.getCreatedId(response);
        UserResource userResource = realmResource.users().get(userId);
        RoleRepresentation guestRealmRole = realmResource.roles().get(GUEST).toRepresentation();

        // Assign realm role GUEST to user
        userResource.roles().realmLevel().add(Collections.singletonList(guestRealmRole));

        return new GuestUserVm(userId, guestUserEmail, GUEST);
    }

    private String generateSafeString() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[12];
        random.nextBytes(bytes);
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        return encoder.encodeToString(bytes);
    }

    public CustomerVm create(CustomerPostVm customerPostVm) {
        // Get realm
        RealmResource realmResource = keycloak.realm(keycloakPropsConfig.getRealm());

        if (checkUsernameExists(realmResource, customerPostVm.username())) {
            throw new DuplicatedException(Constants.ErrorCode.USERNAME_ALREADY_EXITED, customerPostVm.username());
        }
        if (checkEmailExists(realmResource, customerPostVm.email())) {
            throw new DuplicatedException(Constants.ErrorCode.USER_WITH_EMAIL_ALREADY_EXITED, customerPostVm.email());
        }

        // Define user
        UserRepresentation user = new UserRepresentation();
        user.setUsername(customerPostVm.username());
        user.setFirstName(customerPostVm.firstName());
        user.setLastName(customerPostVm.lastName());
        user.setEmail(customerPostVm.email());
        CredentialRepresentation credential = createPasswordCredentials(customerPostVm.password());
        user.setCredentials(Collections.singletonList(credential));
        user.setEnabled(true);
        Response response = realmResource.users().create(user);

        // get new user
        String userId = CreatedResponseUtil.getCreatedId(response);
        UserResource userResource = realmResource.users().get(userId);

        // Assign realm role to user
        RoleRepresentation realmRole = realmResource.roles().get(customerPostVm.role()).toRepresentation();
        userResource.roles().realmLevel().add(Collections.singletonList(realmRole));

        return CustomerVm.fromUserRepresentation(user);
    }

    private boolean checkUsernameExists(RealmResource realmResource, String username) {
        // Search for users by username
        List<UserRepresentation> users = realmResource.users().search(username, true);
        return !users.isEmpty();
    }

    private boolean checkEmailExists(RealmResource realmResource, String email) {
        // Search for users by email
        List<UserRepresentation> users = realmResource.users().search(null, null, null, email, 0, 1);
        return !users.isEmpty();
    }
}
