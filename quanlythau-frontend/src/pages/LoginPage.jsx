import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ tenDangNhap: '', matKhau: '' });
  const [loi, setLoi] = useState('');
  const [dangTai, setDangTai] = useState(false);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoi('');
    setDangTai(true);
    try {
      await login(form.tenDangNhap, form.matKhau);
      navigate('/dashboard');
    } catch (err) {
      setLoi(err.response?.data?.message || 'Sai tên đăng nhập hoặc mật khẩu');
    } finally {
      setDangTai(false);
    }
  };

  return (
    <div className="auth-page flex min-h-screen items-center justify-center px-4 py-10">
      <div className="w-full max-w-md">
        <div className="mb-7 text-center">
          <span className="brand-mark mx-auto mb-4">Q</span>
          <p className="mb-2 text-xs font-extrabold uppercase tracking-[0.2em] text-amber-600">Quản lý công trình</p>
          <h1 className="font-display text-2xl font-extrabold tracking-tight text-blueprint-900">
            Chào mừng trở lại
          </h1>
          <p className="mt-2 text-sm text-slate-500">Đăng nhập để tiếp tục quản lý công việc của bạn.</p>
        </div>

        <form onSubmit={handleSubmit} className="auth-card space-y-1 rounded-2xl border border-concrete-200 bg-white p-6 shadow-xl shadow-blueprint-900/5 sm:p-8">
          {loi && (
            <div className="mb-5 rounded-lg border border-red-200 bg-red-50 px-3 py-2.5 text-sm text-red-700">{loi}</div>
          )}

          <label htmlFor="tenDangNhap" className="mb-1.5 block text-sm font-bold text-blueprint-900">Tên đăng nhập</label>
          <input
            id="tenDangNhap"
            name="tenDangNhap"
            value={form.tenDangNhap}
            onChange={handleChange}
            required
            autoComplete="username"
            className="mb-5 w-full rounded-lg border border-concrete-200 bg-slate-50/70 px-3.5 py-3 focus:bg-white focus:outline-none focus:ring-2 focus:ring-amber-500"
            placeholder="vd: bathau"
          />

          <label htmlFor="matKhau" className="mb-1.5 block text-sm font-bold text-blueprint-900">Mật khẩu</label>
          <input
            id="matKhau"
            type="password"
            name="matKhau"
            value={form.matKhau}
            onChange={handleChange}
            required
            autoComplete="current-password"
            className="mb-7 w-full rounded-lg border border-concrete-200 bg-slate-50/70 px-3.5 py-3 focus:bg-white focus:outline-none focus:ring-2 focus:ring-amber-500"
            placeholder="••••••••"
          />

          <button
            type="submit"
            disabled={dangTai}
            className="w-full rounded-lg bg-blueprint-900 py-3 font-bold text-white shadow-md shadow-blueprint-900/15 transition-colors hover:bg-blueprint-700 disabled:opacity-50"
          >
            {dangTai ? 'Đang đăng nhập...' : 'Đăng nhập'}
          </button>
        </form>

        <p className="mt-6 text-center text-sm text-slate-500">
          Chưa có tài khoản?{' '}
          <Link to="/register" className="font-bold text-blueprint-700 hover:text-amber-600">Đăng ký</Link>
        </p>
      </div>
    </div>
  );
}
