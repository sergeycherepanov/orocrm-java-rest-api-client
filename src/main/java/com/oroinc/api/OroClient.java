package com.oroinc.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;


public interface OroClient {

    static class OroUser {
        public int id;
        public String title;
        public String username;
        public String email;
        public String phone;
        public String firstName;
        public String middleName;
        public String lastName;
        public String nameSuffix;
        public String birthday;
        public String organization;
        public String avatar;
        public String enabled;
        public String lastLogin;
        public String createdAt;
        public String updatedAt;
        public String loginCount;
        public String passwordChangedAt;

    }

    @GET("api/rest/{version}/users")
    Call<List<OroUser>> getUsers(@Path("version") String version);
}
