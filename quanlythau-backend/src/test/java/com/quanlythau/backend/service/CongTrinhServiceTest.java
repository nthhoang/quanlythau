package com.quanlythau.backend.service;

import com.quanlythau.backend.dto.congtrinh.PhanCongRequest;
import com.quanlythau.backend.entity.CongTrinh;
import com.quanlythau.backend.entity.LoaiThongBao;
import com.quanlythau.backend.entity.NguoiDung;
import com.quanlythau.backend.entity.PhanCongCongTrinh;
import com.quanlythau.backend.entity.TrangThaiCongTrinh;
import com.quanlythau.backend.entity.VaiTro;
import com.quanlythau.backend.exception.BadRequestException;
import com.quanlythau.backend.repository.CongTrinhRepository;
import com.quanlythau.backend.repository.NguoiDungRepository;
import com.quanlythau.backend.repository.PhanCongCongTrinhRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CongTrinhServiceTest {

    @Mock
    private CongTrinhRepository congTrinhRepository;

    @Mock
    private NguoiDungRepository nguoiDungRepository;

    @Mock
    private PhanCongCongTrinhRepository phanCongRepository;

    @Mock
    private ThongBaoService thongBaoService;

    @Mock
    private NguoiDungService nguoiDungService;

    @InjectMocks
    private CongTrinhService congTrinhService;

    @Test
    void ganThoRejectsWorkerAlreadyAssignedToAnotherProject() {
        CongTrinh target = CongTrinh.builder().id(1L).nguoiTaoId(1L).build();
        NguoiDung worker = NguoiDung.builder().id(2L).vaiTro(VaiTro.THO).build();
        when(congTrinhRepository.findByIdAndNguoiTaoId(1L, 1L)).thenReturn(Optional.of(target));
        when(nguoiDungRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(worker));
        when(phanCongRepository.existsByCongTrinhIdAndNguoiDungIdAndNgayKetThucIsNull(1L, 2L))
                .thenReturn(false);
        when(phanCongRepository.existsByNguoiDungIdAndNgayKetThucIsNull(2L)).thenReturn(true);

        assertThrows(
                BadRequestException.class,
                () -> congTrinhService.ganTho(1L, new PhanCongRequest(2L, null), 1L)
        );

        InOrder readOrder = inOrder(nguoiDungRepository, congTrinhRepository);
        readOrder.verify(congTrinhRepository).findByIdAndNguoiTaoId(1L, 1L);
        readOrder.verify(nguoiDungRepository).findByIdForUpdate(2L);
        verify(phanCongRepository, never()).save(any(PhanCongCongTrinh.class));
        verify(thongBaoService, never()).tao(any(), any(), any(), any());
    }

    @Test
    void updateTrangThaiNotifiesEveryActiveWorkerWhenProjectIsPaused() {
        CongTrinh project = CongTrinh.builder()
                .id(1L)
                .tenCongTrinh("Công trình A")
                .trangThai(TrangThaiCongTrinh.DANG_THI_CONG)
                .build();
        NguoiDung worker = NguoiDung.builder().id(2L).build();
        PhanCongCongTrinh assignment = PhanCongCongTrinh.builder()
                .congTrinh(project)
                .nguoiDung(worker)
                .build();
        when(congTrinhRepository.findByIdAndNguoiTaoIdForUpdate(1L, 1L)).thenReturn(Optional.of(project));
        when(phanCongRepository.findByCongTrinhIdAndNgayKetThucIsNull(1L))
                .thenReturn(List.of(assignment));

        congTrinhService.updateTrangThai(1L, TrangThaiCongTrinh.TAM_DUNG, 1L);

        verify(thongBaoService).tao(
                eq(worker),
                eq(LoaiThongBao.CONG_TRINH_TAM_DUNG),
                eq("Công trình tạm dừng"),
                eq("Công trình \"Công trình A\" bạn đang thi công đã được tạm dừng")
        );
        verify(congTrinhRepository).save(project);
    }

    @Test
    void updateTrangThaiDoesNotSendDuplicateNotificationIfAlreadyPaused() {
        CongTrinh project = CongTrinh.builder()
                .id(1L)
                .trangThai(TrangThaiCongTrinh.TAM_DUNG)
                .build();
        when(congTrinhRepository.findByIdAndNguoiTaoIdForUpdate(1L, 1L)).thenReturn(Optional.of(project));

        congTrinhService.updateTrangThai(1L, TrangThaiCongTrinh.TAM_DUNG, 1L);

        verify(phanCongRepository, never()).findByCongTrinhIdAndNgayKetThucIsNull(1L);
        verify(thongBaoService, never()).tao(any(), any(), any(), any());
    }

    @Test
    void getAllReturnsOnlyTheAuthenticatedContractorsProjects() {
        when(congTrinhRepository.findByNguoiTaoId(41L)).thenReturn(List.of(
                CongTrinh.builder().id(1L).nguoiTaoId(41L).build()
        ));

        var projects = congTrinhService.getAll(41L, VaiTro.CHU_THAU);

        org.junit.jupiter.api.Assertions.assertEquals(1, projects.size());
        verify(congTrinhRepository).findByNguoiTaoId(41L);
        verify(congTrinhRepository, never()).findAll();
    }
}
