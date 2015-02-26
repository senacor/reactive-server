package com.senacor.reactile.service;

import com.senacor.reactile.auth.User;
import com.senacor.reactile.auth.UserId;
import org.junit.Test;
import rx.Observable;

import static com.senacor.reactile.auth.User.aUser;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ActionInspectorTest {

    @Test
    public void thatActionAnnotationsAreProcessed() {
        ServiceMetadata serviceMetadata = ActionInspector.getServiceMetadata(TestServiceVerticle.class);
        assertThat(serviceMetadata.hasAction("get"), is(true));
        assertThat(serviceMetadata.hasAction("addUser"), is(true));
        assertThat(serviceMetadata.getActions(), hasSize(2));
    }


    private static class TestServiceVerticle {
        @Action("get")
        public Observable<User> getUser(UserId userId) {
            return Observable.just(user());
        }

        @Action
        private Observable<User> addUser(User user) {
            return Observable.just(user());
        }

        private Observable<User> removeUser(User user) {
            return Observable.just(user());
        }

        private User user() {
            return aUser().withId("007").withFirstName("James").withLastName("Bond").build();
        }
    }

}