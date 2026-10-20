package com.quanlythau.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quanlythau.backend.dto.vattu.DongVatTuGoiYResponse;
import com.quanlythau.backend.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * Goi Gemini API (goi Vision cho OCR hoa don, va goi van ban thuan cho Tro ly
 * AI). Dung RestClient co san trong Spring (khong them thu vien moi) de goi
 * thang REST endpoint cua Google.
 *
 * Neu Google doi ten model sau nay, chi can sua "gemini.model" trong
 * application.yml, khong can sua code o day.
 */
@Service
@RequiredArgsConstructor
public class GeminiService {

    private static final String ENDPOINT_GOC = "https://generativelanguage.googleapis.com/v1beta/models/";

    private final ObjectMapper objectMapper;
    private final RestClient restClient = RestClient.create();

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.model}")
    private String model;

    private static final String PROMPT_HOA_DON = """
            Đây là ảnh hóa đơn hoặc phiếu xuất vật tư xây dựng (có thể là cát, đá, \
            xi măng, gạch, sắt thép, thiết bị điện nước...). Hãy đọc kỹ và trả về \
            MỘT MẢNG JSON duy nhất (không kèm giải thích, không dùng markdown), mỗi \
            phần tử có đúng các khóa: "tenVatTu" (chuỗi), "soLuong" (số), "donVi" \
            (chuỗi, ví dụ "kg", "bao", "m3", "cây"), "donGia" (số, đơn giá 1 đơn vị, \
            đơn vị tiền VND, không có ký tự khác ngoài số). Nếu không đọc được đơn \
            giá của một dòng thì để donGia = 0. Nếu ảnh không phải hóa đơn/phiếu \
            xuất vật tư hoặc không đọc được gì, trả về mảng rỗng [].""";

    /** OCR hoa don vat tu tu anh - tra ve danh sach GOI Y de nguoi dung xem lai truoc khi luu. */
    public List<DongVatTuGoiYResponse> docHoaDon(byte[] anhBytes, String mimeType) {
        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(
                                Map.of("text", PROMPT_HOA_DON),
                                Map.of("inline_data", Map.of("mime_type", mimeType, "data", Base64.getEncoder().encodeToString(anhBytes)))
                        )
                )),
                "generationConfig", Map.of("responseMimeType", "application/json")
        );

        String vanBan = trichXuatVanBan(goiGemini(body));
        return phanTichDanhSachVatTu(vanBan);
    }

    /** Hoi dap bang van ban thuan (khong anh) - dung cho Tro ly AI, tra loi dua tren nguCanh (du lieu that) duoc truyen vao. */
    public String hoiDap(String nguCanh, String cauHoi) {
        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(Map.of("text", nguCanh + "\n\nCâu hỏi: " + cauHoi))
                ))
        );

        return trichXuatVanBan(goiGemini(body));
    }

    private String goiGemini(Map<String, Object> body) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new BadRequestException("Chưa cấu hình Gemini API key (gemini.api-key trong application.yml)");
        }
        try {
            return restClient.post()
                    .uri(ENDPOINT_GOC + model + ":generateContent")
                    .header("x-goog-api-key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            throw new BadRequestException("Gọi Gemini API thất bại: " + e.getMessage());
        }
    }

    private String trichXuatVanBan(String phanHoiJson) {
        try {
            JsonNode goc = objectMapper.readTree(phanHoiJson);
            return goc.path("candidates").path(0).path("content").path("parts").path(0).path("text").asText();
        } catch (Exception e) {
            throw new BadRequestException("Không đọc được phản hồi từ Gemini");
        }
    }

    private List<DongVatTuGoiYResponse> phanTichDanhSachVatTu(String vanBanJson) {
        try {
            JsonNode mang = objectMapper.readTree(vanBanJson);
            List<DongVatTuGoiYResponse> ketQua = new ArrayList<>();
            if (mang.isArray()) {
                for (JsonNode dong : mang) {
                    ketQua.add(DongVatTuGoiYResponse.builder()
                            .tenVatTu(dong.path("tenVatTu").asText(""))
                            .soLuong(dong.has("soLuong") ? new BigDecimal(dong.path("soLuong").asText("0")) : BigDecimal.ZERO)
                            .donVi(dong.path("donVi").asText(""))
                            .donGia(dong.has("donGia") ? new BigDecimal(dong.path("donGia").asText("0")) : BigDecimal.ZERO)
                            .build());
                }
            }
            return ketQua;
        } catch (Exception e) {
            throw new BadRequestException("Không đọc được kết quả từ Gemini, thử lại với ảnh rõ hơn");
        }
    }
}
