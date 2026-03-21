package data;

import com.recommend.server.dto.CollegeDTO;
import com.recommend.server.dto.Coordinates;
import com.recommend.server.model.College;
import com.recommend.server.repository.CollegeRepository;
import com.recommend.server.repository.CourseRepository;
import com.recommend.server.service.DataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageTest {

    @Mock
    private CollegeRepository collegeRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private DataService collegeService;

    @TempDir
    Path tempDir;

    private static final Pattern FILENAME_PATTERN =
            Pattern.compile("^/storage/[a-f0-9]{8}\\.[a-zA-Z0-9]+$");

    @BeforeEach
    void setUp() {
        String path = tempDir.toAbsolutePath().toString();
        ReflectionTestUtils.setField(collegeService, "IMG_DIR", path);
    }

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

    @Test
    @DisplayName("saveImage — should preserve .jpg extension")
    void saveImage_devePreservarExtensaoJpg() {
        String result = collegeService.saveImage(mockImage("foto.jpg"));
        assertThat(result).endsWith(".jpg");
    }

    @Test
    @DisplayName("saveImage — should preserve .png extension")
    void saveImage_devePreservarExtensaoPng() {
        String result = collegeService.saveImage(mockImage("logo.png"));
        assertThat(result).endsWith(".png");
    }

    @Test
    @DisplayName("saveImage — should preserve .webp extension")
    void saveImage_devePreservarExtensaoWebp() {
        String result = collegeService.saveImage(mockImage("banner.webp"));
        assertThat(result).endsWith(".webp");
    }

    @Test
    @DisplayName("saveImage — should generate unique names for the same file")
    void saveImage_deveGerarNomesUnicos() {
        String first  = collegeService.saveImage(mockImage("img.jpg"));
        String second = collegeService.saveImage(mockImage("img.jpg"));
        assertThat(first).isNotEqualTo(second);
    }

    @Test
    @DisplayName("saveImage — should create file on disk with generated name")
    void saveImage_deveCriarArquivoEmDisco() {
        String filename = collegeService.saveImage(mockImage("foto.jpg"));
        String cleanName = filename.replace("/storage/", "");
        assertThat(tempDir.resolve(cleanName)).exists();
    }

    @Test
    @DisplayName("saveImage — saved file content should match original")
    void saveImage_devePreservarConteudoDoArquivo() throws Exception {
        byte[] content = "real-content".getBytes();
        MockMultipartFile file = new MockMultipartFile(
                "image", "foto.jpg", MediaType.IMAGE_JPEG_VALUE, content
        );
        String filename = collegeService.saveImage(file);
        String cleanName = filename.replace("/storage/", "");
        assertThat(Files.readAllBytes(tempDir.resolve(cleanName))).isEqualTo(content);
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
    @DisplayName("insertColleges — image should follow format {8hex}.ext with prefix")
    void insertColleges_comImagem_imageDeveSegueFormato() {
        when(collegeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        List<College> result = collegeService.insertColleges(
                List.of(buildDTO(mockImage("foto.jpg")))
        );
        assertThat(result.getFirst().getImage()).matches(FILENAME_PATTERN);
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
}