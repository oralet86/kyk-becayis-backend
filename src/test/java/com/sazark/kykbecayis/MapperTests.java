package com.sazark.kykbecayis;

import com.sazark.kykbecayis.config.TestSecurityConfig;
import com.sazark.kykbecayis.core.enums.GenderType;
import com.sazark.kykbecayis.core.mapper.BlockMapper;
import com.sazark.kykbecayis.core.mapper.DormMapper;
import com.sazark.kykbecayis.core.mapper.PostingMapper;
import com.sazark.kykbecayis.core.mapper.UserMapper;
import com.sazark.kykbecayis.housing.block.Block;
import com.sazark.kykbecayis.housing.block.BlockRepository;
import com.sazark.kykbecayis.housing.dorm.Dorm;
import com.sazark.kykbecayis.housing.dorm.DormRepository;
import com.sazark.kykbecayis.housing.dto.BlockDto;
import com.sazark.kykbecayis.housing.dto.DormDto;
import com.sazark.kykbecayis.posting.Posting;
import com.sazark.kykbecayis.posting.dto.PostingCreateRequest;
import com.sazark.kykbecayis.posting.dto.PostingDto;
import com.sazark.kykbecayis.user.User;
import com.sazark.kykbecayis.user.UserRepository;
import com.sazark.kykbecayis.user.dto.UserCreateRequest;
import com.sazark.kykbecayis.user.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class MapperTests {

    private BlockRepository blockRepo;
    private DormRepository dormRepo;
    private UserRepository userRepo;

    private BlockMapper blockMapper;
    private DormMapper dormMapper;
    private PostingMapper postingMapper;
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        blockRepo = mock(BlockRepository.class);
        dormRepo = mock(DormRepository.class);
        userRepo = mock(UserRepository.class);

        blockMapper = new BlockMapper(dormRepo);
        dormMapper = new DormMapper(blockRepo);
        postingMapper = new PostingMapper(dormRepo, userRepo);
        userMapper = new UserMapper(dormRepo);
    }

    /* Block Mapper */

    @Test
    void blockMapper_null() {
        assertThat(blockMapper.toDTO(null)).isNull();
        assertThat(blockMapper.toEntity(null)).isNull();
    }

    @Test
    void blockMapper_roundTrip() {
        Dorm dorm = new Dorm();
        dorm.setId(7L);
        when(dormRepo.findById(7L)).thenReturn(Optional.of(dorm));

        BlockDto dto = BlockDto.builder()
                .id(3L).type(GenderType.FEMALE)
                .fullAddress("FA")
                .city("C").location("L")
                .name("N").dormId(7L)
                .build();

        // toEntity
        Block entity = blockMapper.toEntity(dto);
        assertThat(entity.getId()).isEqualTo(3L);
        assertThat(entity.getDorm()).isSameAs(dorm);

        // toDTO
        entity.setDorm(dorm);
        entity.setType(GenderType.MALE);
        entity.setFullAddress("FA2");
        entity.setCity("C2");
        entity.setLocation("L2");
        entity.setName("N2");

        BlockDto dto2 = blockMapper.toDTO(entity);
        assertThat(dto2).isEqualTo(
                BlockDto.builder()
                        .id(3L)
                        .type(GenderType.MALE)
                        .fullAddress("FA2")
                        .city("C2")
                        .location("L2")
                        .name("N2")
                        .dormId(7L)
                        .build()
        );
    }

    /* Dorm Mapper */

    @Test
    void dormMapper_null() {
        assertThat(dormMapper.toDTO(null)).isNull();
        assertThat(dormMapper.toEntity(null)).isNull();
    }

    @Test
    void dormMapper_roundTrip() {
        Block b1 = new Block();
        b1.setId(11L);
        Block b2 = new Block();
        b2.setId(12L);
        when(blockRepo.findAllById(List.of(11L, 12L))).thenReturn(List.of(b1, b2));

        DormDto dto = DormDto.builder()
                .id(5L).type(GenderType.MALE)
                .fullAddress("DA").city("DC")
                .name("DN").phoneNumber("555")
                .location("DL").blockIds(List.of(11L, 12L))
                .build();

        Dorm entity = dormMapper.toEntity(dto);
        assertThat(entity.getId()).isEqualTo(5L);
        assertThat(entity.getBlocks()).containsExactly(b1, b2);

        // toDTO
        entity.setBlocks(List.of(b1, b2));
        DormDto dto2 = dormMapper.toDTO(entity);
        assertThat(dto2).isEqualTo(
                DormDto.builder()
                        .id(5L)
                        .type(GenderType.MALE)
                        .fullAddress("DA")
                        .city("DC")
                        .name("DN")
                        .phoneNumber("555")
                        .location("DL")
                        .blockIds(List.of(11L, 12L))
                        .build()
        );
    }

    /* Posting Mapper */

    @Test
    void postingMapper_null() {
        assertThat(postingMapper.toDTO(null)).isNull();
        assertThat(postingMapper.toEntity((PostingDto) null)).isNull();
        assertThat(postingMapper.toEntity((PostingCreateRequest) null)).isNull();
    }

    @Test
    void postingMapper_toDTO_and_toEntity_dto() {
        User user = new User();
        user.setId(2L);
        Dorm sd = new Dorm();
        sd.setId(3L);
        Dorm td = new Dorm();
        td.setId(4L);
        Posting p = Posting.builder()
                .id(9L)
                .isValid(false)
                .date(LocalDate.of(2025, 7, 15))
                .user(user)
                .sourceDorm(sd)
                .targetDorms(List.of(td))
                .build();

        PostingDto dto = postingMapper.toDTO(p);
        assertThat(dto).isEqualTo(
                PostingDto.builder()
                        .id(9L)
                        .isValid(false)
                        .date("2025-07-15")
                        .userId(2L)
                        .sourceDormId(3L)
                        .targetDormIds(List.of(4L))
                        .build()
        );

        // toEntity(dto)
        when(userRepo.findById(2L)).thenReturn(Optional.of(user));
        when(dormRepo.findById(3L)).thenReturn(Optional.of(sd));
        when(dormRepo.findAllById(List.of(4L))).thenReturn(List.of(td));

        Posting e2 = postingMapper.toEntity(dto);
        assertThat(e2.getId()).isEqualTo(9L);
        assertThat(e2.getUser()).isSameAs(user);
        assertThat(e2.getTargetDorms()).containsExactly(td);
    }

    @Test
    void postingMapper_toEntity_request() {
        User user = new User();
        user.setId(7L);
        Dorm sd = new Dorm();
        sd.setId(8L);
        Dorm td = new Dorm();
        td.setId(9L);

        when(userRepo.findById(7L)).thenReturn(Optional.of(user));
        when(dormRepo.findById(8L)).thenReturn(Optional.of(sd));
        when(dormRepo.findAllById(List.of(9L))).thenReturn(List.of(td));

        PostingCreateRequest req = PostingCreateRequest.builder()
                .userId(7L)
                .sourceDormId(8L)
                .targetDormIds(List.of(9L))
                .build();

        Posting e = postingMapper.toEntity(req);
        assertThat(e.getUser()).isSameAs(user);
        assertThat(e.getSourceDorm()).isSameAs(sd);
        assertThat(e.getTargetDorms()).containsExactly(td);
    }

    /* User Mapper */

    @Test
    void userMapper_null() {
        assertThat(userMapper.toDTO(null)).isNull();
        assertThat(userMapper.toEntity((UserDto) null)).isNull();
        assertThat(userMapper.toEntity((UserCreateRequest) null)).isNull();
    }

    @Test
    void userMapper_toEntity_and_toDTO() {
        Dorm d = new Dorm();
        d.setId(33L);
        when(dormRepo.findById(33L)).thenReturn(Optional.of(d));

        UserCreateRequest creq = UserCreateRequest.builder()
                .firstname("f").surname("s")
                .email("e").phone("p")
                .city("c").gender(null)
                .currentDormId(33L)
                .build();

        User u1 = userMapper.toEntity(creq);
        assertThat(u1.getFirstname()).isEqualTo("f");
        assertThat(u1.getCurrentDorm()).isSameAs(d);

        UserDto dto = UserDto.builder()
                .id(55L)
                .firstname("F").surname("S")
                .email("X").phone("P")
                .city("C").gender(null)
                .roles(Set.of())
                .currentDormId(33L)
                .postingIds(List.of(99L))
                .build();

        when(dormRepo.findById(33L)).thenReturn(Optional.of(d));

        User u2 = userMapper.toEntity(dto);
        assertThat(u2.getId()).isEqualTo(55L);
        assertThat(u2.getCurrentDorm()).isSameAs(d);

        // map back to DTO
        u2.setPostings(List.of(Posting.builder().id(99L).build()));
        u2.setRoles(Set.of());
        UserDto dto2 = userMapper.toDTO(u2);
        assertThat(dto2).isEqualTo(
                UserDto.builder()
                        .id(55L)
                        .firstname("F")
                        .surname("S")
                        .email("X")
                        .phone("P")
                        .city("C")
                        .gender(null)
                        .roles(Set.of())
                        .isAdmin(u2.isAdmin())
                        .currentDormId(33L)
                        .postingIds(List.of(99L))
                        .build()
        );
    }
}
