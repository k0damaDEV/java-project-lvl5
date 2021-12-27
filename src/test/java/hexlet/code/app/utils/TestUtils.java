package hexlet.code.app.utils;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.api.DBRider;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.service.impl.JWTTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static hexlet.code.app.controller.TaskStatusController.TASK_STATUS_CONTROLLER_PATH;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@Component
@DBRider
@DataSet("users.yml")
public class TestUtils {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JWTTokenService tokenService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskStatusRepository taskStatusRepository;

    public static final String BASE_API_URL = "/api";

    public TaskStatus regDefaultTaskStatus() throws Exception {
        String taskStatusCreateJson = readFileContent("src/test/resources/fixtures/taskStatusCreateJson.json");
        final User user = userRepository.findAll().get(0);
        MockHttpServletResponse resp = perform(
                post(BASE_API_URL + TASK_STATUS_CONTROLLER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskStatusCreateJson),
                user.getEmail()
        ).andReturn().getResponse();

        return taskStatusRepository.findAll().get(0);
    }

    public ResultActions perform(final MockHttpServletRequestBuilder request) throws Exception {
        return mockMvc.perform(request);
    }

    public ResultActions perform(final MockHttpServletRequestBuilder request, final String byUser) throws Exception {
        final String token = tokenService.expiring(Map.of("username", byUser));
        request.header(HttpHeaders.AUTHORIZATION, token);

        return perform(request);
    }

    public String readFileContent(String path) throws IOException {
        Path resultPath = Paths.get(path).toAbsolutePath().normalize();
        return Files.readString(resultPath);
    }
}
