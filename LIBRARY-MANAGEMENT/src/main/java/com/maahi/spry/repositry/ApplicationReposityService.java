package com.maahi.spry.repositry;

import com.maahi.spry.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class ApplicationReposityService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void saveUser(User request) {

        mongoTemplate.insert(request);
    }
}
