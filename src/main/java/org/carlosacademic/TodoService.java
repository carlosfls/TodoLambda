package org.carlosacademic;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TodoService {

    public String registerTodo(String id){
        try (HttpClient client = HttpClient.newHttpClient()){
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://jsonplaceholder.typicode.com/todos/"+id))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200){
                return response.body();
            }else{
                return response.statusCode()+"";
            }
        }catch (Exception e){
            System.out.println("Error: "+ e.getMessage());
        }

        return "Error";
    }
}
