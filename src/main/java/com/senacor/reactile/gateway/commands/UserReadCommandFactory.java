package com.senacor.reactile.gateway.commands;

import com.senacor.reactile.service.user.UserId;

/**
 * Created by sfuss on 22.10.15.
 */
public interface UserReadCommandFactory {

    UserReadCommand create(UserId userId);

}
