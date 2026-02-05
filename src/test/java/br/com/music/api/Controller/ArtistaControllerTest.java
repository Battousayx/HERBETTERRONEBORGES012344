package br.com.music.api.Controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import br.com.music.api.Controller.dto.ArtistaDto;
import br.com.music.api.Services.ArtistaService;

@WebMvcTest(ArtistaController.class)
class ArtistaControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ArtistaService service;

    @Autowired
    ObjectMapper objectMapper;

    // @Test
    // @WithMockUser
    // void testListReturnsOk() throws Exception {
    //     when(service.list()).thenReturn(List.of(new ArtistaDto(1L, "A", "CANTOR", true)));
    //     mockMvc.perform(get("/v1/artistas")).andExpect(status().isOk());
    // }

    @Test
    @WithMockUser
    void testCreateReturnsCreated() throws Exception {
        ArtistaDto created = new ArtistaDto(10L, "New", "CANTOR", true);
        when(service.create(any(ArtistaDto.class))).thenReturn(created);

        mockMvc.perform(post("/v1/artistas").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"New\", \"tipo\":\"CANTOR\"}"))
            .andExpect(status().isCreated());
    }
}
