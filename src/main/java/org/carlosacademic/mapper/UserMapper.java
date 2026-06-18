package org.carlosacademic.mapper;

import org.carlosacademic.domain.UserDTO;
import org.carlosacademic.table.DUser;

public class UserMapper {

    public static DUser toDUser(UserDTO userDTO){
        DUser dUser = new DUser();
        dUser.setId(userDTO.id());
        dUser.setName(userDTO.name());
        dUser.setUsername(userDTO.username());
        dUser.setEmail(userDTO.email());

        return dUser;
    }

    public static UserDTO toTodoDto(DUser dUser){
        return new UserDTO(
                dUser.getId(),
                dUser.getName(),
                dUser.getUsername(),
                dUser.getEmail());
    }
}
