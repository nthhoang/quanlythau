import { useEffect, useRef, useState } from 'react';
import axiosClient, { BACKEND_ORIGIN } from '../api/axiosClient';
import Navbar from '../components/Navbar';

const formatVND = (value) => {
  if (value === null || value === undefined) return '—';
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
};
const emptyForm = { tenVatTu: '', soLuong: '', donVi: '', donGia: '', nhaCungCap: '', ngayNhap: new Date().toISOString().slice(0, 10), ghiChu: '' };

export default function QuanLyVatTuPage() {
  const [danhSachCongTrinh, setDanhSachCongTrinh] = useState([]);
  const [congTrinhId, setCongTrinhId] = useState('');
  const [danhSachVatTu, setDanhSachVatTu] = useState([]);
  const [tongChiPhi, setTongChiPhi] = useState(0);
  const [dangTai, setDangTai] = useState(false);
  const [hienForm, setHienForm] = useState(false);
  const [form, setForm] = useState(emptyForm);
  const [dangGui, setDangGui] = useState(false);
  const [dangQuet, setDangQuet] = useState(false);
  const [ketQuaOcr, setKetQuaOcr] = useState(null);
  const [dangLuuOcr, setDangLuuOcr] = useState(false);
  const fileInputRef = useRef(null);
  const [dieuChuyenId, setDieuChuyenId] = useState(null);
  const [formDieuChuyen, setFormDieuChuyen] = useState({ congTrinhDichId: '', soLuong: '' });
  const [dangChuyen, setDangChuyen] = useState(false);
  const [loi, setLoi] = useState('');
  const [thongBao, setThongBao] = useState('');

  useEffect(() => {
    axiosClient.get('/cong-trinh').then((res) => setDanhSachCongTrinh(res.data)).catch(() => setLoi('Không tải được danh sách công trình'));
  }, []);

  const taiDuLieu = async (id) => {
    if (!id) return;
    setDangTai(true);
    try {
      const [ds, tong] = await Promise.all([axiosClient.get(`/vat-tu/cong-trinh/${id}`), axiosClient.get(`/vat-tu/cong-trinh/${id}/tong-chi-phi`)]);
      setDanhSachVatTu(ds.data);
      setTongChiPhi(tong.data.tongChiPhi);
      setLoi('');
    } catch { setLoi('Không tải được vật tư của công trình này'); }
    finally { setDangTai(false); }
  };

  useEffect(() => {
    taiDuLieu(congTrinhId);
    setKetQuaOcr(null);
    setHienForm(false);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [congTrinhId]);

  const submitThuCong = async (e) => {
    e.preventDefault();
    setDangGui(true);
    try {
      await axiosClient.post(`/vat-tu/cong-trinh/${congTrinhId}`, { ...form, soLuong: Number(form.soLuong), donGia: form.donGia ? Number(form.donGia) : 0 });
      setForm(emptyForm);
      setHienForm(false);
      await taiDuLieu(congTrinhId);
    } catch (err) { setLoi(err.response?.data?.message || 'Thêm vật tư thất bại'); }
    finally { setDangGui(false); }
  };

  const xoa = async (id) => {
    if (!window.confirm('Xoá dòng vật tư này?')) return;
    try { await axiosClient.delete(`/vat-tu/${id}`); await taiDuLieu(congTrinhId); }
    catch { setLoi('Xoá thất bại'); }
  };

  const chonFileHoaDon = () => fileInputRef.current?.click();

  const handleChonFile = async (e) => {
    const file = e.target.files?.[0];
    if (!file) return;
    setDangQuet(true); setLoi(''); setKetQuaOcr(null);
    try {
      const formData = new FormData();
      formData.append('file', file);
      const res = await axiosClient.post('/vat-tu/quet-hoa-don', formData, { headers: { 'Content-Type': 'multipart/form-data' } });
      if (res.data.danhSachGoiY.length === 0) {
        setThongBao('Không đọc được dòng vật tư nào từ ảnh này, thử ảnh rõ hơn hoặc nhập tay.');
      }
      setKetQuaOcr(res.data);
    } catch (err) { setLoi(err.response?.data?.message || 'Quét hóa đơn thất bại'); }
    finally { setDangQuet(false); e.target.value = ''; }
  };

  const suaDongOcr = (index, field, value) => {
    setKetQuaOcr((truoc) => { const danhSach = [...truoc.danhSachGoiY]; danhSach[index] = { ...danhSach[index], [field]: value }; return { ...truoc, danhSachGoiY: danhSach }; });
  };
  const xoaDongOcr = (index) => {
    setKetQuaOcr((truoc) => ({ ...truoc, danhSachGoiY: truoc.danhSachGoiY.filter((_, i) => i !== index) }));
  };

  const xacNhanLuuOcr = async () => {
    setDangLuuOcr(true);
    try {
      await axiosClient.post('/vat-tu/hang-loat', {
        congTrinhId: Number(congTrinhId), anhHoaDon: ketQuaOcr.anhHoaDon,
        danhSach: ketQuaOcr.danhSachGoiY.map((d) => ({ tenVatTu: d.tenVatTu, soLuong: Number(d.soLuong), donVi: d.donVi, donGia: Number(d.donGia), ngayNhap: new Date().toISOString().slice(0, 10) })),
      });
      setThongBao(`Đã lưu ${ketQuaOcr.danhSachGoiY.length} dòng vật tư từ hóa đơn.`);
      setKetQuaOcr(null);
      await taiDuLieu(congTrinhId);
    } catch (err) { setLoi(err.response?.data?.message || 'Lưu thất bại'); }
    finally { setDangLuuOcr(false); }
  };

  const moDieuChuyen = (id) => { setDieuChuyenId(id); setFormDieuChuyen({ congTrinhDichId: '', soLuong: '' }); };

  const xacNhanDieuChuyen = async (e) => {
    e.preventDefault();
    setDangChuyen(true);
    try {
      await axiosClient.post(`/vat-tu/${dieuChuyenId}/dieu-chuyen`, { congTrinhDichId: Number(formDieuChuyen.congTrinhDichId), soLuong: Number(formDieuChuyen.soLuong) });
      setThongBao('Đã điều chuyển vật tư.');
      setDieuChuyenId(null);
      await taiDuLieu(congTrinhId);
    } catch (err) { setLoi(err.response?.data?.message || 'Điều chuyển thất bại'); }
    finally { setDangChuyen(false); }
  };

  return (
    <div className="min-h-screen bg-concrete-50">
      <Navbar />
      <main className="max-w-5xl mx-auto px-6 py-8">
        <h2 className="font-display font-extrabold text-xl text-blueprint-900 mb-6">Quản lý vật tư</h2>
        {loi && <div className="mb-4 text-sm bg-red-50 text-red-700 border border-red-200 rounded px-3 py-2">{loi}</div>}
        {thongBao && <div className="mb-4 text-sm bg-green-50 text-green-700 border border-green-200 rounded px-3 py-2">{thongBao}</div>}

        <div className="mb-6">
          <label className="block text-sm font-medium text-blueprint-900 mb-1">Công trình</label>
          <select value={congTrinhId} onChange={(e) => setCongTrinhId(e.target.value)} className="w-full border border-concrete-200 rounded px-3 py-2.5 bg-white font-semibold">
            <option value="">-- chọn công trình --</option>
            {danhSachCongTrinh.map((c) => <option key={c.id} value={c.id}>{c.tenCongTrinh}</option>)}
          </select>
        </div>

        {congTrinhId && (
          <>
            <div className="bg-blueprint-900 text-white rounded-lg p-5 mb-6 flex items-center justify-between">
              <span className="font-display font-semibold">Tổng chi phí vật tư công trình này</span>
              <span className="font-display font-extrabold text-xl text-amber-500">{formatVND(tongChiPhi)}</span>
            </div>

            <div className="flex gap-3 mb-6">
              <button onClick={() => setHienForm(!hienForm)} className="bg-blueprint-900 hover:bg-blueprint-700 text-white font-semibold px-4 py-2 rounded transition-colors">
                {hienForm ? 'Đóng lại' : '+ Thêm vật tư'}
              </button>
              <button onClick={chonFileHoaDon} disabled={dangQuet} className="bg-amber-500 hover:bg-amber-600 text-blueprint-900 font-semibold px-4 py-2 rounded transition-colors disabled:opacity-50">
                {dangQuet ? 'Đang đọc ảnh...' : '📷 Quét hóa đơn'}
              </button>
              <input ref={fileInputRef} type="file" accept="image/*" onChange={handleChonFile} className="hidden" />
            </div>

            {hienForm && (
              <form onSubmit={submitThuCong} className="bg-white border border-concrete-200 rounded-lg p-6 mb-6 grid grid-cols-2 gap-3">
                <div className="col-span-2"><label className="block text-sm font-medium text-blueprint-900 mb-1">Tên vật tư</label>
                  <input required value={form.tenVatTu} onChange={(e) => setForm({ ...form, tenVatTu: e.target.value })} className="w-full border border-concrete-200 rounded px-3 py-2" placeholder="VD: Xi măng Hà Tiên" /></div>
                <div><label className="block text-sm font-medium text-blueprint-900 mb-1">Số lượng</label>
                  <input type="number" required value={form.soLuong} onChange={(e) => setForm({ ...form, soLuong: e.target.value })} className="w-full border border-concrete-200 rounded px-3 py-2" /></div>
                <div><label className="block text-sm font-medium text-blueprint-900 mb-1">Đơn vị</label>
                  <input value={form.donVi} onChange={(e) => setForm({ ...form, donVi: e.target.value })} className="w-full border border-concrete-200 rounded px-3 py-2" placeholder="bao, kg, m3..." /></div>
                <div><label className="block text-sm font-medium text-blueprint-900 mb-1">Đơn giá (VNĐ)</label>
                  <input type="number" value={form.donGia} onChange={(e) => setForm({ ...form, donGia: e.target.value })} className="w-full border border-concrete-200 rounded px-3 py-2" /></div>
                <div><label className="block text-sm font-medium text-blueprint-900 mb-1">Ngày nhập</label>
                  <input type="date" value={form.ngayNhap} onChange={(e) => setForm({ ...form, ngayNhap: e.target.value })} className="w-full border border-concrete-200 rounded px-3 py-2" /></div>
                <div className="col-span-2"><label className="block text-sm font-medium text-blueprint-900 mb-1">Nhà cung cấp</label>
                  <input value={form.nhaCungCap} onChange={(e) => setForm({ ...form, nhaCungCap: e.target.value })} className="w-full border border-concrete-200 rounded px-3 py-2" /></div>
                <div className="col-span-2">
                  <button type="submit" disabled={dangGui} className="bg-blueprint-900 hover:bg-blueprint-700 text-white font-semibold px-5 py-2.5 rounded transition-colors disabled:opacity-50">
                    {dangGui ? 'Đang lưu...' : 'Lưu vật tư'}
                  </button>
                </div>
              </form>
            )}

            {ketQuaOcr && (
              <div className="bg-white border border-amber-300 rounded-lg p-6 mb-6">
                <h3 className="font-display font-bold text-blueprint-900 mb-1">Kết quả đọc từ hóa đơn — xem lại trước khi lưu</h3>
                <p className="text-xs text-blueprint-900/50 mb-4">AI có thể đọc sai, hãy kiểm tra và sửa lại các dòng dưới đây.</p>
                {ketQuaOcr.danhSachGoiY.map((d, i) => (
                  <div key={i} className="grid grid-cols-12 gap-2 mb-2 items-center">
                    <input value={d.tenVatTu} onChange={(e) => suaDongOcr(i, 'tenVatTu', e.target.value)} className="col-span-4 border border-concrete-200 rounded px-2 py-1.5 text-sm" placeholder="Tên vật tư" />
                    <input type="number" value={d.soLuong} onChange={(e) => suaDongOcr(i, 'soLuong', e.target.value)} className="col-span-2 border border-concrete-200 rounded px-2 py-1.5 text-sm" placeholder="SL" />
                    <input value={d.donVi} onChange={(e) => suaDongOcr(i, 'donVi', e.target.value)} className="col-span-2 border border-concrete-200 rounded px-2 py-1.5 text-sm" placeholder="Đơn vị" />
                    <input type="number" value={d.donGia} onChange={(e) => suaDongOcr(i, 'donGia', e.target.value)} className="col-span-3 border border-concrete-200 rounded px-2 py-1.5 text-sm" placeholder="Đơn giá" />
                    <button onClick={() => xoaDongOcr(i)} className="col-span-1 text-red-500 hover:text-red-700 text-xs font-semibold">Xoá</button>
                  </div>
                ))}
                <div className="flex gap-3 mt-4">
                  <button onClick={xacNhanLuuOcr} disabled={dangLuuOcr || ketQuaOcr.danhSachGoiY.length === 0} className="bg-amber-500 hover:bg-amber-600 text-blueprint-900 font-bold px-5 py-2.5 rounded transition-colors disabled:opacity-50">
                    {dangLuuOcr ? 'Đang lưu...' : `✓ Xác nhận lưu ${ketQuaOcr.danhSachGoiY.length} dòng`}
                  </button>
                  <button onClick={() => setKetQuaOcr(null)} className="text-sm text-blueprint-900/50 font-semibold">Huỷ</button>
                </div>
              </div>
            )}

            {dangTai ? (
              <p className="text-blueprint-900/50">Đang tải...</p>
            ) : danhSachVatTu.length === 0 ? (
              <div className="bg-white border border-dashed border-concrete-200 rounded-lg p-10 text-center text-blueprint-900/50">Chưa có vật tư nào cho công trình này.</div>
            ) : (
              <div className="space-y-2">
                {danhSachVatTu.map((v) => (
                  <div key={v.id} className="bg-white border border-concrete-200 rounded-lg p-4">
                    <div className="flex items-start justify-between gap-4">
                      {v.anhHoaDon && (
                        <a href={BACKEND_ORIGIN + v.anhHoaDon} target="_blank" rel="noreferrer">
                          <img src={BACKEND_ORIGIN + v.anhHoaDon} alt="Hóa đơn" className="w-12 h-12 object-cover rounded border border-concrete-200" />
                        </a>
                      )}
                      <div className="flex-1">
                        <p className="font-semibold text-blueprint-900">{v.tenVatTu}</p>
                        <p className="text-sm text-blueprint-900/60">{v.soLuong} {v.donVi} × {formatVND(v.donGia)} {v.nhaCungCap && `· ${v.nhaCungCap}`}</p>
                        {v.ngayNhap && <p className="text-xs text-blueprint-900/40">{v.ngayNhap}</p>}
                      </div>
                      <div className="text-right">
                        <p className="font-bold text-blueprint-900">{formatVND(v.thanhTien)}</p>
                        <div className="flex gap-2 mt-1 justify-end">
                          <button onClick={() => moDieuChuyen(v.id)} className="text-xs text-blueprint-500 font-semibold hover:underline">Chuyển</button>
                          <button onClick={() => xoa(v.id)} className="text-xs text-red-500 font-semibold hover:underline">Xoá</button>
                        </div>
                      </div>
                    </div>
                    {dieuChuyenId === v.id && (
                      <form onSubmit={xacNhanDieuChuyen} className="mt-3 pt-3 border-t border-concrete-200 flex flex-wrap items-end gap-2">
                        <div><label className="block text-xs text-blueprint-900/60 mb-1">Chuyển đến công trình</label>
                          <select required value={formDieuChuyen.congTrinhDichId} onChange={(e) => setFormDieuChuyen({ ...formDieuChuyen, congTrinhDichId: e.target.value })} className="border border-concrete-200 rounded px-2 py-1.5 text-sm bg-white">
                            <option value="">-- chọn --</option>
                            {danhSachCongTrinh.filter((c) => c.id !== v.congTrinhId).map((c) => <option key={c.id} value={c.id}>{c.tenCongTrinh}</option>)}
                          </select>
                        </div>
                        <div><label className="block text-xs text-blueprint-900/60 mb-1">Số lượng (tối đa {v.soLuong})</label>
                          <input type="number" required max={v.soLuong} min="0.01" step="0.01" value={formDieuChuyen.soLuong} onChange={(e) => setFormDieuChuyen({ ...formDieuChuyen, soLuong: e.target.value })} className="w-28 border border-concrete-200 rounded px-2 py-1.5 text-sm" /></div>
                        <button type="submit" disabled={dangChuyen} className="text-sm bg-blueprint-900 hover:bg-blueprint-700 text-white font-semibold px-3 py-1.5 rounded disabled:opacity-50">
                          {dangChuyen ? 'Đang chuyển...' : 'Xác nhận'}
                        </button>
                        <button type="button" onClick={() => setDieuChuyenId(null)} className="text-sm text-blueprint-900/40">Huỷ</button>
                      </form>
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
