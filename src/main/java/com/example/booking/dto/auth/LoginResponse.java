package com.example.booking.dto.auth;

public class LoginResponse {

    private String token;
    private String type = "Bearer";

    public LoginResponse() {
    }

    public LoginResponse(String token, String type) {
        this.token = token;
        this.type = type != null ? type : "Bearer";
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static LoginResponseBuilder builder() {
        return new LoginResponseBuilder();
    }

    public static class LoginResponseBuilder {
        private String token;
        private String type = "Bearer";

        LoginResponseBuilder() {
        }

        public LoginResponseBuilder token(String token) {
            this.token = token;
            return this;
        }

        public LoginResponseBuilder type(String type) {
            this.type = type;
            return this;
        }

        public LoginResponse build() {
            return new LoginResponse(token, type);
        }
    }
}
