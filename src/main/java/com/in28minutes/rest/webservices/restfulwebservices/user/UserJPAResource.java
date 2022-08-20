package com.in28minutes.rest.webservices.restfulwebservices.user;

import com.in28minutes.rest.webservices.restfulwebservices.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
public class UserJPAResource {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserDaoService service;

    //retrieveAllUsers
    @GetMapping("/jpa/users")
    public ResponseEntity<List<User>> retrieveAllUsers() {
        List<User> allUsers = userRepository.findAll();

        if(allUsers.size() <= 0) {
            throw new UserNotFoundException("No users found");
        }
        return ResponseEntity.ok(allUsers);
    }

    //retrieveUserById
    @GetMapping("/jpa/users/{id}")
    public EntityModel<User> retrieveUser(@PathVariable int id) {
        Optional<User> user = userRepository.findById(id);

        if(!user.isPresent()) {
            throw new UserNotFoundException("id: " + id);
        }

        EntityModel<User> model = EntityModel.of(user.get());
        WebMvcLinkBuilder linkToUsers = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).retrieveAllUsers());
        WebMvcLinkBuilder linkToSelf = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).retrieveUser(id));
        model.add(linkToUsers.withRel("all-users"));
        model.add(linkToSelf.withSelfRel());

        return model;
    }

    @PostMapping("/jpa/users")
    public ResponseEntity<Object> createUser(@RequestBody @Valid User user) {
        User savedUser = userRepository.save(user);
        URI newLocation = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUser.getId())
                .toUri();
        return ResponseEntity.created(newLocation).build();
    }

        @DeleteMapping("/jpa/users/{id}")
        public ResponseEntity<Object> deleteUser(@PathVariable int id) {
            Optional<User> user = userRepository.findById(id);
            if(!user.isPresent()) {
                throw new UserNotFoundException("id: " + id);
            }
            userRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }

        @GetMapping("/jpa/users/{id}/posts")
        public List<Post> retrieveAllUserPosts(@PathVariable int id) {
            Optional<User> user = userRepository.findById(id);

            if(!user.isPresent()) {
                throw new UserNotFoundException("id: " + id);
            }

            List<Post> posts = user.get().getPosts();
            return posts;
        }

    @PostMapping("/jpa/users/{id}/posts")
    public ResponseEntity<Object> createPost(@PathVariable int id, @RequestBody Post post) {
        Optional<User> userOptional = userRepository.findById(id);

        if(!userOptional.isPresent()) {
            throw new UserNotFoundException("id: " + id);
        }

        User user = userOptional.get();

        post.setUser(user);

        postRepository.save(post);

        URI newLocation = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(post.getId())
                .toUri();
        return ResponseEntity.created(newLocation).build();
    }


}
