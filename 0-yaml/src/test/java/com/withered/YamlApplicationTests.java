package com.withered;

import com.withered.pojo.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class YamlApplicationTests {
    @Autowired
    private Person person;
    @Test
    public void testPerson() {
        System.out.println("-------------------");
        System.out.println(person.getName());
        System.out.println(person.getAge());
        System.out.println(person.getBirth());
        System.out.println(person.getMaps());
        System.out.println(person.getLists());
        System.out.println(person.getDog().getName());
        System.out.println(person.getDog().getAge());
    }

}
