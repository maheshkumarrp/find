package com.autonomy.abc.matchers;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.hamcrest.Matchers.*;

public class CommonMatchers {
    private CommonMatchers() {}

    public static <T> Matcher<Iterable<T>> containsItems(List<? extends T> list) {
        @SuppressWarnings("unchecked")
        T[] empties = (T[]) new Object[list.size()];
        return hasItems(list.toArray(empties));
    }

    public static <T> Matcher<Iterable<? super T>> containsItems(final List<? extends T> list, final Comparator<? super T> comparator) {
        return new TypeSafeMatcher<Iterable<? super T>>() {
            @Override
            protected boolean matchesSafely(Iterable<? super T> item) {
                for (T element : list) {
                    if (!containsItem(element, comparator).matches(item)) {
                        return false;
                    }
                }
                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a collection containing all of ").appendValueList("{", ", ", "}", list);
            }
        };
    }

    public static <T> Matcher<Iterable<? super T>> containsItem(final T value, final Comparator<? super T> comparator) {
        return hasItem(comparesEqualTo(value, comparator));
    }

    public static <T> Matcher<T> comparesEqualTo(final T value, final Comparator<? super T> comparator) {
        return new TypeSafeMatcher<T>() {
            @Override
            protected boolean matchesSafely(T item) {
                return comparator.compare(item, value) == 0;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("an item comparing equal to ").appendValue(value);
            }
        };
    }
}
