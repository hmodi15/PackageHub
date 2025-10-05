package com.example.androidexample;


import androidx.annotation.IdRes;
import androidx.navigation.NavAction;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.matcher.ViewMatchers;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.runner.AndroidJUnitRunner;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;

import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;


@RunWith(AndroidJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SteenhoekSystemTest {


    private static final int SIMULATED_DELAY_MS = 500;

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);


    @Before
    public void setUp(){
        Intents.init();
    }

    @After
    public void tearDown(){
        Intents.release();
    }

    @Test
    public void test1SignUpButton(){
        Espresso.onView(withId(R.id.signingUpButton)).perform(ViewActions.click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Intents.intended(IntentMatchers.hasComponent(SignUpActivity.class.getName()));

        onView(withId(R.id.email)).perform(typeText("test40@gmail.com"));
        onView(withId(R.id.password)).perform(typeText("password"));
        onView(withId(R.id.repassword)).perform(typeText("password"));
        onView(withId(R.id.phone)).perform(typeText("1234567890"));
        onView(withId(R.id.name)).perform(typeText("Test User"), closeSoftKeyboard());
        onView(withId(R.id.Address)).perform(typeText("1234 Street Name"), closeSoftKeyboard());
        onView(withId(R.id.postalCode)).perform(typeText("12345"), closeSoftKeyboard());

        onView(withId(R.id.signupbtn)).perform(click());

        try {
            Thread.sleep(2000); // Adjust the delay as needed
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        intended(hasComponent(MainActivity.class.getName()));
    }

    @Test
    public void test2ChatThenLogout(){

        onView(withId(R.id.bottomNavigationView)).perform(selectBottomNavigationItem(R.id.profile));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.supportLayout)).perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Type a message into the chat input field
        onView(withId(R.id.msgEdt)).perform(typeText("hello, this is a test"), closeSoftKeyboard());

        // Click the send button
        onView(withId(R.id.sendBtn)).perform(click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Click the disconnect button
        onView(withId(R.id.backMainBtn)).perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.logoutBtn)).perform(click());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verify that the MainActivity is launched after logout
        intended(hasComponent(MainActivity.class.getName()));
    }


    @Test
    public void test3Login(){
        String email = "test40@gmail.com";
        String password = "password";

        Espresso.onView(withId(R.id.username)).perform(typeText(email));
        Espresso.onView(withId(R.id.password)).perform(typeText(password), closeSoftKeyboard());
        Espresso.onView(withId(R.id.loginButton)).perform(ViewActions.click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Intents.intended(IntentMatchers.hasComponent(HomeActivity.class.getName()));
        onView(withId(R.id.bottomNavigationView)).perform(selectBottomNavigationItem(R.id.occupents));

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String message = "Hello, this is a test message!";
        onView(withId(R.id.msgEdt)).perform(typeText(message), closeSoftKeyboard());
        onView(withId(R.id.sendBtn)).perform(click());
        onView(withId(R.id.postRecycler)).check(matches(hasDescendant(withText(message))));
        onView(withId(R.id.bottomNavView)).perform(selectBottomNavigationItem(R.id.managerNav_home));

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.bottomNavigationView)).perform(selectBottomNavigationItem(R.id.profile));

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.logoutBtn)).perform(click());
        intended(hasComponent(MainActivity.class.getName()));
    }

    @Test
    public void test4LoginAndDelete(){
        String email = "test40@gmail.com";
        String password = "password";

        // Perform login
        onView(withId(R.id.username)).perform(typeText(email));
        onView(withId(R.id.password)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.loginButton)).perform(click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Wait for the home page to appear
        onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()));

        // Navigate to user profile
        onView(withId(R.id.bottomNavigationView)).perform(selectBottomNavigationItem(R.id.profile));

        // Wait for user profile page to appear
        onView(withId(R.id.emailView)).check(matches(isDisplayed()));

        // Open settings
        onView(withId(R.id.settingsBtn)).perform(click());

        // Wait for settings page to appear
        onView(withId(R.id.deleteAccountBtn)).check(matches(isDisplayed()));

        // Click delete account
        onView(withId(R.id.deleteAccountBtn)).perform(click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Click delete in the dialog
        onView(withText("DELETE")).perform(click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        intended(hasComponent(MainActivity.class.getName()));

        onView(withId(R.id.username)).perform(typeText(email));
        onView(withId(R.id.password)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.loginButton)).perform(click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Assert that the login attempt fails and the login page is displayed
        onView(withId(R.id.bottomNavigationView)).check(doesNotExist());
        intended(hasComponent(MainActivity.class.getName()));
    }


    private static ViewAction selectBottomNavigationItem(@IdRes final int itemId) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return allOf(isAssignableFrom(BottomNavigationView.class), isDisplayed());
            }

            @Override
            public String getDescription() {
                return "select item with id " + itemId;
            }

            @Override
            public void perform(UiController uiController, View view) {
                BottomNavigationView navigationView = (BottomNavigationView) view;
                navigationView.setSelectedItemId(itemId);
            }
        };

    /*@Test
    public void reverseDefaultString(){
        String testString = "defaultstring";
        String resultString = "gnirtstluafed";

        activityScenarioRule.getScenario().onActivity(activity -> {
            activity.defaultString = testString;
            activity.aSwitch.setChecked(true);
        });

        onView(withId(R.id.submit)).perform(click());
        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        // Verify that volley returned the correct value
        onView(withId(R.id.myTextView)).check(matches(withText(endsWith(resultString))));
    }*/

        /**
         * Start the server and run this test
         *
         * This test uses the user input string value from edittext
         * instead of the default string within the activity
         *
         * the default string value is set to null by activityScenarioRule upon activity creation
         * meanwhile the switch is set to reading the user input value
         */
    /*@Test
    public void reverseInputString(){

        String testString = "inputstring";
        String resultString = "gnirtstupni";

        activityScenarioRule.getScenario().onActivity(activity -> {
            activity.defaultString = null;
            activity.aSwitch.setChecked(false);
        });

        // Type in testString and send request
        onView(withId(R.id.stringEntry)).perform(typeText(testString), closeSoftKeyboard());

        // Click button to submit
        onView(withId(R.id.submit)).perform(click());

        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        // Verify that volley returned the correct value
        onView(withId(R.id.myTextView)).check(matches(withText(endsWith(resultString))));
    }
*/
        /**
         * Start the server and run this test
         *
         * This test uses the default string value specified within the activity
         * instead of the input string from edittext
         *
         * the default string value is set by activityScenarioRule upon activity creation
         * meanwhile the switch is set to reading the default value
         */
   /* @Test
    public void capitalizeDefaultString() {

        String testString = "defaultstring";
        String resultString = "DEFAULTSTRING";

        activityScenarioRule.getScenario().onActivity(activity -> {
            activity.defaultString = testString;
            activity.aSwitch.setChecked(true);
        });

        onView(withId(R.id.submit2)).perform(click());

        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
        }

        // Verify that volley returned the correct value
        onView(withId(R.id.myTextView)).check(matches(withText(endsWith(resultString))));
    }
*/
        /**
         * Start the server and run this test
         *
         * This test uses the user input string value from edittext
         * instead of the default string within the activity
         *
         * the default string value is set to null by activityScenarioRule upon activity creation
         * meanwhile the switch is set to reading the user input value
         */
    /*@Test
    public void capitalizeInputString() {

        String testString = "inputstring";
        String resultString = "INPUTSTRING";

        activityScenarioRule.getScenario().onActivity(activity -> {
            activity.defaultString = null;
            activity.aSwitch.setChecked(false);
        });

        // Type in testString and send request
        onView(withId(R.id.stringEntry)).perform(typeText(testString), closeSoftKeyboard());

        // Click button to submit
        onView(withId(R.id.submit2)).perform(click());

        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        // Verify that volley returned the correct value
        onView(withId(R.id.myTextView)).check(matches(withText(endsWith(resultString))));
    }*/

    }

}
