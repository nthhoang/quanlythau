import { useEffect, useRef, useState } from 'react';
import { taiModelKhuonMat, trichXuatKhuonMat } from '../utils/faceapi';

export default function CameraCapture({ onCapture, nhan = 'Chụp ảnh' }) {
  const videoRef = useRef(null);
  const canvasRef = useRef(null);
  const streamRef = useRef(null);

  const [dangTaiModel, setDangTaiModel] = useState(true);
  const [dangMoCamera, setDangMoCamera] = useState(false);
  const [loi, setLoi] = useState('');
  const [anhDaChup, setAnhDaChup] = useState(null);
  const [dangXuLy, setDangXuLy] = useState(false);

  useEffect(() => {
    taiModelKhuonMat()
      .then(() => setDangTaiModel(false))
      .catch(() => setLoi('Không tải được model nhận diện khuôn mặt. Kiểm tra lại kết nối mạng.'));

    return () => dungCamera();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const moCamera = async () => {
    setLoi('');
    try {
      const stream = await navigator.mediaDevices.getUserMedia({
        video: { facingMode: 'user', width: 480, height: 480 },
      });
      streamRef.current = stream;
      setDangMoCamera(true);
    } catch {
      setLoi('Không mở được camera. Kiểm tra quyền truy cập camera của trình duyệt.');
    }
  };

  useEffect(() => {
    if (dangMoCamera && videoRef.current && streamRef.current) {
      videoRef.current.srcObject = streamRef.current;
    }
  }, [dangMoCamera]);

  const dungCamera = () => {
    if (streamRef.current) {
      streamRef.current.getTracks().forEach((t) => t.stop());
      streamRef.current = null;
    }
    setDangMoCamera(false);
  };

  const chupAnh = async () => {
    if (!videoRef.current || !canvasRef.current) return;
    setDangXuLy(true);
    setLoi('');

    const video = videoRef.current;
    const canvas = canvasRef.current;

    if (!video.videoWidth || !video.videoHeight) {
      setLoi('Camera chưa sẵn sàng, đợi 1-2 giây rồi bấm chụp lại.');
      setDangXuLy(false);
      return;
    }

    canvas.width = video.videoWidth;
    canvas.height = video.videoHeight;
    canvas.getContext('2d').drawImage(video, 0, 0, canvas.width, canvas.height);

    try {
      const descriptor = await trichXuatKhuonMat(canvas);
      if (!descriptor) {
        setLoi('Không phát hiện khuôn mặt trong khung hình. Đưa mặt vào giữa khung và thử lại.');
        setDangXuLy(false);
        return;
      }

      canvas.toBlob((blob) => {
        const previewUrl = URL.createObjectURL(blob);
        setAnhDaChup({ previewUrl });
        dungCamera();
        setDangXuLy(false);
        onCapture({ descriptor, blob, previewUrl });
      }, 'image/jpeg', 0.9);
    } catch {
      setLoi('Xử lý ảnh thất bại, thử lại nhé');
      setDangXuLy(false);
    }
  };

  const chupLai = () => {
    setAnhDaChup(null);
    moCamera();
  };

  if (dangTaiModel) {
    return <p className="text-sm text-blueprint-900/50">Đang tải model nhận diện khuôn mặt...</p>;
  }

  return (
    <div>
      {loi && (
        <div className="mb-3 text-sm bg-red-50 text-red-700 border border-red-200 rounded px-3 py-2">
          {loi}
        </div>
      )}

      <canvas ref={canvasRef} className="hidden" />

      {anhDaChup ? (
        <div className="text-center">
          <img
            src={anhDaChup.previewUrl}
            alt="Ảnh đã chụp"
            className="w-48 h-48 object-cover rounded-lg mx-auto border border-concrete-200"
          />
          <button type="button" onClick={chupLai} className="mt-3 text-sm text-amber-600 font-semibold hover:underline">
            Chụp lại
          </button>
        </div>
      ) : dangMoCamera ? (
        <div className="text-center">
          <video
            ref={videoRef}
            autoPlay
            playsInline
            muted
            className="w-48 h-48 object-cover rounded-lg mx-auto bg-black scale-x-[-1]"
          />
          <button
            type="button"
            onClick={chupAnh}
            disabled={dangXuLy}
            className="mt-3 w-full bg-amber-500 hover:bg-amber-600 text-blueprint-900 font-semibold py-2 rounded transition-colors disabled:opacity-50"
          >
            {dangXuLy ? 'Đang xử lý...' : nhan}
          </button>
        </div>
      ) : (
        <button
          type="button"
          onClick={moCamera}
          className="w-full border border-blueprint-900 text-blueprint-900 font-semibold py-2 rounded hover:bg-blueprint-100/40 transition-colors"
        >
          Mở camera
        </button>
      )}
    </div>
  );
}
