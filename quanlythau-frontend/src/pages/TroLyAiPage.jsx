import { useEffect, useRef, useState } from 'react';
import axiosClient from '../api/axiosClient';
import Navbar from '../components/Navbar';

const CAU_HOI_GOI_Y = [
  'Công trình nào đang lãi nhiều nhất?',
  'Công trình nào có nguy cơ lỗ?',
  'Thợ nào có đơn giá công cao nhất?',
  'Tổng lãi/lỗ tạm tính của tất cả công trình là bao nhiêu?',
];

export default function TroLyAiPage() {
  const [lichSu, setLichSu] = useState([]);
  const [cauHoi, setCauHoi] = useState('');
  const [dangGui, setDangGui] = useState(false);
  const [loi, setLoi] = useState('');
  const cuoiRef = useRef(null);

  useEffect(() => {
    cuoiRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [lichSu]);

  const guiCauHoi = async (noiDung) => {
    if (!noiDung.trim() || dangGui) return;
    setLichSu((truoc) => [...truoc, { vaiTro: 'nguoiDung', noiDung }]);
    setCauHoi('');
    setDangGui(true);
    setLoi('');
    try {
      const res = await axiosClient.post('/tro-ly-ai/hoi-dap', { cauHoi: noiDung });
      setLichSu((truoc) => [...truoc, { vaiTro: 'ai', noiDung: res.data.traLoi }]);
    } catch (err) {
      setLoi(err.response?.data?.message || 'Trợ lý AI gặp lỗi, thử lại nhé');
    } finally {
      setDangGui(false);
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    guiCauHoi(cauHoi);
  };

  return (
    <div className="min-h-screen bg-concrete-50 flex flex-col">
      <Navbar />
      <main className="max-w-3xl mx-auto px-6 py-8 flex-1 flex flex-col w-full">
        <h2 className="font-display font-extrabold text-xl text-blueprint-900 mb-1">Trợ lý AI</h2>
        <p className="text-sm text-blueprint-900/50 mb-6">Hỏi nhanh về công trình, chi phí, thợ — trả lời dựa trên dữ liệu thật trong hệ thống của bạn.</p>

        {loi && <div className="mb-4 text-sm bg-red-50 text-red-700 border border-red-200 rounded px-3 py-2">{loi}</div>}

        <div className="flex-1 bg-white border border-concrete-200 rounded-lg p-5 mb-4 overflow-y-auto" style={{ minHeight: 400, maxHeight: 500 }}>
          {lichSu.length === 0 ? (
            <div className="h-full flex flex-col items-center justify-center gap-3">
              <p className="text-sm text-blueprint-900/40 text-center mb-2">Thử hỏi một trong các câu dưới đây:</p>
              <div className="flex flex-wrap gap-2 justify-center">
                {CAU_HOI_GOI_Y.map((c) => (
                  <button key={c} onClick={() => guiCauHoi(c)} className="text-xs bg-blueprint-100/60 hover:bg-blueprint-100 text-blueprint-900 px-3 py-1.5 rounded-full">
                    {c}
                  </button>
                ))}
              </div>
            </div>
          ) : (
            <div className="space-y-3">
              {lichSu.map((tin, i) => (
                <div key={i} className={`flex ${tin.vaiTro === 'nguoiDung' ? 'justify-end' : 'justify-start'}`}>
                  <div className={`max-w-[80%] px-4 py-2.5 rounded-lg text-sm whitespace-pre-wrap ${tin.vaiTro === 'nguoiDung' ? 'bg-blueprint-900 text-white' : 'bg-concrete-50 text-blueprint-900'}`}>
                    {tin.noiDung}
                  </div>
                </div>
              ))}
              {dangGui && (
                <div className="flex justify-start">
                  <div className="bg-concrete-50 text-blueprint-900/50 px-4 py-2.5 rounded-lg text-sm">Đang suy nghĩ...</div>
                </div>
              )}
              <div ref={cuoiRef} />
            </div>
          )}
        </div>

        <form onSubmit={handleSubmit} className="flex gap-2">
          <input
            value={cauHoi}
            onChange={(e) => setCauHoi(e.target.value)}
            placeholder="Hỏi gì đó về công trình, chi phí, lương..."
            className="flex-1 border border-concrete-200 rounded px-4 py-2.5"
          />
          <button type="submit" disabled={dangGui || !cauHoi.trim()} className="bg-amber-500 hover:bg-amber-600 text-blueprint-900 font-semibold px-5 py-2.5 rounded transition-colors disabled:opacity-50">
            Gửi
          </button>
        </form>
      </main>
    </div>
  );
}
