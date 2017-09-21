package com.feedhenry.securenativeandroidtemplate;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;

import com.feedhenry.securenativeandroidtemplate.di.SecureTestApplication;
import com.feedhenry.securenativeandroidtemplate.domain.callbacks.Callback;
import com.feedhenry.securenativeandroidtemplate.domain.models.Note;
import com.feedhenry.securenativeandroidtemplate.domain.repositories.NoteRepository;
import com.feedhenry.securenativeandroidtemplate.domain.store.NoteDataStore;
import com.feedhenry.securenativeandroidtemplate.domain.store.NoteDataStoreFactory;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;

/**
 * Created by weili on 14/09/2017.
 */

public class StorageFeatureTest {

    @Rule
    public ActivityTestRule activityRule = new ActivityTestRule(MainActivity.class);

    @Inject
    NoteRepository noteRepository;

    private static final String TEST_TITLE = "testTitle";
    private static final String TEST_CONTENT = "testContent";

    private static final String TEST_TITLE_UPDATED = "testTitleUpdated";

    @Before
    public void setUp() {
        SecureTestApplication application = (SecureTestApplication) InstrumentationRegistry.getTargetContext().getApplicationContext();
        application.getComponent().inject(this);
    }

    @Test
    public void testNotesOperations() {
        //go to the notes list view
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withText(R.string.fragment_title_storage)).perform(click());
        //the list view should be appear
        onView(withId(R.id.notes_list_view)).check(matches(isDisplayed()));
        //click on the add button
        onView(withId(R.id.add_note_btn)).perform(click());
        //details view should be appear
        onView(withId(R.id.note_details_view)).check(matches(isDisplayed()));
        //enter the title & content
        onView(withId(R.id.note_title_field)).perform(typeText(TEST_TITLE), closeSoftKeyboard());
        onView(withId(R.id.note_content_field)).perform(typeText(TEST_CONTENT), closeSoftKeyboard());
        //save
        onView(withId(R.id.save_note_btn)).perform(click());
        //the list view should show again, and it should have the new note
        onView(withId(R.id.notes_list_view)).check(matches(isDisplayed()));
        onView(withId(R.id.notes_list_view)).check(matches(hasDescendant(withText(TEST_TITLE))));
        //should be able to click on it
        onView(withId(R.id.notes_list_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        //details view should be loaded
        onView(withId(R.id.note_details_view)).check(matches(isDisplayed()));
        onView(withId(R.id.note_title_field)).check(matches(withText(TEST_TITLE)));
        onView(withId(R.id.note_content_field)).check(matches(withText(TEST_CONTENT)));
        //update the title and save
        onView(withId(R.id.note_title_field)).perform(clearText(), typeText(TEST_TITLE_UPDATED), closeSoftKeyboard());
        onView(withId(R.id.save_note_btn)).perform(click());
        //title should be updated on the list view
        onView(withId(R.id.notes_list_view)).check(matches(hasDescendant(withText(TEST_TITLE_UPDATED))));
    }
}
