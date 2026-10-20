import { useEffect, useState } from 'react';
import axiosClient from '../api/axiosClient';
import Navbar from '../components/Navbar';

const formatVND = (value) => {
  if (value === null || value === undefined) return '—';
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
};

export default function LuongCuaToiPage() {
  const [danhSach, setDanhSach] = useState([]);
  const [dangTai, setDangTai] = useState(true);
  const [loi, setLoi] = useState('');

  useEffect(() => {
    axiosClient.get('/luong/cua-toi').then((res) => setDanhSach(res.data)).catch(() => setLoi('Không tải được lịch sử lương')).finally(() => setDangTai(false));
  }, []);

  return (
    <div className="min-h-screen bg-concrete-50">
      <Navbar />
      <main className="max-w-2xl mx-auto px-6 py-8">
        <h2 className="font-display font-extrabold text-xl text-blueprint-900 mb-1">Lương của tôi</h2>
        <p className="text-sm text-blueprint-900/50 mb-6">Các kỳ lương chủ thầu đã chốt.</p>
        {loi && <div className="mb-4 text-sm bg-red-50 text-red-700 border border-red-200 rounded px-3 py-2">{loi}</div>}
        {dangTai ? (
          <p className="text-blueprint-900/50">Đang tải...</p>
        ) : danhSach.length === 0 ? (
          <div className="bg-white border border-dashed border-concrete-200 rounded-lg p-10 text-center text-blueprint-900/50">Chưa có kỳ lương nào được chốt.</div>
        ) : (
          <div className="space-y-3">
            {danhSach.map((b) => (
              <div key={b.id} className="bg-white border border-concrete-200 rounded-lg p-4">
                <div className="flex items-center justify-between mb-2">
                  <p className="font-semibold text-blueprint-900">{b.tuNgay} → {b.denNgay}</p>
                  <span className={`text-xs font-semibold px-2 py-1 rounded ${b.trangThai === 'DA_THANH_TOAN' ? 'bg-green-100 text-green-700' : 'bg-amber-100 text-amber-700'}`}>
                    {b.trangThai === 'DA_THANH_TOAN' ? 'Đã thanh toán' : 'Chưa thanh toán'}
                  </span>
                </div>
                <div className="grid grid-cols-2 gap-2 text-sm text-blueprint-900/70">
                  <p>Tổng công: <span className="font-semibold text-blueprint-900">{b.tongCong}</span></p>
                  <p>Tiền cơ bản: <span className="font-semibold text-blueprint-900">{formatVND(b.tienCoBan)}</span></p>
                  <p>Tiền tăng ca: <span className="font-semibold text-blueprint-900">{formatVND(b.tienTangCa)}</span></p>
                  <p>Tạm ứng: <span className="font-semibold text-red-600">- {formatVND(b.tongTamUng)}</span></p>
                </div>
                <p className="mt-2 pt-2 border-t border-concrete-200 font-bold text-green-700">Thực nhận: {formatVND(b.thucNhan)}</p>
              </div>
            ))}
          </div>
        )}
      </main>
    </div>
  );
}
