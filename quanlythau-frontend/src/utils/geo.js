// Lay vi tri GPS hien tai, tra ve Promise({ latitude, longitude }).
export function layViTriHienTai() {
  return new Promise((resolve, reject) => {
    if (!navigator.geolocation) {
      reject(new Error('Trình duyệt không hỗ trợ định vị GPS'));
      return;
    }
    navigator.geolocation.getCurrentPosition(
      (vt) => {
        resolve({ latitude: vt.coords.latitude, longitude: vt.coords.longitude });
      },
      (err) => {
        reject(new Error('Không lấy được vị trí: ' + err.message));
      },
      { enableHighAccuracy: true, timeout: 10000 }
    );
  });
}
