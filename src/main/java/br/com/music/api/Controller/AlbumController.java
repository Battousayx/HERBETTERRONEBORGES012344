package br.com.music.api.Controller;

import java.net.URI;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import org.springdoc.core.annotations.ParameterObject;
import br.com.music.api.Controller.dto.AlbumDto;
import br.com.music.api.Services.AlbumService;

@RestController
@RequestMapping("/v1/albuns")
@Tag(name = "Albuns", description = "Endpoints da API para gerenciar álbuns")
public class AlbumController {

    private final AlbumService service;

    public AlbumController(AlbumService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar todos os álbuns", description = "Recupera uma lista paginada de todos os álbuns no sistema")
    @ApiResponse(responseCode = "200", description = "Lista de álbuns recuperada com sucesso", 
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = AlbumDto.class)))
    public Page<AlbumDto> list(
            @ParameterObject Pageable pageable) {
        return service.list(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter álbum por ID", description = "Recupera um álbum pelo seu ID único")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Álbum encontrado", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AlbumDto.class))),
        @ApiResponse(responseCode = "404", description = "Álbum não encontrado", content = @Content())
    })
    public ResponseEntity<AlbumDto> get(
            @Parameter(description = "ID do álbum", required = true)
            @PathVariable Long id,
            @ParameterObject Pageable pageable) {
        return service.get(id, pageable)
                .map(album -> ResponseEntity.ok(album))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Criar novo álbum", description = "Cria um novo álbum com os detalhes fornecidos")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Álbum criado com sucesso", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AlbumDto.class))),
        @ApiResponse(responseCode = "400", description = "Entrada inválida", content = @Content())
    })
    public ResponseEntity<AlbumDto> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Detalhes do álbum")
            @RequestBody @Valid AlbumDto dto,
            UriComponentsBuilder uriBuilder) {
        AlbumDto created = service.create(dto);
        URI uri = uriBuilder.path("/v1/albuns/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uri).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar álbum", description = "Atualiza um álbum existente com novas informações")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Álbum atualizado com sucesso", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AlbumDto.class))),
        @ApiResponse(responseCode = "404", description = "Álbum não encontrado", content = @Content()),
        @ApiResponse(responseCode = "400", description = "Entrada inválida", content = @Content())
    })
    public ResponseEntity<AlbumDto> update(
            @Parameter(description = "ID do álbum", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Detalhes do álbum atualizado")
            @RequestBody @Valid AlbumDto dto) {
        return service.update(id, dto)
                .map(updated -> ResponseEntity.ok(updated))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar álbum", description = "Deleta um álbum pelo seu ID único")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Álbum deletado com sucesso", content = @Content()),
        @ApiResponse(responseCode = "404", description = "Álbum não encontrado", content = @Content())
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do álbum", required = true)
            @PathVariable Long id) {
        return service.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
