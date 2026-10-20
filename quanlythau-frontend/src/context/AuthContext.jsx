import { createContext, useContext, useState } from 'react';
import axiosClient from '../api/axiosClient';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem('user');
    return saved ? JSON.parse(saved) : null;
  });

  const luuPhien = (data) => {
    const { token, id, hoTen, vaiTro } = data;
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify({ id, hoTen, vaiTro }));
    setUser({ id, hoTen, vaiTro });
  };

  const login = async (tenDangNhap, matKhau) => {
    const res = await axiosClient.post('/auth/login', { tenDangNhap, matKhau });
    luuPhien(res.data);
    return res.data;
  };

  const register = async (payload) => {
    const res = await axiosClient.post('/auth/register', payload);
    luuPhien(res.data);
    return res.data;
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
