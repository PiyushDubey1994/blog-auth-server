package com.blogsite.auth.service.registration.impl;


import com.blogsite.auth.entity.BlogUser;
import com.blogsite.auth.exception.UserNotFoundException;
import com.blogsite.auth.repository.BlogUserRepository;
import com.blogsite.auth.service.registration.BlogUserRegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BlogUserRegistrationServiceImpl implements BlogUserRegistrationService {

    private static final Logger logger = LoggerFactory.getLogger(BlogUserRegistrationServiceImpl.class);

    @Autowired
    protected BlogUserRepository blogUserRepository;


    @Override
    public BlogUser findById(Long id) {
        logger.info("ID -{}",id);
        return blogUserRepository.findById(id).get();
    }

    @Override
    public BlogUser createUser(BlogUser user) {
        return blogUserRepository.save(user);
    }

    @Override
    public Boolean existsByUsername(String username) {
        return blogUserRepository.findByUsername(username).isPresent();
    }

    @Override
    public Boolean existsByEmail(String email) {
        return blogUserRepository.findByEmail(email).isPresent();
    }

    @Override
    public List<BlogUser> findAll() {
        return blogUserRepository.findAll();
    }

    @Override
    public Boolean deleteBlogUser(Long id) {
        if (blogUserRepository.existsById(id)) {
            blogUserRepository.deleteById(id);
        } else {
            throw new UserNotFoundException("Blog not found with id " + id);
        }
        //return postRepository.existsById(id);
        return true;
    }


}
