package com.oroinc.api;

import retrofit2.Response;
import retrofit2.Call;

import java.io.IOException;
import java.util.List;

public class Api {
    public static void main(String[] args) throws Exception {

        // Set api version
        String apiVersion = "latest";

        // Create a very simple REST adapter which points the OroApplication API endpoint.
        OroClient client = ServiceGenerator.createService(OroClient.class);

        // Fetch and print a list of the OroClient.OroUser to this library.
        Call<List<OroClient.OroUser>> call =
                client.getUsers(apiVersion);

        try {
            Response response = call.execute();

            System.out.println("DEBUG RESPONSE: Code: " + response.code());
            System.out.println("DEBUG RESPONSE: Message: " + response.message());

            List<OroClient.OroUser> users = (List<OroClient.OroUser>) response.body();


            if (users instanceof List) {
                System.out.println("\nTotal Users Found:" + users.size() + "\n");
                for (OroClient.OroUser user : users) {
                    System.out.println(
                            user.username + " <" + user.email + ">");
                }
            }
        } catch (IOException e) {
            // handle errors
        }
    }
}
