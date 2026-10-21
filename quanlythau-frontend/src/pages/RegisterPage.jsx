import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const CHUYEN_MON_OPTIONS = [
  { value: 'THO_CHINH', label: 'Thợ chính' },
  { value: 'THO_PHU', label: 'Thợ phụ' },
  { value: 'DIEN_NUOC', label: 'Điện nước' },
  { value: 'THO_SON', label: 'Thợ sơn' },
  { value: 'THO_MOC', label: 'Thợ mộc' },
  { value: 'KHAC', label: 'Khác' },
];

export default function RegisterPage() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({
    tenDangNhap: '', matKhau: '', hoTen: '', soDienThoai: '', email: '',
    vaiTro: 'CHU_THAU', chuyenMon: 'THO_CHINH', donGiaCong: '',
  });
  const [loi, setLoi] = useState('');
  const [dangTai, setDangTai] = useState(false);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoi('');
    setDangTai(true);
    try {
      const payload = {
        tenDangNhap: form.tenDangNhap,
        matKhau: form.matKhau,
        hoTen: form.hoTen,
        soDienThoai: form.soDienThoai || null,
        email: form.email || null,
        vaiTro: form.vaiTro,
        chuyenMon: form.vaiTro === 'THO' ? form.chuyenMon : null,
        donGiaCong: form.vaiTro === 'THO' && form.donGiaCong ? Number(form.donGiaCong) : null,
      };
      await register(payload);
      navigate('/dashboard');
    } catch (err) {
      setLoi(err.response?.data?.message || 'Đăng ký thất bại, thử lại nhé');
    } finally {
      setDangTai(false);
    }
  };

  return (
    <div className="auth-page flex min-h-screen items-center justify-center px-4 py-10">
      <div className="w-full max-w-md">
        <div className="mb-6 text-center">
          <span className="brand-mark mx-auto mb-4">Q</span>
          <p className="mb-2 text-xs font-extrabold uppercase tracking-[0.2em] text-amber-600">Bắt đầu ngay hôm nay</p>
          <h1 className="font-display text-2xl font-extrabold tracking-tight text-blueprint-900">Tạo tài khoản</h1>
          <p className="mt-2 text-sm text-slate-500">Thiết lập không gian quản lý công trình của bạn.</p>
        </div>

        <form onSubmit={handleSubmit} className="auth-card space-y-4 rounded-2xl border border-concrete-200 bg-white p-5 shadow-xl shadow-blueprint-900/5 sm:p-7">
          {loi && (
            <div className="rounded-lg border border-red-200 bg-red-50 px-3 py-2.5 text-sm text-red-700">{loi}</div>
          )}

          <div>
            <label className="mb-1.5 block text-sm font-bold text-blueprint-900">Vai trò</label>
            <div className="grid grid-cols-2 gap-2">
              <button type="button" onClick={() => setForm({ ...form, vaiTro: 'CHU_THAU' })}
                className={`rounded-lg border py-2.5 text-sm font-bold transition-colors ${form.vaiTro === 'CHU_THAU' ? 'border-blueprint-900 bg-blueprint-900 text-white shadow-sm' : 'border-concrete-200 text-blueprint-900/70 hover:bg-blueprint-100/50'}`}>
                Chủ thầu
              </button>
              <button type="button" onClick={() => setForm({ ...form, vaiTro: 'THO' })}
                className={`rounded-lg border py-2.5 text-sm font-bold transition-colors ${form.vaiTro === 'THO' ? 'border-blueprint-900 bg-blueprint-900 text-white shadow-sm' : 'border-concrete-200 text-blueprint-900/70 hover:bg-blueprint-100/50'}`}>
                Thợ
              </button>
            </div>
          </div>

          <div>
            <label className="mb-1.5 block text-sm font-bold text-blueprint-900">Họ tên</label>
            <input name="hoTen" value={form.hoTen} onChange={handleChange} required
              className="w-full rounded-lg border border-concrete-200 bg-slate-50/70 px-3 py-2.5 focus:bg-white focus:outline-none focus:ring-2 focus:ring-amber-500" />
          </div>

          <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
            <div>
              <label className="mb-1.5 block text-sm font-bold text-blueprint-900">Tên đăng nhập</label>
              <input name="tenDangNhap" value={form.tenDangNhap} onChange={handleChange} required
                className="w-full rounded-lg border border-concrete-200 bg-slate-50/70 px-3 py-2.5 focus:bg-white focus:outline-none focus:ring-2 focus:ring-amber-500" />
            </div>
            <div>
              <label className="mb-1.5 block text-sm font-bold text-blueprint-900">Mật khẩu</label>
              <input type="password" name="matKhau" value={form.matKhau} onChange={handleChange} required
                className="w-full rounded-lg border border-concrete-200 bg-slate-50/70 px-3 py-2.5 focus:bg-white focus:outline-none focus:ring-2 focus:ring-amber-500" />
            </div>
          </div>

          <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
            <div>
              <label className="mb-1.5 block text-sm font-bold text-blueprint-900">Số điện thoại</label>
              <input name="soDienThoai" value={form.soDienThoai} onChange={handleChange}
                className="w-full rounded-lg border border-concrete-200 bg-slate-50/70 px-3 py-2.5 focus:bg-white focus:outline-none focus:ring-2 focus:ring-amber-500" />
            </div>
            <div>
              <label className="mb-1.5 block text-sm font-bold text-blueprint-900">Email</label>
              <input name="email" value={form.email} onChange={handleChange}
                className="w-full rounded-lg border border-concrete-200 bg-slate-50/70 px-3 py-2.5 focus:bg-white focus:outline-none focus:ring-2 focus:ring-amber-500" />
            </div>
          </div>

          {form.vaiTro === 'THO' && (
            <div className="grid grid-cols-1 gap-3 rounded-xl border border-blueprint-100 bg-blueprint-100/35 p-3 sm:grid-cols-2">
              <div>
                <label className="mb-1.5 block text-sm font-bold text-blueprint-900">Chuyên môn</label>
                <select name="chuyenMon" value={form.chuyenMon} onChange={handleChange}
                  className="w-full rounded-lg border border-concrete-200 bg-white px-3 py-2.5">
                  {CHUYEN_MON_OPTIONS.map((o) => <option key={o.value} value={o.value}>{o.label}</option>)}
                </select>
              </div>
              <div>
                <label className="mb-1.5 block text-sm font-bold text-blueprint-900">Đơn giá công/ngày</label>
                <input type="number" name="donGiaCong" value={form.donGiaCong} onChange={handleChange}
                  placeholder="VD: 350000"
                  className="w-full rounded-lg border border-concrete-200 bg-white px-3 py-2.5 focus:outline-none focus:ring-2 focus:ring-amber-500" />
              </div>
            </div>
          )}

          <button type="submit" disabled={dangTai}
            className="w-full rounded-lg bg-blueprint-900 py-3 font-bold text-white shadow-md shadow-blueprint-900/15 transition-colors hover:bg-blueprint-700 disabled:opacity-50">
            {dangTai ? 'Đang tạo...' : 'Đăng ký'}
          </button>
        </form>

        <p className="mt-6 text-center text-sm text-slate-500">
          Đã có tài khoản?{' '}
          <Link to="/login" className="font-bold text-blueprint-700 hover:text-amber-600">Đăng nhập</Link>
        </p>
      </div>
    </div>
  );
}
