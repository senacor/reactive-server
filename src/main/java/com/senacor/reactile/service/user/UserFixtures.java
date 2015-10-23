package com.senacor.reactile.service.user;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by sfuss on 23.10.15.
 */
public class UserFixtures {

    public static final List FIRST_NAMES = Arrays.asList("Adam", "Anneliese", "Berthold", "Berta", "Christopher", "Charlotte", "Dennis", "Dorothea");
    public static final List LAST_NAMES = Arrays.asList("Kugler", "Lurchig", "Monheim", "Naaber", "Peine", "Quaid", "Rastatt");

    public static final List<String> USER_IDS = Arrays.asList("momann","mmenzel","rwinzinger","aloch","adick","aangel","cstar","akeefer", "swalter", "ttran");

    private static  final Random rd = new Random();

    private static String pickRandom(List<String> source) {
        return source.get(rd.nextInt(source.size()));
    }


    public static UserId pickRandomUserId(){
        return new UserId(pickRandom(USER_IDS));
    }

    public static User createUser(UserId userId, String branchId){
        return User.aUser()
                .withId(userId)
                .withFirstName(pickRandom(FIRST_NAMES))
                .withLastName(pickRandom(LAST_NAMES))
                .withBranch(branchId).build();

    }

}
