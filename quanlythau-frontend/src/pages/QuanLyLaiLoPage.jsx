import { useEffect, useState } from 'react';
import axiosClient from '../api/axiosClient';
import Navbar from '../components/Navbar';

const formatVND = (value) => {
  if (value === null || value === undefined) return '—';
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
};
const emptyChiPhi = { loaiChiPhi: '', soTien: '', ngay: new Date().toISOString().slice(0, 10), ghiChu: '' };

export default function QuanLyLaiLoPage() {
  const [danhSach, setDanhSach] = useState([]);
  const [dangTai, setDangTai] = useState(true);
  const [loi, setLoi] = useState('');
  const [thongBao, setThongBao] = useState('');
  const [phanTramTangCa, setPhanTramTangCa] = useState(30);
  const [nguongCanhBao, setNguongCanhBao] = useState('');
  const [dangLuuCaiDat, setDangLuuCaiDat] = useState(false);
  const [moRongId, setMoRongId] = useState(null);
  const [chiTietChiPhiKhac, setChiTietChiPhiKhac] = useState({});
  const [formChiPhi, setFormChiPhi] = useState(emptyChiPhi);
  const [dangGuiChiPhi, setDangGuiChiPhi] = useState(false);

  const taiDanhSach = async () => {
    setDangTai(true);
    try { const res = await axiosClient.get('/lai-lo/tat-ca'); setDanhSach(res.data); setLoi(''); }
    catch { setLoi('Không tải được dữ liệu lãi/lỗ'); }
    finally { setDangTai(false); }
  };

  useEffect(() => {
    axiosClient.get('/cai-dat').then((res) => { setPhanTramTangCa(res.data.phanTramTangCa); setNguongCanhBao(res.data.nguongCanhBaoChiPhi); }).catch(() => {});
    taiDanhSach();
  }, []);

  const luuCaiDat = async () => {
    setDangLuuCaiDat(true);
    try {
      await axiosClient.put('/cai-dat', { phanTramTangCa: Number(phanTramTangCa), nguongCanhBaoChiPhi: Number(nguongCanhBao) });
      setThongBao('Đã lưu ngưỡng cảnh báo.');
      await taiDanhSach();
    } catch (err) { setLoi(err.response?.data?.message || 'Lưu cài đặt thất bại'); }
    finally { setDangLuuCaiDat(false); }
  };

  const taiChiPhiKhac = async (congTrinhId) => {
    try { const res = await axiosClient.get(`/chi-phi-khac/cong-trinh/${congTrinhId}`); setChiTietChiPhiKhac((truoc) => ({ ...truoc, [congTrinhId]: res.data })); }
    catch { setLoi('Không tải được chi tiết chi phí khác'); }
  };

  const toggleMoRong = (congTrinhId) => {
    if (moRongId === congTrinhId) { setMoRongId(null); return; }
    setMoRongId(congTrinhId);
    setFormChiPhi(emptyChiPhi);
    if (!chiTietChiPhiKhac[congTrinhId]) taiChiPhiKhac(congTrinhId);
  };

  const themChiPhi = async (e, congTrinhId) => {
    e.preventDefault();
    setDangGuiChiPhi(true);
    try {
      await axiosClient.post(`/chi-phi-khac/cong-trinh/${congTrinhId}`, { ...formChiPhi, soTien: Number(formChiPhi.soTien) });
      setFormChiPhi(emptyChiPhi);
      await taiChiPhiKhac(congTrinhId);
      await taiDanhSach();
    } catch (err) { setLoi(err.response?.data?.message || 'Thêm chi phí thất bại'); }
    finally { setDangGuiChiPhi(false); }
  };

  const xoaChiPhi = async (id, congTrinhId) => {
    if (!window.confirm('Xoá khoản chi phí này?')) return;
    try { await axiosClient.delete(`/chi-phi-khac/${id}`); await taiChiPhiKhac(congTrinhId); await taiDanhSach(); }
    catch { setLoi('Xoá thất bại'); }
  };

  return (
    <div className="min-h-screen bg-concrete-50">
      <Navbar />
      <main className="max-w-5xl mx-auto px-6 py-8">
        <h2 className="font-display font-extrabold text-xl text-blueprint-900 mb-6">Chi phí và lãi/lỗ</h2>
        {loi && <div className="mb-4 text-sm bg-red-50 text-red-700 border border-red-200 rounded px-3 py-2">{loi}</div>}
        {thongBao && <div className="mb-4 text-sm bg-green-50 text-green-700 border border-green-200 rounded px-3 py-2">{thongBao}</div>}

        <div className="bg-white border border-concrete-200 rounded-lg p-5 mb-6 flex items-end gap-3">
          <div>
            <label className="block text-sm font-medium text-blueprint-900 mb-1">Cảnh báo khi chi phí đã chi tới</label>
            <div className="flex items-center gap-2">
              <input type="number" min="1" max="100" value={nguongCanhBao} onChange={(e) => setNguongCanhBao(e.target.value)} className="w-24 border border-concrete-200 rounded px-3 py-2" />
              <span className="text-blueprint-900/60">% giá trị hợp đồng</span>
            </div>
          </div>
          <button onClick={luuCaiDat} disabled={dangLuuCaiDat} className="bg-blueprint-900 hover:bg-blueprint-700 text-white font-semibold px-4 py-2 rounded transition-colors disabled:opacity-50">
            {dangLuuCaiDat ? 'Đang lưu...' : 'Lưu'}
          </button>
        </div>

        {dangTai ? (
          <p className="text-blueprint-900/50">Đang tải...</p>
        ) : danhSach.length === 0 ? (
          <div className="bg-white border border-dashed border-concrete-200 rounded-lg p-10 text-center text-blueprint-900/50">Chưa có công trình nào.</div>
        ) : (
          <div className="space-y-3">
            {danhSach.map((c) => (
              <div key={c.congTrinhId} className={`bg-white border rounded-lg p-5 ${c.vuotNguongCanhBao ? 'border-red-300' : 'border-concrete-200'}`}>
                <div className="flex items-center justify-between mb-2">
                  <h3 className="font-display font-bold text-blueprint-900">{c.tenCongTrinh}</h3>
                  {c.vuotNguongCanhBao && <span className="text-xs font-bold text-red-700 bg-red-100 px-2 py-1 rounded">⚠ Đã chi {c.phanTramDaChi}% hợp đồng</span>}
                </div>
                <div className="grid grid-cols-2 md:grid-cols-4 gap-3 text-sm mb-3">
                  <div><p className="text-blueprint-900/50">Giá trị hợp đồng</p><p className="font-semibold text-blueprint-900">{formatVND(c.giaTriHopDong)}</p></div>
                  <div><p className="text-blueprint-900/50">Tổng chi phí</p><p className="font-semibold text-blueprint-900">{formatVND(c.tongChiPhi)}</p></div>
                  <div><p className="text-blueprint-900/50">Lãi/lỗ tạm tính</p>
                    <p className={`font-bold ${c.laiLoTamTinh >= 0 ? 'text-green-700' : 'text-red-600'}`}>{c.laiLoTamTinh == null ? '—' : formatVND(c.laiLoTamTinh)}</p></div>
                  <div><p className="text-blueprint-900/50">% đã chi</p><p className="font-semibold text-blueprint-900">{c.phanTramDaChi == null ? '—' : `${c.phanTramDaChi}%`}</p></div>
                </div>
                <button onClick={() => toggleMoRong(c.congTrinhId)} className="text-xs font-semibold text-blueprint-500 hover:text-blueprint-900">
                  Chi tiết {moRongId === c.congTrinhId ? '▲' : '▼'}
                </button>
                {moRongId === c.congTrinhId && (
                  <div className="mt-4 pt-4 border-t border-concrete-200">
                    <div className="grid grid-cols-3 gap-3 text-sm mb-4">
                      <div className="bg-concrete-50 rounded p-3"><p className="text-blueprint-900/50 text-xs">Nhân công</p><p className="font-semibold text-blueprint-900">{formatVND(c.chiPhiNhanCong)}</p></div>
                      <div className="bg-concrete-50 rounded p-3"><p className="text-blueprint-900/50 text-xs">Vật tư</p><p className="font-semibold text-blueprint-900">{formatVND(c.chiPhiVatTu)}</p></div>
                      <div className="bg-concrete-50 rounded p-3"><p className="text-blueprint-900/50 text-xs">Chi phí khác</p><p className="font-semibold text-blueprint-900">{formatVND(c.chiPhiKhac)}</p></div>
                    </div>
                    <h4 className="text-sm font-semibold text-blueprint-900 mb-2">Chi phí khác (thuê máy móc, vận chuyển...)</h4>
                    {(chiTietChiPhiKhac[c.congTrinhId] || []).map((cp) => (
                      <div key={cp.id} className="flex items-center justify-between text-sm bg-concrete-50 rounded px-3 py-2 mb-1.5">
                        <span>{cp.ngay} · {cp.loaiChiPhi} · {formatVND(cp.soTien)} {cp.ghiChu && `— ${cp.ghiChu}`}</span>
                        <button onClick={() => xoaChiPhi(cp.id, c.congTrinhId)} className="text-xs text-red-500 hover:text-red-700 font-semibold">Xoá</button>
                      </div>
                    ))}
                    <form onSubmit={(e) => themChiPhi(e, c.congTrinhId)} className="flex flex-wrap items-end gap-2 mt-3">
                      <input required placeholder="Loại chi phí (VD: thuê máy trộn)" value={formChiPhi.loaiChiPhi} onChange={(e) => setFormChiPhi({ ...formChiPhi, loaiChiPhi: e.target.value })} className="flex-1 min-w-[160px] border border-concrete-200 rounded px-2 py-1.5 text-sm" />
                      <input required type="number" min="1" placeholder="Số tiền" value={formChiPhi.soTien} onChange={(e) => setFormChiPhi({ ...formChiPhi, soTien: e.target.value })} className="w-28 border border-concrete-200 rounded px-2 py-1.5 text-sm" />
                      <input type="date" value={formChiPhi.ngay} onChange={(e) => setFormChiPhi({ ...formChiPhi, ngay: e.target.value })} className="border border-concrete-200 rounded px-2 py-1.5 text-sm" />
                      <button type="submit" disabled={dangGuiChiPhi} className="text-sm bg-blueprint-900 hover:bg-blueprint-700 text-white font-semibold px-3 py-1.5 rounded disabled:opacity-50">
                        {dangGuiChiPhi ? 'Đang lưu...' : '+ Thêm'}
                      </button>
                    </form>
                  </div>
                )}
              </div>
            ))}
          </div>
        )}
      </main>
    </div>
  );
}
