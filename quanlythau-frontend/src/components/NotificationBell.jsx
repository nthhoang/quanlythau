import { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axiosClient from '../api/axiosClient';
import { taoKetNoiThongBao } from '../utils/websocket';

const LOAI_DIEU_HUONG = {
  PHAN_CONG_MOI: '/dashboard',
  CONG_TRINH_TAM_DUNG: '/dashboard',
  CHAM_CONG_TU_CHOI: '/cong-cua-toi',
  CHAM_CONG_CAN_DUYET: '/quan-ly-cham-cong',
  LUONG_DA_CHOT: '/luong-cua-toi',
  LUONG_DA_THANH_TOAN: '/luong-cua-toi',
};

function thoiGianTuongDoi(iso) {
  const giay = Math.floor((Date.now() - new Date(iso).getTime()) / 1000);
  if (giay < 60) return 'Vừa xong';
  if (giay < 3600) return `${Math.floor(giay / 60)} phút trước`;
  if (giay < 86400) return `${Math.floor(giay / 3600)} giờ trước`;
  return `${Math.floor(giay / 86400)} ngày trước`;
}

export default function NotificationBell() {
  const [danhSach, setDanhSach] = useState([]);
  const [soLuongChuaDoc, setSoLuongChuaDoc] = useState(0);
  const [hienDanhSach, setHienDanhSach] = useState(false);
  const [loi, setLoi] = useState('');
  const clientRef = useRef(null);
  const navigate = useNavigate();

  const taiDanhSach = async () => {
    try {
      const res = await axiosClient.get('/thong-bao');
      setDanhSach(res.data);
      setSoLuongChuaDoc(res.data.filter((t) => !t.daDoc).length);
      setLoi('');
    } catch (err) {
      // In lỗi thật ra console thay vì nuốt im lặng - mở DevTools (F12) > Console để xem chi tiết nếu thông báo không hiện.
      console.error('Không tải được thông báo:', err);
      setLoi('Không tải được thông báo. Mở Console (F12) xem chi tiết lỗi.');
    }
  };

  useEffect(() => {
    taiDanhSach();

    clientRef.current = taoKetNoiThongBao({
      onNhanThongBao: (thongBaoMoi) => {
        setDanhSach((truoc) => [thongBaoMoi, ...truoc]);
        setSoLuongChuaDoc((truoc) => truoc + 1);
      },
      onLoi: (thongDiep) => {
        console.error('WebSocket thông báo lỗi:', thongDiep);
      },
    });

    // Poll lai dinh ky de du phong: neu WebSocket vi ly do gi khong ket noi
    // duoc (mang, proxy chan...), thong bao van hien ra sau toi da ~25s
    // thay vi mat han cho toi khi F5 thu cong.
    const dinhKy = setInterval(taiDanhSach, 25000);

    return () => {
      clientRef.current?.deactivate();
      clearInterval(dinhKy);
    };
  }, []);

  const banVaoThongBao = async (thongBao) => {
    if (!thongBao.daDoc) {
      try {
        await axiosClient.patch(`/thong-bao/${thongBao.id}/da-doc`);
        setDanhSach((truoc) => truoc.map((t) => (t.id === thongBao.id ? { ...t, daDoc: true } : t)));
        setSoLuongChuaDoc((truoc) => Math.max(0, truoc - 1));
      } catch (err) {
        console.error('Đánh dấu đã đọc thất bại:', err);
      }
    }
    setHienDanhSach(false);
    const duong = LOAI_DIEU_HUONG[thongBao.loai];
    if (duong) navigate(duong);
  };

  const danhDauTatCa = async () => {
    try {
      await axiosClient.patch('/thong-bao/da-doc-tat-ca');
      setDanhSach((truoc) => truoc.map((t) => ({ ...t, daDoc: true })));
      setSoLuongChuaDoc(0);
    } catch (err) {
      console.error('Đánh dấu tất cả đã đọc thất bại:', err);
    }
  };

  return (
    <div className="relative">
      <button onClick={() => setHienDanhSach((truoc) => !truoc)} className="relative text-blueprint-100/80 hover:text-white p-1.5">
        🔔
        {soLuongChuaDoc > 0 && (
          <span className="absolute -top-1 -right-1 bg-amber-500 text-blueprint-900 text-[10px] font-bold rounded-full w-4 h-4 flex items-center justify-center">
            {soLuongChuaDoc > 9 ? '9+' : soLuongChuaDoc}
          </span>
        )}
      </button>

      {hienDanhSach && (
        <div className="absolute right-0 mt-2 w-80 bg-white border border-concrete-200 rounded-lg shadow-lg z-50 text-ink-900">
          <div className="flex items-center justify-between px-4 py-2 border-b border-concrete-200">
            <span className="font-semibold text-sm text-blueprint-900">Thông báo</span>
            {soLuongChuaDoc > 0 && (
              <button onClick={danhDauTatCa} className="text-xs text-blueprint-500 font-semibold hover:underline">
                Đánh dấu đã đọc tất cả
              </button>
            )}
          </div>
          <div className="max-h-96 overflow-y-auto">
            {loi ? (
              <p className="text-sm text-red-600 px-4 py-6 text-center">{loi}</p>
            ) : danhSach.length === 0 ? (
              <p className="text-sm text-blueprint-900/40 px-4 py-6 text-center">Chưa có thông báo nào.</p>
            ) : (
              danhSach.map((t) => (
                <button
                  key={t.id}
                  onClick={() => banVaoThongBao(t)}
                  className={`w-full text-left px-4 py-3 border-b border-concrete-200 last:border-0 hover:bg-concrete-50 transition-colors ${!t.daDoc ? 'bg-blueprint-100/30' : ''}`}
                >
                  <p className="text-sm font-semibold text-blueprint-900">{t.tieuDe}</p>
                  <p className="text-xs text-blueprint-900/60 mt-0.5">{t.noiDung}</p>
                  <p className="text-[11px] text-blueprint-900/40 mt-1">{thoiGianTuongDoi(t.createdAt)}</p>
                </button>
              ))
            )}
          </div>
        </div>
      )}
    </div>
  );
}
