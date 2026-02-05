package br.com.music.api.Controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.music.api.Services.ImageService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        String id = imageService.upload(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<String> getImage(@PathVariable String id) {
        String base64 = imageService.getBase64(id);
        if (base64 == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(base64);
    }

    @GetMapping(value = "/{id}/raw")
    public ResponseEntity<byte[]> getRawImage(@PathVariable String id) {
        try {
            var img = imageService.download(id);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(img.contentType()));
            headers.setContentLength(img.data().length);
            return new ResponseEntity<>(img.data(), headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
