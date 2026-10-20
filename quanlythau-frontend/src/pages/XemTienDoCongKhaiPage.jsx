import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import axiosClient, { BACKEND_ORIGIN } from '../api/axiosClient';
import ProgressBar from '../components/ProgressBar';

const LOAI_HINH_LABEL = { XAY_MOI: 'Xây mới', SUA_CHUA: 'Sửa chữa', CAI_TAO: 'Cải tạo' };
const TRANG_THAI_LABEL = { DANG_THI_CONG: 'Đang thi công', TAM_DUNG: 'Tạm dừng', HOAN_THANH: 'Hoàn thành' };

export default function XemTienDoCongKhaiPage() {
  const { shareToken } = useParams();
  const [congTrinh, setCongTrinh] = useState(null);
  const [dangTai, setDangTai] = useState(true);
  const [loi, setLoi] = useState('');

  useEffect(() => {
    axiosClient
      .get(`/public/cong-trinh/${shareToken}`)
      .then((res) => setCongTrinh(res.data))
      .catch(() => setLoi('Không tìm thấy công trình. Link có thể không đúng, vui lòng hỏi lại nhà thầu.'))
      .finally(() => setDangTai(false));
  }, [shareToken]);

  return (
    <div className="min-h-screen bg-concrete-50">
      <header className="bg-blueprint-900 text-white py-5">
        <div className="max-w-2xl mx-auto px-6">
          <h1 className="font-display font-extrabold text-lg tracking-tight">XEM TIẾN ĐỘ CÔNG TRÌNH</h1>
          <p className="text-blueprint-100/60 text-xs">Trang xem công khai — không cần đăng nhập</p>
        </div>
      </header>

      <main className="max-w-2xl mx-auto px-6 py-8">
        {dangTai ? (
          <p className="text-blueprint-900/50">Đang tải...</p>
        ) : loi ? (
          <div className="text-sm bg-red-50 text-red-700 border border-red-200 rounded px-4 py-3">{loi}</div>
        ) : (
          <>
            <div className="bg-white border border-concrete-200 rounded-lg p-6 mb-6">
              <div className="flex items-start justify-between mb-1">
                <h2 className="font-display font-bold text-lg text-blueprint-900">{congTrinh.tenCongTrinh}</h2>
                <span className="text-[11px] uppercase font-semibold text-blueprint-500 bg-blueprint-100 px-2 py-0.5 rounded">
                  {LOAI_HINH_LABEL[congTrinh.loaiHinh] || congTrinh.loaiHinh}
                </span>
              </div>
              <p className="text-sm text-blueprint-900/60 mb-4">{congTrinh.diaChi}</p>

              <ProgressBar percent={congTrinh.tienDoPhanTram} />

              <div className="text-sm text-blueprint-900/70 mt-4 pt-4 border-t border-concrete-200 space-y-1">
                <p>Trạng thái: <span className="font-semibold">{TRANG_THAI_LABEL[congTrinh.trangThai] || congTrinh.trangThai}</span></p>
                {congTrinh.ngayBatDau && <p>Bắt đầu: {congTrinh.ngayBatDau} {congTrinh.ngayDuKienHoanThanh && `→ dự kiến xong ${congTrinh.ngayDuKienHoanThanh}`}</p>}
              </div>
            </div>

            <h3 className="font-display font-bold text-blueprint-900 mb-3">Nhật ký thi công</h3>
            {congTrinh.nhatKyThiCong.length === 0 ? (
              <div className="bg-white border border-dashed border-concrete-200 rounded-lg p-10 text-center text-blueprint-900/50">Chưa có nhật ký thi công nào.</div>
            ) : (
              <div className="space-y-3">
                {congTrinh.nhatKyThiCong.map((n) => (
                  <div key={n.id} className="bg-white border border-concrete-200 rounded-lg p-5">
                    <p className="font-semibold text-blueprint-900 mb-2">{n.ngay}</p>
                    <p className="text-sm text-blueprint-900/80 whitespace-pre-wrap mb-3">{n.noiDung}</p>
                    {n.danhSachAnh?.length > 0 && (
                      <div className="flex flex-wrap gap-2">
                        {n.danhSachAnh.map((url, i) => (
                          <a key={i} href={BACKEND_ORIGIN + url} target="_blank" rel="noreferrer">
                            <img src={BACKEND_ORIGIN + url} alt="Ảnh hiện trường" className="w-20 h-20 object-cover rounded border border-concrete-200" />
                          </a>
                        ))}
                      </div>
                    )}
                  </div>
                ))}
              </div>
            )}
          </>
        )}
      </main>
    </div>
  );
}
