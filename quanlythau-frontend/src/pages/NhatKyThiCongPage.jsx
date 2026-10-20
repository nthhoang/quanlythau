import { useEffect, useRef, useState } from 'react';
import axiosClient, { BACKEND_ORIGIN } from '../api/axiosClient';
import { uploadAnh } from '../api/upload';
import { useAuth } from '../context/AuthContext';
import Navbar from '../components/Navbar';

const emptyForm = { ngay: new Date().toISOString().slice(0, 10), noiDung: '', danhSachAnh: [] };

export default function NhatKyThiCongPage() {
  const { user } = useAuth();
  const isChuThau = user?.vaiTro === 'CHU_THAU';

  const [danhSachCongTrinh, setDanhSachCongTrinh] = useState([]);
  const [congTrinhId, setCongTrinhId] = useState('');
  const [danhSach, setDanhSach] = useState([]);
  const [dangTai, setDangTai] = useState(false);

  const [hienForm, setHienForm] = useState(false);
  const [form, setForm] = useState(emptyForm);
  const [dangUpload, setDangUpload] = useState(false);
  const [dangGui, setDangGui] = useState(false);
  const fileInputRef = useRef(null);

  const [loi, setLoi] = useState('');
  const [thongBao, setThongBao] = useState('');

  useEffect(() => {
    axiosClient.get('/cong-trinh').then((res) => setDanhSachCongTrinh(res.data)).catch(() => setLoi('Không tải được danh sách công trình'));
  }, []);

  const taiDanhSach = async (id) => {
    if (!id) return;
    setDangTai(true);
    try {
      const res = await axiosClient.get(`/nhat-ky-thi-cong/cong-trinh/${id}`);
      setDanhSach(res.data);
      setLoi('');
    } catch {
      setLoi('Không tải được nhật ký thi công');
    } finally {
      setDangTai(false);
    }
  };

  useEffect(() => {
    taiDanhSach(congTrinhId);
    setHienForm(false);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [congTrinhId]);

  const congTrinhDangChon = danhSachCongTrinh.find((c) => String(c.id) === String(congTrinhId));

  const chonAnh = () => fileInputRef.current?.click();

  const handleChonAnh = async (e) => {
    const files = Array.from(e.target.files || []);
    if (files.length === 0) return;
    setDangUpload(true);
    setLoi('');
    try {
      const urls = [];
      for (const file of files) {
        const url = await uploadAnh(file);
        urls.push(url);
      }
      setForm((truoc) => ({ ...truoc, danhSachAnh: [...truoc.danhSachAnh, ...urls] }));
    } catch {
      setLoi('Tải ảnh thất bại, thử lại nhé');
    } finally {
      setDangUpload(false);
      e.target.value = '';
    }
  };

  const xoaAnhKhoiForm = (index) => {
    setForm((truoc) => ({ ...truoc, danhSachAnh: truoc.danhSachAnh.filter((_, i) => i !== index) }));
  };

  const submitNhatKy = async (e) => {
    e.preventDefault();
    setDangGui(true);
    try {
      await axiosClient.post(`/nhat-ky-thi-cong/cong-trinh/${congTrinhId}`, form);
      setForm(emptyForm);
      setHienForm(false);
      setThongBao('Đã lưu nhật ký thi công.');
      await taiDanhSach(congTrinhId);
    } catch (err) {
      setLoi(err.response?.data?.message || 'Lưu nhật ký thất bại');
    } finally {
      setDangGui(false);
    }
  };

  const xoaNhatKy = async (id) => {
    if (!window.confirm('Xoá nhật ký này?')) return;
    try {
      await axiosClient.delete(`/nhat-ky-thi-cong/${id}`);
      await taiDanhSach(congTrinhId);
    } catch {
      setLoi('Xoá thất bại');
    }
  };

  const saoChepLinkChiaSe = () => {
    if (!congTrinhDangChon) return;
    const link = `${window.location.origin}/xem-tien-do/${congTrinhDangChon.shareToken}`;
    navigator.clipboard.writeText(link).then(
      () => setThongBao('Đã sao chép link chia sẻ vào clipboard.'),
      () => setThongBao(`Link chia sẻ: ${link}`)
    );
  };

  return (
    <div className="min-h-screen bg-concrete-50">
      <Navbar />
      <main className="max-w-4xl mx-auto px-6 py-8">
        <h2 className="font-display font-extrabold text-xl text-blueprint-900 mb-6">Nhật ký thi công</h2>

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
            <div className="flex gap-3 mb-6">
              {isChuThau && (
                <button onClick={() => setHienForm(!hienForm)} className="bg-blueprint-900 hover:bg-blueprint-700 text-white font-semibold px-4 py-2 rounded transition-colors">
                  {hienForm ? 'Đóng lại' : '+ Ghi nhật ký hôm nay'}
                </button>
              )}
              {isChuThau && (
                <button onClick={saoChepLinkChiaSe} className="bg-amber-500 hover:bg-amber-600 text-blueprint-900 font-semibold px-4 py-2 rounded transition-colors">
                  🔗 Sao chép link cho chủ nhà
                </button>
              )}
            </div>

            {hienForm && (
              <form onSubmit={submitNhatKy} className="bg-white border border-concrete-200 rounded-lg p-6 mb-6">
                <div className="mb-3">
                  <label className="block text-sm font-medium text-blueprint-900 mb-1">Ngày</label>
                  <input type="date" value={form.ngay} onChange={(e) => setForm({ ...form, ngay: e.target.value })} className="border border-concrete-200 rounded px-3 py-2" />
                </div>
                <div className="mb-3">
                  <label className="block text-sm font-medium text-blueprint-900 mb-1">Nội dung công việc hôm nay</label>
                  <textarea required rows={4} value={form.noiDung} onChange={(e) => setForm({ ...form, noiDung: e.target.value })}
                    placeholder="VD: Đổ móng xong khu A, dự kiến mai bắt đầu xây tường..."
                    className="w-full border border-concrete-200 rounded px-3 py-2" />
                </div>
                <div className="mb-4">
                  <label className="block text-sm font-medium text-blueprint-900 mb-1">Ảnh hiện trường</label>
                  <div className="flex flex-wrap gap-2 mb-2">
                    {form.danhSachAnh.map((url, i) => (
                      <div key={i} className="relative">
                        <img src={BACKEND_ORIGIN + url} alt="Ảnh hiện trường" className="w-16 h-16 object-cover rounded border border-concrete-200" />
                        <button type="button" onClick={() => xoaAnhKhoiForm(i)} className="absolute -top-1.5 -right-1.5 bg-red-500 text-white rounded-full w-5 h-5 text-xs leading-none">×</button>
                      </div>
                    ))}
                  </div>
                  <button type="button" onClick={chonAnh} disabled={dangUpload} className="text-sm border border-blueprint-900 text-blueprint-900 font-semibold px-3 py-1.5 rounded hover:bg-blueprint-100/40 disabled:opacity-50">
                    {dangUpload ? 'Đang tải ảnh...' : '+ Thêm ảnh'}
                  </button>
                  <input ref={fileInputRef} type="file" accept="image/*" multiple onChange={handleChonAnh} className="hidden" />
                </div>
                <button type="submit" disabled={dangGui} className="bg-blueprint-900 hover:bg-blueprint-700 text-white font-semibold px-5 py-2.5 rounded transition-colors disabled:opacity-50">
                  {dangGui ? 'Đang lưu...' : 'Lưu nhật ký'}
                </button>
              </form>
            )}

            {dangTai ? (
              <p className="text-blueprint-900/50">Đang tải...</p>
            ) : danhSach.length === 0 ? (
              <div className="bg-white border border-dashed border-concrete-200 rounded-lg p-10 text-center text-blueprint-900/50">Chưa có nhật ký nào cho công trình này.</div>
            ) : (
              <div className="space-y-3">
                {danhSach.map((n) => (
                  <div key={n.id} className="bg-white border border-concrete-200 rounded-lg p-5">
                    <div className="flex items-center justify-between mb-2">
                      <p className="font-semibold text-blueprint-900">{n.ngay}</p>
                      {isChuThau && (
                        <button onClick={() => xoaNhatKy(n.id)} className="text-xs text-red-500 hover:text-red-700 font-semibold">Xoá</button>
                      )}
                    </div>
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
