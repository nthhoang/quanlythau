import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function ProtectedRoute({ children, requiredRole }) {
  const { user } = useAuth();

  if (!user) {
    return <Navigate to="/login" replace />;
  }
  if (requiredRole && user.vaiTro !== requiredRole) {
    return <Navigate to="/dashboard" replace />;
  }

  return children;
}
