package com.senacor.reactile.service.newsticker;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;


public class GuiceExperiments {

    public static class MyDao {
        public void doImportantStuff() {
            System.out.println("Important!");
        }
    }

    public interface MyService {
        public void doImportantStuff();
    }

    public static class MyServiceImpl implements MyService {
        private final MyDao myDao;

        @Inject
        public MyServiceImpl(MyDao myDao) {
            this.myDao = myDao;
        }

        public void doImportantStuff() {
            myDao.doImportantStuff();
        }
    }

    public static class MyModule extends AbstractModule {

        @Override
        protected void configure() {
            bind(MyService.class).to(MyServiceImpl.class);
        }
    }

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new MyModule());

        MyService myService = injector.getInstance(MyService.class);

        myService.doImportantStuff();
    }
}
