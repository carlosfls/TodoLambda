package org.carlosacademic.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.carlosacademic.domain.TodoDTO;
import org.carlosacademic.mapper.TodoMapper;
import org.carlosacademic.repositories.TodoRepository;
import org.carlosacademic.repositories.impl.TodoRepositoryImpl;
import org.carlosacademic.table.DTodo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TodoService {

    private final TodoRepository todoRepository;
    private final ObjectMapper objectMapper;
    private static final String API_URL = System.getenv("TODO_API_URL");

    public TodoService() {
        objectMapper = new ObjectMapper();
        todoRepository = new TodoRepositoryImpl();
    }

    public String createTodo(String id){
        try (HttpClient client = HttpClient.newHttpClient()){
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL +"/todos/"+id))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200 && response.body()!=null){
                return register(response.body());
            }else{
                return response.statusCode()+"";
            }
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public String register(String todo){
        if (!todo.isEmpty()){
            try{
                TodoDTO t = objectMapper.readValue(todo, TodoDTO.class);
                DTodo dTodo = TodoMapper.toDTodo(t);
                todoRepository.save(dTodo);
                return todo;
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return "Not found";
    }
}
