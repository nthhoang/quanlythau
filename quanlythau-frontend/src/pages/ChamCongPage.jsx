import { useEffect, useState } from 'react';
import axiosClient from '../api/axiosClient';
import { uploadAnh } from '../api/upload';
import Navbar from '../components/Navbar';
import CameraCapture from '../components/CameraCapture';

const LOAI_CONG_OPTIONS = [
  { value: 'CA_NGAY', label: 'Cả ngày' },
  { value: 'NUA_CONG', label: 'Nửa công' },
];
const KHOA_LUU_CONG_TRINH = 'kiosk_cong_trinh_id';
const nhanLoaiCong = (loaiCong) => LOAI_CONG_OPTIONS.find((o) => o.value === loaiCong)?.label || loaiCong;

export default function ChamCongPage() {
  const [danhSachCongTrinh, setDanhSachCongTrinh] = useState([]);
  const [congTrinhId, setCongTrinhId] = useState(localStorage.getItem(KHOA_LUU_CONG_TRINH) || '');
  const [danhSachTrangThai, setDanhSachTrangThai] = useState([]);
  const [phanTramTangCa, setPhanTramTangCa] = useState(30);
  const [dangTai, setDangTai] = useState(true);
  const [loi, setLoi] = useState('');
  const [thongBao, setThongBao] = useState('');
  const [thoDangChon, setThoDangChon] = useState(null);
  const [loaiCong, setLoaiCong] = useState('CA_NGAY');
  const [coTangCa, setCoTangCa] = useState(false);
  const [dangGui, setDangGui] = useState(false);

  useEffect(() => {
    axiosClient.get('/cong-trinh').then((res) => setDanhSachCongTrinh(res.data)).catch(() => setLoi('Không tải được danh sách công trình'));
    axiosClient.get('/cai-dat').then((res) => setPhanTramTangCa(res.data.phanTramTangCa)).catch(() => {});
  }, []);

  const taiTrangThai = async (id) => {
    if (!id) return;
    setDangTai(true);
    try {
      const res = await axiosClient.get(`/cham-cong/trang-thai-cong-trinh/${id}`);
      setDanhSachTrangThai(res.data);
      setLoi('');
    } catch {
      setLoi('Không tải được danh sách thợ của công trình này');
    } finally {
      setDangTai(false);
    }
  };

  useEffect(() => {
    if (congTrinhId) {
      localStorage.setItem(KHOA_LUU_CONG_TRINH, congTrinhId);
      taiTrangThai(congTrinhId);
    } else {
      setDangTai(false);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [congTrinhId]);

  const chonTho = (tho) => {
    if (tho.daChamCongTaiDay) return;
    setThoDangChon(tho);
    setThongBao('');
    setLoi('');
  };

  const quayLaiLuoi = () => {
    setThoDangChon(null);
    setLoaiCong('CA_NGAY');
    setCoTangCa(false);
  };

  const handleThietLapKhuonMat = async ({ descriptor }) => {
    setDangGui(true);
    try {
      await axiosClient.post('/cham-cong/khuon-mat', { nguoiDungId: thoDangChon.nguoiDungId, faceDescriptor: descriptor });
      setThongBao(`Đã thiết lập khuôn mặt cho ${thoDangChon.hoTen}. Bấm lại tên để chấm công.`);
      quayLaiLuoi();
      await taiTrangThai(congTrinhId);
    } catch (err) {
      setLoi(err.response?.data?.message || 'Thiết lập khuôn mặt thất bại');
    } finally {
      setDangGui(false);
    }
  };

  const handleChamCong = async ({ descriptor, blob }) => {
    setDangGui(true);
    setLoi('');
    try {
      const urlAnh = await uploadAnh(blob);
      await axiosClient.post('/cham-cong', {
        nguoiDungId: thoDangChon.nguoiDungId, congTrinhId: Number(congTrinhId),
        loaiCong, coTangCa, faceDescriptor: descriptor, anhChamCong: urlAnh,
      });
      setThongBao(`Đã chấm công cho ${thoDangChon.hoTen}.`);
      quayLaiLuoi();
      await taiTrangThai(congTrinhId);
    } catch (err) {
      setLoi(err.response?.data?.message || 'Chấm công thất bại, thử lại nhé');
    } finally {
      setDangGui(false);
    }
  };

  const moTaCham = (c) => `${nhanLoaiCong(c.loaiCong)}${c.coTangCa ? ' + tăng ca' : ''} tại ${c.tenCongTrinh}`;

  return (
    <div className="min-h-screen bg-concrete-50">
      <Navbar />
      <main className="max-w-3xl mx-auto px-6 py-8">
        <h2 className="font-display font-extrabold text-xl text-blueprint-900 mb-1">Chấm công tại công trình</h2>
        <p className="text-sm font-semibold text-amber-600 mb-1 capitalize">
          {new Date().toLocaleDateString('vi-VN', { weekday: 'long', day: '2-digit', month: '2-digit', year: 'numeric' })}
        </p>
        <p className="text-sm text-blueprint-900/50 mb-6">Để máy này cố định tại công trình. Ai đến làm thì chọn đúng tên mình rồi quét mặt.</p>

        <div className="mb-6">
          <label className="block text-sm font-medium text-blueprint-900 mb-1">Công trình của máy này</label>
          <select value={congTrinhId} onChange={(e) => { setCongTrinhId(e.target.value); setThoDangChon(null); }}
            className="w-full border border-concrete-200 rounded px-3 py-2.5 bg-white font-semibold">
            <option value="">-- chọn công trình --</option>
            {danhSachCongTrinh.map((c) => <option key={c.id} value={c.id}>{c.tenCongTrinh}</option>)}
          </select>
        </div>

        {loi && <div className="mb-4 text-sm bg-red-50 text-red-700 border border-red-200 rounded px-3 py-2">{loi}</div>}
        {thongBao && <div className="mb-4 text-sm bg-green-50 text-green-700 border border-green-200 rounded px-3 py-2">{thongBao}</div>}

        {!congTrinhId ? (
          <p className="text-center text-blueprint-900/40 mt-10">Chọn công trình để bắt đầu chấm công.</p>
        ) : thoDangChon ? (
          <div className="bg-white border border-concrete-200 rounded-lg p-6">
            <button onClick={quayLaiLuoi} className="text-sm text-blueprint-500 font-semibold mb-4">← Quay lại danh sách</button>
            <h3 className="font-display font-bold text-lg text-blueprint-900 mb-1">{thoDangChon.hoTen}</h3>
            {thoDangChon.chamCongNoiKhacHomNay?.length > 0 ? (
              <p className="text-xs text-blueprint-500 mb-4">Hôm nay đã có: {thoDangChon.chamCongNoiKhacHomNay.map(moTaCham).join(', ')}</p>
            ) : (<div className="mb-3" />)}

            {!thoDangChon.daThietLapKhuonMat ? (
              <>
                <p className="text-sm text-blueprint-900/70 mb-4">{thoDangChon.hoTen} chưa thiết lập khuôn mặt. Đưa mặt vào khung hình rõ ràng, đủ sáng rồi chụp.</p>
                <CameraCapture onCapture={handleThietLapKhuonMat} nhan="Thiết lập khuôn mặt" />
              </>
            ) : (
              <>
                <div className="mb-3">
                  <label className="block text-sm font-medium text-blueprint-900 mb-1">Loại công</label>
                  <div className="grid grid-cols-2 gap-2">
                    {LOAI_CONG_OPTIONS.map((o) => (
                      <button key={o.value} type="button" onClick={() => setLoaiCong(o.value)}
                        className={`text-sm py-2 rounded border font-semibold transition-colors ${loaiCong === o.value ? 'bg-blueprint-900 text-white border-blueprint-900' : 'border-concrete-200 text-blueprint-900/70'}`}>
                        {o.label}
                      </button>
                    ))}
                  </div>
                </div>
                <label className="flex items-center gap-2 mb-4 bg-blueprint-100/40 rounded p-3 cursor-pointer">
                  <input type="checkbox" checked={coTangCa} onChange={(e) => setCoTangCa(e.target.checked)} className="w-4 h-4" />
                  <span className="text-sm font-medium text-blueprint-900">Có tăng ca hôm nay (+{phanTramTangCa}% tiền công)</span>
                </label>
                <CameraCapture onCapture={handleChamCong} nhan={`Quét mặt & chấm công cho ${thoDangChon.hoTen}`} />
                {dangGui && <p className="text-xs text-blueprint-900/40 mt-2">Đang xử lý...</p>}
              </>
            )}
          </div>
        ) : dangTai ? (
          <p className="text-center text-blueprint-900/50 mt-10">Đang tải...</p>
        ) : danhSachTrangThai.length === 0 ? (
          <div className="bg-white border border-dashed border-concrete-200 rounded-lg p-10 text-center text-blueprint-900/50">
            Công trình này chưa có thợ nào được phân công. Vào trang Công trình để gán thợ trước.
          </div>
        ) : (
          <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
            {danhSachTrangThai.map((tho) => (
              <button key={tho.nguoiDungId} onClick={() => chonTho(tho)} disabled={tho.daChamCongTaiDay}
                className={`text-left bg-white border rounded-lg p-4 transition-colors ${tho.daChamCongTaiDay ? 'border-green-200 bg-green-50 cursor-default' : 'border-concrete-200 hover:border-blueprint-500 cursor-pointer'}`}>
                <p className="font-semibold text-blueprint-900">{tho.hoTen}</p>
                {tho.daChamCongTaiDay ? (
                  <p className="text-xs text-green-700 font-semibold mt-1">
                    ✅ {nhanLoaiCong(tho.chamCongTaiDay?.loaiCong)}{tho.chamCongTaiDay?.coTangCa && ' + tăng ca'}
                    {tho.chamCongTaiDay?.gioCheckIn && ` · lúc ${new Date(tho.chamCongTaiDay.gioCheckIn).toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' })}`}
                  </p>
                ) : !tho.daThietLapKhuonMat ? (
                  <p className="text-xs text-amber-600 font-semibold mt-1">⚠ Chưa thiết lập khuôn mặt</p>
                ) : (
                  <p className="text-xs text-blueprint-900/40 mt-1">⭕ Chưa chấm công — bấm để quét mặt</p>
                )}
                {tho.chamCongNoiKhacHomNay?.length > 0 && (
                  <p className="text-[11px] text-blueprint-500 mt-1.5 pt-1.5 border-t border-concrete-200">
                    Hôm nay đã có: {tho.chamCongNoiKhacHomNay.map(moTaCham).join(', ')}
                  </p>
                )}
              </button>
            ))}
          </div>
        )}
      </main>
    </div>
  );
}
