package org.carlosacademic.mapper;

import org.carlosacademic.domain.UserDTO;
import org.carlosacademic.table.DUser;

public class UserMapper {

    public static DUser toDUser(UserDTO userDTO){
        DUser dUser = new DUser();
        dUser.setId(userDTO.id());
        dUser.setUsername(userDTO.username());
        dUser.setEmail(userDTO.email());

        return dUser;
    }

    public static UserDTO toTodoDto(DUser dUser){
        return new UserDTO(
                dUser.id(),
                dUser.name(),
                dUser.username(),
                dUser.email());
    }
}
