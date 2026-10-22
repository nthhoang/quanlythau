import { useEffect, useState } from 'react';
import axiosClient, { BACKEND_ORIGIN } from '../api/axiosClient';
import Navbar from '../components/Navbar';

const LOAI_CONG_LABEL = { CA_NGAY: 'Cả ngày', NUA_CONG: 'Nửa công' };
const TRANG_THAI_LABEL = {
  TU_DONG_XAC_NHAN: { text: 'Đã xác nhận', mau: 'bg-green-100 text-green-700' },
  CAN_DUYET: { text: 'Chờ duyệt', mau: 'bg-amber-100 text-amber-700' },
  CHU_THAU_TAO: { text: 'Chấm hộ', mau: 'bg-blueprint-100 text-blueprint-700' },
  TU_CHOI: { text: 'Đã từ chối', mau: 'bg-red-100 text-red-700' },
};
const emptyFormThuCong = {
  nguoiDungId: '', congTrinhId: '', ngayChamCong: new Date().toISOString().slice(0, 10),
  loaiCong: 'CA_NGAY', coTangCa: false, ghiChu: '',
};

export default function QuanLyChamCongPage() {
  const [danhSach, setDanhSach] = useState([]);
  const [danhSachTho, setDanhSachTho] = useState([]);
  const [danhSachCongTrinh, setDanhSachCongTrinh] = useState([]);
  const [dangTai, setDangTai] = useState(true);
  const [loi, setLoi] = useState('');
  const [locCongTrinh, setLocCongTrinh] = useState('');
  const [locNgay, setLocNgay] = useState('');
  const [locTrangThai, setLocTrangThai] = useState('');
  const [hienFormThuCong, setHienFormThuCong] = useState(false);
  const [formThuCong, setFormThuCong] = useState(emptyFormThuCong);
  const [dangGui, setDangGui] = useState(false);

  const taiDanhSach = async () => {
    setDangTai(true);
    try {
      const params = {};
      if (locCongTrinh) params.congTrinhId = locCongTrinh;
      if (locNgay) params.ngay = locNgay;
      if (locTrangThai) params.trangThai = locTrangThai;
      const res = await axiosClient.get('/cham-cong', { params });
      setDanhSach(res.data);
      setLoi('');
    } catch {
      setLoi('Không tải được danh sách chấm công');
    } finally {
      setDangTai(false);
    }
  };

  const taiDuLieuGoc = async () => {
    try {
      const [tho, congTrinh] = await Promise.all([axiosClient.get('/nguoi-dung/tho/doi-ngu'), axiosClient.get('/cong-trinh')]);
      setDanhSachTho(tho.data.filter((t) => t.active));
      setDanhSachCongTrinh(congTrinh.data);
    } catch {
      setLoi('Không tải được danh sách thợ/công trình');
    }
  };

  useEffect(() => { taiDuLieuGoc(); // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);
  useEffect(() => { taiDanhSach(); // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [locCongTrinh, locNgay, locTrangThai]);

  const duyet = async (id, dongY) => {
    try { await axiosClient.patch(`/cham-cong/${id}/duyet`, { dongY }); await taiDanhSach(); }
    catch { setLoi('Duyệt thất bại'); }
  };
  const suaLoaiCong = async (id, loaiCongMoi) => {
    try { await axiosClient.patch(`/cham-cong/${id}/loai-cong`, { loaiCong: loaiCongMoi }); await taiDanhSach(); }
    catch { setLoi('Sửa loại công thất bại'); }
  };
  const suaTangCa = async (id, coTangCaMoi) => {
    try { await axiosClient.patch(`/cham-cong/${id}/tang-ca`, { coTangCa: coTangCaMoi }); await taiDanhSach(); }
    catch { setLoi('Sửa tăng ca thất bại'); }
  };
  const xoa = async (id) => {
    if (!window.confirm('Xoá bản ghi chấm công này?')) return;
    try { await axiosClient.delete(`/cham-cong/${id}`); await taiDanhSach(); }
    catch { setLoi('Xoá thất bại'); }
  };
  const submitThuCong = async (e) => {
    e.preventDefault();
    setDangGui(true);
    try {
      await axiosClient.post('/cham-cong/thu-cong', { ...formThuCong, nguoiDungId: Number(formThuCong.nguoiDungId), congTrinhId: Number(formThuCong.congTrinhId) });
      setFormThuCong(emptyFormThuCong);
      setHienFormThuCong(false);
      await taiDanhSach();
    } catch (err) { setLoi(err.response?.data?.message || 'Chấm công thay thất bại'); }
    finally { setDangGui(false); }
  };

  return (
    <div className="min-h-screen bg-concrete-50">
      <Navbar />
      <main className="max-w-5xl mx-auto px-6 py-8">
        <div className="flex items-center justify-between mb-6">
          <h2 className="font-display font-extrabold text-xl text-blueprint-900">Quản lý chấm công</h2>
          <button onClick={() => setHienFormThuCong(!hienFormThuCong)}
            className="bg-amber-500 hover:bg-amber-600 text-blueprint-900 font-semibold px-4 py-2 rounded transition-colors">
            {hienFormThuCong ? 'Đóng lại' : '+ Chấm công thay'}
          </button>
        </div>

        {loi && <div className="mb-4 text-sm bg-red-50 text-red-700 border border-red-200 rounded px-3 py-2">{loi}</div>}

        {hienFormThuCong && (
          <form onSubmit={submitThuCong} className="bg-white border border-concrete-200 rounded-lg p-6 mb-6 grid grid-cols-2 gap-3">
            <div>
              <label className="block text-sm font-medium text-blueprint-900 mb-1">Thợ</label>
              <select required value={formThuCong.nguoiDungId} onChange={(e) => setFormThuCong({ ...formThuCong, nguoiDungId: e.target.value })}
                className="w-full border border-concrete-200 rounded px-3 py-2 bg-white">
                <option value="">{danhSachTho.length ? '-- chọn thợ --' : '-- không có thợ đang hoạt động --'}</option>
                {danhSachTho.map((t) => <option key={t.id} value={t.id}>{t.hoTen}</option>)}
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-blueprint-900 mb-1">Công trình</label>
              <select required value={formThuCong.congTrinhId} onChange={(e) => setFormThuCong({ ...formThuCong, congTrinhId: e.target.value })}
                className="w-full border border-concrete-200 rounded px-3 py-2 bg-white">
                <option value="">-- chọn công trình --</option>
                {danhSachCongTrinh.map((c) => <option key={c.id} value={c.id}>{c.tenCongTrinh}</option>)}
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-blueprint-900 mb-1">Ngày</label>
              <input type="date" required value={formThuCong.ngayChamCong} onChange={(e) => setFormThuCong({ ...formThuCong, ngayChamCong: e.target.value })}
                className="w-full border border-concrete-200 rounded px-3 py-2" />
            </div>
            <div>
              <label className="block text-sm font-medium text-blueprint-900 mb-1">Loại công</label>
              <select value={formThuCong.loaiCong} onChange={(e) => setFormThuCong({ ...formThuCong, loaiCong: e.target.value })}
                className="w-full border border-concrete-200 rounded px-3 py-2 bg-white">
                {Object.entries(LOAI_CONG_LABEL).map(([v, l]) => <option key={v} value={v}>{l}</option>)}
              </select>
            </div>
            <div className="flex items-end">
              <label className="flex items-center gap-2 cursor-pointer">
                <input type="checkbox" checked={formThuCong.coTangCa} onChange={(e) => setFormThuCong({ ...formThuCong, coTangCa: e.target.checked })} className="w-4 h-4" />
                <span className="text-sm text-blueprint-900">Có tăng ca</span>
              </label>
            </div>
            <div className="col-span-2">
              <label className="block text-sm font-medium text-blueprint-900 mb-1">Ghi chú</label>
              <input value={formThuCong.ghiChu} onChange={(e) => setFormThuCong({ ...formThuCong, ghiChu: e.target.value })}
                placeholder="VD: thợ quên chấm công, mất sóng..." className="w-full border border-concrete-200 rounded px-3 py-2" />
            </div>
            <div className="col-span-2">
              <button type="submit" disabled={dangGui}
                className="bg-blueprint-900 hover:bg-blueprint-700 text-white font-semibold px-5 py-2.5 rounded transition-colors disabled:opacity-50">
                {dangGui ? 'Đang lưu...' : 'Lưu chấm công'}
              </button>
            </div>
          </form>
        )}

        <div className="flex flex-wrap gap-3 mb-5">
          <select value={locCongTrinh} onChange={(e) => setLocCongTrinh(e.target.value)} className="border border-concrete-200 rounded px-3 py-2 bg-white text-sm">
            <option value="">Tất cả công trình</option>
            {danhSachCongTrinh.map((c) => <option key={c.id} value={c.id}>{c.tenCongTrinh}</option>)}
          </select>
          <input type="date" value={locNgay} onChange={(e) => setLocNgay(e.target.value)} className="border border-concrete-200 rounded px-3 py-2 text-sm" />
          <select value={locTrangThai} onChange={(e) => setLocTrangThai(e.target.value)} className="border border-concrete-200 rounded px-3 py-2 bg-white text-sm">
            <option value="">Tất cả trạng thái</option>
            {Object.entries(TRANG_THAI_LABEL).map(([v, l]) => <option key={v} value={v}>{l.text}</option>)}
          </select>
        </div>

        {dangTai ? (
          <p className="text-blueprint-900/50">Đang tải...</p>
        ) : danhSach.length === 0 ? (
          <div className="bg-white border border-dashed border-concrete-200 rounded-lg p-10 text-center text-blueprint-900/50">Chưa có bản ghi chấm công nào khớp bộ lọc.</div>
        ) : (
          <div className="space-y-3">
            {danhSach.map((c) => (
              <div key={c.id} className="bg-white border border-concrete-200 rounded-lg p-4 flex gap-4 items-start">
                {c.anhChamCong && (
                  <a href={BACKEND_ORIGIN + c.anhChamCong} target="_blank" rel="noreferrer">
                    <img src={BACKEND_ORIGIN + c.anhChamCong} alt="Ảnh chấm công" className="w-16 h-16 object-cover rounded border border-concrete-200" />
                  </a>
                )}
                <div className="flex-1">
                  <div className="flex items-center justify-between">
                    <p className="font-semibold text-blueprint-900">{c.hoTenTho} <span className="text-blueprint-900/40 font-normal">— {c.tenCongTrinh}</span></p>
                    <span className={`text-xs font-semibold px-2 py-1 rounded ${TRANG_THAI_LABEL[c.trangThaiXacThuc]?.mau}`}>{TRANG_THAI_LABEL[c.trangThaiXacThuc]?.text}</span>
                  </div>
                  <p className="text-sm text-blueprint-900/60 mb-2">{c.ngayChamCong}{c.khoangCachGuong != null && ` · lệch khuôn mặt ${c.khoangCachGuong.toFixed(2)}`}</p>
                  <div className="flex items-center gap-2 flex-wrap">
                    <select value={c.loaiCong} onChange={(e) => suaLoaiCong(c.id, e.target.value)} className="text-xs border border-concrete-200 rounded px-2 py-1 bg-white">
                      {Object.entries(LOAI_CONG_LABEL).map(([v, l]) => <option key={v} value={v}>{l}</option>)}
                    </select>
                    <label className="flex items-center gap-1 text-xs text-blueprint-900/70 cursor-pointer">
                      <input type="checkbox" checked={!!c.coTangCa} onChange={(e) => suaTangCa(c.id, e.target.checked)} className="w-3.5 h-3.5" />
                      Tăng ca
                    </label>
                    {c.trangThaiXacThuc === 'CAN_DUYET' && (
                      <>
                        <button onClick={() => duyet(c.id, true)} className="text-xs bg-green-600 hover:bg-green-700 text-white font-semibold px-3 py-1 rounded">Duyệt</button>
                        <button onClick={() => duyet(c.id, false)} className="text-xs bg-red-500 hover:bg-red-600 text-white font-semibold px-3 py-1 rounded">Từ chối</button>
                      </>
                    )}
                    <button onClick={() => xoa(c.id)} className="text-xs text-blueprint-900/40 hover:text-red-600 font-semibold px-2 py-1">Xoá</button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </main>
    </div>
  );
}
