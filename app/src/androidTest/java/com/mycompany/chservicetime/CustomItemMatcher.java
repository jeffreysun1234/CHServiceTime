package com.mycompany.chservicetime;

import android.support.test.espresso.ViewInteraction;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Checks.checkArgument;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * Created by szhx on 4/3/2016.
 */
public class CustomItemMatcher {

    public static enum DATA_VIEW_TYPE {LISTVIEW, RECYCLERVIEW}

    ;

    /**
     * Matches a Item with a specific ID
     */
//    public static Matcher<Object> withBookId(final int bookId) {
//        return new BoundedMatcher<Object, Book>(Book.class) {
//            @Override
//            protected boolean matchesSafely(Book book) {
//                return bookId == book.getId();
//            }
//
//            @Override
//            public void describeTo(Description description) {
//                description.appendText("with id: " + bookId);
//            }
//        };
//    }

    /**
     * A custom {@link Matcher} which matches an item in a {@link ListView} or {@link RecyclerView} by its text.
     * <p/>
     * View constraints:
     * <ul>
     * <li>View must be a child of a {@link ListView} or {@link RecyclerView}
     * <ul>
     *
     * @param itemText the text to match
     * @return Matcher that matches text in the given view
     */
    public static Matcher<View> withItemText(final String itemText, DATA_VIEW_TYPE dataViewType) {
        checkArgument(!TextUtils.isEmpty(itemText), "itemText cannot be null or empty");
        return new TypeSafeMatcher<View>() {
            @Override
            public boolean matchesSafely(View item) {
                if (dataViewType.equals(DATA_VIEW_TYPE.RECYCLERVIEW)) {
                    return allOf(
                            isDescendantOfA(isAssignableFrom(RecyclerView.class)), withText(itemText))
                            .matches(item);
                } else if (dataViewType.equals(DATA_VIEW_TYPE.LISTVIEW)) {
                    return allOf(isDescendantOfA(isAssignableFrom(ListView.class)),
                            withText(itemText)).matches(item);
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is isDescendantOfA Data View with text " + itemText);
            }
        };
    }

    public static ViewInteraction matchToolbarTitle(CharSequence title) {
        return onView(allOf(isAssignableFrom(TextView.class), withParent(isAssignableFrom(Toolbar.class))))
                .check(matches(withText(title.toString())));
    }
}
