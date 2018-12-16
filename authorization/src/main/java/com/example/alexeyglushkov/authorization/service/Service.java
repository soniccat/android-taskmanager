package com.example.alexeyglushkov.authorization.service;

import com.example.alexeyglushkov.authorization.Auth.Account;
import com.example.alexeyglushkov.authorization.Auth.AuthCredentials;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandProvider;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandRunner;

import io.reactivex.Single;

/**
 * Created by alexeyglushkov on 26.11.15.
 */
public interface Service {
    Account getAccount();
    void setAccount(Account account);
    void setServiceCommandProvider(ServiceCommandProvider provider);
    void setServiceCommandRunner(ServiceCommandRunner runner);

    <T> Single<T> runCommand(final ServiceCommand<T> command, boolean canSignIn);
    <T> Single<T> runCommand(final ServiceCommand<T> command);

    void cancel(ServiceCommand cmd);
}
