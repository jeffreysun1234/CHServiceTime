/*
 * Copyright (C) 2016 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mycompany.chservicetime.data.firebase.testUtil;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import rx.Notification;
import rx.Subscriber;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;


/**
 * A test {@link Subscriber} and JUnit rule which guarantees all events are asserted.
 */
public final class RecordingSubscriber<T> extends Subscriber<T> {
    private final long initialRequest;
    private final Deque<Notification<T>> events = new ArrayDeque<>();

    private RecordingSubscriber(long initialRequest) {
        this.initialRequest = initialRequest;
    }

    @Override
    public void onStart() {
        request(initialRequest);
    }

    @Override
    public void onNext(T value) {
        events.add(Notification.createOnNext(value));
    }

    @Override
    public void onCompleted() {
        events.add(Notification.<T>createOnCompleted());
    }

    @Override
    public void onError(Throwable e) {
        events.add(Notification.<T>createOnError(e));
    }

    private Notification<T> takeNotification() {
        Notification<T> notification = events.pollFirst();
        if (notification == null) {
            throw new AssertionError("No event found!");
        }
        return notification;
    }

    public T takeValue() {
        Notification<T> notification = takeNotification();
        assertThat(String.format("Expected onNext event but was %s", notification),
                notification.isOnNext(), is(equalTo(true)));
        return notification.getValue();
    }

    public Throwable takeError() {
        Notification<T> notification = takeNotification();
        assertThat(String.format("Expected onError event but was %s", notification),
                notification.isOnError(), is(equalTo(true)));
        return notification.getThrowable();
    }

    public RecordingSubscriber<T> assertAnyValue() {
        takeValue();
        return this;
    }

    public RecordingSubscriber<T> assertValue(T value) {
        assertThat(takeValue(), is(equalTo(value)));
        return this;
    }

    public void assertCompleted() {
        Notification<T> notification = takeNotification();
        assertThat(String.format("Expected onCompleted event but was %s", notification),
                notification.isOnCompleted(), is(equalTo(true)));
        assertNoEvents();
    }

    public void assertError(Throwable throwable) {
        assertThat(takeError(), is(equalTo(throwable)));
    }

    public void assertError(Class<? extends Throwable> errorClass) {
        assertError(errorClass, null);
    }

    public void assertError(Class<? extends Throwable> errorClass, String message) {
        Throwable throwable = takeError();
        assertThat(throwable, is(instanceOf(errorClass)));
        if (message != null) {
            assertThat(throwable.getMessage(), equalToIgnoringCase(message));
        }
        assertNoEvents();
    }

    public void assertNoEvents() {
        assertThat("Unconsumed events found!", events, empty());
    }

    public void requestMore(long amount) {
        request(amount);
    }

    public static final class Rule implements TestRule {
        final List<RecordingSubscriber<?>> subscribers = new ArrayList<>();

        public <T> RecordingSubscriber<T> create() {
            return createWithInitialRequest(Long.MAX_VALUE);
        }

        public <T> RecordingSubscriber<T> createWithInitialRequest(long initialRequest) {
            RecordingSubscriber<T> subscriber = new RecordingSubscriber<>(initialRequest);
            subscribers.add(subscriber);
            return subscriber;
        }

        @Override
        public Statement apply(final Statement base, Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    base.evaluate();
                    for (RecordingSubscriber<?> subscriber : subscribers) {
                        subscriber.assertNoEvents();
                    }
                }
            };
        }
    }
}