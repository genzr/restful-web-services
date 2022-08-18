package com.in28minutes.rest.webservices.restfulwebservices.user;

import com.in28minutes.rest.webservices.restfulwebservices.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
public class UserResource {

    @Autowired
    private UserDaoService service;

    //retrieveAllUsers
    @GetMapping("/users")
    public ResponseEntity<List<User>> retrieveAllUsers() {
        List<User> allUsers = service.findAll();

        if(allUsers.size() <= 0) {
            throw new UserNotFoundException("No users found");
        }
        return ResponseEntity.ok(allUsers);
    }

    //retrieveUserById
    @GetMapping("users/{id}")
    public ResponseEntity<User> retrieveUser(@PathVariable int id) {
        User user = service.findOne(id);

        if(user == null) {
            throw new UserNotFoundException("id: " + id);
        }

        return ResponseEntity.ok(user);
    }

    @PostMapping("/users")
    public ResponseEntity<Object> createUser(@RequestBody User user) {
        User savedUser = service.save(user);

        URI newLocation = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUser.getId())
                .toUri();

        return ResponseEntity.created(newLocation).build();
    }

        @DeleteMapping("/users/{id}")
        public ResponseEntity<Object> deleteUser(@PathVariable int id) {

            User user = service.findOne(id);

            if(user == null) {
                throw new UserNotFoundException("id: " + id);
            }

            boolean deleted = service.delete(id);
            if(deleted) {
                return ResponseEntity.ok().build();
            }

            return ResponseEntity.badRequest().build();
        }

}
