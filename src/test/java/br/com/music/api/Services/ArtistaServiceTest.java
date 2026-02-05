package br.com.music.api.Services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.music.api.Controller.dto.ArtistaDto;
import br.com.music.api.Domain.Artista;
import br.com.music.api.Domain.Enums.TipoArtista;
import br.com.music.api.Repository.ArtistaRepository;

@ExtendWith(MockitoExtension.class)
class ArtistaServiceTest {

    @Mock
    ArtistaRepository repository;

    @Mock
    ArtistaMapper mapper;

    @InjectMocks
    ArtistaService service;


    @Test
    void create_SavesAndReturnsDto() {
        ArtistaDto dto = new ArtistaDto(null, "New", "CANTOR", true);
        Artista entidade = new Artista();
        entidade.setNome("New");
        when(mapper.toEntity(dto)).thenReturn(entidade);
        Artista saved = new Artista();
        saved.setId(5L);
        saved.setNome("New");
        when(repository.save(entidade)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(new ArtistaDto(5L, "New", "CANTOR", true));

        ArtistaDto res = service.create(dto);
        assertNotNull(res);
        assertEquals(5L, res.getId());
    }
}
