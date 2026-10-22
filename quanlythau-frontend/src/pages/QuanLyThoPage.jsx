import { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import axiosClient from '../api/axiosClient';
import Navbar from '../components/Navbar';

const CHUYEN_MON_LABEL = {
  THO_CHINH: 'Thợ chính',
  THO_PHU: 'Thợ phụ',
  DIEN_NUOC: 'Điện nước',
  THO_SON: 'Thợ sơn',
  THO_MOC: 'Thợ mộc',
  KHAC: 'Khác',
};

const CHUYEN_MON_OPTIONS = Object.entries(CHUYEN_MON_LABEL).map(([value, label]) => ({ value, label }));

const formatVND = (value) => {
  if (value === null || value === undefined) return 'Chưa thiết lập';
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
};

export default function QuanLyThoPage() {
  const [danhSachTho, setDanhSachTho] = useState([]);
  const [dangTai, setDangTai] = useState(true);
  const [loi, setLoi] = useState('');
  const [thongBao, setThongBao] = useState('');
  const [thongTinCapTaiKhoan, setThongTinCapTaiKhoan] = useState(null);
  const [tuKhoa, setTuKhoa] = useState('');
  const [hienThoDaNghi, setHienThoDaNghi] = useState(false);
  const [thoDangSua, setThoDangSua] = useState('');
  const [donGiaMoi, setDonGiaMoi] = useState('');
  const [dangLuu, setDangLuu] = useState(false);
  const [hienFormTao, setHienFormTao] = useState(false);
  const [dangTao, setDangTao] = useState(false);
  const [formTao, setFormTao] = useState({
    tenDangNhap: '', matKhau: '', hoTen: '', soDienThoai: '', chuyenMon: 'THO_CHINH', donGiaCong: '',
  });

  const taiDoiNgu = async () => {
    try {
      const response = await axiosClient.get('/nguoi-dung/tho/doi-ngu');
      setDanhSachTho(response.data);
      setLoi('');
      return true;
    } catch (error) {
      setLoi(error.response?.data?.message || 'Không tải được danh sách đội thợ.');
      return false;
    } finally {
      setDangTai(false);
    }
  };

  const taoTho = async (event) => {
    event.preventDefault();
    setDangTao(true);
    setLoi('');
    setThongBao('');
    setThongTinCapTaiKhoan(null);
    try {
      await axiosClient.post('/nguoi-dung/tho', {
        ...formTao,
        soDienThoai: formTao.soDienThoai || null,
        donGiaCong: Number(formTao.donGiaCong),
      });
      setThongTinCapTaiKhoan({
        tenDangNhap: formTao.tenDangNhap,
        matKhau: formTao.matKhau,
      });
      setFormTao({ tenDangNhap: '', matKhau: '', hoTen: '', soDienThoai: '', chuyenMon: 'THO_CHINH', donGiaCong: '' });
      setHienFormTao(false);
      await taiDoiNgu();
      setThongBao('Đã tạo tài khoản thợ riêng trong đội của bạn. Hãy gửi thông tin đăng nhập bên dưới cho thợ.');
    } catch (error) {
      setLoi(error.response?.data?.message || 'Không thể tạo tài khoản thợ.');
    } finally {
      setDangTao(false);
    }
  };

  useEffect(() => {
    let mounted = true;
    axiosClient.get('/nguoi-dung/tho/doi-ngu')
      .then((response) => {
        if (mounted) setDanhSachTho(response.data);
      })
      .catch((error) => {
        if (mounted) setLoi(error.response?.data?.message || 'Không tải được danh sách đội thợ.');
      })
      .finally(() => {
        if (mounted) setDangTai(false);
      });
    return () => { mounted = false; };
  }, []);

  const danhSachHienThi = useMemo(() => {
    const keyword = tuKhoa.trim().toLocaleLowerCase('vi');
    return danhSachTho.filter((tho) => {
      if (!hienThoDaNghi && !tho.active) return false;
      return !keyword
        || tho.hoTen?.toLocaleLowerCase('vi').includes(keyword)
        || tho.soDienThoai?.includes(keyword)
        || CHUYEN_MON_LABEL[tho.chuyenMon]?.toLocaleLowerCase('vi').includes(keyword);
    });
  }, [danhSachTho, hienThoDaNghi, tuKhoa]);

  const batDauSuaDonGia = (tho) => {
    setThoDangSua(String(tho.id));
    setDonGiaMoi(tho.donGiaCong ?? '');
    setLoi('');
    setThongBao('');
  };

  const luuDonGia = async (tho) => {
    setDangLuu(true);
    setLoi('');
    setThongBao('');
    try {
      const response = await axiosClient.patch(`/nguoi-dung/${tho.id}/don-gia-cong`, {
        donGiaCong: Number(donGiaMoi),
      });
      setDanhSachTho((current) => current.map((item) => (
        item.id === response.data.id ? { ...item, ...response.data } : item
      )));
      setThoDangSua('');
      setThongBao(`Đã cập nhật đơn giá của ${tho.hoTen}. Đơn giá mới chỉ áp dụng cho lượt chấm công tiếp theo.`);
    } catch (error) {
      setLoi(error.response?.data?.message || 'Cập nhật đơn giá thất bại.');
    } finally {
      setDangLuu(false);
    }
  };

  const choThoNghiViec = async (tho) => {
    const congTrinh = tho.congTrinhDangLamViec?.join(', ') || 'các công trình đang phân công';
    if (!window.confirm(
      `Ngừng tài khoản ${tho.hoTen} và kết thúc phân công tại ${congTrinh}? Thợ sẽ không đăng nhập được nữa. Lịch sử chấm công, tạm ứng và lương vẫn được giữ nguyên.`
    )) return;

    setLoi('');
    setThongBao('');
    try {
      await axiosClient.delete(`/nguoi-dung/${tho.id}/doi-ngu`);
      setDangTai(true);
      const daTaiLai = await taiDoiNgu();
      if (daTaiLai) {
        setThongBao(`Đã ngừng tài khoản của ${tho.hoTen} và gỡ thợ khỏi đội đang hoạt động. Lịch sử được giữ nguyên.`);
      } else {
        setLoi(`Đã ngừng tài khoản của ${tho.hoTen}, nhưng không tải lại được danh sách.`);
      }
    } catch (error) {
      setLoi(error.response?.data?.message || 'Không thể kết thúc phân công của thợ.');
    }
  };

  return (
    <div className="min-h-screen bg-concrete-50">
      <Navbar />
      <main className="mx-auto max-w-6xl px-4 py-8 sm:px-6">
        <div className="mb-6 flex flex-wrap items-end justify-between gap-3">
          <div>
            <p className="mb-1 text-xs font-extrabold uppercase tracking-[0.16em] text-amber-600">Nhân sự</p>
            <h1 className="font-display text-xl font-extrabold text-blueprint-900">Quản lý đội thợ</h1>
            <p className="mt-1 max-w-2xl text-sm text-blueprint-900/60">
              Tài khoản do bạn tạo và quản lý, chỉ thuộc đội của bạn. Ngừng thợ sẽ khóa đăng nhập và giữ nguyên dữ liệu lịch sử.
            </p>
          </div>
          <div className="flex gap-2">
            <button onClick={() => setHienFormTao((current) => !current)} className="rounded-lg bg-amber-500 px-4 py-2.5 text-sm font-bold text-blueprint-900 hover:bg-amber-600">
              {hienFormTao ? 'Đóng biểu mẫu' : '+ Tạo tài khoản thợ'}
            </button>
            <Link to="/dashboard" className="rounded-lg bg-blueprint-900 px-4 py-2.5 text-sm font-bold text-white hover:bg-blueprint-700">
              Phân công công trình
            </Link>
          </div>
        </div>

        {loi && <div role="alert" className="mb-4 rounded-lg border border-red-200 bg-red-50 px-3 py-2.5 text-sm text-red-700">{loi}</div>}
        {thongBao && <div role="status" className="mb-4 rounded-lg border border-green-200 bg-green-50 px-3 py-2.5 text-sm text-green-700">{thongBao}</div>}
        {thongTinCapTaiKhoan && (
          <section className="mb-4 rounded-lg border border-amber-300 bg-amber-50 p-4 text-sm text-blueprint-900">
            <p className="font-bold">Thông tin cấp cho thợ (chỉ hiển thị ở phiên này)</p>
            <p className="mt-2">Tên đăng nhập: <code>{thongTinCapTaiKhoan.tenDangNhap}</code></p>
            <p>Mật khẩu: <code>{thongTinCapTaiKhoan.matKhau}</code></p>
            <p className="mt-2 text-xs text-blueprint-900/65">Hãy gửi bảo mật cho thợ và hướng dẫn đổi mật khẩu sau lần đăng nhập đầu tiên.</p>
            <button onClick={() => setThongTinCapTaiKhoan(null)} className="mt-2 text-xs font-bold text-blueprint-700 underline">Ẩn thông tin</button>
          </section>
        )}

        {hienFormTao && (
          <form onSubmit={taoTho} className="mb-5 grid gap-3 rounded-xl border border-concrete-200 bg-white p-4 sm:grid-cols-2 lg:grid-cols-3">
            <label className="text-sm font-semibold text-blueprint-900">
              Họ tên
              <input required maxLength="100" value={formTao.hoTen} onChange={(event) => setFormTao({ ...formTao, hoTen: event.target.value })} className="mt-1 w-full rounded-lg border border-concrete-200 px-3 py-2 font-normal" />
            </label>
            <label className="text-sm font-semibold text-blueprint-900">
              Tên đăng nhập (toàn hệ thống)
              <input required maxLength="50" value={formTao.tenDangNhap} onChange={(event) => setFormTao({ ...formTao, tenDangNhap: event.target.value })} className="mt-1 w-full rounded-lg border border-concrete-200 px-3 py-2 font-normal" />
            </label>
            <label className="text-sm font-semibold text-blueprint-900">
              Mật khẩu cấp cho thợ (ít nhất 8 ký tự)
              <input required minLength="8" type="password" value={formTao.matKhau} onChange={(event) => setFormTao({ ...formTao, matKhau: event.target.value })} className="mt-1 w-full rounded-lg border border-concrete-200 px-3 py-2 font-normal" />
            </label>
            <label className="text-sm font-semibold text-blueprint-900">
              Số điện thoại
              <input maxLength="15" value={formTao.soDienThoai} onChange={(event) => setFormTao({ ...formTao, soDienThoai: event.target.value })} className="mt-1 w-full rounded-lg border border-concrete-200 px-3 py-2 font-normal" />
            </label>
            <label className="text-sm font-semibold text-blueprint-900">
              Chuyên môn
              <select value={formTao.chuyenMon} onChange={(event) => setFormTao({ ...formTao, chuyenMon: event.target.value })} className="mt-1 w-full rounded-lg border border-concrete-200 bg-white px-3 py-2 font-normal">
                {CHUYEN_MON_OPTIONS.map((option) => <option key={option.value} value={option.value}>{option.label}</option>)}
              </select>
            </label>
            <label className="text-sm font-semibold text-blueprint-900">
              Đơn giá công/ngày (VND)
              <input required type="number" min="0" step="1000" value={formTao.donGiaCong} onChange={(event) => setFormTao({ ...formTao, donGiaCong: event.target.value })} className="mt-1 w-full rounded-lg border border-concrete-200 px-3 py-2 font-normal" />
            </label>
            <div className="sm:col-span-2 lg:col-span-3">
              <button disabled={dangTao} className="rounded-lg bg-blueprint-900 px-4 py-2.5 text-sm font-bold text-white disabled:opacity-50">
                {dangTao ? 'Đang tạo tài khoản...' : 'Tạo thợ và cấp tài khoản'}
              </button>
            </div>
          </form>
        )}

        <section className="mb-4 flex flex-wrap items-center gap-3 rounded-xl border border-concrete-200 bg-white p-4">
          <input
            aria-label="Tìm thợ"
            value={tuKhoa}
            onChange={(event) => setTuKhoa(event.target.value)}
            placeholder="Tìm theo tên, số điện thoại, chuyên môn..."
            className="min-w-[220px] flex-1 rounded-lg border border-concrete-200 px-3 py-2"
          />
          <label className="flex items-center gap-2 text-sm font-semibold text-blueprint-900">
            <input type="checkbox" checked={hienThoDaNghi} onChange={(event) => setHienThoDaNghi(event.target.checked)} />
            Hiện cả tài khoản đã ngừng
          </label>
        </section>

        {dangTai ? (
          <p className="py-12 text-center text-sm text-blueprint-900/50">Đang tải danh sách...</p>
        ) : danhSachHienThi.length === 0 ? (
          <section className="rounded-xl border border-dashed border-concrete-200 bg-white px-5 py-12 text-center">
            <h2 className="font-display font-bold text-blueprint-900">
              {danhSachTho.length === 0 ? 'Đội thợ chưa có thành viên' : 'Không tìm thấy thợ phù hợp'}
            </h2>
            <p className="mx-auto mt-2 max-w-lg text-sm text-blueprint-900/60">
              {danhSachTho.length === 0
                ? 'Tạo tài khoản riêng cho thợ tại đây; chỉ bạn mới quản lý và phân công được tài khoản này.'
                : 'Thử thay đổi từ khóa hoặc bật tùy chọn hiển thị thợ đã kết thúc phân công.'}
            </p>
            {danhSachTho.length === 0 && (
              <Link to="/dashboard" className="mt-4 inline-flex rounded-lg bg-amber-500 px-4 py-2 font-bold text-blueprint-900 hover:bg-amber-600">
                Đi tới công trình
              </Link>
            )}
          </section>
        ) : (
          <div className="overflow-hidden rounded-xl border border-concrete-200 bg-white">
            <div className="overflow-x-auto">
              <table className="w-full min-w-[760px] text-left text-sm">
                <thead className="bg-blueprint-100/60 text-xs uppercase tracking-wide text-blueprint-900/60">
                  <tr>
                    <th className="px-4 py-3">Thợ</th>
                    <th className="px-4 py-3">Công trình đang làm</th>
                    <th className="px-4 py-3">Đơn giá công/ngày</th>
                    <th className="px-4 py-3">Trạng thái</th>
                    <th className="px-4 py-3 text-right">Thao tác</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-concrete-200">
                  {danhSachHienThi.map((tho) => (
                    <tr key={tho.id} className="align-top">
                      <td className="px-4 py-4">
                        <p className="font-bold text-blueprint-900">{tho.hoTen}</p>
                        <p className="mt-1 text-xs text-blueprint-900/55">Tài khoản: {tho.tenDangNhap}</p>
                        <p className="mt-1 text-xs text-blueprint-900/55">{CHUYEN_MON_LABEL[tho.chuyenMon] || 'Chưa cập nhật chuyên môn'}</p>
                        {tho.soDienThoai && <p className="mt-1 text-xs text-blueprint-900/55">{tho.soDienThoai}</p>}
                      </td>
                      <td className="px-4 py-4">
                        {tho.congTrinhDangLamViec?.length
                          ? tho.congTrinhDangLamViec.join(', ')
                          : <span className="text-blueprint-900/45">Không có phân công đang hoạt động</span>}
                      </td>
                      <td className="px-4 py-4">
                        {thoDangSua === String(tho.id) ? (
                          <div className="flex items-center gap-2">
                            <input
                              type="number"
                              min="0"
                              step="1000"
                              value={donGiaMoi}
                              onChange={(event) => setDonGiaMoi(event.target.value)}
                              aria-label={`Đơn giá công của ${tho.hoTen}`}
                              className="w-36 rounded-lg border border-concrete-200 px-2.5 py-2"
                            />
                            <button
                              onClick={() => luuDonGia(tho)}
                              disabled={dangLuu || donGiaMoi === ''}
                              className="rounded-lg bg-blueprint-900 px-3 py-2 text-xs font-bold text-white disabled:opacity-50"
                            >
                              {dangLuu ? 'Đang lưu...' : 'Lưu'}
                            </button>
                            <button onClick={() => setThoDangSua('')} className="px-1 text-xs font-semibold text-blueprint-900/60">
                              Hủy
                            </button>
                          </div>
                        ) : (
                          <div>
                            <span className="font-semibold text-blueprint-900">{formatVND(tho.donGiaCong)}</span>
                            {tho.dangLamViec && (
                              <button onClick={() => batDauSuaDonGia(tho)} className="ml-3 text-xs font-bold text-blueprint-700 hover:text-amber-600">
                                Điều chỉnh
                              </button>
                            )}
                          </div>
                        )}
                      </td>
                      <td className="px-4 py-4">
                        <span className={`rounded-full px-2.5 py-1 text-xs font-bold ${
                          !tho.active ? 'bg-red-50 text-red-700' : tho.dangLamViec ? 'bg-green-50 text-green-700' : 'bg-slate-100 text-slate-600'
                        }`}>
                          {!tho.active ? 'Đã ngừng tài khoản' : tho.dangLamViec ? 'Đang làm việc' : 'Chưa có phân công'}
                        </span>
                      </td>
                      <td className="px-4 py-4 text-right">
                        {tho.active && (
                          <button onClick={() => choThoNghiViec(tho)} className="rounded-lg border border-red-200 px-3 py-2 text-xs font-bold text-red-700 hover:bg-red-50">
                            Xóa khỏi đội / khóa tài khoản
                          </button>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
            <p className="border-t border-concrete-200 px-4 py-3 text-xs text-blueprint-900/50">
              Đơn giá điều chỉnh chỉ áp dụng cho lượt chấm công mới; đơn giá đã ghi nhận và các kỳ lương đã chốt không bị thay đổi.
            </p>
          </div>
        )}
      </main>
    </div>
  );
}
