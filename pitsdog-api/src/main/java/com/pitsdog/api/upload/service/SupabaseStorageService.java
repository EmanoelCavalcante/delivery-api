package com.pitsdog.api.upload.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class SupabaseStorageService {

    private static final Logger log = LoggerFactory.getLogger(SupabaseStorageService.class);

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_PNG_VALUE
    );

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            ".jpg",
            ".jpeg",
            ".png"
    );

    private static final long MAX_SIZE_BYTES = 5L * 1024 * 1024;

    private final RestTemplate restTemplate;
    private final String supabaseUrl;
    private final String supabaseServiceRoleKey;
    private final String bucket;

    public SupabaseStorageService(
            RestTemplate restTemplate,
            @Value("${supabase.url}") String supabaseUrl,
            @Value("${supabase.service-role-key}") String supabaseServiceRoleKey,
            @Value("${supabase.storage.bucket}") String bucket
    ) {
        this.restTemplate = restTemplate;
        this.supabaseUrl = removerBarraFinal(supabaseUrl);
        this.supabaseServiceRoleKey = supabaseServiceRoleKey;
        this.bucket = bucket;

        log.info(
                "SupabaseStorageService iniciado. supabaseUrl={}, bucket={}",
                this.supabaseUrl,
                this.bucket
        );
    }

    public String uploadImagem(MultipartFile file, String folder) {
        validarArquivo(file);
        validarConfiguracoes();

        String objectPath = gerarCaminhoArquivo(file, folder);
        String uploadUrl = montarUploadUrl(objectPath);

        byte[] bytes;

        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            log.error("Erro ao ler bytes do arquivo enviado.", e);

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Erro ao ler o arquivo de imagem."
            );
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(supabaseServiceRoleKey);
        headers.set("apikey", supabaseServiceRoleKey);
        headers.set("x-upsert", "true");
        headers.setContentType(MediaType.parseMediaType(file.getContentType()));

        HttpEntity<byte[]> requestEntity = new HttpEntity<>(bytes, headers);

        log.info(
                "Iniciando upload no Supabase Storage. bucket={}, objectPath={}, contentType={}, tamanho={} bytes",
                bucket,
                objectPath,
                file.getContentType(),
                file.getSize()
        );

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    uploadUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            log.info(
                    "Resposta do Supabase Storage. status={}, body={}",
                    response.getStatusCode(),
                    response.getBody()
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        "Erro ao enviar imagem para o Supabase Storage."
                );
            }

            String publicUrl = gerarUrlPublica(objectPath);

            log.info("URL pública gerada para imagem: {}", publicUrl);

            return publicUrl;

        } catch (HttpStatusCodeException e) {
            log.error(
                    "Erro HTTP do Supabase Storage. status={}, body={}",
                    e.getStatusCode(),
                    e.getResponseBodyAsString(),
                    e
            );

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Erro no Supabase Storage: " + e.getStatusCode()
            );

        } catch (RestClientException e) {
            log.error("Erro de comunicação com Supabase Storage.", e);

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Erro de comunicação com o Supabase Storage."
            );

        } catch (ResponseStatusException e) {
            throw e;

        } catch (Exception e) {
            log.error("Erro inesperado ao fazer upload no Supabase Storage.", e);

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro inesperado ao enviar imagem para o storage."
            );
        }
    }

    private void validarConfiguracoes() {
        if (!StringUtils.hasText(supabaseUrl)) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "SUPABASE_URL não configurada."
            );
        }

        if (!StringUtils.hasText(supabaseServiceRoleKey)) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "SUPABASE_SERVICE_ROLE_KEY não configurada."
            );
        }

        if (!StringUtils.hasText(bucket)) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "SUPABASE_STORAGE_BUCKET não configurado."
            );
        }
    }

    private void validarArquivo(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Nenhum arquivo enviado."
            );
        }

        String contentType = file.getContentType();

        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Formato de imagem inválido. Envie apenas JPEG ou PNG."
            );
        }

        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Imagem muito grande. O limite é 5MB."
            );
        }

        String originalFilename = file.getOriginalFilename();

        if (!StringUtils.hasText(originalFilename) || !temExtensaoValida(originalFilename)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Extensão inválida. Envie apenas .jpg, .jpeg ou .png."
            );
        }
    }

    private boolean temExtensaoValida(String filename) {
        String lower = filename.toLowerCase(Locale.ROOT);
        return ALLOWED_EXTENSIONS.stream().anyMatch(lower::endsWith);
    }

    private String gerarCaminhoArquivo(MultipartFile file, String folder) {
        String safeFolder = normalizarFolder(folder);
        String extensao = obterExtensao(file);

        return safeFolder + "/" + UUID.randomUUID() + extensao;
    }

    private String obterExtensao(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null) {
            return ".jpg";
        }

        String lower = originalFilename.toLowerCase(Locale.ROOT);

        if (lower.endsWith(".jpeg")) {
            return ".jpeg";
        }

        if (lower.endsWith(".jpg")) {
            return ".jpg";
        }

        if (lower.endsWith(".png")) {
            return ".png";
        }

        return ".jpg";
    }

    private String montarUploadUrl(String objectPath) {
        return supabaseUrl
                + "/storage/v1/object/"
                + bucket
                + "/"
                + encodePath(objectPath);
    }

    private String gerarUrlPublica(String objectPath) {
        return supabaseUrl
                + "/storage/v1/object/public/"
                + bucket
                + "/"
                + encodePath(objectPath);
    }

    private String encodePath(String path) {
        return UriUtils.encodePath(path, StandardCharsets.UTF_8);
    }

    private String normalizarFolder(String folder) {
        if (!StringUtils.hasText(folder)) {
            return "geral";
        }

        String normalized = Normalizer.normalize(folder, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9/_-]", "-")
                .replaceAll("-+", "-")
                .replaceAll("^/+", "")
                .replaceAll("/+$", "");

        if (!StringUtils.hasText(normalized)) {
            return "geral";
        }

        return normalized;
    }

    private String removerBarraFinal(String value) {
        if (value == null) {
            return "";
        }

        return value.endsWith("/")
                ? value.substring(0, value.length() - 1)
                : value;
    }
}