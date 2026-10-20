import { Client } from '@stomp/stompjs';
import { BACKEND_ORIGIN } from '../api/axiosClient';

/**
 * Mo 1 ket noi WebSocket (STOMP) toi backend de nhan thong bao real-time.
 * Token JWT duoc gui qua STOMP CONNECT header (khong phai HTTP header, vi
 * trinh duyet khong cho gan header tuy y luc mo WebSocket) - backend doc va
 * xac thuc o JwtChannelInterceptor. Tra ve client de noi goi tu huy
 * (deactivate) khi component unmount / dang xuat.
 */
export function taoKetNoiThongBao({ onNhanThongBao, onLoi }) {
  const token = localStorage.getItem('token');
  if (!token) return null;

  const wsUrl = BACKEND_ORIGIN.replace(/^http/, 'ws') + '/ws';

  const client = new Client({
    brokerURL: wsUrl,
    connectHeaders: { Authorization: `Bearer ${token}` },
    reconnectDelay: 5000,
    heartbeatIncoming: 10000,
    heartbeatOutgoing: 10000,
    onConnect: () => {
      client.subscribe('/user/queue/thong-bao', (message) => {
        try {
          onNhanThongBao(JSON.parse(message.body));
        } catch (err) {
          console.error('Không đọc được nội dung thông báo:', err);
        }
      });
    },
    onStompError: (frame) => {
      onLoi?.(`STOMP lỗi: ${frame.headers?.message || 'không rõ nguyên nhân'}`);
    },
    onWebSocketError: () => {
      onLoi?.(`Không mở được kết nối WebSocket tới ${wsUrl}`);
    },
    onWebSocketClose: (event) => {
      onLoi?.(`Kết nối WebSocket đã đóng (mã ${event.code}) - sẽ tự kết nối lại`);
    },
  });

  client.activate();
  return client;
}
