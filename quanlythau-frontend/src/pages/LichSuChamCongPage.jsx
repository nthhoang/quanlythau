import { useEffect, useState } from 'react';
import axiosClient from '../api/axiosClient';
import Navbar from '../components/Navbar';
import CameraCapture from '../components/CameraCapture';

const LOAI_CONG_LABEL = { CA_NGAY: 'Cả ngày', NUA_CONG: 'Nửa công' };
const TRANG_THAI_LABEL = {
  TU_DONG_XAC_NHAN: { text: 'Đã xác nhận', mau: 'bg-green-100 text-green-700' },
  CAN_DUYET: { text: 'Chờ chủ thầu duyệt', mau: 'bg-amber-100 text-amber-700' },
  CHU_THAU_TAO: { text: 'Chủ thầu chấm hộ', mau: 'bg-blueprint-100 text-blueprint-700' },
  TU_CHOI: { text: 'Bị từ chối', mau: 'bg-red-100 text-red-700' },
};

export default function LichSuChamCongPage() {
  const [danhSach, setDanhSach] = useState([]);
  const [dangTai, setDangTai] = useState(true);
  const [loi, setLoi] = useState('');
  const [thongBao, setThongBao] = useState('');
  const [daThietLapKhuonMat, setDaThietLapKhuonMat] = useState(true);
  const [hienThietLapLai, setHienThietLapLai] = useState(false);

  const taiTatCa = async () => {
    setDangTai(true);
    try {
      const [lichSu, trangThai] = await Promise.all([
        axiosClient.get('/cham-cong/cua-toi'),
        axiosClient.get('/cham-cong/khuon-mat/trang-thai-cua-toi'),
      ]);
      setDanhSach(lichSu.data);
      setDaThietLapKhuonMat(trangThai.data.daThietLap);
      setLoi('');
    } catch { setLoi('Không tải được dữ liệu chấm công'); }
    finally { setDangTai(false); }
  };

  useEffect(() => { taiTatCa(); }, []);

  const handleThietLapKhuonMat = async ({ descriptor }) => {
    try {
      await axiosClient.post('/cham-cong/khuon-mat', { faceDescriptor: descriptor });
      setThongBao('Thiết lập khuôn mặt thành công. Khi tới công trình, chủ thầu sẽ quét mặt để đối chiếu với ảnh này.');
      setHienThietLapLai(false);
      await taiTatCa();
    } catch (err) { setLoi(err.response?.data?.message || 'Thiết lập khuôn mặt thất bại'); }
  };

  return (
    <div className="min-h-screen bg-concrete-50">
      <Navbar />
      <main className="max-w-2xl mx-auto px-6 py-8">
        <h2 className="font-display font-extrabold text-xl text-blueprint-900 mb-1">Công của tôi</h2>
        <p className="text-sm text-blueprint-900/50 mb-6">Chấm công thật diễn ra tại công trình (chủ thầu quét mặt trên máy chung). Đây là nơi bạn thiết lập khuôn mặt và xem lại lịch sử.</p>

        {loi && <div className="mb-4 text-sm bg-red-50 text-red-700 border border-red-200 rounded px-3 py-2">{loi}</div>}
        {thongBao && <div className="mb-4 text-sm bg-green-50 text-green-700 border border-green-200 rounded px-3 py-2">{thongBao}</div>}

        {!dangTai && (!daThietLapKhuonMat || hienThietLapLai) ? (
          <div className="bg-white border border-concrete-200 rounded-lg p-6 mb-6">
            <p className="text-sm text-blueprint-900/70 mb-4">
              {daThietLapKhuonMat ? 'Chụp lại ảnh khuôn mặt mới (sẽ thay thế ảnh cũ).' : 'Thiết lập khuôn mặt một lần — khi tới công trình, chủ thầu sẽ quét mặt bạn để đối chiếu với ảnh này. Đưa mặt vào khung hình rõ ràng, đủ sáng.'}
            </p>
            <CameraCapture onCapture={handleThietLapKhuonMat} nhan="Thiết lập khuôn mặt" />
          </div>
        ) : (
          !dangTai && (
            <div className="bg-white border border-concrete-200 rounded-lg p-4 mb-6 flex items-center justify-between">
              <p className="text-sm text-green-700 font-semibold">✓ Đã thiết lập khuôn mặt</p>
              <button onClick={() => setHienThietLapLai(true)} className="text-sm text-blueprint-500 font-semibold hover:underline">Thiết lập lại</button>
            </div>
          )
        )}

        <h3 className="font-display font-bold text-blueprint-900 mb-3">Lịch sử chấm công</h3>
        {dangTai ? (
          <p className="text-blueprint-900/50">Đang tải...</p>
        ) : danhSach.length === 0 ? (
          <div className="bg-white border border-dashed border-concrete-200 rounded-lg p-10 text-center text-blueprint-900/50">Chưa có bản ghi chấm công nào trong 2 tháng gần đây.</div>
        ) : (
          <div className="space-y-2">
            {danhSach.map((c) => (
              <div key={c.id} className="bg-white border border-concrete-200 rounded-lg p-4 flex items-center justify-between">
                <div>
                  <p className="font-semibold text-blueprint-900">{c.ngayChamCong} · {c.tenCongTrinh}</p>
                  <p className="text-sm text-blueprint-900/60">{LOAI_CONG_LABEL[c.loaiCong]}{c.coTangCa && ' + tăng ca'}</p>
                </div>
                <span className={`text-xs font-semibold px-2 py-1 rounded ${TRANG_THAI_LABEL[c.trangThaiXacThuc]?.mau}`}>{TRANG_THAI_LABEL[c.trangThaiXacThuc]?.text}</span>
              </div>
            ))}
          </div>
        )}
      </main>
    </div>
  );
}
