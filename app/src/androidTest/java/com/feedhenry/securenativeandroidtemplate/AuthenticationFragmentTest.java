package com.feedhenry.securenativeandroidtemplate;

import android.support.test.runner.AndroidJUnit4;

import com.feedhenry.securenativeandroidtemplate.authenticate.AuthenticateResult;
import com.feedhenry.securenativeandroidtemplate.authenticate.SimpleAuthenticationProviderImpl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasFocus;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertTrue;

/**
 * Created by weili on 04/09/2017.
 */
@RunWith(AndroidJUnit4.class)
public class AuthenticationFragmentTest extends FragmentTestBase{
    SimpleAuthenticationFragment simpleAuthenticationFragment;

    static final String TEST_USERNAME_ERROR = "testuser_error";
    static final String TEST_USERNAME_SUCCESS = "testuser_success";
    static final String TEST_PASSWORD = "password";

    private boolean authenticated = false;

    SimpleAuthenticationProviderImpl authProvider = new SimpleAuthenticationProviderImpl() {
        @Override
        public AuthenticateResult authenticateWithUsernameAndPassword(String username, String password) {
            if (TEST_USERNAME_ERROR.equalsIgnoreCase(username)) {
                return null;
            } else {
                return new AuthenticateResult();
            }
        }
    };

    @Before
    public void setUp() throws Exception {
        simpleAuthenticationFragment = new SimpleAuthenticationFragment();
        simpleAuthenticationFragment.setAuthProvider(authProvider);
        loadFragment(simpleAuthenticationFragment);
    }

    @Test
    public void authMissingField() throws Exception {
        onView(withId(R.id.username)).perform(typeText(TEST_USERNAME_ERROR));
        onView(withId(R.id.sign_in_button)).perform(click());
        onView(withId(R.id.password)).check(matches(hasFocus()));
    }

    @Test
    public void authFailed() throws Exception {
        onView(withId(R.id.username)).perform(typeText(TEST_USERNAME_ERROR));
        onView(withId(R.id.password)).perform(typeText(TEST_PASSWORD));
        onView(withId(R.id.sign_in_button)).perform(click());
        onView(withId(R.id.password)).check(matches(hasFocus()));
    }

    @Test
    public void authSuccess() throws Exception {
        simpleAuthenticationFragment.setAuthSuccessCallback(new SimpleAuthenticationFragment.AuthenticateSuccessCallback() {
            @Override
            public void authenticated(AuthenticateResult result) {
                if (result != null) {
                    authenticated = true;
                }
            }
        });
        onView(withId(R.id.username)).perform(typeText(TEST_USERNAME_SUCCESS));
        onView(withId(R.id.password)).perform(typeText(TEST_PASSWORD));
        onView(withId(R.id.sign_in_button)).perform(click());
        assertTrue(authenticated);
    }
}