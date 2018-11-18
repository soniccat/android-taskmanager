package com.example.alexeyglushkov.authorization.service;

import com.example.alexeyglushkov.authorization.Auth.Account;
import com.example.alexeyglushkov.authorization.Auth.AuthCredentials;
import com.example.alexeyglushkov.authorization.Auth.Authorizer;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandProvider;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandProxy;
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

    // for cases when auth failed and a command isn't created
    //void setAuthCompletion(ServiceCommand.CommandCallback authCompletion);

    // pass a ServiceCommandProxy to create the command after authorization
    Single<ServiceCommand> runCommand(ServiceCommandProxy proxy);
    Single<ServiceCommand> runCommand(ServiceCommandProxy proxy, boolean canSignIn);

    public void cancel(ServiceCommand cmd);
}
