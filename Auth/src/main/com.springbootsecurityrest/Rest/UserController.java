package Rest;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import Model.User;
import Service.UserService;
import java.util.List;


@RestController
public class UserController {

    private UserService Service;

    public UserController(UserService service) {
        this.Service = service;
    }

    @GetMapping(path = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<User> getAll() {
        return this.Service.getAll();
    }
}