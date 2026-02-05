package br.com.music.api.Services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.music.api.Controller.dto.ArtistaDto;
import br.com.music.api.Domain.Artista;
import br.com.music.api.Repository.ArtistaRepository;

@Service
public class ArtistaService {

    private final ArtistaRepository repository;
    private final ArtistaMapper mapper;
    public ArtistaService(ArtistaRepository repository, ArtistaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<ArtistaDto> list(String sortArtistaNome) {
        if (sortArtistaNome != null && !sortArtistaNome.trim().isEmpty()) {
            Sort sort = sortArtistaNome.equalsIgnoreCase("asc") 
                ? Sort.by(Sort.Direction.ASC, "nome")
                : Sort.by(Sort.Direction.DESC, "nome");
            return repository.findAll(sort).stream().map(mapper::toDto).collect(Collectors.toList());
        }
        return repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public Optional<ArtistaDto> get(Long id, String sortArtistaNome) {
        return repository.findById(id).map(mapper::toDto);
    }

    @Transactional
    public ArtistaDto create(ArtistaDto dto) {
        Artista entidade = new Artista();
        entidade.setNome(dto.getNome());
        entidade.setTipo(mapper.stringToEnum(dto.getTipo()));
        entidade.setAtivo(dto.getAtivo() == null ? true : dto.getAtivo());
        Artista saved = repository.save(entidade);
        return mapper.toDto(saved);
    }

    @Transactional
    public Optional<ArtistaDto> update(Long id, ArtistaDto dto) {
        return repository.findById(id)
                .map(artista -> {
                    artista.setNome(dto.getNome());
                    artista.setTipo(mapper.stringToEnum(dto.getTipo()));
                    artista.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);
                    Artista updated = repository.save(artista);
                    return mapper.toDto(updated);
                });
    }

    @Transactional
    public boolean delete(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}
