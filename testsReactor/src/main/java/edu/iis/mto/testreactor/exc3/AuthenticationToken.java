package edu.iis.mto.testreactor.exc3;

import java.util.Objects;

public class AuthenticationToken {

    private final int authorizationCode;
    private final String userId;

    private AuthenticationToken(Builder builder) {
        this.authorizationCode = builder.authorizationCode;
        this.userId = Objects.requireNonNull(builder.userId, "userId == null");
    }

    public int getAuthorizationCode() {
        return authorizationCode;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(authorizationCode, userId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AuthenticationToken other = (AuthenticationToken) obj;
        return authorizationCode == other.authorizationCode && Objects.equals(userId, other.userId);
    }

    @Override
    public String toString() {
        return "Autorization [authorizationCode=" + authorizationCode + ", userId=" + userId + "]";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private int authorizationCode;
        private String userId;

        private Builder() {}

        public Builder withAuthorizationCode(int authorizationCode) {
            this.authorizationCode = authorizationCode;
            return this;
        }

        public Builder withUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public AuthenticationToken build() {
            return new AuthenticationToken(this);
        }
    }

}
