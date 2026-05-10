package data;

import com.recommend.server.config.MongoConfig;
import com.recommend.server.service.DataService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest(classes = com.recommend.server.Main.class)
public class SaveImageTest {

    @Autowired
    private DataService dataService;

    @Test
    @Transactional
    public void testSaveImage() {
        MultipartFile image = null;

    }
}
