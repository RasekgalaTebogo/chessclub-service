package za.co.chessclub.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import za.co.chessclub.model.Member;
import za.co.chessclub.model.MemberCreateDTO;
import za.co.chessclub.model.MemberDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    MemberMapper INSTANCE = Mappers.getMapper(MemberMapper.class);

    // DTO to Entity
    Member toEntity(MemberCreateDTO dto);

    // Entity to DTO
    MemberDTO toDTO(Member entity);

    // Entities to DTOs
    List<MemberDTO> toDTOs(List<Member> entity);
}
