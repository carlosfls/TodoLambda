package org.carlosacademic.repositories;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import org.carlosacademic.table.DTodo;

public interface TodoRepository {

    void save(DTodo dTodo);

    void saveIfNotExist(DTodo dTodo, LambdaLogger logger);
}
