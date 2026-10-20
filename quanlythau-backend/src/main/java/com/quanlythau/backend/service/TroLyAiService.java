package com.quanlythau.backend.service;

import com.quanlythau.backend.dto.laillo.LaiLoResponse;
import com.quanlythau.backend.entity.NguoiDung;
import com.quanlythau.backend.entity.VaiTro;
import com.quanlythau.backend.repository.NguoiDungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Tro ly AI hoi dap nhanh cho chu thau. KHONG de AI tu sinh cau truy van
 * database (rui ro sai/lo du lieu) - thay vao do lay du lieu THAT tu cac
 * service da co san (lai lo, danh sach tho...), dong goi thanh 1 doan van
 * ban ngu canh, roi giao cho Gemini tra loi DUA HOAN TOAN vao ngu canh do.
 */
@Service
@RequiredArgsConstructor
public class TroLyAiService {

    private final GeminiService geminiService;
    private final LaiLoService laiLoService;
    private final NguoiDungRepository nguoiDungRepository;

    private static final String HUONG_DAN_HE_THONG = """
            Bạn là trợ lý ảo cho chủ thầu xây dựng tư nhân. Trả lời câu hỏi \
            DỰA HOÀN TOÀN vào dữ liệu được cung cấp bên dưới, KHÔNG bịa thêm \
            số liệu không có trong dữ liệu. Trả lời ngắn gọn, súc tích, bằng \
            tiếng Việt, giọng thân thiện như đang nói chuyện trực tiếp. Nếu \
            dữ liệu không đủ để trả lời, hãy nói rõ là chưa có đủ thông tin \
            thay vì đoán.""";

    @Transactional(readOnly = true)
    public String hoiDap(String cauHoi) {
        String nguCanh = HUONG_DAN_HE_THONG + "\n\n" + xayDungNguCanh();
        return geminiService.hoiDap(nguCanh, cauHoi);
    }

    private String xayDungNguCanh() {
        StringBuilder sb = new StringBuilder();

        sb.append("DANH SÁCH CÔNG TRÌNH VÀ TÌNH HÌNH CHI PHÍ / LÃI LỖ:\n");
        List<LaiLoResponse> danhSachLaiLo = laiLoService.tinhLaiLoTatCa();
        if (danhSachLaiLo.isEmpty()) {
            sb.append("(chưa có công trình nào)\n");
        }
        for (LaiLoResponse c : danhSachLaiLo) {
            sb.append("- ").append(c.getTenCongTrinh())
                    .append(" | trạng thái: ").append(c.getTrangThaiCongTrinh())
                    .append(" | tiến độ: ").append(c.getTienDoPhanTram()).append("%")
                    .append(" | giá trị hợp đồng: ").append(c.getGiaTriHopDong())
                    .append(" | chi phí nhân công: ").append(c.getChiPhiNhanCong())
                    .append(" | chi phí vật tư: ").append(c.getChiPhiVatTu())
                    .append(" | chi phí khác: ").append(c.getChiPhiKhac())
                    .append(" | tổng chi phí: ").append(c.getTongChiPhi())
                    .append(" | lãi/lỗ tạm tính: ").append(c.getLaiLoTamTinh())
                    .append(" | % đã chi so với hợp đồng: ").append(c.getPhanTramDaChi())
                    .append(c.isVuotNguongCanhBao() ? " | CẢNH BÁO: đã vượt ngưỡng chi phí cho phép" : "")
                    .append("\n");
        }

        sb.append("\nDANH SÁCH THỢ:\n");
        List<NguoiDung> danhSachTho = nguoiDungRepository.findByVaiTro(VaiTro.THO);
        if (danhSachTho.isEmpty()) {
            sb.append("(chưa có thợ nào)\n");
        }
        for (NguoiDung tho : danhSachTho) {
            sb.append("- ").append(tho.getHoTen())
                    .append(" | chuyên môn: ").append(tho.getChuyenMon())
                    .append(" | đơn giá công/ngày: ").append(tho.getDonGiaCong())
                    .append("\n");
        }

        return sb.toString();
    }
}
