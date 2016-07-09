package com.example.alexeyglushkov.authorization.service;

import com.example.alexeyglushkov.authorization.Auth.Account;
import com.example.alexeyglushkov.authorization.Auth.AuthCredentials;
import com.example.alexeyglushkov.authorization.Auth.Authorizer;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandProvider;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandProxy;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandRunner;

/**
 * Created by alexeyglushkov on 26.11.15.
 */
public interface Service {
    Account getAccount();
    void setAccount(Account account);
    void setServiceCommandProvider(ServiceCommandProvider provider);
    void setServiceCommandRunner(ServiceCommandRunner runner);

    void setAuthCompletion(AuthCompletion authCompletion);

    // pass a ServiceCommandProxy to create the command after authorization
    void runCommand(ServiceCommandProxy proxy);
    void runCommand(ServiceCommandProxy proxy, boolean canSignIn);
    void runCommand(ServiceCommandProxy proxy, boolean canSignIn, AuthCompletion authCompletion);

    interface AuthCompletion {
        void onFinished(ServiceCommand command, AuthError error);
    }

    class AuthError extends Error {
        private static final long serialVersionUID = 6206983256074915330L;

        public enum Reason {
            InnerError,
            Cancelled,
            NotAuthorized
        }

        protected Reason reason;

        protected void setReason(Reason arReason) {
            reason = arReason;
        }

        public AuthError(Reason reason, Throwable throwable) {
            super(throwable);
            this.reason = reason;
        }
    }
}
