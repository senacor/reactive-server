package com.senacor.reactile.service.newsticker;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;

import rx.Observable;

/**
 * @author Andreas Keefer
 */
public class NewsTickerStream {

    public static final int INTERVAL_WAIT_TIME = 100;
    private static final List<String> TITLES = Arrays.asList("DGAP-News: Global PVQ",
            "Gabriel und Seehofer stellen Bedingungen an Griechenland",
            "DGAP-Adhoc: Energiedienst Holding AG: Halbjahresergebnis unter dem Vorjahr",
            "DGAP-News: Der Insolvenzverwalter der Global PVQ SE (ehemals Q-Cells SE), Henning Schorisch, teilt mit:",
            "DGAP-Stimmrechtsanteile: Linde AG",
            "DGAP-Stimmrechte: Linde AG",
            "WOCHENVORSCHAU: Termine bis 30. Juni 2015",
            "TAGESVORSCHAU: Termine am 24. Juni 2015",
            "Appointment of François-Xavier Roger as Nestlé Chief Financial Officer",
            "Retirement of José Lopez - Appointment of Magdi Batato",
            "Air Liquide Starts up Its Largest Industrial Investment Ever in Yanbu, Saudi Arabia",
            "Verdi weitet Streiks bei Deutscher Post aus",
            "EANS-Kapitalmarktinformation: Raiffeisenlandesbank Oberösterreich AG",
            "dpa-AFX Überblick: KONJUNKTUR vom 23.06.2015 - 17.00 Uhr",
            "Brüssel hofft auf Einigung im griechischen Schuldendrama",
            "Mietpreisbremse von Juli an auch in Hamburg und Nordrhein-Westfalen");
    private static final Random RANDOM = new Random();

    public Observable<News> getNewsObservable() {
        return Observable.interval(INTERVAL_WAIT_TIME, TimeUnit.MILLISECONDS)
                .map(count -> News.newBuilder()
                        .withTitle(TITLES.get(RANDOM.nextInt(TITLES.size())))
                        .withNews(RandomStringUtils.randomAlphabetic(RANDOM.nextInt(1000)))
                        .build());
    }
}
