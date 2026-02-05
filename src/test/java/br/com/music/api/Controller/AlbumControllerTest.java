package br.com.music.api.Controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import br.com.music.api.Controller.dto.AlbumDto;
import br.com.music.api.Services.AlbumService;

@WebMvcTest(AlbumController.class)
class AlbumControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AlbumService service;

    @Autowired
    ObjectMapper objectMapper;

    // @Test
    // @WithMockUser
    // void testListReturnsOk() throws Exception {
    //     when(service.list()).thenReturn(List.of(new AlbumDto(1L, "Abbey Road", LocalDate.of(2023, 1, 15), true)));
    //     mockMvc.perform(get("/v1/albuns")).andExpect(status().isOk());
    // }

    @Test
    @WithMockUser
    void testCreateReturnsCreated() throws Exception {
        AlbumDto created = new AlbumDto(10L, "New Album", LocalDate.of(2024, 1, 1), true);
        when(service.create(any(AlbumDto.class))).thenReturn(created);

        mockMvc.perform(post("/v1/albuns").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"titulo\":\"New\", \"dataLancamento\":\"2024-01-01\"}"))
            .andExpect(status().isCreated());
    }
}
