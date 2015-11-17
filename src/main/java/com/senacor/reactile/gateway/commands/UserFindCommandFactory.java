package com.senacor.reactile.gateway.commands;

import java.util.Map;

/**
 * Created by swalter on 23.10.15.
 */
public interface UserFindCommandFactory {

    UserFindCommand create(Map<String,String> map);

}
