package com.example.alexeyglushkov.authorization.service;

import com.example.alexeyglushkov.authorization.Auth.Account;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandProvider;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandRunner;

/**
 * Created by alexeyglushkov on 26.11.15.
 */
public interface Service {
    Account getAccount();
    void setAccount(Account account);
    void setServiceCommandProvider(ServiceCommandProvider provider);
    void setServiceCommandRunner(ServiceCommandRunner runner);

    void runCommand(ServiceCommand command);
    void runCommand(ServiceCommand command, boolean needSign);
}
