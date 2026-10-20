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
    <div className="min-h-screen flex items-center justify-center bg-concrete-50 px-4">
      <div className="w-full max-w-sm">
        <div className="text-center mb-8">
          <h1 className="font-display font-extrabold text-2xl text-blueprint-900 tracking-tight">
            QUẢN LÝ THẦU XÂY DỰNG
          </h1>
          <p className="text-blueprint-900/50 text-sm mt-1">Đăng nhập để tiếp tục</p>
        </div>

        <form onSubmit={handleSubmit} className="bg-white border border-concrete-200 rounded-lg p-7 shadow-sm">
          {loi && (
            <div className="mb-4 text-sm bg-red-50 text-red-700 border border-red-200 rounded px-3 py-2">{loi}</div>
          )}

          <label className="block text-sm font-medium text-blueprint-900 mb-1">Tên đăng nhập</label>
          <input
            name="tenDangNhap"
            value={form.tenDangNhap}
            onChange={handleChange}
            required
            className="w-full border border-concrete-200 rounded px-3 py-2 mb-4 focus:outline-none focus:ring-2 focus:ring-amber-500"
            placeholder="vd: bathau"
          />

          <label className="block text-sm font-medium text-blueprint-900 mb-1">Mật khẩu</label>
          <input
            type="password"
            name="matKhau"
            value={form.matKhau}
            onChange={handleChange}
            required
            className="w-full border border-concrete-200 rounded px-3 py-2 mb-6 focus:outline-none focus:ring-2 focus:ring-amber-500"
            placeholder="••••••••"
          />

          <button
            type="submit"
            disabled={dangTai}
            className="w-full bg-blueprint-900 hover:bg-blueprint-700 text-white font-semibold py-2.5 rounded transition-colors disabled:opacity-50"
          >
            {dangTai ? 'Đang đăng nhập...' : 'Đăng nhập'}
          </button>
        </form>

        <p className="text-center text-sm text-blueprint-900/60 mt-5">
          Chưa có tài khoản?{' '}
          <Link to="/register" className="text-amber-600 font-semibold hover:underline">Đăng ký</Link>
        </p>
      </div>
    </div>
  );
}
