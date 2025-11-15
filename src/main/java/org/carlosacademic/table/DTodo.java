package org.carlosacademic.table;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class DTodo {

    private int userId;
    private int id;
    private String title;
    private boolean completed;

    @DynamoDbPartitionKey
    public int getId() {
        return id;
    }

    @DynamoDbAttribute("user_id")
    public int getUserId() {
        return userId;
    }

    public void setId(int id) {
        this.id = id;
    }

    @DynamoDbAttribute("title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @DynamoDbAttribute("completed")
    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "DTodo{" +
                "userId=" + userId +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", completed=" + completed +
                '}';
    }
}
