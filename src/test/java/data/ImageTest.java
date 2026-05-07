package data;

import com.recommend.server.dto.CollegeDTO;
import com.recommend.server.dto.Coordinates;
import com.recommend.server.model.College;
import com.recommend.server.repository.CollegeRepository;
import com.recommend.server.repository.CourseRepository;
import com.recommend.server.service.DataService;
import com.recommend.server.service.ImageStorageService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageTest {

    @Mock
    private CollegeRepository collegeRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private ImageStorageService imageStorageService;

    @InjectMocks
    private DataService collegeService;

    private static final Pattern OBJECT_ID_PATTERN =
            Pattern.compile("^[a-f0-9]{24}$");

    private MockMultipartFile mockImage(String originalName) {
        return new MockMultipartFile(
                "image", originalName,
                MediaType.IMAGE_JPEG_VALUE,
                "fake-image-bytes".getBytes()
        );
    }

    private CollegeDTO buildDTO(MockMultipartFile image) {
        return new CollegeDTO(
                "Unicamp", "Universidade Estadual",
                new Coordinates(-22.9, -47.1),
                List.of(), image
        );
    }

    @BeforeEach
    void setUp() {
        lenient().when(imageStorageService.saveImage(any())).thenAnswer(invocation -> {
            return new ObjectId().toHexString();
        });
    }

    @Test
    @DisplayName("saveImage — should return valid ObjectId hex")
    void saveImage_deveRetornarObjectIdHex() {
        String result = collegeService.saveImage(mockImage("foto.jpg"));
        assertThat(result).matches(OBJECT_ID_PATTERN);
    }

    @Test
    @DisplayName("saveImage — should generate unique ObjectIds for same file")
    void saveImage_deveGerarIdsUnicos() {
        String first  = collegeService.saveImage(mockImage("img.jpg"));
        String second = collegeService.saveImage(mockImage("img.jpg"));
        assertThat(first).isNotEqualTo(second);
    }

    @Test
    @DisplayName("insertColleges — should not set image when image is null")
    void insertColleges_imagemNula_naoDeveSetarImage() {
        CollegeDTO dto = new CollegeDTO(
                "Fatec", "desc", new Coordinates(-23.5, -46.6), List.of(), null
        );
        when(collegeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        List<College> result = collegeService.insertColleges(List.of(dto));
        assertThat(result.getFirst().getImage()).isNull();
    }

    @Test
    @DisplayName("insertColleges — should not set image when image is empty")
    void insertColleges_imagemVazia_naoDeveSetarImage() {
        MockMultipartFile empty = new MockMultipartFile(
                "image", "", MediaType.IMAGE_JPEG_VALUE, new byte[0]
        );
        when(collegeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        List<College> result = collegeService.insertColleges(List.of(buildDTO(empty)));
        assertThat(result.getFirst().getImage()).isNull();
    }

    @Test
    @DisplayName("insertColleges — image should be a valid ObjectId hex")
    void insertColleges_comImagem_imageDeveSerObjectId() {
        when(collegeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        List<College> result = collegeService.insertColleges(
                List.of(buildDTO(mockImage("foto.jpg")))
        );
        assertThat(result.getFirst().getImage()).matches(OBJECT_ID_PATTERN);
    }

    @Test
    @DisplayName("insertColleges — should map name, description and locale correctly")
    void insertColleges_deveMappearCamposBasicos() {
        Coordinates locale = new Coordinates(-22.9, -47.1);
        CollegeDTO dto = new CollegeDTO("Unicamp", "State University", locale, List.of(), null);
        when(collegeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        College result = collegeService.insertColleges(List.of(dto)).getFirst();

        assertThat(result.getName()).isEqualTo("Unicamp");
        assertThat(result.getLocale()).isEqualTo(locale);
    }

    @Test
    @DisplayName("insertColleges — each college should be persisted exactly once")
    void insertColleges_deveChamarSaveUmaVezPorCollege() {
        CollegeDTO dto1 = new CollegeDTO("A", "d", new Coordinates(0.0, 0.0), List.of(), null);
        CollegeDTO dto2 = new CollegeDTO("B", "d", new Coordinates(0.0, 0.0), List.of(), null);
        when(collegeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        collegeService.insertColleges(List.of(dto1, dto2));

        verify(collegeRepository, times(2)).save(any(College.class));
    }

    @Test
    @DisplayName("insertColleges — should return list in the original order")
    void insertColleges_deveRetornarTodosNaOrdem() {
        CollegeDTO dto1 = new CollegeDTO("A", "d", new Coordinates(0.0, 0.0), List.of(), null);
        CollegeDTO dto2 = new CollegeDTO("B", "d", new Coordinates(0.0, 0.0), List.of(), null);
        when(collegeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        List<College> result = collegeService.insertColleges(List.of(dto1, dto2));
        assertThat(result).extracting(College::getName).containsExactly("A", "B");
    }

    @Test
    @DisplayName("saveImage — should delegate to ImageStorageService")
    void saveImage_deveDelegarParaImageStorageService() {
        MockMultipartFile file = mockImage("foto.jpg");
        collegeService.saveImage(file);
        verify(imageStorageService, times(1)).saveImage(file);
    }
}
