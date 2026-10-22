import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import DashboardPage from './pages/DashboardPage';
import ChamCongPage from './pages/ChamCongPage';
import QuanLyChamCongPage from './pages/QuanLyChamCongPage';
import LichSuChamCongPage from './pages/LichSuChamCongPage';
import QuanLyLuongPage from './pages/QuanLyLuongPage';
import LuongCuaToiPage from './pages/LuongCuaToiPage';
import QuanLyVatTuPage from './pages/QuanLyVatTuPage';
import QuanLyLaiLoPage from './pages/QuanLyLaiLoPage';
import ThongKePage from './pages/ThongKePage';
import TroLyAiPage from './pages/TroLyAiPage';
import NhatKyThiCongPage from './pages/NhatKyThiCongPage';
import XemTienDoCongKhaiPage from './pages/XemTienDoCongKhaiPage';
import QuanLyThoPage from './pages/QuanLyThoPage';

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/xem-tien-do/:shareToken" element={<XemTienDoCongKhaiPage />} />
          <Route path="/dashboard" element={<ProtectedRoute><DashboardPage /></ProtectedRoute>} />
          <Route path="/cham-cong" element={<ProtectedRoute requiredRole="CHU_THAU"><ChamCongPage /></ProtectedRoute>} />
          <Route path="/quan-ly-cham-cong" element={<ProtectedRoute requiredRole="CHU_THAU"><QuanLyChamCongPage /></ProtectedRoute>} />
          <Route path="/cong-cua-toi" element={<ProtectedRoute requiredRole="THO"><LichSuChamCongPage /></ProtectedRoute>} />
          <Route path="/quan-ly-luong" element={<ProtectedRoute requiredRole="CHU_THAU"><QuanLyLuongPage /></ProtectedRoute>} />
          <Route path="/quan-ly-tho" element={<ProtectedRoute requiredRole="CHU_THAU"><QuanLyThoPage /></ProtectedRoute>} />
          <Route path="/luong-cua-toi" element={<ProtectedRoute requiredRole="THO"><LuongCuaToiPage /></ProtectedRoute>} />
          <Route path="/quan-ly-vat-tu" element={<ProtectedRoute requiredRole="CHU_THAU"><QuanLyVatTuPage /></ProtectedRoute>} />
          <Route path="/chi-phi-lai-lo" element={<ProtectedRoute requiredRole="CHU_THAU"><QuanLyLaiLoPage /></ProtectedRoute>} />
          <Route path="/thong-ke" element={<ProtectedRoute requiredRole="CHU_THAU"><ThongKePage /></ProtectedRoute>} />
          <Route path="/tro-ly-ai" element={<ProtectedRoute requiredRole="CHU_THAU"><TroLyAiPage /></ProtectedRoute>} />
          <Route path="/nhat-ky-thi-cong" element={<ProtectedRoute><NhatKyThiCongPage /></ProtectedRoute>} />
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}
