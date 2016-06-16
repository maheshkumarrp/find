package com.autonomy.abc.users;

import com.autonomy.abc.base.IsoHsodTestBase;
import com.autonomy.abc.fixtures.EmailTearDownStrategy;
import com.autonomy.abc.fixtures.UserTearDownStrategy;
import com.autonomy.abc.selenium.auth.HsodNewUser;
import com.autonomy.abc.selenium.auth.HsodUser;
import com.autonomy.abc.selenium.error.Errors;
import com.autonomy.abc.selenium.users.HsodUserCreationModal;
import com.autonomy.abc.selenium.users.HsodUserService;
import com.autonomy.abc.selenium.users.HsodUsersPage;
import com.autonomy.abc.selenium.users.UserNotCreatedException;
import com.autonomy.abc.shared.UserTestHelper;
import com.hp.autonomy.frontend.selenium.config.TestConfig;
import com.hp.autonomy.frontend.selenium.element.GritterNotice;
import com.hp.autonomy.frontend.selenium.element.ModalView;
import com.hp.autonomy.frontend.selenium.framework.logging.ActiveBug;
import com.hp.autonomy.frontend.selenium.framework.logging.RelatedTo;
import com.hp.autonomy.frontend.selenium.framework.logging.ResolvedBug;
import com.hp.autonomy.frontend.selenium.users.NewUser;
import com.hp.autonomy.frontend.selenium.users.Role;
import com.hp.autonomy.frontend.selenium.users.User;
import com.hp.autonomy.frontend.selenium.util.ElementUtil;
import com.hp.autonomy.frontend.selenium.util.Waits;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

import static com.hp.autonomy.frontend.selenium.framework.state.TestStateAssert.assertThat;
import static com.hp.autonomy.frontend.selenium.framework.state.TestStateAssert.verifyThat;
import static com.hp.autonomy.frontend.selenium.matchers.ElementMatchers.*;
import static com.hp.autonomy.frontend.selenium.matchers.StringMatchers.containsString;
import static org.hamcrest.Matchers.*;
import static org.openqa.selenium.lift.Matchers.displayed;

public class UserManagementHostedITCase extends IsoHsodTestBase {
    private final NewUser aNewUser;
    private final UserTestHelper helper;

    private HsodUserService userService;
    private HsodUsersPage usersPage;

    public UserManagementHostedITCase(final TestConfig config) {
        super(config);
        aNewUser = config.getNewUser("james");
        helper = new UserTestHelper(getApplication(), config);
    }

    @Before
    public void setUp() {
        userService = getApplication().userService();
        usersPage = userService.goToUsers();
        userService.deleteOtherUsers();
    }

    @After
    public void emailTearDown() {
        new EmailTearDownStrategy(getMainSession(), getConfig().getAuthenticationStrategy()).tearDown(this);
    }

    @After
    public void userTearDown() {
        new UserTearDownStrategy(getInitialUser()).tearDown(this);
    }

    @Test
    @ResolvedBug("CSA-1775")
    @ActiveBug("CSA-1800")
    public void testCannotAddInvalidEmail(){
        final HsodNewUser newUser = new HsodNewUser("jeremy","jeremy");

        verifyAddingInvalidUser(newUser);
        verifyThat(usersPage.getUsernames(), not(hasItem(newUser.getUsername())));

        //Sometimes it requires us to add a valid user before invalid users show up
        userService.createNewUser(new HsodNewUser("Valid", gmailString("NonInvalidEmail")), Role.ADMIN);

        Waits.loadOrFadeWait();

        verifyThat(usersPage.getUsernames(), not(hasItem(newUser.getUsername())));
    }

    // unlike on-prem, duplicate usernames (display names) are allowed
    @Test
    public void testDuplicateUsername() {
        final User user = userService.createNewUser(aNewUser, Role.ADMIN);
        assertThat(usersPage.getUsernames(), hasSize(1));
        verifyAddingValidUser(new HsodNewUser(user.getUsername(), gmailString("isValid")));
    }

    @Test
    @ResolvedBug("CSA-1776")
    @ActiveBug("CSA-1800")
    public void testAddingValidDuplicateAfterInvalid() {
        final String username = "bob";
        verifyAddingInvalidUser(new HsodNewUser(username, "INVALID_EMAIL"));
        verifyAddingValidUser(new HsodNewUser(username, gmailString("isValid")));
        verifyAddingValidUser(new HsodNewUser(username, gmailString("alsoValid")));
    }

    private void verifyAddingInvalidUser(final HsodNewUser invalidUser) {
        final int existingUsers = usersPage.getUsernames().size();
        usersPage.createUserButton().click();

        try {
            usersPage.addNewUser(invalidUser, Role.ADMIN);
        } catch (TimeoutException | UserNotCreatedException e){
            /* Expected behaviour */
        }

        verifyModalElements();
        verifyThat(ModalView.getVisibleModalView(getDriver()).getText(), containsString(Errors.User.CREATING));
        usersPage.userCreationModal().close();

        verifyThat("number of users has not increased", usersPage.getUsernames(), hasSize(existingUsers));

        Waits.loadOrFadeWait();

        verifyThat("number of users has not increased after refresh", usersPage.getUsernames(), hasSize(existingUsers));
    }

    private HsodUser verifyAddingValidUser(final HsodNewUser validUser) {
        final int existingUsers = usersPage.getUsernames().size();
        usersPage.createUserButton().click();

        final HsodUser user = usersPage.addNewUser(validUser, Role.ADMIN);

        verifyModalElements();
        verifyThat(ModalView.getVisibleModalView(getDriver()).getText(), not(containsString(Errors.User.CREATING)));
        usersPage.userCreationModal().close();

        verifyThat(usersPage.getUsernames(), hasItem(validUser.getUsername()));

        Waits.loadOrFadeWait();
        verifyThat("exactly one new user appears", usersPage.getUsernames(), hasSize(existingUsers + 1));
        return user;
    }

    private void verifyModalElements() {
        final HsodUserCreationModal modal = usersPage.userCreationModal();
        verifyModalElement(modal.usernameInput().getElement());
        verifyModalElement(modal.emailInput().getElement());
        verifyModalElement(modal.roleDropdown());
        verifyModalElement(modal.createButton());
    }

    private void verifyModalElement(final WebElement input) {
        verifyThat(getContainingDiv(input), not(hasClass("has-error")));
    }

    @Test
    public void testResettingAuthentication(){
        final NewUser newUser = getConfig().generateNewUser();

        final User user = userService.createNewUser(newUser,Role.USER);
        getConfig().getAuthenticationStrategy().authenticate(user);


        waitForUserConfirmed(user);

        userService.resetAuthentication(user);

        verifyThat(usersPage.getText(), containsString("Done! A reset authentication email has been sent to " + user.getUsername()));

        new Thread(){
            @Override
            public void run() {
                new WebDriverWait(getDriver(),180)
                        .withMessage("User never reset their authentication")
                        .until(GritterNotice.notificationContaining("User " + user.getUsername() + " reset their authentication"));

                LOGGER.info("User reset their authentication notification shown");
            }
        }.start();
        getConfig().getAuthenticationStrategy().authenticate(user);
    }

    @Test
    public void testEditingUsername(){
        final User user = userService.createNewUser(new HsodNewUser("editUsername", gmailString("editUsername")), Role.ADMIN);

        verifyThat(usersPage.getUsernames(), hasItem(user.getUsername()));

        userService.editUsername(user, "Dave");

        verifyThat(usersPage.getUsernames(), hasItem("Dave"));

        try {
            userService.editUsername(user, "");
        } catch (final TimeoutException e) { /* Should fail here as you're giving it an invalid username */ }

        final WebElement formGroup = usersPage.getUserRow(user).usernameEditBox();
        verifyThat(formGroup, displayed());
        verifyThat(formGroup, hasClass("has-error"));
    }

    @Test
    public void testAddingAndAuthenticatingUser(){
        final User user = userService.createNewUser(getConfig().generateNewUser(), Role.USER);
        getConfig().getAuthenticationStrategy().authenticate(user);

        waitForUserConfirmed(user);
        verifyThat(usersPage.getUserRow(user).isConfirmed(), is(true));
    }

    @Test
    @ResolvedBug("CSA-1766")
    @RelatedTo("CSA-1663")
    public void testCreateUser(){
        usersPage.createUserButton().click();
        assertThat(usersPage, modalIsDisplayed());
        final HsodUserCreationModal newUserModal = usersPage.userCreationModal();
        verifyThat(newUserModal, hasTextThat(startsWith("Create New Users")));

        newUserModal.createButton().click();
        verifyThat(newUserModal, containsText(Errors.User.BLANK_EMAIL));

        final String username = "Andrew";

        newUserModal.usernameInput().setValue(username);
        newUserModal.emailInput().clear();
        newUserModal.createButton().click();
        verifyThat(newUserModal, containsText(Errors.User.BLANK_EMAIL));

        newUserModal.emailInput().setValue("hodtestqa401+CreateUserTest@gmail.com");
        newUserModal.selectRole(Role.USER);
        newUserModal.createUser();
//        verifyThat(newUserModal, containsText("Done! User Andrew successfully created"));

        usersPage.userCreationModal().close();
        verifyThat(usersPage, not(containsText("Create New Users")));   //Not sure what this is meant to be doing? Verifying modal no longer open??

        verifyThat(usersPage.getUsernames(),hasItem(username));
    }

    @Test
    public void testAddStupidlyLongUsername() {
        final String longUsername = StringUtils.repeat("a", 100);
        helper.verifyCreateDeleteInTable(new HsodNewUser(longUsername, "hodtestqa401+longusername@gmail.com"));
    }

    @Test
    @ActiveBug("HOD-532")
    public void testUserConfirmedWithoutRefreshing(){
        final User user = userService.createNewUser(getConfig().generateNewUser(), Role.USER);
        getConfig().getAuthenticationStrategy().authenticate(user);

        new WebDriverWait(getDriver(), 30).pollingEvery(5,TimeUnit.SECONDS).until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(final WebDriver driver) {
                return usersPage.getUserRow(user).isConfirmed();
            }
        });
    }

    private void waitForUserConfirmed(final User user){
        new WebDriverWait(getDriver(),30).pollingEvery(10, TimeUnit.SECONDS).withMessage("User not showing as confirmed").until(new WaitForUserToBeConfirmed(user));
    }

    private class WaitForUserToBeConfirmed implements ExpectedCondition<Boolean>{
        private final User user;

        WaitForUserToBeConfirmed(final User user){
            this.user = user;
        }

        @Override
        public Boolean apply(final WebDriver driver) {
            getWindow().refresh();
            usersPage = getElementFactory().getUsersPage();
            Waits.loadOrFadeWait();
            return usersPage.getUserRow(user).isConfirmed();
        }
    }

    private WebElement getContainingDiv(final WebElement webElement){
        return ElementUtil.ancestor(webElement, 2);
    }

    public static String gmailString(final String plus){
        return "hodtestqa401+" + plus + "@gmail.com";
    }
}
