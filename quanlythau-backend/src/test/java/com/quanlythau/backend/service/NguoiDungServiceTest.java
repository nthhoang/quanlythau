package com.quanlythau.backend.service;

import com.quanlythau.backend.dto.nguoidung.DonGiaCongRequest;
import com.quanlythau.backend.dto.nguoidung.TaoThoRequest;
import com.quanlythau.backend.dto.nguoidung.ThoResponse;
import com.quanlythau.backend.entity.ChuyenMon;
import com.quanlythau.backend.entity.CongTrinh;
import com.quanlythau.backend.entity.NguoiDung;
import com.quanlythau.backend.entity.PhanCongCongTrinh;
import com.quanlythau.backend.entity.QuanHeThoChuThau;
import com.quanlythau.backend.entity.VaiTro;
import com.quanlythau.backend.exception.BadRequestException;
import com.quanlythau.backend.exception.ResourceNotFoundException;
import com.quanlythau.backend.repository.NguoiDungRepository;
import com.quanlythau.backend.repository.PhanCongCongTrinhRepository;
import com.quanlythau.backend.repository.QuanHeThoChuThauRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class NguoiDungServiceTest {

    @Mock
    private NguoiDungRepository nguoiDungRepository;

    @Mock
    private PhanCongCongTrinhRepository phanCongRepository;

    @Mock
    private QuanHeThoChuThauRepository quanHeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private NguoiDungService nguoiDungService;

    @Test
    void getDoiNguOnlyReturnsWorkersFromContractorsOwnProjectsAndShowsActiveAssignments() {
        NguoiDung worker = NguoiDung.builder()
                .id(10L)
                .chuThauId(1L)
                .active(true)
                .hoTen("Nguyễn Văn A")
                .chuyenMon(ChuyenMon.THO_CHINH)
                .donGiaCong(BigDecimal.valueOf(350000))
                .build();
        QuanHeThoChuThau employment = QuanHeThoChuThau.builder()
                .id(11L)
                .chuThauId(1L)
                .tho(worker)
                .donGiaCong(BigDecimal.valueOf(350000))
                .ngayBatDau(LocalDate.now().minusDays(2))
                .build();
        PhanCongCongTrinh activeAssignment = PhanCongCongTrinh.builder()
                .nguoiDung(worker)
                .congTrinh(CongTrinh.builder().tenCongTrinh("Nhà phố").build())
                .build();
        when(phanCongRepository.findActiveAssignmentsByContractorId(1L)).thenReturn(List.of(activeAssignment));
        when(quanHeRepository.findByChuThauId(1L)).thenReturn(List.of(employment));
        when(nguoiDungRepository.findByChuThauIdAndVaiTroOrderByHoTen(1L, VaiTro.THO)).thenReturn(List.of(worker));

        var team = nguoiDungService.getDoiNgu(1L);

        assertEquals(1, team.size());
        assertEquals("Nguyễn Văn A", team.get(0).getHoTen());
        assertTrue(team.get(0).getDangLamViec());
        assertEquals(LocalDate.now().minusDays(2), team.get(0).getNgayThamGia());
        assertEquals(List.of("Nhà phố"), team.get(0).getCongTrinhDangLamViec());
    }

    @Test
    void suaDonGiaCongRejectsWorkersOutsideContractorsTeam() {
        when(nguoiDungRepository.existsByChuThauIdAndIdAndVaiTro(1L, 10L, VaiTro.THO)).thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> nguoiDungService.suaDonGiaCong(1L, 10L, new DonGiaCongRequest(BigDecimal.valueOf(400000)))
        );
    }

    @Test
    void taoThoCreatesAccountOwnedByCurrentContractorWithEncodedPassword() {
        TaoThoRequest request = new TaoThoRequest();
        request.setTenDangNhap("tho_minh");
        request.setMatKhau("matkhau123");
        request.setHoTen("Nguyễn Văn Minh");
        request.setChuyenMon(ChuyenMon.THO_CHINH);
        request.setDonGiaCong(BigDecimal.valueOf(400000));
        when(nguoiDungRepository.existsByTenDangNhap("tho_minh")).thenReturn(false);
        when(passwordEncoder.encode("matkhau123")).thenReturn("encoded-password");
        when(nguoiDungRepository.save(any(NguoiDung.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ThoResponse response = nguoiDungService.taoTho(1L, request);

        ArgumentCaptor<NguoiDung> workerCaptor = ArgumentCaptor.forClass(NguoiDung.class);
        verify(nguoiDungRepository).save(workerCaptor.capture());
        assertEquals("tho_minh", response.getTenDangNhap());
        assertEquals(LocalDate.now(), response.getNgayThamGia());
        assertEquals("encoded-password", workerCaptor.getValue().getMatKhau());
        assertEquals(1L, workerCaptor.getValue().getChuThauId());
        verify(quanHeRepository).save(any(QuanHeThoChuThau.class));
    }

    @Test
    void cannotAssignWorkerWhileTheyAreEmployedByAnotherContractor() {
        NguoiDung worker = NguoiDung.builder().id(10L).vaiTro(VaiTro.THO).chuThauId(1L).active(true).build();
        when(nguoiDungRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(worker));

        assertThrows(BadRequestException.class, () -> nguoiDungService.layHoacTaoQuanHe(2L, worker, true));
    }

    @Test
    void choThoNghiViecEndsOnlyThisContractorsActiveAssignments() {
        NguoiDung worker = NguoiDung.builder().id(10L).vaiTro(VaiTro.THO).chuThauId(1L).active(true).build();
        PhanCongCongTrinh assignment = PhanCongCongTrinh.builder()
                .nguoiDung(worker)
                .congTrinh(CongTrinh.builder().id(20L).build())
                .ngayBatDau(LocalDate.now().minusDays(3))
                .build();
        when(nguoiDungRepository.existsByChuThauIdAndIdAndVaiTro(1L, 10L, VaiTro.THO)).thenReturn(true);
        when(nguoiDungRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(worker));
        when(nguoiDungRepository.findById(10L)).thenReturn(Optional.of(worker));
        when(quanHeRepository.findFirstByChuThauIdAndThoIdAndNgayKetThucIsNull(1L, 10L))
                .thenReturn(Optional.empty());
        when(phanCongRepository.findActiveAssignmentsByContractorAndWorker(1L, 10L))
                .thenReturn(List.of(assignment));

        nguoiDungService.choThoNghiViec(1L, 10L);

        assertEquals(LocalDate.now(), assignment.getNgayKetThuc());
        assertFalse(worker.getActive());
        verify(phanCongRepository).findActiveAssignmentsByContractorAndWorker(1L, 10L);
    }
}
