package com.example.medcom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestService {
    @Autowired
    private TestRepository testRepository;

    public TestEntity saveTestEntity(TestEntity testEntity) {
        return testRepository.save(testEntity);
    }
}