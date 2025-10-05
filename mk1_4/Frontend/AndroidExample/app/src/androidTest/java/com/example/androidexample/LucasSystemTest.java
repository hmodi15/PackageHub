package com.example.androidexample;

import com.example.androidexample.ManagerDashboardActivity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.StringEndsWith.endsWith;

import android.util.Log;

/**
 * This testing file uses ActivityScenarioRule instead of ActivityTestRule
 * to demonstrate system testings cases.
 * 
 * adding this line here so maybe the PR picks up that I added a commit :P
 */
@RunWith(AndroidJUnit4ClassRunner.class)
@LargeTest   // large execution time
public class LucasSystemTest {
    private static final int SIMULATED_DELAY_MS = 5000;

    @Rule
    public ActivityScenarioRule<ManagerDashboardActivity> activityScenarioRule = new ActivityScenarioRule<>(ManagerDashboardActivity.class);

    /**
     * Start the server and run this test
     *
     * This test uses a test package to add to the recycler view in the
     * manager dashboard. This is useful for checking that the package listener
     * operations are working correctly
     *
     */
    @Test
    public void testCreatePackage(){
        Package testPackage = new Package(1, "joe", "today", "123");
        activityScenarioRule.getScenario().onActivity(activity -> {
            activity.loadPackageRecyclerTesting(); //load in the package recycler
            activity.onCreatePackage(testPackage); //create a dummy package
        });

        //onView(withId(R.id.submit)).perform(click()); //shouldn't need to click anything should auto update
        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        // Verify that the recycler view reflects the new value
        //onView(withId(R.id.packageRecyclerView)).perform(RecyclerViewActions.scrollTo(hasDescendant(withText(testPackage.getSecurityCode()))));
        onView(withText(testPackage.getOccupantName())).check(matches(isDisplayed()));
        //onView(withId(R.id.testText)).check(matches(withText(endsWith(testPackage.getOccupantName()))));
    }

    /**
     * Start the server and run this test
     *
     * This test uses a test room to add to the recycler view in the
     * manager dashboard. This is useful for checking that the room listener
     * operations are working correctly
     *
     */
    @Test
    public void testCreateRoom(){
        Room testRoom = new Room(1, "1234", "4567 Street", "Big Business", 69420);
        activityScenarioRule.getScenario().onActivity(activity -> {
            activity.loadRoomRecyclerTesting(); //load in the rooms recycler
            activity.onCreateRoomTesting(testRoom);
        });

        //onView(withId(R.id.submit)).perform(click()); //shouldn't need to click anything should auto update
        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        // Verify that the recycler view reflects the new value
        //onView(withId(R.id.roomRecyclerView)).perform(RecyclerViewActions.scrollTo(hasDescendant(withText(testRoom.getApartmentNumber()))));
        onView(withText(testRoom.getApartmentNumber())).check(matches(isDisplayed()));
    }

    @Test
    public void testManualCreatePackage(){
        Package testPackage = new Package(1, "joescoolcarthatgoesreallyfastlongstringtotest", "today", "123");
        activityScenarioRule.getScenario().onActivity(activity -> {
            //activity.loadPackageRecyclerTesting(); //load in the package recycler
            //activity.onCreatePackage(testPackage); //create a dummy package
            activity.manualPackageEntryTesting(); //loads in the manual package add screen
        });

        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        //fill out the form with package info
        onView(withId(R.id.editTextOccupantName)).perform(typeText(testPackage.getOccupantName()), closeSoftKeyboard());
        onView(withId(R.id.editTextDeliveryDate)).perform(typeText(testPackage.getDeliveryDate()), closeSoftKeyboard());
        onView(withId(R.id.editTextSecurityCode)).perform(typeText(testPackage.getSecurityCode()), closeSoftKeyboard());
        onView(withId(R.id.buttonAdd)).perform(click());

        // Let the screen load
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {}

        activityScenarioRule.getScenario().onActivity(activity -> {
            activity.loadPackageRecyclerTesting(); //load in the package recycler
            //activity.onCreatePackage(testPackage); //create a dummy package
            Log.i("pkg manual test", "created successfully");
        });

        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        // Verify that the recycler view reflects the new value
        //onView(withId(R.id.packageRecyclerView)).perform(RecyclerViewActions.scrollTo(hasDescendant(withText(testPackage.getSecurityCode()))));
        onView(withText(testPackage.getOccupantName())).check(matches(isDisplayed()));
        //onView(withId(R.id.testText)).check(matches(withText(endsWith(testPackage.getOccupantName()))));
    }

    @Test
    public void testTesseractScan(){
        Package testPackage = new Package(1, "joescoolcarthatgoesreallyfastlongstringtotest", "today", "123");
        activityScenarioRule.getScenario().onActivity(activity -> {
            //activity.loadPackageRecyclerTesting(); //load in the package recycler
            //activity.onCreatePackage(testPackage); //create a dummy package
            activity.manualTesseractTesting(); //loads in the manual package add screen
        });

        //wait for the camera to load up
        try {
            Thread.sleep(5000); //it can take a LONG time for this to load
        } catch (InterruptedException e) {}
        onView(withId(R.id.capture)).perform(click());

        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(40000); //it can take a LONG time for this to load
        } catch (InterruptedException e) {}

        //fill out the form with package info (CURRENTLY SET TO AUTO-BLANK)
        onView(withId(R.id.editTextOccupantName)).perform(typeText(testPackage.getOccupantName()), closeSoftKeyboard());
        onView(withId(R.id.editTextAddress)).perform(typeText("Big Business"), closeSoftKeyboard());
        onView(withId(R.id.editTextDeliveryDate)).perform(typeText(testPackage.getDeliveryDate()), closeSoftKeyboard());
        onView(withId(R.id.editTextSecurityCode)).perform(typeText(testPackage.getSecurityCode()), closeSoftKeyboard());
        onView(withId(R.id.buttonAdd)).perform(click());

        // Let the screen load and volley handle things
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {}

        activityScenarioRule.getScenario().onActivity(activity -> {
            activity.loadPackageRecyclerTesting(); //load in the package recycler
            //activity.onCreatePackage(testPackage); //create a dummy package
            Log.i("pkg manual test", "created successfully");
        });

        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        // Verify that the recycler view reflects the new value
        //onView(withId(R.id.packageRecyclerView)).perform(RecyclerViewActions.scrollTo(hasDescendant(withText(testPackage.getSecurityCode()))));
        onView(withText(testPackage.getOccupantName())).check(matches(isDisplayed()));
        //onView(withId(R.id.testText)).check(matches(withText(endsWith(testPackage.getOccupantName()))));
    }
}
