import { useEffect, useState } from 'react';
import axiosClient from '../api/axiosClient';
import Navbar from '../components/Navbar';

const formatVND = (value) => {
  if (value === null || value === undefined) return '—';
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
};
const homNayISO = () => new Date().toISOString().slice(0, 10);
const dauThangISO = () => { const d = new Date(); return new Date(d.getFullYear(), d.getMonth(), 1).toISOString().slice(0, 10); };
const emptyTamUng = { soTien: '', ngayUng: homNayISO(), ghiChu: '' };

export default function QuanLyLuongPage() {
  const [phanTramTangCa, setPhanTramTangCa] = useState('');
  const [nguongCanhBaoChiPhi, setNguongCanhBaoChiPhi] = useState(80);
  const [dangLuuCaiDat, setDangLuuCaiDat] = useState(false);
  const [danhSachTho, setDanhSachTho] = useState([]);
  const [thoDangChonId, setThoDangChonId] = useState('');
  const [donGiaMoi, setDonGiaMoi] = useState('');
  const [dangLuuDonGia, setDangLuuDonGia] = useState(false);
  const [tuNgay, setTuNgay] = useState(dauThangISO());
  const [denNgay, setDenNgay] = useState(homNayISO());
  const [xemTruoc, setXemTruoc] = useState(null);
  const [dangTinh, setDangTinh] = useState(false);
  const [dangChot, setDangChot] = useState(false);
  const [danhSachTamUng, setDanhSachTamUng] = useState([]);
  const [formTamUng, setFormTamUng] = useState(emptyTamUng);
  const [dangGuiTamUng, setDangGuiTamUng] = useState(false);
  const [lichSuLuong, setLichSuLuong] = useState([]);
  const [loi, setLoi] = useState('');
  const [thongBao, setThongBao] = useState('');

  const thoDangChon = danhSachTho.find((t) => String(t.id) === String(thoDangChonId));

  useEffect(() => {
    axiosClient.get('/cai-dat').then((res) => {
      setPhanTramTangCa(res.data.phanTramTangCa);
      setNguongCanhBaoChiPhi(res.data.nguongCanhBaoChiPhi);
    }).catch(() => {});
    axiosClient.get('/nguoi-dung/tho').then((res) => setDanhSachTho(res.data)).catch(() => setLoi('Không tải được danh sách thợ'));
  }, []);

  useEffect(() => {
    if (!thoDangChon) return;
    setDonGiaMoi(thoDangChon.donGiaCong ?? '');
    setXemTruoc(null);
    taiTamUng(thoDangChon.id);
    taiLichSuLuong(thoDangChon.id);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [thoDangChonId]);

  const taiTamUng = async (nguoiDungId) => {
    try { const res = await axiosClient.get(`/tam-ung/tho/${nguoiDungId}`); setDanhSachTamUng(res.data); }
    catch { setLoi('Không tải được danh sách tạm ứng'); }
  };
  const taiLichSuLuong = async (nguoiDungId) => {
    try { const res = await axiosClient.get(`/luong/lich-su/${nguoiDungId}`); setLichSuLuong(res.data); }
    catch { setLoi('Không tải được lịch sử lương'); }
  };

  const luuCaiDat = async () => {
    setDangLuuCaiDat(true);
    try {
      await axiosClient.put('/cai-dat', { phanTramTangCa: Number(phanTramTangCa), nguongCanhBaoChiPhi: Number(nguongCanhBaoChiPhi) });
      setThongBao('Đã lưu % tăng ca. Áp dụng cho các lượt chấm công từ giờ trở đi.');
    } catch (err) { setLoi(err.response?.data?.message || 'Lưu cài đặt thất bại'); }
    finally { setDangLuuCaiDat(false); }
  };

  const luuDonGia = async () => {
    setDangLuuDonGia(true);
    try {
      const res = await axiosClient.patch(`/nguoi-dung/${thoDangChonId}/don-gia-cong`, { donGiaCong: Number(donGiaMoi) });
      setDanhSachTho((ds) => ds.map((t) => (t.id === res.data.id ? res.data : t)));
      setThongBao(`Đã cập nhật đơn giá công cho ${thoDangChon.hoTen}. Chỉ áp dụng cho các lượt chấm công từ giờ trở đi.`);
    } catch (err) { setLoi(err.response?.data?.message || 'Sửa đơn giá công thất bại'); }
    finally { setDangLuuDonGia(false); }
  };

  const xemTruocLuong = async () => {
    setDangTinh(true); setLoi(''); setXemTruoc(null);
    try {
      const res = await axiosClient.get('/luong/xem-truoc', { params: { nguoiDungId: thoDangChonId, tuNgay, denNgay } });
      setXemTruoc(res.data);
    } catch (err) { setLoi(err.response?.data?.message || 'Tính lương thất bại'); }
    finally { setDangTinh(false); }
  };

  const chotLuong = async () => {
    if (!window.confirm(`Chốt lương ${thoDangChon.hoTen} từ ${tuNgay} đến ${denNgay}? Sau khi chốt sẽ không tính lại được.`)) return;
    setDangChot(true);
    try {
      await axiosClient.post('/luong/chot', { nguoiDungId: Number(thoDangChonId), tuNgay, denNgay });
      setThongBao('Đã chốt lương thành công.');
      setXemTruoc(null);
      await taiLichSuLuong(thoDangChonId);
    } catch (err) { setLoi(err.response?.data?.message || 'Chốt lương thất bại'); }
    finally { setDangChot(false); }
  };

  const themTamUng = async (e) => {
    e.preventDefault();
    setDangGuiTamUng(true);
    try {
      await axiosClient.post('/tam-ung', { nguoiDungId: Number(thoDangChonId), soTien: Number(formTamUng.soTien), ngayUng: formTamUng.ngayUng, ghiChu: formTamUng.ghiChu });
      setFormTamUng(emptyTamUng);
      await taiTamUng(thoDangChonId);
    } catch (err) { setLoi(err.response?.data?.message || 'Thêm tạm ứng thất bại'); }
    finally { setDangGuiTamUng(false); }
  };

  const xoaTamUng = async (id) => {
    if (!window.confirm('Xoá khoản tạm ứng này?')) return;
    try { await axiosClient.delete(`/tam-ung/${id}`); await taiTamUng(thoDangChonId); }
    catch { setLoi('Xoá tạm ứng thất bại'); }
  };

  const danhDauDaTra = async (id) => {
    try { await axiosClient.patch(`/luong/${id}/da-thanh-toan`); await taiLichSuLuong(thoDangChonId); }
    catch { setLoi('Cập nhật thất bại'); }
  };

  return (
    <div className="min-h-screen bg-concrete-50">
      <Navbar />
      <main className="max-w-4xl mx-auto px-6 py-8">
        <h2 className="font-display font-extrabold text-xl text-blueprint-900 mb-6">Quản lý lương</h2>
        {loi && <div className="mb-4 text-sm bg-red-50 text-red-700 border border-red-200 rounded px-3 py-2">{loi}</div>}
        {thongBao && <div className="mb-4 text-sm bg-green-50 text-green-700 border border-green-200 rounded px-3 py-2">{thongBao}</div>}

        <div className="bg-white border border-concrete-200 rounded-lg p-5 mb-6 flex items-end gap-3">
          <div>
            <label className="block text-sm font-medium text-blueprint-900 mb-1">% cộng thêm khi tăng ca</label>
            <div className="flex items-center gap-2">
              <input type="number" min="0" value={phanTramTangCa} onChange={(e) => setPhanTramTangCa(e.target.value)} className="w-24 border border-concrete-200 rounded px-3 py-2" />
              <span className="text-blueprint-900/60">%</span>
            </div>
          </div>
          <button onClick={luuCaiDat} disabled={dangLuuCaiDat} className="bg-blueprint-900 hover:bg-blueprint-700 text-white font-semibold px-4 py-2 rounded transition-colors disabled:opacity-50">
            {dangLuuCaiDat ? 'Đang lưu...' : 'Lưu'}
          </button>
          <p className="text-xs text-blueprint-900/40 ml-2">Áp dụng chung cho mọi thợ, mọi lượt tăng ca kể từ lúc lưu — không ảnh hưởng các ngày đã chấm công trước đó.</p>
        </div>

        <div className="mb-6">
          <label className="block text-sm font-medium text-blueprint-900 mb-1">Chọn thợ để xem/tính lương</label>
          <select value={thoDangChonId} onChange={(e) => setThoDangChonId(e.target.value)} className="w-full border border-concrete-200 rounded px-3 py-2.5 bg-white font-semibold">
            <option value="">-- chọn thợ --</option>
            {danhSachTho.map((t) => <option key={t.id} value={t.id}>{t.hoTen}</option>)}
          </select>
        </div>

        {thoDangChon && (
          <>
            <div className="bg-white border border-concrete-200 rounded-lg p-5 mb-6 flex items-end gap-3">
              <div>
                <label className="block text-sm font-medium text-blueprint-900 mb-1">Đơn giá công/ngày của {thoDangChon.hoTen}</label>
                <input type="number" min="0" value={donGiaMoi} onChange={(e) => setDonGiaMoi(e.target.value)} className="w-40 border border-concrete-200 rounded px-3 py-2" />
              </div>
              <button onClick={luuDonGia} disabled={dangLuuDonGia} className="bg-blueprint-900 hover:bg-blueprint-700 text-white font-semibold px-4 py-2 rounded transition-colors disabled:opacity-50">
                {dangLuuDonGia ? 'Đang lưu...' : 'Lưu'}
              </button>
            </div>

            <div className="bg-white border border-concrete-200 rounded-lg p-5 mb-6">
              <h3 className="font-display font-bold text-blueprint-900 mb-3">Tính lương theo kỳ</h3>
              <div className="flex flex-wrap items-end gap-3 mb-4">
                <div><label className="block text-xs font-medium text-blueprint-900/60 mb-1">Từ ngày</label>
                  <input type="date" value={tuNgay} onChange={(e) => setTuNgay(e.target.value)} className="border border-concrete-200 rounded px-3 py-2" /></div>
                <div><label className="block text-xs font-medium text-blueprint-900/60 mb-1">Đến ngày</label>
                  <input type="date" value={denNgay} onChange={(e) => setDenNgay(e.target.value)} className="border border-concrete-200 rounded px-3 py-2" /></div>
                <button onClick={xemTruocLuong} disabled={dangTinh} className="bg-blueprint-500 hover:bg-blueprint-700 text-white font-semibold px-4 py-2 rounded transition-colors disabled:opacity-50">
                  {dangTinh ? 'Đang tính...' : 'Xem trước'}
                </button>
              </div>

              {xemTruoc && (
                <div className="border-t border-concrete-200 pt-4">
                  {xemTruoc.soBanGhiChuaXacNhan > 0 && (
                    <div className="mb-3 text-sm bg-amber-50 text-amber-700 border border-amber-200 rounded px-3 py-2">
                      Có {xemTruoc.soBanGhiChuaXacNhan} bản ghi chấm công trong kỳ đang "chờ duyệt" hoặc "bị từ chối" — chưa tính vào lương.
                      Vào <span className="font-semibold">Quản lý chấm công</span> để duyệt trước nếu cần.
                    </div>
                  )}
                  <div className="grid grid-cols-2 md:grid-cols-3 gap-3 mb-4 text-sm">
                    <div><p className="text-blueprint-900/50">Tổng công</p><p className="font-semibold text-blueprint-900">{xemTruoc.tongCong}</p></div>
                    <div><p className="text-blueprint-900/50">Tiền cơ bản</p><p className="font-semibold text-blueprint-900">{formatVND(xemTruoc.tienCoBan)}</p></div>
                    <div><p className="text-blueprint-900/50">Tiền tăng ca</p><p className="font-semibold text-blueprint-900">{formatVND(xemTruoc.tienTangCa)}</p></div>
                    <div><p className="text-blueprint-900/50">Tổng tiền công</p><p className="font-semibold text-blueprint-900">{formatVND(xemTruoc.tongTienCong)}</p></div>
                    <div><p className="text-blueprint-900/50">Đã tạm ứng</p><p className="font-semibold text-red-600">- {formatVND(xemTruoc.tongTamUng)}</p></div>
                    <div><p className="text-blueprint-900/50">Thực nhận</p><p className="font-bold text-green-700 text-base">{formatVND(xemTruoc.thucNhan)}</p></div>
                  </div>
                  <button onClick={chotLuong} disabled={dangChot} className="bg-amber-500 hover:bg-amber-600 text-blueprint-900 font-bold px-5 py-2.5 rounded transition-colors disabled:opacity-50">
                    {dangChot ? 'Đang chốt...' : '✓ Chốt lương kỳ này'}
                  </button>
                </div>
              )}
            </div>

            <div className="bg-white border border-concrete-200 rounded-lg p-5 mb-6">
              <h3 className="font-display font-bold text-blueprint-900 mb-3">Tạm ứng lương</h3>
              <form onSubmit={themTamUng} className="flex flex-wrap items-end gap-3 mb-4">
                <div><label className="block text-xs font-medium text-blueprint-900/60 mb-1">Số tiền</label>
                  <input type="number" min="1" required value={formTamUng.soTien} onChange={(e) => setFormTamUng({ ...formTamUng, soTien: e.target.value })} className="w-32 border border-concrete-200 rounded px-3 py-2" /></div>
                <div><label className="block text-xs font-medium text-blueprint-900/60 mb-1">Ngày ứng</label>
                  <input type="date" required value={formTamUng.ngayUng} onChange={(e) => setFormTamUng({ ...formTamUng, ngayUng: e.target.value })} className="border border-concrete-200 rounded px-3 py-2" /></div>
                <div className="flex-1 min-w-[140px]"><label className="block text-xs font-medium text-blueprint-900/60 mb-1">Ghi chú</label>
                  <input value={formTamUng.ghiChu} onChange={(e) => setFormTamUng({ ...formTamUng, ghiChu: e.target.value })} className="w-full border border-concrete-200 rounded px-3 py-2" /></div>
                <button type="submit" disabled={dangGuiTamUng} className="bg-blueprint-900 hover:bg-blueprint-700 text-white font-semibold px-4 py-2 rounded transition-colors disabled:opacity-50">
                  {dangGuiTamUng ? 'Đang lưu...' : '+ Thêm'}
                </button>
              </form>
              {danhSachTamUng.length === 0 ? (
                <p className="text-sm text-blueprint-900/40">Chưa có khoản tạm ứng nào.</p>
              ) : (
                <div className="space-y-2">
                  {danhSachTamUng.map((t) => (
                    <div key={t.id} className="flex items-center justify-between text-sm bg-concrete-50 rounded px-3 py-2">
                      <span>{t.ngayUng} · {formatVND(t.soTien)} {t.ghiChu && `— ${t.ghiChu}`}</span>
                      <button onClick={() => xoaTamUng(t.id)} className="text-xs text-red-500 hover:text-red-700 font-semibold">Xoá</button>
                    </div>
                  ))}
                </div>
              )}
            </div>

            <div className="bg-white border border-concrete-200 rounded-lg p-5">
              <h3 className="font-display font-bold text-blueprint-900 mb-3">Lịch sử lương đã chốt</h3>
              {lichSuLuong.length === 0 ? (
                <p className="text-sm text-blueprint-900/40">Chưa có kỳ lương nào được chốt.</p>
              ) : (
                <div className="space-y-2">
                  {lichSuLuong.map((b) => (
                    <div key={b.id} className="flex items-center justify-between text-sm bg-concrete-50 rounded px-3 py-2">
                      <div>
                        <p className="font-semibold text-blueprint-900">{b.tuNgay} → {b.denNgay}</p>
                        <p className="text-blueprint-900/60">{b.tongCong} công · {formatVND(b.tongTienCong)} · thực nhận {formatVND(b.thucNhan)}</p>
                      </div>
                      {b.trangThai === 'DA_THANH_TOAN' ? (
                        <span className="text-xs font-semibold text-green-700 bg-green-100 px-2 py-1 rounded">Đã thanh toán</span>
                      ) : (
                        <button onClick={() => danhDauDaTra(b.id)} className="text-xs bg-blueprint-900 text-white font-semibold px-3 py-1 rounded hover:bg-blueprint-700">Đánh dấu đã trả</button>
                      )}
                    </div>
                  ))}
                </div>
              )}
            </div>
          </>
        )}
      </main>
    </div>
  );
}
