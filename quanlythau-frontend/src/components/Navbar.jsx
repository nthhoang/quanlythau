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
    `nav-link shrink-0 text-sm font-semibold px-3.5 py-2 rounded-lg transition-colors ${
      isActive ? 'nav-link-active' : 'text-blueprint-700/75 hover:text-blueprint-900 hover:bg-blueprint-100/70'
    }`;

  return (
    <header className="app-header">
      <div className="max-w-7xl mx-auto px-4 sm:px-6">
        <div className="flex min-h-[76px] items-center justify-between gap-4">
          <NavLink to="/dashboard" className="flex min-w-0 items-center gap-3 no-underline">
            <span className="brand-mark" aria-hidden="true">Q</span>
            <span className="min-w-0">
              <span className="block truncate font-display text-sm sm:text-base font-extrabold tracking-tight text-blueprint-900">
                QUẢN LÝ THẦU XÂY DỰNG
              </span>
              <span className="mt-0.5 block text-[11px] font-medium tracking-wide text-slate-500">
                ĐIỀU HÀNH CÔNG TRÌNH
              </span>
            </span>
          </NavLink>

          {user && (
            <div className="flex shrink-0 items-center gap-2 sm:gap-4">
              <NotificationBell />
              <div className="hidden border-l border-concrete-200 pl-4 text-right sm:block">
                <p className="max-w-40 truncate text-sm font-bold text-blueprint-900">{user.hoTen}</p>
                <span className="text-[11px] font-semibold uppercase tracking-wider text-blueprint-500">
                  {user.vaiTro === 'CHU_THAU' ? 'Chủ thầu' : 'Thợ'}
                </span>
              </div>
              <button
                onClick={handleLogout}
                className="rounded-lg border border-concrete-200 bg-white px-3 py-2 text-xs font-bold text-blueprint-700 shadow-sm hover:border-blueprint-500 hover:text-blueprint-900 sm:px-3.5 sm:text-sm"
              >
                Đăng xuất
              </button>
            </div>
          )}
        </div>

        {user && (
          <nav aria-label="Điều hướng chính" className="nav-scroll flex items-center gap-1 overflow-x-auto border-t border-concrete-200 py-2">
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
      </div>
    </header>
  );
}
