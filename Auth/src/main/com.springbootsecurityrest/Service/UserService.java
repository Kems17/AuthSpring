package Service;

import Model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import Repository.UserRepository;


import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;


@Service
public class UserService implements UserDetailsService {

    private Repository.UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAll() {
        return userRepository.getAll();
    }

    public User getByLogin(String login) {
        return userRepository.getByLogin(login);
    }

    @Override
        public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
            User user= getByLogin(login);
            if (Objects.isNull(user)) {
                throw new UsernameNotFoundException(String.format("User %s is not found", login));
            }
            return new org.springframework.security.core.userdetails.User(user.getLogin(), user.getPassword(), true, true, true, true, new HashSet<>(List.of(() -> "ROLE_USER")));
        }
    }