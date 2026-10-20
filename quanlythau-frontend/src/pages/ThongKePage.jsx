import { useEffect, useState } from 'react';
import * as XLSX from 'xlsx';
import { Bar, BarChart, CartesianGrid, Legend, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import axiosClient from '../api/axiosClient';
import Navbar from '../components/Navbar';

const formatVND = (value) => {
  if (value === null || value === undefined) return '—';
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
};
const homNayISO = () => new Date().toISOString().slice(0, 10);
const dauThangISO = () => { const d = new Date(); return new Date(d.getFullYear(), d.getMonth(), 1).toISOString().slice(0, 10); };

function xuatExcel(rows, tenSheet, tenFile) {
  const ws = XLSX.utils.json_to_sheet(rows);
  const wb = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(wb, ws, tenSheet);
  XLSX.writeFile(wb, tenFile);
}

export default function ThongKePage() {
  const [danhSachLaiLo, setDanhSachLaiLo] = useState([]);
  const [dangTaiLaiLo, setDangTaiLaiLo] = useState(true);
  const [loi, setLoi] = useState('');

  const [tuNgay, setTuNgay] = useState(dauThangISO());
  const [denNgay, setDenNgay] = useState(homNayISO());
  const [baoCaoLuong, setBaoCaoLuong] = useState(null);
  const [dangTinhLuong, setDangTinhLuong] = useState(false);

  useEffect(() => {
    axiosClient.get('/lai-lo/tat-ca').then((res) => setDanhSachLaiLo(res.data)).catch(() => setLoi('Không tải được dữ liệu chi phí')).finally(() => setDangTaiLaiLo(false));
  }, []);

  const xemBaoCaoLuong = async () => {
    setDangTinhLuong(true);
    setLoi('');
    try {
      const res = await axiosClient.get('/luong/xem-truoc-tat-ca', { params: { tuNgay, denNgay } });
      setBaoCaoLuong(res.data);
    } catch (err) {
      setLoi(err.response?.data?.message || 'Tính báo cáo lương thất bại');
    } finally {
      setDangTinhLuong(false);
    }
  };

  const soCongTrinhDangThiCong = danhSachLaiLo.filter((c) => c.trangThaiCongTrinh === 'DANG_THI_CONG').length;
  const tongDoanhThu = danhSachLaiLo.reduce((tong, c) => tong + (c.giaTriHopDong || 0), 0);
  const tongChiPhi = danhSachLaiLo.reduce((tong, c) => tong + (c.tongChiPhi || 0), 0);
  const tongLaiLo = danhSachLaiLo.reduce((tong, c) => tong + (c.laiLoTamTinh || 0), 0);

  const duLieuBieuDo = danhSachLaiLo.map((c) => ({
    ten: c.tenCongTrinh.length > 12 ? c.tenCongTrinh.slice(0, 12) + '…' : c.tenCongTrinh,
    'Nhân công': c.chiPhiNhanCong,
    'Vật tư': c.chiPhiVatTu,
    'Chi phí khác': c.chiPhiKhac,
  }));

  const xuatExcelChiPhi = () => {
    xuatExcel(
      danhSachLaiLo.map((c) => ({
        'Công trình': c.tenCongTrinh,
        'Giá trị hợp đồng': c.giaTriHopDong,
        'Chi phí nhân công': c.chiPhiNhanCong,
        'Chi phí vật tư': c.chiPhiVatTu,
        'Chi phí khác': c.chiPhiKhac,
        'Tổng chi phí': c.tongChiPhi,
        'Lãi/lỗ tạm tính': c.laiLoTamTinh,
        '% đã chi': c.phanTramDaChi,
      })),
      'Chi phí theo công trình',
      'chi-phi-cong-trinh.xlsx'
    );
  };

  const xuatExcelLuong = () => {
    if (!baoCaoLuong) return;
    xuatExcel(
      baoCaoLuong.map((b) => ({
        'Thợ': b.hoTenTho,
        'Từ ngày': b.tuNgay,
        'Đến ngày': b.denNgay,
        'Tổng công': b.tongCong,
        'Tiền cơ bản': b.tienCoBan,
        'Tiền tăng ca': b.tienTangCa,
        'Tổng tiền công': b.tongTienCong,
        'Tạm ứng': b.tongTamUng,
        'Thực nhận': b.thucNhan,
      })),
      'Báo cáo lương',
      `bao-cao-luong-${tuNgay}-den-${denNgay}.xlsx`
    );
  };

  return (
    <div className="min-h-screen bg-concrete-50">
      <Navbar />
      <main className="max-w-5xl mx-auto px-6 py-8">
        <h2 className="font-display font-extrabold text-xl text-blueprint-900 mb-6">Thống kê và báo cáo</h2>
        {loi && <div className="mb-4 text-sm bg-red-50 text-red-700 border border-red-200 rounded px-3 py-2">{loi}</div>}

        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
          <div className="bg-white border border-concrete-200 rounded-lg p-4">
            <p className="text-xs text-blueprint-900/50">Công trình đang thi công</p>
            <p className="font-display font-extrabold text-2xl text-blueprint-900">{soCongTrinhDangThiCong}</p>
          </div>
          <div className="bg-white border border-concrete-200 rounded-lg p-4">
            <p className="text-xs text-blueprint-900/50">Tổng doanh thu</p>
            <p className="font-display font-extrabold text-xl text-blueprint-900">{formatVND(tongDoanhThu)}</p>
          </div>
          <div className="bg-white border border-concrete-200 rounded-lg p-4">
            <p className="text-xs text-blueprint-900/50">Tổng chi phí</p>
            <p className="font-display font-extrabold text-xl text-blueprint-900">{formatVND(tongChiPhi)}</p>
          </div>
          <div className="bg-white border border-concrete-200 rounded-lg p-4">
            <p className="text-xs text-blueprint-900/50">Lợi nhuận tạm tính</p>
            <p className={`font-display font-extrabold text-xl ${tongLaiLo >= 0 ? 'text-green-700' : 'text-red-600'}`}>{formatVND(tongLaiLo)}</p>
          </div>
        </div>

        <div className="bg-white border border-concrete-200 rounded-lg p-5 mb-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="font-display font-bold text-blueprint-900">Chi phí theo công trình</h3>
            <button onClick={xuatExcelChiPhi} disabled={danhSachLaiLo.length === 0}
              className="text-sm bg-blueprint-900 hover:bg-blueprint-700 text-white font-semibold px-3 py-1.5 rounded transition-colors disabled:opacity-40">
              ⬇ Xuất Excel
            </button>
          </div>
          {dangTaiLaiLo ? (
            <p className="text-blueprint-900/50 text-sm">Đang tải...</p>
          ) : danhSachLaiLo.length === 0 ? (
            <p className="text-blueprint-900/40 text-sm">Chưa có công trình nào.</p>
          ) : (
            <div style={{ width: '100%', height: 320 }}>
              <ResponsiveContainer>
                <BarChart data={duLieuBieuDo}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="ten" tick={{ fontSize: 12 }} />
                  <YAxis tickFormatter={(v) => `${(v / 1000000).toFixed(0)}tr`} tick={{ fontSize: 12 }} />
                  <Tooltip formatter={(v) => formatVND(v)} />
                  <Legend />
                  <Bar dataKey="Nhân công" stackId="a" fill="#2e5a82" />
                  <Bar dataKey="Vật tư" stackId="a" fill="#e3a008" />
                  <Bar dataKey="Chi phí khác" stackId="a" fill="#94a3b8" />
                </BarChart>
              </ResponsiveContainer>
            </div>
          )}
        </div>

        <div className="bg-white border border-concrete-200 rounded-lg p-5">
          <h3 className="font-display font-bold text-blueprint-900 mb-3">Báo cáo công và lương — tất cả thợ</h3>
          <div className="flex flex-wrap items-end gap-3 mb-4">
            <div><label className="block text-xs font-medium text-blueprint-900/60 mb-1">Từ ngày</label>
              <input type="date" value={tuNgay} onChange={(e) => setTuNgay(e.target.value)} className="border border-concrete-200 rounded px-3 py-2" /></div>
            <div><label className="block text-xs font-medium text-blueprint-900/60 mb-1">Đến ngày</label>
              <input type="date" value={denNgay} onChange={(e) => setDenNgay(e.target.value)} className="border border-concrete-200 rounded px-3 py-2" /></div>
            <button onClick={xemBaoCaoLuong} disabled={dangTinhLuong} className="bg-blueprint-500 hover:bg-blueprint-700 text-white font-semibold px-4 py-2 rounded transition-colors disabled:opacity-50">
              {dangTinhLuong ? 'Đang tính...' : 'Xem báo cáo'}
            </button>
            {baoCaoLuong && (
              <button onClick={xuatExcelLuong} className="text-sm bg-blueprint-900 hover:bg-blueprint-700 text-white font-semibold px-3 py-2 rounded transition-colors">
                ⬇ Xuất Excel
              </button>
            )}
          </div>

          {baoCaoLuong && (
            baoCaoLuong.length === 0 ? (
              <p className="text-sm text-blueprint-900/40">Chưa có thợ nào trong hệ thống.</p>
            ) : (
              <div className="overflow-x-auto">
                <table className="w-full text-sm">
                  <thead>
                    <tr className="text-left text-blueprint-900/50 border-b border-concrete-200">
                      <th className="py-2 pr-3">Thợ</th>
                      <th className="py-2 pr-3">Tổng công</th>
                      <th className="py-2 pr-3">Tiền cơ bản</th>
                      <th className="py-2 pr-3">Tăng ca</th>
                      <th className="py-2 pr-3">Tổng tiền công</th>
                      <th className="py-2 pr-3">Tạm ứng</th>
                      <th className="py-2">Thực nhận</th>
                    </tr>
                  </thead>
                  <tbody>
                    {baoCaoLuong.map((b) => (
                      <tr key={b.nguoiDungId} className="border-b border-concrete-200 last:border-0">
                        <td className="py-2 pr-3 font-semibold text-blueprint-900">{b.hoTenTho}</td>
                        <td className="py-2 pr-3">{b.tongCong}</td>
                        <td className="py-2 pr-3">{formatVND(b.tienCoBan)}</td>
                        <td className="py-2 pr-3">{formatVND(b.tienTangCa)}</td>
                        <td className="py-2 pr-3">{formatVND(b.tongTienCong)}</td>
                        <td className="py-2 pr-3 text-red-600">- {formatVND(b.tongTamUng)}</td>
                        <td className="py-2 font-bold text-green-700">{formatVND(b.thucNhan)}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )
          )}
        </div>
      </main>
    </div>
  );
}
