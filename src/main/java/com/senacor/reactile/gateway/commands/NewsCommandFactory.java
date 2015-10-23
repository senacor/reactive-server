package com.senacor.reactile.gateway.commands;

/**
 * Factory for StartCommand
 * <p>
 * User: Andreas Keefer, Senacor Technologies AG
 * Date: 16.04.15
 * Time: 16:10
 *
 * @author Andreas Keefer (andreas.keefer@senacor.com), Senacor Technologies AG
 */
public interface NewsCommandFactory {

    NewsCommand create(Integer number);
}
