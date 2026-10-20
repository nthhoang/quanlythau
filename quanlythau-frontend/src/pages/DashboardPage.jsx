import { useEffect, useState } from 'react';
import axiosClient from '../api/axiosClient';
import { useAuth } from '../context/AuthContext';
import Navbar from '../components/Navbar';
import ProgressBar from '../components/ProgressBar';

const LOAI_HINH_LABEL = { XAY_MOI: 'Xây mới', SUA_CHUA: 'Sửa chữa', CAI_TAO: 'Cải tạo' };
const TRANG_THAI_LABEL = { DANG_THI_CONG: 'Đang thi công', TAM_DUNG: 'Tạm dừng', HOAN_THANH: 'Hoàn thành' };

const formatVND = (value) => {
  if (value === null || value === undefined) return '—';
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
};

const emptyForm = {
  tenCongTrinh: '', diaChi: '', tenChuNha: '', sdtChuNha: '', loaiHinh: 'XAY_MOI',
  ngayBatDau: '', ngayDuKienHoanThanh: '', giaTriHopDong: '',
};

export default function DashboardPage() {
  const { user } = useAuth();
  const [danhSach, setDanhSach] = useState([]);
  const [dangTai, setDangTai] = useState(true);
  const [loi, setLoi] = useState('');
  const [hienForm, setHienForm] = useState(false);
  const [form, setForm] = useState(emptyForm);
  const [dangGui, setDangGui] = useState(false);

  const [danhSachTho, setDanhSachTho] = useState([]);
  const [moRongId, setMoRongId] = useState(null);
  const [thoTheoCongTrinh, setThoTheoCongTrinh] = useState({});
  const [thoDuocChon, setThoDuocChon] = useState({});

  const isChuThau = user?.vaiTro === 'CHU_THAU';

  const taiDanhSach = async () => {
    setDangTai(true);
    try {
      const res = await axiosClient.get('/cong-trinh');
      setDanhSach(res.data);
      setLoi('');
    } catch {
      setLoi('Không tải được danh sách công trình. Kiểm tra backend đã chạy chưa (localhost:8080).');
    } finally {
      setDangTai(false);
    }
  };

  useEffect(() => {
    taiDanhSach();
    if (isChuThau) {
      axiosClient.get('/nguoi-dung/tho').then((res) => setDanhSachTho(res.data)).catch(() => setLoi('Không tải được danh sách thợ'));
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleFormChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleTaoCongTrinh = async (e) => {
    e.preventDefault();
    setDangGui(true);
    try {
      const payload = {
        ...form,
        giaTriHopDong: form.giaTriHopDong ? Number(form.giaTriHopDong) : null,
        ngayBatDau: form.ngayBatDau || null,
        ngayDuKienHoanThanh: form.ngayDuKienHoanThanh || null,
      };
      await axiosClient.post('/cong-trinh', payload);
      setForm(emptyForm);
      setHienForm(false);
      await taiDanhSach();
    } catch (err) {
      setLoi(err.response?.data?.message || 'Tạo công trình thất bại');
    } finally {
      setDangGui(false);
    }
  };

  const capNhatTienDo = async (id, tienDoMoi) => {
    try {
      await axiosClient.patch(`/cong-trinh/${id}/tien-do`, { tienDoPhanTram: tienDoMoi });
      await taiDanhSach();
    } catch {
      setLoi('Cập nhật tiến độ thất bại');
    }
  };

  const capNhatTrangThai = async (id, trangThaiMoi) => {
    try {
      await axiosClient.patch(`/cong-trinh/${id}/trang-thai`, { trangThai: trangThaiMoi });
      await taiDanhSach();
    } catch {
      setLoi('Cập nhật trạng thái thất bại');
    }
  };

  const taiDanhSachThoCuaCongTrinh = async (congTrinhId) => {
    try {
      const res = await axiosClient.get(`/cong-trinh/${congTrinhId}/tho`);
      setThoTheoCongTrinh((truoc) => ({ ...truoc, [congTrinhId]: res.data }));
    } catch {
      setLoi('Không tải được danh sách thợ của công trình này');
    }
  };

  const toggleMoRong = (congTrinhId) => {
    if (moRongId === congTrinhId) {
      setMoRongId(null);
      return;
    }
    setMoRongId(congTrinhId);
    if (!thoTheoCongTrinh[congTrinhId]) {
      taiDanhSachThoCuaCongTrinh(congTrinhId);
    }
  };

  const themTho = async (congTrinhId) => {
    const nguoiDungId = thoDuocChon[congTrinhId];
    if (!nguoiDungId) return;
    try {
      await axiosClient.post(`/cong-trinh/${congTrinhId}/phan-cong`, { nguoiDungId: Number(nguoiDungId) });
      setThoDuocChon((truoc) => ({ ...truoc, [congTrinhId]: '' }));
      await taiDanhSachThoCuaCongTrinh(congTrinhId);
    } catch (err) {
      setLoi(err.response?.data?.message || 'Thêm thợ thất bại');
    }
  };

  const goTho = async (congTrinhId, phanCongId) => {
    try {
      await axiosClient.delete(`/cong-trinh/${congTrinhId}/phan-cong/${phanCongId}`);
      await taiDanhSachThoCuaCongTrinh(congTrinhId);
    } catch {
      setLoi('Gỡ thợ thất bại');
    }
  };

  return (
    <div className="min-h-screen bg-concrete-50">
      <Navbar />
      <main className="max-w-6xl mx-auto px-6 py-8">
        <div className="flex items-center justify-between mb-6">
          <h2 className="font-display font-extrabold text-xl text-blueprint-900">Công trình đang quản lý</h2>
          {isChuThau && (
            <button onClick={() => setHienForm(!hienForm)}
              className="bg-amber-500 hover:bg-amber-600 text-blueprint-900 font-semibold px-4 py-2 rounded transition-colors">
              {hienForm ? 'Đóng lại' : '+ Thêm công trình'}
            </button>
          )}
        </div>

        {loi && (
          <div className="mb-6 text-sm bg-red-50 text-red-700 border border-red-200 rounded px-3 py-2">{loi}</div>
        )}

        {hienForm && (
          <form onSubmit={handleTaoCongTrinh} className="bg-white border border-concrete-200 rounded-lg p-6 mb-8 grid grid-cols-2 gap-4">
            <div className="col-span-2">
              <label className="block text-sm font-medium text-blueprint-900 mb-1">Tên công trình</label>
              <input name="tenCongTrinh" value={form.tenCongTrinh} onChange={handleFormChange} required
                className="w-full border border-concrete-200 rounded px-3 py-2" placeholder="VD: Nhà anh Ba - thôn 2" />
            </div>
            <div className="col-span-2">
              <label className="block text-sm font-medium text-blueprint-900 mb-1">Địa chỉ</label>
              <input name="diaChi" value={form.diaChi} onChange={handleFormChange} required
                className="w-full border border-concrete-200 rounded px-3 py-2" />
            </div>
            <div>
              <label className="block text-sm font-medium text-blueprint-900 mb-1">Tên chủ nhà</label>
              <input name="tenChuNha" value={form.tenChuNha} onChange={handleFormChange}
                className="w-full border border-concrete-200 rounded px-3 py-2" />
            </div>
            <div>
              <label className="block text-sm font-medium text-blueprint-900 mb-1">SĐT chủ nhà</label>
              <input name="sdtChuNha" value={form.sdtChuNha} onChange={handleFormChange}
                className="w-full border border-concrete-200 rounded px-3 py-2" />
            </div>
            <div>
              <label className="block text-sm font-medium text-blueprint-900 mb-1">Loại hình</label>
              <select name="loaiHinh" value={form.loaiHinh} onChange={handleFormChange}
                className="w-full border border-concrete-200 rounded px-3 py-2 bg-white">
                {Object.entries(LOAI_HINH_LABEL).map(([value, label]) => <option key={value} value={value}>{label}</option>)}
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-blueprint-900 mb-1">Giá trị hợp đồng (VNĐ)</label>
              <input type="number" name="giaTriHopDong" value={form.giaTriHopDong} onChange={handleFormChange}
                className="w-full border border-concrete-200 rounded px-3 py-2" />
            </div>
            <div>
              <label className="block text-sm font-medium text-blueprint-900 mb-1">Ngày bắt đầu</label>
              <input type="date" name="ngayBatDau" value={form.ngayBatDau} onChange={handleFormChange}
                className="w-full border border-concrete-200 rounded px-3 py-2" />
            </div>
            <div>
              <label className="block text-sm font-medium text-blueprint-900 mb-1">Dự kiến hoàn thành</label>
              <input type="date" name="ngayDuKienHoanThanh" value={form.ngayDuKienHoanThanh} onChange={handleFormChange}
                className="w-full border border-concrete-200 rounded px-3 py-2" />
            </div>
            <div className="col-span-2">
              <button type="submit" disabled={dangGui}
                className="bg-blueprint-900 hover:bg-blueprint-700 text-white font-semibold px-5 py-2.5 rounded transition-colors disabled:opacity-50">
                {dangGui ? 'Đang lưu...' : 'Lưu công trình'}
              </button>
            </div>
          </form>
        )}

        {dangTai ? (
          <p className="text-blueprint-900/50">Đang tải...</p>
        ) : danhSach.length === 0 ? (
          <div className="bg-white border border-dashed border-concrete-200 rounded-lg p-10 text-center text-blueprint-900/50">
            Chưa có công trình nào. {isChuThau && 'Bấm "+ Thêm công trình" để tạo cái đầu tiên.'}
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
            {danhSach.map((ct) => (
              <div key={ct.id} className="bg-white border border-concrete-200 rounded-lg p-5">
                <div className="flex items-start justify-between mb-1">
                  <h3 className="font-display font-bold text-blueprint-900">{ct.tenCongTrinh}</h3>
                  <span className="text-[11px] uppercase font-semibold text-blueprint-500 bg-blueprint-100 px-2 py-0.5 rounded">
                    {LOAI_HINH_LABEL[ct.loaiHinh] || ct.loaiHinh}
                  </span>
                </div>
                <p className="text-sm text-blueprint-900/60 mb-3">{ct.diaChi}</p>

                <div className="text-sm text-blueprint-900/80 space-y-1 mb-4">
                  {ct.tenChuNha && <p>Chủ nhà: {ct.tenChuNha} {ct.sdtChuNha && `- ${ct.sdtChuNha}`}</p>}
                  {isChuThau && <p>Giá trị hợp đồng: {formatVND(ct.giaTriHopDong)}</p>}
                  {ct.ngayBatDau && <p>Bắt đầu: {ct.ngayBatDau} {ct.ngayDuKienHoanThanh && `→ ${ct.ngayDuKienHoanThanh}`}</p>}
                </div>

                <ProgressBar percent={ct.tienDoPhanTram} />

                {isChuThau && (
                  <div className="flex items-center gap-2 mt-4 pt-4 border-t border-concrete-200">
                    <select value={ct.trangThai} onChange={(e) => capNhatTrangThai(ct.id, e.target.value)}
                      className="text-xs border border-concrete-200 rounded px-2 py-1 bg-white">
                      {Object.entries(TRANG_THAI_LABEL).map(([value, label]) => <option key={value} value={value}>{label}</option>)}
                    </select>
                    <input type="number" min="0" max="100" defaultValue={ct.tienDoPhanTram}
                      onBlur={(e) => capNhatTienDo(ct.id, Number(e.target.value))}
                      className="w-16 text-xs border border-concrete-200 rounded px-2 py-1"
                      title="Nhập % tiến độ rồi bấm ra ngoài ô để lưu" />
                    <span className="text-xs text-blueprint-900/40">% tiến độ</span>
                  </div>
                )}

                {isChuThau && (
                  <div className="mt-3 pt-3 border-t border-concrete-200">
                    <button onClick={() => toggleMoRong(ct.id)} className="text-xs font-semibold text-blueprint-500 hover:text-blueprint-900">
                      👷 Thợ đang làm {moRongId === ct.id ? '▲' : '▼'}
                    </button>

                    {moRongId === ct.id && (
                      <div className="mt-3 space-y-2">
                        {(thoTheoCongTrinh[ct.id] || []).length === 0 ? (
                          <p className="text-xs text-blueprint-900/40">Chưa có thợ nào được phân công.</p>
                        ) : (
                          thoTheoCongTrinh[ct.id].map((pc) => (
                            <div key={pc.id} className="flex items-center justify-between text-sm bg-concrete-50 rounded px-3 py-1.5">
                              <span>{pc.hoTenTho} {pc.soDienThoaiTho && `- ${pc.soDienThoaiTho}`}</span>
                              <button onClick={() => goTho(ct.id, pc.id)} className="text-xs text-red-500 hover:text-red-700 font-semibold">Gỡ</button>
                            </div>
                          ))
                        )}

                        <div className="flex gap-2 pt-1">
                          <select value={thoDuocChon[ct.id] || ''} onChange={(e) => setThoDuocChon((truoc) => ({ ...truoc, [ct.id]: e.target.value }))}
                            className="flex-1 text-sm border border-concrete-200 rounded px-2 py-1.5 bg-white">
                            <option value="">-- chọn thợ để thêm --</option>
                            {danhSachTho.filter((t) => !(thoTheoCongTrinh[ct.id] || []).some((pc) => pc.nguoiDungId === t.id)).map((t) => (
                              <option key={t.id} value={t.id}>{t.hoTen}</option>
                            ))}
                          </select>
                          <button onClick={() => themTho(ct.id)} disabled={!thoDuocChon[ct.id]}
                            className="text-sm bg-blueprint-900 hover:bg-blueprint-700 text-white font-semibold px-3 py-1.5 rounded disabled:opacity-40">
                            Thêm
                          </button>
                        </div>
                      </div>
                    )}
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
