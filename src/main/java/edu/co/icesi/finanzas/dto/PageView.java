package edu.co.icesi.finanzas.dto;
import org.springframework.data.domain.Page;
import java.util.List;
/** Contrato de paginacion independiente de la serializacion interna de Spring. */
public record PageView<T>(List<T> content, int number, int size, long totalElements, int totalPages) {
    public static <T> PageView<T> from(Page<T> page) {
        return new PageView<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }
}
