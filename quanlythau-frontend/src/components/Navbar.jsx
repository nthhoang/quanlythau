import { useAuth } from '../context/AuthContext';
import { NavLink, useNavigate } from 'react-router-dom';
import NotificationBell from './NotificationBell';

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const linkClass = ({ isActive }) =>
    `text-sm font-semibold px-3 py-1.5 rounded transition-colors ${
      isActive ? 'bg-amber-500 text-blueprint-900' : 'text-blueprint-100/80 hover:bg-blueprint-700'
    }`;

  return (
    <header className="bg-blueprint-900 text-white">
      <div className="max-w-6xl mx-auto px-6 py-4 flex items-center justify-between flex-wrap gap-3">
        <div>
          <h1 className="font-display font-extrabold text-lg tracking-tight">
            QUẢN LÝ THẦU XÂY DỰNG
          </h1>
          <p className="text-blueprint-100/60 text-xs">Đội thi công tư nhân</p>
        </div>

        {user && (
          <nav className="flex items-center gap-2 flex-wrap">
            <NavLink to="/dashboard" className={linkClass}>Công trình</NavLink>
            <NavLink to="/nhat-ky-thi-cong" className={linkClass}>Nhật ký thi công</NavLink>
            {user.vaiTro === 'THO' && (
              <NavLink to="/cong-cua-toi" className={linkClass}>Công của tôi</NavLink>
            )}
            {user.vaiTro === 'THO' && (
              <NavLink to="/luong-cua-toi" className={linkClass}>Lương của tôi</NavLink>
            )}
            {user.vaiTro === 'CHU_THAU' && (
              <NavLink to="/cham-cong" className={linkClass}>Chấm công</NavLink>
            )}
            {user.vaiTro === 'CHU_THAU' && (
              <NavLink to="/quan-ly-cham-cong" className={linkClass}>Quản lý chấm công</NavLink>
            )}
            {user.vaiTro === 'CHU_THAU' && (
              <NavLink to="/quan-ly-luong" className={linkClass}>Quản lý lương</NavLink>
            )}
            {user.vaiTro === 'CHU_THAU' && (
              <NavLink to="/quan-ly-vat-tu" className={linkClass}>Vật tư</NavLink>
            )}
            {user.vaiTro === 'CHU_THAU' && (
              <NavLink to="/chi-phi-lai-lo" className={linkClass}>Chi phí & Lãi lỗ</NavLink>
            )}
            {user.vaiTro === 'CHU_THAU' && (
              <NavLink to="/thong-ke" className={linkClass}>Thống kê</NavLink>
            )}
            {user.vaiTro === 'CHU_THAU' && (
              <NavLink to="/tro-ly-ai" className={linkClass}>Trợ lý AI</NavLink>
            )}
          </nav>
        )}

        {user && (
          <div className="flex items-center gap-4">
            <NotificationBell />
            <div className="text-right">
              <p className="text-sm font-semibold">{user.hoTen}</p>
              <span className="text-[11px] uppercase tracking-wide text-amber-500">
                {user.vaiTro === 'CHU_THAU' ? 'Chủ thầu' : 'Thợ'}
              </span>
            </div>
            <button
              onClick={handleLogout}
              className="text-sm bg-blueprint-700 hover:bg-blueprint-500 transition-colors px-3 py-1.5 rounded"
            >
              Đăng xuất
            </button>
          </div>
        )}
      </div>
    </header>
  );
}
