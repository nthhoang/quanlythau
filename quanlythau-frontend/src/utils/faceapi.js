import * as faceapi from '@vladmandic/face-api';

const MODEL_URL = '/models';
let daTaiModel = false;
let dangTai = null;

export async function taiModelKhuonMat() {
  if (daTaiModel) return;
  if (dangTai) return dangTai;

  dangTai = Promise.all([
    faceapi.nets.tinyFaceDetector.loadFromUri(MODEL_URL),
    faceapi.nets.faceLandmark68Net.loadFromUri(MODEL_URL),
    faceapi.nets.faceRecognitionNet.loadFromUri(MODEL_URL),
  ]).then(() => {
    daTaiModel = true;
  });

  return dangTai;
}

export async function trichXuatKhuonMat(elementAnh) {
  await taiModelKhuonMat();

  const ketQua = await faceapi
    .detectSingleFace(elementAnh, new faceapi.TinyFaceDetectorOptions())
    .withFaceLandmarks()
    .withFaceDescriptor();

  if (!ketQua) return null;

  return Array.from(ketQua.descriptor);
}
