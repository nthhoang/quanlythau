import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function RegisterPage() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({
    tenDangNhap: '', matKhau: '', hoTen: '', soDienThoai: '', email: '',
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
        vaiTro: 'CHU_THAU',
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
          <p className="mt-2 text-sm text-slate-500">Tạo tài khoản chủ thầu. Bạn sẽ tạo và cấp tài khoản thợ trong đội của mình.</p>
        </div>

        <form onSubmit={handleSubmit} className="auth-card space-y-4 rounded-2xl border border-concrete-200 bg-white p-5 shadow-xl shadow-blueprint-900/5 sm:p-7">
          {loi && (
            <div className="rounded-lg border border-red-200 bg-red-50 px-3 py-2.5 text-sm text-red-700">{loi}</div>
          )}

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
