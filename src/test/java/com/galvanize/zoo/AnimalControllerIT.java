package com.galvanize.zoo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AnimalControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AnimalRepository animalRepository;

    @BeforeEach
    void setUp() {
        animalRepository.deleteAll();
    }

    @Test
    void create_fetchAll() throws Exception {
        AnimalDto input = new AnimalDto("monkey", Type.WALKING, null);
        mockMvc.perform(
            post("/animals")
                .content(objectMapper.writeValueAsString(input))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isCreated());

        mockMvc.perform(get("/animals"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("length()").value(1))
            .andExpect(jsonPath("[0].name").value("monkey"))
            .andExpect(jsonPath("[0].mood").value(Mood.UNHAPPY.name()))
            .andExpect(jsonPath("[0].type").value(Type.WALKING.name()));
    }

    @Test
    void feed() throws Exception {
        AnimalDto monkey = new AnimalDto("monkey", Type.WALKING, null);
        mockMvc.perform(
            post("/animals")
                .content(objectMapper.writeValueAsString(monkey))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isCreated());

        mockMvc.perform(
            post("/animals/monkey/feed")
        )
            .andExpect(status().isOk());

        mockMvc.perform(get("/animals"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("[0].name").value("monkey"))
            .andExpect(jsonPath("[0].mood").value(Mood.HAPPY.name()));
    }
}
