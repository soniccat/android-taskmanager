package com.example.alexeyglushkov.authorization.Auth;

/**
 * Created by alexeyglushkov on 31.10.15.
 */
public interface Authorizer {
    void authorize(AuthorizerCompletion completion);
    void signCommand(ServiceCommand command, AuthCredentials credentials);

    void setServiceCommandProvider(ServiceCommandProvider provider);
    void setServiceCommandRunner(ServiceCommandRunner runner);

    interface AuthorizerCompletion {
        void onFinished(AuthCredentials credentials, AuthError error);
    }

    class AuthError extends Error {
        private static final long serialVersionUID = 6206983256074915330L;

        public enum Reason {
            InnerError,
            UnknownError,
            Cancelled,
            NotAuthorized // for services
        }

        protected Reason reason;

        public Reason getReason() {
            return reason;
        }

        public AuthError(Reason reason, Throwable throwable) {
            super(throwable);
            this.reason = reason;
        }

        public AuthError(String detailMessage, Reason reason, Throwable throwable) {
            super(detailMessage, throwable);
            this.reason = reason;
        }
    }
}
