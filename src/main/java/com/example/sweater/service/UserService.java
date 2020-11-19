package com.example.sweater.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.sweater.domain.Role;
import com.example.sweater.domain.User;
import com.example.sweater.repos.UserRepo;

@Service
public class UserService implements UserDetailsService {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private MailSender mailSender;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Value("${hostname}")
	private String hostname;

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		User user = userRepo.findByUsername(username);
		
		if (user == null) {
			throw new UsernameNotFoundException("User not found");
		}
		
		return user;

	}

	public boolean addUser(User user) {
		User userFromDb = userRepo.findByUsername(user.getUsername());

		if (userFromDb != null) { // Пользователь найден в БД и не будет добавлен
			return false;
		}

		user.setActive(false);
		user.setRoles(Collections.singleton(Role.USER));
		user.setActivation(UUID.randomUUID().toString());
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		
		userRepo.save(user);

		sendMessage(user);

		return true;// Пользователь успешно добавлен
	}

	private void sendMessage(User user) {
		if (!StringUtils.isEmpty(user.getEmail())) {
			String message = String.format(
					"Hello, %s!\n" + "Welcome to Sweater. Please visit next link: http://%s/activate/%s",
					user.getUsername(), hostname, user.getActivation());

			mailSender.send(user.getEmail(), "Activation code", message);
		}
	}

	public boolean activateUser(String code) {
		User user = userRepo.findByActivation(code);

		if (user == null) {
			return false;
		}

		user.setActivation(null);
		user.setActive(true);
		userRepo.save(user);

		return true;
	}

	public List<User> findAll() {
		// TODO Auto-generated method stub
		return userRepo.findAll();
	}

	public void saveUser(User user, String username, Map<String, String> form) {
		user.setUsername(username);

		Set<String> roles = Arrays.stream(Role.values()).map(Role::name).collect(Collectors.toSet());

		user.getRoles().clear();
		for (String key : form.keySet()) { // Добавление роли по ключу
			if (roles.contains(key)) {
				user.getRoles().add(Role.valueOf(key));
			}
		}

		userRepo.save(user);
	}

	public void updateProfile(User user, String password, String email) {
		String userEmail = user.getEmail();

		boolean isEmailChanged = (email != null && !email.equals(userEmail))
				|| (userEmail != null && !userEmail.equals(email)); // Изменился ли email

		if (isEmailChanged) {
			user.setEmail(email);

			if (!StringUtils.isEmpty(email)) {
				user.setActivation(UUID.randomUUID().toString());
			}
		}

		if (!StringUtils.isEmpty(password)) {
			user.setPassword(password);
		}
		
		userRepo.save(user);
		
		if (isEmailChanged) {
			sendMessage(user);
		}
	}
	public void subscribe(User currentUser, User user) {
        user.getSubscribers().add(currentUser);

        userRepo.save(user);
    }

    public void unsubscribe(User currentUser, User user) {
        user.getSubscribers().remove(currentUser);

        userRepo.save(user);
    }
}
