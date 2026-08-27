package com.example.booking.dto.auth;

import com.example.booking.entity.Role;

public class UserSummaryDto {

    private Long id;
    private String username;
    private Role role;

    public UserSummaryDto() {
    }

    public UserSummaryDto(Long id, String username, Role role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public static UserSummaryDtoBuilder builder() {
        return new UserSummaryDtoBuilder();
    }

    public static class UserSummaryDtoBuilder {
        private Long id;
        private String username;
        private Role role;

        UserSummaryDtoBuilder() {
        }

        public UserSummaryDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserSummaryDtoBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UserSummaryDtoBuilder role(Role role) {
            this.role = role;
            return this;
        }

        public UserSummaryDto build() {
            return new UserSummaryDto(id, username, role);
        }
    }
}
