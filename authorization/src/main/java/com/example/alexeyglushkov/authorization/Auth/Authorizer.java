package com.example.alexeyglushkov.authorization.Auth;

import androidx.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by alexeyglushkov on 31.10.15.
 */
public interface Authorizer {
    Single<AuthCredentials> authorize();
    void signCommand(ServiceCommand command, AuthCredentials credentials);

    void setServiceCommandProvider(ServiceCommandProvider provider);
    void setServiceCommandRunner(ServiceCommandRunner runner);

    class AuthError extends Error {
        private static final long serialVersionUID = 6206983256074915330L;

        public enum Reason {
            InnerError,
            UnknownError,
            Cancelled,
            NotAuthorized // for services
        }

        protected @NonNull Reason reason;

        public @NonNull Reason getReason() {
            return reason;
        }

        public AuthError(@NonNull Reason reason, Throwable throwable) {
            super(throwable);
            this.reason = reason;
        }

        public AuthError(String detailMessage, @NonNull Reason reason, Throwable throwable) {
            super(detailMessage, throwable);
            this.reason = reason;
        }
    }
}
