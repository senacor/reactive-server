package com.senacor.reactile.service.branch;

import rx.Observable;

public class ObservableExperiments {

    public static void main(String[] args) {
        Observable<String> serviceObservable = Observable.just("1").map(s -> slowService(s));
        //Observable<String> serviceObservable = Observable.defer(() -> Observable.just(slowService("1")));
        //Observable<String> serviceObservable = Observable.just(slowService("1"));

        System.out.println("wurst");

        serviceObservable.subscribe(System.out::println, Throwable::printStackTrace, ()-> System.out.println("Completed!"));
    }

    private static String slowService(String s) {
        try {
            Thread.sleep(2_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return s + " enhanced!!!";
    }
}
