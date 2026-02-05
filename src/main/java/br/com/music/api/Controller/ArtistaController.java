package br.com.music.api.Controller;

import java.net.URI;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import br.com.music.api.Controller.dto.ArtistaDto;
import br.com.music.api.Services.ArtistaService;

@RestController
@RequestMapping("/v1/artistas")
@Tag(name = "Artistas", description = "Endpoints da API para gerenciar artistas")
public class ArtistaController {

    private final ArtistaService service;

    public ArtistaController(ArtistaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar todos os artistas", description = "Recupera uma lista de todos os artistas no sistema")
    @ApiResponse(responseCode = "200", description = "Lista de artistas recuperada com sucesso", 
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ArtistaDto.class)))
    public List<ArtistaDto> list(
            @Parameter(description = "Ordenação do campo artistaNome (asc ou desc)", required = false)
            @RequestParam(value = "sortArtistaNome", required = false) String sortArtistaNome) {
        return service.list(sortArtistaNome);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter artista por ID", description = "Recupera um artista pelo seu ID único")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Artista encontrado", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ArtistaDto.class))),
        @ApiResponse(responseCode = "404", description = "Artista não encontrado", content = @Content())
    })
    public ResponseEntity<ArtistaDto> get(
            @Parameter(description = "ID do artista", required = true)
            @PathVariable Long id,
            @Parameter(description = "Ordenação do campo artistaNome (asc ou desc)", required = false)
            @RequestParam(value = "sortArtistaNome", required = false) String sortArtistaNome) {
        return service.get(id, sortArtistaNome)
                .map(a -> ResponseEntity.ok(a))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Criar novo artista", description = "Cria um novo artista com os detalhes fornecidos")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Artista criado com sucesso", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ArtistaDto.class))),
        @ApiResponse(responseCode = "400", description = "Entrada inválida", content = @Content())
    })
    public ResponseEntity<ArtistaDto> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Detalhes do artista")
            @RequestBody @Valid ArtistaDto dto, 
            UriComponentsBuilder uriBuilder) {
        ArtistaDto created = service.create(dto);
        URI uri = uriBuilder.path("/v1/artistas/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uri).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar artista", description = "Atualiza um artista existente com novas informações")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Artista atualizado com sucesso", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ArtistaDto.class))),
        @ApiResponse(responseCode = "404", description = "Artista não encontrado", content = @Content()),
        @ApiResponse(responseCode = "400", description = "Entrada inválida", content = @Content())
    })
    public ResponseEntity<ArtistaDto> update(
            @Parameter(description = "ID do artista", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Detalhes do artista atualizado")
            @RequestBody @Valid ArtistaDto dto) {
        return service.update(id, dto)
                .map(updated -> ResponseEntity.ok(updated))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar artista", description = "Deleta um artista pelo seu ID único")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Artista deletado com sucesso", content = @Content()),
        @ApiResponse(responseCode = "404", description = "Artista não encontrado", content = @Content())
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do artista", required = true)
            @PathVariable Long id) {
        return service.delete(id) 
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
