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
    <div className="min-h-screen flex items-center justify-center bg-concrete-50 px-4 py-10">
      <div className="w-full max-w-md">
        <div className="text-center mb-6">
          <h1 className="font-display font-extrabold text-2xl text-blueprint-900 tracking-tight">TẠO TÀI KHOẢN</h1>
        </div>

        <form onSubmit={handleSubmit} className="bg-white border border-concrete-200 rounded-lg p-7 shadow-sm space-y-4">
          {loi && (
            <div className="text-sm bg-red-50 text-red-700 border border-red-200 rounded px-3 py-2">{loi}</div>
          )}

          <div>
            <label className="block text-sm font-medium text-blueprint-900 mb-1">Vai trò</label>
            <div className="grid grid-cols-2 gap-2">
              <button type="button" onClick={() => setForm({ ...form, vaiTro: 'CHU_THAU' })}
                className={`py-2 rounded border text-sm font-semibold transition-colors ${form.vaiTro === 'CHU_THAU' ? 'bg-blueprint-900 text-white border-blueprint-900' : 'border-concrete-200 text-blueprint-900/70'}`}>
                Chủ thầu
              </button>
              <button type="button" onClick={() => setForm({ ...form, vaiTro: 'THO' })}
                className={`py-2 rounded border text-sm font-semibold transition-colors ${form.vaiTro === 'THO' ? 'bg-blueprint-900 text-white border-blueprint-900' : 'border-concrete-200 text-blueprint-900/70'}`}>
                Thợ
              </button>
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-blueprint-900 mb-1">Họ tên</label>
            <input name="hoTen" value={form.hoTen} onChange={handleChange} required
              className="w-full border border-concrete-200 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-amber-500" />
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-sm font-medium text-blueprint-900 mb-1">Tên đăng nhập</label>
              <input name="tenDangNhap" value={form.tenDangNhap} onChange={handleChange} required
                className="w-full border border-concrete-200 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-amber-500" />
            </div>
            <div>
              <label className="block text-sm font-medium text-blueprint-900 mb-1">Mật khẩu</label>
              <input type="password" name="matKhau" value={form.matKhau} onChange={handleChange} required
                className="w-full border border-concrete-200 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-amber-500" />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-sm font-medium text-blueprint-900 mb-1">Số điện thoại</label>
              <input name="soDienThoai" value={form.soDienThoai} onChange={handleChange}
                className="w-full border border-concrete-200 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-amber-500" />
            </div>
            <div>
              <label className="block text-sm font-medium text-blueprint-900 mb-1">Email</label>
              <input name="email" value={form.email} onChange={handleChange}
                className="w-full border border-concrete-200 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-amber-500" />
            </div>
          </div>

          {form.vaiTro === 'THO' && (
            <div className="grid grid-cols-2 gap-3 bg-blueprint-100/40 rounded p-3">
              <div>
                <label className="block text-sm font-medium text-blueprint-900 mb-1">Chuyên môn</label>
                <select name="chuyenMon" value={form.chuyenMon} onChange={handleChange}
                  className="w-full border border-concrete-200 rounded px-3 py-2 bg-white">
                  {CHUYEN_MON_OPTIONS.map((o) => <option key={o.value} value={o.value}>{o.label}</option>)}
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-blueprint-900 mb-1">Đơn giá công/ngày</label>
                <input type="number" name="donGiaCong" value={form.donGiaCong} onChange={handleChange}
                  placeholder="VD: 350000"
                  className="w-full border border-concrete-200 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-amber-500" />
              </div>
            </div>
          )}

          <button type="submit" disabled={dangTai}
            className="w-full bg-blueprint-900 hover:bg-blueprint-700 text-white font-semibold py-2.5 rounded transition-colors disabled:opacity-50">
            {dangTai ? 'Đang tạo...' : 'Đăng ký'}
          </button>
        </form>

        <p className="text-center text-sm text-blueprint-900/60 mt-5">
          Đã có tài khoản?{' '}
          <Link to="/login" className="text-amber-600 font-semibold hover:underline">Đăng nhập</Link>
        </p>
      </div>
    </div>
  );
}
